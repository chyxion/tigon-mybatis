package me.chyxion.tigon.mybatis;

import java.io.Serializable;

/**
 * Basic entity
 *
 * @author Donghuang
 * @date Aug 31, 2020 16:14:17
 */
public interface BasicEntity extends Serializable {

    /**
     * before entity insert, for override
     */
    default void beforeInsert() {
    }

    /**
     * before entity update, for override
     */
    default void beforeUpdate() {
    }
}
