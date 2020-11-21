package me.chyxion.tigon.mybatis;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark entity field does not update
 *
 * @author Donghuang
 * @date Jun 25, 2017 09:57:32
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface NotUpdate {
}
