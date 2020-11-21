package me.chyxion.tigon.mybatis;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark table of entity or mapper
 *
 * @author Donghuang
 * @date Feb 21, 2017 19:18:34
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Table {

    /**
     * table name
     * @return table name
     */
    String value();
}
