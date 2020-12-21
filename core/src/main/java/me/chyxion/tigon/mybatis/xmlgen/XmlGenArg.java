package me.chyxion.tigon.mybatis.xmlgen;

import lombok.val;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;
import lombok.extern.slf4j.Slf4j;
import me.chyxion.tigon.mybatis.Table;
import me.chyxion.tigon.mybatis.SuperMapper;
import org.apache.ibatis.parsing.XPathParser;
import me.chyxion.tigon.mybatis.util.StrUtils;
import me.chyxion.tigon.mybatis.util.AssertUtils;
import me.chyxion.tigon.mybatis.util.EntityUtils;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author Donghuang
 * @date Jan 09, 2020 17:32:15
 */
@Slf4j
@Getter
public class XmlGenArg {
    private final XPathParser parser;
    private final Document document;
    private final Class<SuperMapper<?>> mapperClass;
    private final Class<?> entityClass;
    private final String table;
    @Setter
    private boolean genKeys;

    public XmlGenArg(final XPathParser parser,
                     final Document document,
                     final Class<SuperMapper<?>> mapperClass) {

        this.parser = parser;
        this.document = document;
        this.mapperClass = mapperClass;
        this.entityClass =
            GenericTypeResolver.resolveTypeArguments(
                mapperClass, SuperMapper.class)[0];
        this.table = table();
        log.info("Entity [{}] mapper [{}] table [{}] found.", entityClass, mapperClass, table);
    }

    /**
     * get table
     * @return table
     */
    private String table() {

        // find table from mapper annotation
        val mapperAnnoTable =
            AnnotationUtils.findAnnotation(mapperClass, Table.class);
        if (mapperAnnoTable != null) {
            val table = mapperAnnoTable.value();
            log.info("Mapper [{}] annotation table [{}] found.", mapperClass, table);
            AssertUtils.state(StrUtils.isNotBlank(table),
                "Annotation table name could not be blank");
            return table;
        }

        val entityAnnoTable =
            AnnotationUtils.findAnnotation(entityClass, Table.class);
        if (entityAnnoTable != null) {
            val table = entityAnnoTable.value();
            log.info("Entity [{}] annotation table [{}] found.", entityClass, table);
            AssertUtils.state(StrUtils.isNotBlank(table),
                "Annotation table name could not be blank");
            return table;
        }

        val modelName = entityClass.getSimpleName();
        val table = StrUtils.camelToUnderscore(modelName);
        log.info("Get table name [{}] from entity class name [{}]", table, modelName);
        return table;
    }

    /**
     * entity cols
     *
     * @return cols
     */
    public String cols() {
        return StrUtils.join(EntityUtils.cols(entityClass), ", ");
    }
}
