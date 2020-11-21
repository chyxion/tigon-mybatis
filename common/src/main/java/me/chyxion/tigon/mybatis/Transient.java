package me.chyxion.tigon.mybatis;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark entity field as transient, do not insert or update
 *
 * @author Donghuang
 * @date Mar 16, 2017 23:57:44
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Transient {
}
