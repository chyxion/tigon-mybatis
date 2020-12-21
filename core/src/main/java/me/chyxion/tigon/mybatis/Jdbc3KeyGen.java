package me.chyxion.tigon.mybatis;

import lombok.var;
import lombok.val;
import java.util.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import java.sql.ResultSetMetaData;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.mapping.MappedStatement;
import me.chyxion.tigon.mybatis.util.EntityUtils;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.keygen.KeyGenerator;

/**
 * JDBC3 key generator, supports multiple keys return
 *
 * @author Donghuang
 * @date 2017/1/9 14:46
 */
@Slf4j
class Jdbc3KeyGen implements KeyGenerator {

    static final Jdbc3KeyGen INSTANCE = new Jdbc3KeyGen();
    static final String MS_KEY_GEN_FIELD = "keyGenerator";

    /**
     * {@inheritDoc}
     */
    @Override
    public void processBefore(Executor executor,
        MappedStatement ms,
        Statement stmt,
        Object parameter) {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processAfter(final Executor executor,
        final MappedStatement ms,
        final Statement stmt,
        final Object params) {

        try (val rs = stmt.getGeneratedKeys()) {
            val configuration = ms.getConfiguration();
            val typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            var keyProps = ms.getKeyProperties();

            if (keyProps == null || keyProps.length == 0) {
                keyProps = new String[] { EntityUtils.ID };
            }

            val rsmd = rs.getMetaData();
            if (rsmd.getColumnCount() >= keyProps.length) {
                TypeHandler<?>[] typeHandlers = null;
                for (val model : getModels(params)) {
                    log.debug("Process model [{}] key.", model);
                    // there should be one row for each statement (also one for each params)
                    if (rs.next()) {
                        val metaModel = configuration.newMetaObject(model);
                        if (typeHandlers == null) {
                            typeHandlers = getTypeHandlers(typeHandlerRegistry, metaModel, keyProps, rsmd);
                        }
                        populateKeys(rs, metaModel, keyProps, typeHandlers);
                        log.debug("Populate model [{}] key result.", model);
                    }
                    else {
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            throw new ExecutorException(
                "Error getting generated key or setting result to params object. Cause: " + e, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> getModels(final Object objParams) {
        if (objParams instanceof MapperMethod.ParamMap) {
            val mapParams = (Map<String, Object>) objParams;

            if (mapParams.containsKey(SuperMapper.PARAM_MODEL_KEY)) {
                return Arrays.asList(mapParams.get(SuperMapper.PARAM_MODEL_KEY));
            }

            if (mapParams.containsKey(SuperMapper.PARAM_MODELS_KEY)) {
                val objModels = mapParams.get(SuperMapper.PARAM_MODELS_KEY);
                if (objModels.getClass().isArray()) {
                    return Arrays.asList((Object[]) objModels);
                }

                if (objModels instanceof Collection) {
                    return (Collection<Object>) objModels;
                }
            }
        }

        return Collections.emptyList();
    }

    private TypeHandler<?>[] getTypeHandlers(
        final TypeHandlerRegistry typeHandlerRegistry,
        final MetaObject metaParam,
        final String[] keyProperties,
        final ResultSetMetaData rsmd) throws SQLException {

        val typeHandlers = new TypeHandler<?>[keyProperties.length];

        for (int i = 0; i < keyProperties.length; ++i) {
            if (metaParam.hasSetter(keyProperties[i])) {
                typeHandlers[i] = typeHandlerRegistry.getTypeHandler(
                    metaParam.getSetterType(keyProperties[i]),
                    JdbcType.forCode(rsmd.getColumnType(i + 1)));
            }
        }

        return typeHandlers;
    }

    private void populateKeys(
        final ResultSet rs,
        final MetaObject metaParam,
        final String[] keyProperties,
        final TypeHandler<?>[] typeHandlers) throws SQLException {

        for (int i = 0; i < keyProperties.length; ++i) {
            val th = typeHandlers[i];
            if (th != null) {
                metaParam.setValue(keyProperties[i], th.getResult(rs, i + 1));
            }
        }
    }
}
