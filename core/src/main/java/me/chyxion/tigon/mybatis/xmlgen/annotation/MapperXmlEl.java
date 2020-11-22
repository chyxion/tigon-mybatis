package me.chyxion.tigon.mybatis.xmlgen.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Repeatable;
import java.lang.annotation.RetentionPolicy;
import me.chyxion.tigon.mybatis.xmlgen.XmlGenArg;
import me.chyxion.tigon.mybatis.xmlgen.contentprovider.XmlContentProvider;

/**
 * @author Donghuang
 * @date Sep 11, 2019 09:59:32
 */
@Inherited
@Repeatable(CrudXmlEls.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperXmlEl {

    /**
     * xml tag
     *
     * @return xml tag
     */
    Tag tag();

    /**
     * element id
     * @return element id
     */
    String id();

    /**
     * include sql fragment id, for example "Tigon.find"
     *
     * @return include sql fragment id
     */
    String include() default "";

    /**
     * element content provider
     *
     * @return element content provider
     */
    Class<? extends XmlContentProvider> contentProvider() default EmptyProvider.class;

    /**
     * select result type
     *
     * @return select result type
     */
    String resultType() default "";

    enum Tag {
        SQL,
        SELECT,
        INSERT,
        UPDATE,
        DELETE
    }

    final class EmptyProvider extends XmlContentProvider {

        /**
         * {@inheritDoc}
         */
        @Override
        public Content content(final XmlGenArg arg) {
            throw new UnsupportedOperationException(
                    "Empty XML content provider called");
        }
    }

    /**
     * mark result type is entity
     */
    String RESULT_TYPE_ENTITY = "$ENTITY$";

}
