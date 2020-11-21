package me.chyxion.tigon.mybatis;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark entity field does not update when value is null
 *
 * @author Donghuang
 * @date Mar 16, 2017 23:51:00
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface NotUpdateWhenNull {
}
