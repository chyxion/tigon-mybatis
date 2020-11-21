package me.chyxion.tigon.mybatis.xmlgen.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Repeatable;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Donghuang
 * @date Sep 11, 2019 09:59:32
 */
@Repeatable(CrudXmlElements.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrudXmlElement {

    /**
     * xml tag
     *
     * @return tag
     */
    Tag tag();

    /**
     * tag id
     * @return
     */
    String id();

    /**
     * select include sql
     *
     * @return include sql
     */
    String include();

    /**
     * select result type
     *
     * @return select result type
     */
    String resultType() default "";

    enum Tag {
        SELECT,
        INSERT,
        UPDATE,
        DELETE
    }

    /**
     * mark result type is entity
     */
    String RESULT_TYPE_ENTITY = "$ENTITY$";
}
