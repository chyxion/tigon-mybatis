package me.chyxion.tigon.mybatis;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark entity field is primary key
 *
 * @author Donghuang
 * @date Feb 21, 2017 19:03:31
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface PrimaryKey {
}
