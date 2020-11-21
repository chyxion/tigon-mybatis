package me.chyxion.tigon.mybatis.xmlgen.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Donghuang
 * @date Jan 09, 2020 16:03:12
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CrudXmlElements {
    CrudXmlElement[] value();
}
