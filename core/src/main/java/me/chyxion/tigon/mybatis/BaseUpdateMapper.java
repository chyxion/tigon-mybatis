package me.chyxion.tigon.mybatis;

import java.util.Map;
import java.util.Collection;
import org.apache.ibatis.annotations.Param;
import me.chyxion.tigon.mybatis.xmlgen.annotation.MapperXmlEl;

/**
 * @author Donghuang
 * @date Oct 17, 2015 2:09:20 PM
 */
@SuppressWarnings("hiding")
@MapperXmlEl(tag = MapperXmlEl.Tag.UPDATE, id = "update", include = "Tigon.update")
@MapperXmlEl(tag = MapperXmlEl.Tag.UPDATE, id = "setNull", include = "Tigon.setNull")
public interface BaseUpdateMapper<PrimaryKey, Entity>
    extends SuperMapper<Entity> {

    /**
     * update entity by primary key
     * @param entity update entity
     * @return update result
     */
    int update(@Param(PARAM_MODEL_KEY) Entity entity);

    /**
     * update by search
     * @param entity update entity
     * @param search update search
     * @return update result
     */
    int update(@Param(PARAM_MODEL_KEY) Entity entity, @Param(PARAM_SEARCH_KEY) Search search);

    /**
     * update by search
     * @param update update map
     * @param search update search
     * @return update result
     */
    int update(@Param(PARAM_MODEL_KEY) Map<String, ?> update, @Param(PARAM_SEARCH_KEY) Search search);

    /**
     * update by primary key
     * @param update update model
     * @param primaryKey primary key
     * @return update result
     */
    int update(@Param(PARAM_MODEL_KEY) Map<String, ?> update, @Param(PARAM_SEARCH_KEY) PrimaryKey primaryKey);
    /**
     * set col to null
     * @param col col
     * @param primaryKey primary key
     * @return update result
     */
    int setNull(@Param(PARAM_COL_KEY) String col, @Param(PARAM_SEARCH_KEY) PrimaryKey primaryKey);

    /**
     * set col null
     * @param col col
     * @param search search
     * @return update result
     */
    int setNull(@Param(PARAM_COL_KEY) String col, @Param(PARAM_SEARCH_KEY) Search search);

    /**
     * set cols null
     * @param cols cols
     * @param primaryKey primary key
     * @return update result
     */
    int setNull(@Param(PARAM_COLS_KEY) String[] cols, @Param(PARAM_SEARCH_KEY) PrimaryKey primaryKey);

    /**
     * set cols null
     * @param cols cols
     * @param search search
     * @return update result
     */
    int setNull(@Param(PARAM_COLS_KEY) String[] cols, @Param(PARAM_SEARCH_KEY) Search search);

    /**
     * set cols null
     * @param cols cols
     * @param primaryKey primary key
     * @return update result
     */
    int setNull(@Param(PARAM_COLS_KEY) Collection<String> cols, @Param(PARAM_SEARCH_KEY) PrimaryKey primaryKey);

    /**
     * set cols null
     * @param cols cols
     * @param search search
     * @return update result
     */
    int setNull(@Param(PARAM_COLS_KEY) Collection<String> cols, @Param(PARAM_SEARCH_KEY) Search search);
}
