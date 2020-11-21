package me.chyxion.tigon.mybatis;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Donghuang
 * @date Feb 21, 2017 19:27:09
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface UseGeneratedKeys {

    /**
     * keyProperty
     * @return keyProperty
     */
    String value() default "";
}
