package me.chyxion.tigon.mybatis;

import java.util.List;
import java.util.Collection;
import org.apache.ibatis.annotations.Param;
import me.chyxion.tigon.mybatis.xmlgen.annotation.MapperXmlEl;
import static me.chyxion.tigon.mybatis.xmlgen.annotation.MapperXmlEl.Tag.SELECT;
import static me.chyxion.tigon.mybatis.xmlgen.annotation.MapperXmlEl.RESULT_TYPE_ENTITY;

/**
 * @author Donghuang
 * @date Oct 17, 2015 2:09:20 PM
 */
@SuppressWarnings("hiding")
@MapperXmlEl(tag = SELECT, id = "count", resultType = "int")
@MapperXmlEl(tag = SELECT, id = "exists", resultType = "boolean")
@MapperXmlEl(tag = SELECT, id = "find", resultType = RESULT_TYPE_ENTITY)
@MapperXmlEl(tag = SELECT, id = "findCol", resultType = "object", include = "Tigon.selectCol")
@MapperXmlEl(tag = SELECT, id = "list", resultType = RESULT_TYPE_ENTITY)
@MapperXmlEl(tag = SELECT, id = "listCol", resultType = "object", include = "Tigon.selectCol")
public interface BaseQueryMapper<PrimaryKey, Entity> extends SuperMapper<Entity> {

    /**
     * count by search
     * @param search search
     * @return count
     */
    int count(@Param(PARAM_SEARCH_KEY) Search search);

    /**
     * find one by search
     * @param search search
     * @return true if exists rows
     */
    boolean exists(@Param(PARAM_SEARCH_KEY) Search search);

    /**
     * find one by search
     * @param search search
     * @return find result or null
     */
    Entity find(@Param(PARAM_SEARCH_KEY) Search search);

    /**
     * find one by PrimaryKey
     * @param primaryKey primaryKey
     * @return find result or null
     */
    Entity find(@Param(PARAM_SEARCH_KEY) PrimaryKey primaryKey);

    /**
     * find col by search
     *
     * @param col select col
     * @param search search
     * @return col result
     */
    <T> T findCol(@Param(PARAM_COL_KEY) String col, @Param(PARAM_SEARCH_KEY) Search search);

    /**
     * list by search
     *
     * @param search search
     * @return list result or empty list
     */
    List<Entity> list(@Param(PARAM_SEARCH_KEY) Search search);

    /**
     * list by primary keys
     *
     * @param keys primary keys
     * @return list result or empty list
     */
    default List<Entity> list(final Collection<PrimaryKey> keys) {
        return list(new Search(keys));
    }

    /**
     * list by primary keys
     *
     * @param keys primary keys
     * @return list result or empty list
     */
    default List<Entity> list(final PrimaryKey[] keys) {
        return list(new Search(keys));
    }

    /**
     * list col by search
     *
     * @param col select col
     * @param search search
     * @return list result or empty list
     */
    <T> List<T> listCol(@Param(PARAM_COL_KEY) String col, @Param(PARAM_SEARCH_KEY) Search search);
}
