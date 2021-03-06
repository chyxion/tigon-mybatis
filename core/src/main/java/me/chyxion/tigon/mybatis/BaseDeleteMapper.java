package me.chyxion.tigon.mybatis;

import org.apache.ibatis.annotations.Param;
import me.chyxion.tigon.mybatis.xmlgen.annotation.MapperXmlEl;
import static me.chyxion.tigon.mybatis.xmlgen.annotation.MapperXmlEl.Tag.DELETE;

/**
 * @author Donghuang
 * @date Oct 17, 2015 2:09:20 PM
 */
@SuppressWarnings("hiding")
@MapperXmlEl(tag = DELETE, id = "delete")
public interface BaseDeleteMapper<PrimaryKey, Entity> extends SuperMapper<Entity> {

    /**
     * delete by search
     *
     * @param search search
     * @return delete result
     */
    int delete(@Param(PARAM_SEARCH_KEY) Search search);

    /**
     * delete by PrimaryKey
     *
     * @param primaryKey primaryKey
     * @return delete result
     */
    int delete(@Param(PARAM_SEARCH_KEY) PrimaryKey primaryKey);
}
