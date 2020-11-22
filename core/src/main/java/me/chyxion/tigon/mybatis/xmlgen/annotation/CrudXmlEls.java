package me.chyxion.tigon.mybatis.xmlgen.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Donghuang
 * @date Jan 09, 2020 16:03:12
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CrudXmlEls {

    /**
     * grouped connotation values
     *
     * @return grouped connotation values
     */
    MapperXmlEl[] value();
}
