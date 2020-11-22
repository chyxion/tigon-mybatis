package me.chyxion.tigon.mybatis;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import me.chyxion.tigon.mybatis.xmlgen.annotation.MapperXmlEl;

/**
 * @author Donghuang
 * @date Oct 17, 2015 2:09:20 PM
 */
@MapperXmlEl(tag = MapperXmlEl.Tag.SELECT, id = "count", resultType = "int", include = "Tigon.count")
@MapperXmlEl(tag = MapperXmlEl.Tag.SELECT, id = "exists", resultType = "boolean", include = "Tigon.exists")
@MapperXmlEl(tag = MapperXmlEl.Tag.SELECT, id = "find", resultType = MapperXmlEl.RESULT_TYPE_ENTITY, include = "Tigon.find")
@MapperXmlEl(tag = MapperXmlEl.Tag.SELECT, id = "findCol", resultType = "object", include = "Tigon.selectCol")
@MapperXmlEl(tag = MapperXmlEl.Tag.SELECT, id = "list", resultType = MapperXmlEl.RESULT_TYPE_ENTITY, include = "Tigon.list")
@MapperXmlEl(tag = MapperXmlEl.Tag.SELECT, id = "listCol", resultType = "object", include = "Tigon.selectCol")
@SuppressWarnings("hiding")
public interface BaseQueryMapper<PrimaryKey, Entity>
    extends SuperMapper<Entity> {

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
     * @param search search
     * @return list result or empty list
     */
    List<Entity> list(@Param(PARAM_SEARCH_KEY) Search search);

    /**
     * list col by search
     *
     * @param col select col
     * @param search search
     * @return list result or empty list
     */
    <T> List<T> listCol(@Param(PARAM_COL_KEY) String col, @Param(PARAM_SEARCH_KEY) Search search);
}
