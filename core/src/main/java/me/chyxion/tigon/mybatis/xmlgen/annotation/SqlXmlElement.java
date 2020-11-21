package me.chyxion.tigon.mybatis.xmlgen.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Repeatable;
import java.lang.annotation.RetentionPolicy;
import me.chyxion.tigon.mybatis.xmlgen.contentprovider.SqlXmlContentProvider;

/**
 * @author Donghuang
 * @date Sep 11, 2019 09:59:32
 */
@Repeatable(SqlXmlElements.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlXmlElement {

    /**
     * tag id
     * @return
     */
    String id();

    /**
     * tag content
     *
     * @return tag content
     */
    Class<? extends SqlXmlContentProvider> contentProvider();
}
