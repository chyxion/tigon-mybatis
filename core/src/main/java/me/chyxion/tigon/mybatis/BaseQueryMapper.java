package me.chyxion.tigon.mybatis;

import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author Donghuang
 * @date Oct 17, 2015 2:09:20 PM
 */
@SuppressWarnings("hiding")
public interface BaseQueryMapper<PrimaryKey, Entity>
    extends SuperMapper<Entity> {

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

    /**
     * count by search
     * @param search search
     * @return count
     */
    int count(@Param(PARAM_SEARCH_KEY) Search search);
}
