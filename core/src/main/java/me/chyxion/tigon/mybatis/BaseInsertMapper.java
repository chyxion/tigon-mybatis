package me.chyxion.tigon.mybatis;

import java.util.Collection;
import org.apache.ibatis.annotations.Param;
import me.chyxion.tigon.mybatis.xmlgen.annotation.MapperXmlEl;
import static me.chyxion.tigon.mybatis.xmlgen.annotation.MapperXmlEl.Tag.INSERT;

/**
 * @author Donghuang
 * @date Oct 17, 2015 2:09:20 PM
 */
@SuppressWarnings("hiding")
@MapperXmlEl(tag = INSERT, id = "insert")
public interface BaseInsertMapper<Entity> extends SuperMapper<Entity> {

    /**
     * insert entity
     * @param entity entity
     * @return insert result
     */
    int insert(@Param(PARAM_MODEL_KEY) Entity entity);

    /**
     * insert model
     * @param entities entities
     * @return insert result
     */
    int insert(@Param(PARAM_MODELS_KEY) Collection<Entity> entities);

    /**
     * insert model
     * @param entities entities
     * @return insert result
     */
    int insert(@Param(PARAM_MODELS_KEY) Entity[] entities);
}
