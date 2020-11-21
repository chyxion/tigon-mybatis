package me.chyxion.tigon.mybatis;

import lombok.Getter;
import java.util.Arrays;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Donghuang
 * @date 2017/1/24 10:23
 */
@Getter
class Criterion implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Type type;
    private final String col;
    private final Collection<?> values;
    private final Object attr;

    Criterion(Type type, Object attr) {
        this.type = type;
        this.col = null;
        this.values = Collections.emptyList();
        this.attr = attr;
    }

    Criterion(Type type, String col, Object value) {
        this(type, col, Arrays.asList(value));
    }

    Criterion(Type type, String col, Object[] values) {
        this(type, col, Arrays.asList(values));
    }

    Criterion(Type type, String col, Collection<?> values) {
        this.type = type;
        this.col = col;
        this.values = values;
        this.attr = null;
    }

    /**
     * get attr
     *
     * @param <T> attr type
     * @return attr
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttr() {
        return (T) attr;
    }

    @SuppressWarnings("unchecked")
    <T> T getValue() {
        return values.isEmpty() ? null : (T) values.iterator().next();
    }

    public enum Type {
        EQ,
        NE,
        LIKE,
        NOT_LIKE,
        LT,
        LTE,
        GT,
        GTE,
        IS_NULL,
        IS_NOT_NULL,
        BETWEEN,
        NOT_BETWEEN,
        IN,
        NOT_IN,
        AND,
        OR,
        BUILDER
    }
}
