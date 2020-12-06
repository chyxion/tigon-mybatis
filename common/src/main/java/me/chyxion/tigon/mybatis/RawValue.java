package me.chyxion.tigon.mybatis;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark field uses raw value, for example:
 *
 * <pre>
 * @RawValue("uuid()")
 * private String globalKey;
 * </pre>
 *
 * @author Donghuang
 * @date Dec 13, 2018 20:15:03
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface RawValue {

    /**
     * value
     * @return raw value
     */
    String value() default "";
}
