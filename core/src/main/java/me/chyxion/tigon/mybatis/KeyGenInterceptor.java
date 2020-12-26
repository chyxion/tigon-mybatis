package me.chyxion.tigon.mybatis;

import lombok.val;
import java.sql.Connection;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import static me.chyxion.tigon.mybatis.KeyGenInterceptor.METHOD_UPDATE;
import static me.chyxion.tigon.mybatis.KeyGenInterceptor.METHOD_PREPARE;

/**
 * because BatchExecutor:127 hard coded, so update Jdbc3KeyGen in runtime.
 * @see org.apache.ibatis.executor.BatchExecutor#doFlushStatements(boolean isRollback):127
 *
 * @author Donghuang
 * @date 2017/1/9 14:46
 */
@Intercepts({
    @Signature(type = StatementHandler.class,
        method = METHOD_PREPARE,
        args = {
            Connection.class,
            Integer.class
    }),
    @Signature(type = Executor.class,
        method = METHOD_UPDATE,
        args = {
            MappedStatement.class,
            Object.class
    })
})
@Slf4j
class KeyGenInterceptor implements Interceptor {
    public static final String METHOD_PREPARE = "prepare";
    public static final String METHOD_UPDATE = "update";

    /**
     * {@inheritDoc}
     */
    @Override
    public Object intercept(final Invocation invocation) throws Throwable {
        val method = invocation.getMethod().getName();

        if (METHOD_PREPARE.equals(method)) {
            return injectKeyGen(invocation);
        }

        if (METHOD_UPDATE.endsWith(method)) {
            try {
                return invocation.proceed();
            }
            catch (final Throwable e) {
                val statement = (MappedStatement) invocation.getArgs()[0];
                val keyGenerator = statement.getKeyGenerator();
                if (keyGenerator instanceof Jdbc3KeyGen) {
                    log.info("Update error caused, reset mapped statement [{}] key gen.", statement);
                    SystemMetaObject.forObject(statement).setValue(
                        Jdbc3KeyGen.MS_KEY_GEN_FIELD, Jdbc3KeyGenerator.INSTANCE);
                }
                throw e;
            }
        }

        return invocation.proceed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object plugin(Object target) {
        return (target instanceof StatementHandler) ||
            (target instanceof Executor) ?
                Plugin.wrap(target, this) : target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperties(Properties properties) {
    }

    // --
    // private methods

    StatementHandler getStatementHandler(Invocation invocation) {
        val statement = (StatementHandler) invocation.getTarget();

        if (statement instanceof RoutingStatementHandler) {
            return (StatementHandler) SystemMetaObject
                .forObject(statement).getValue("delegate");
        }

        return statement;
    }

    /**
     * intercept and set custom key generator
     *
     * @param invocation invocation
     * @return result
     * @throws Throwable exception
     */
    Object injectKeyGen(final Invocation invocation) throws Throwable {
        val objRtn = invocation.proceed();
        val metaObjStatementHandler =
            SystemMetaObject.forObject(getStatementHandler(invocation));
        val mappedStatement =
            (MappedStatement) metaObjStatementHandler.getValue("mappedStatement");

        if (SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType())) {
            val boundSql = (BoundSql) metaObjStatementHandler.getValue("boundSql");
            val paramObj = boundSql.getParameterObject();
            if (paramObj instanceof MapperMethod.ParamMap) {
                @SuppressWarnings("unchecked")
                val paramMap = (MapperMethod.ParamMap<Object>) paramObj;
                if (paramMap.containsKey(SuperMapper.PARAM_MODEL_KEY) ||
                        paramMap.containsKey(SuperMapper.PARAM_MODELS_KEY)) {
                    val keygen = mappedStatement.getKeyGenerator();
                    if (keygen.getClass().equals(Jdbc3KeyGenerator.class)) {
                        log.debug("Replace JDBC3 key generator.");
                        SystemMetaObject.forObject(mappedStatement).setValue(
                            Jdbc3KeyGen.MS_KEY_GEN_FIELD, Jdbc3KeyGen.INSTANCE);
                    }
                }
            }
        }
        return objRtn;
    }
}
