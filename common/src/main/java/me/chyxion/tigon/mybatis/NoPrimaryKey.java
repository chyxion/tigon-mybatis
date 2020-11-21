package me.chyxion.tigon.mybatis;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark entity has no primary key
 *
 * @author Donghuang
 * @date Aug 31, 2020 21:43:38
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface NoPrimaryKey {
}
