package me.chyxion.tigon.mybatis;

/**
 * @author Donghuang
 * @date Oct 17, 2015 2:09:20 PM
 */
@SuppressWarnings("hiding")
public interface BaseMapper<PrimaryKey, Entity>
    extends BaseQueryMapper<PrimaryKey, Entity>,
        BaseInsertMapper<Entity>,
        BaseUpdateMapper<PrimaryKey, Entity>,
        BaseDeleteMapper<PrimaryKey, Entity> {
}
