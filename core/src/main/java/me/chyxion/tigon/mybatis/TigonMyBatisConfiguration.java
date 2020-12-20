package me.chyxion.tigon.mybatis;

import lombok.*;
import java.util.*;
import org.w3c.dom.Element;
import java.io.InputStream;
import org.w3c.dom.Document;
import lombok.extern.slf4j.Slf4j;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.transform.OutputKeys;
import org.apache.ibatis.parsing.XNode;
import javax.xml.transform.dom.DOMSource;
import org.springframework.core.io.Resource;
import org.apache.ibatis.parsing.XPathParser;
import javax.xml.transform.TransformerFactory;
import me.chyxion.tigon.mybatis.util.StrUtils;
import org.apache.ibatis.session.Configuration;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilderFactory;
import me.chyxion.tigon.mybatis.util.AssertUtils;
import me.chyxion.tigon.mybatis.xmlgen.XmlGenArg;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.context.ApplicationContext;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import me.chyxion.tigon.mybatis.xmlgen.contentprovider.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import me.chyxion.tigon.mybatis.event.TigonMyBatisReadyEvent;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.springframework.beans.factory.annotation.Autowired;
import me.chyxion.tigon.mybatis.xmlgen.annotation.MapperXmlEl;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @author Donghuang
 * @date Jul 12, 2014 3:36:13 PM
 */
@Slf4j
public class TigonMyBatisConfiguration implements InitializingBean {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired(required = false)
    private SqlSessionFactory[] sqlSessionFactories;
    /**
     * After mapper beans initialized
     */
    @Autowired(required = false)
    private SuperMapper<?>[] mappers;

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        if (sqlSessionFactories == null || sqlSessionFactories.length == 0) {
            log.warn("No 'org.apache.ibatis.session.SqlSessionFactory' bean found, Tigon MyBatis ignored.");
            return;
        }

        log.info("Register tigon-mybatis.xml.");
        val path = "classpath*:tigon-mybatis.xml";
        val resources = findResources(path);
        AssertUtils.state(resources.length == 1,
            () -> "No unique resource [" + path + "] found");
        val resource = resources[0];
        log.info("Tigon namespace XML resource [{}] found.", resource);

        for (val sqlSessionFactory : sqlSessionFactories) {
            log.info("Register sql session factory [{}] tigon-mybatis.xml.", sqlSessionFactory);
            val config = sqlSessionFactory.getConfiguration();
            config.setMapUnderscoreToCamelCase(true);
            config.addInterceptor(new KeyGenInterceptor());

            val sqlFragments = config.getSqlFragments();
            new XMLMapperBuilder(
                    resourceInputStream(resource),
                    config,
                    resource.toString(),
                    sqlFragments).parse();

            val argGenXml = new ArgGenXml(
                    new XMLMapperEntityResolver(),
                    config);

            for (val mapper : config.getMapperRegistry().getMappers()) {
                if (SuperMapper.class.isAssignableFrom(mapper)) {
                    log.info("Generate mapper class [{}].", mapper);
                    argGenXml.setMapperClass((Class<SuperMapper<?>>) mapper);
                    argGenXml.setMapperXmlEls(getMapperXmlEls(mapper));

                    val bytesMapper = genMapperXml(argGenXml);

                    if (bytesMapper != null) {
                        log.debug("Mapper XML [{}] generated.", new String(bytesMapper));
                        new XMLMapperBuilder(
                                new ByteArrayInputStream(bytesMapper),
                                config,
                                "[Tigon]" + mapper.getName() + ".xml",
                                sqlFragments).parse();
                    }
                }
            }
        }

        applicationContext.publishEvent(
            new TigonMyBatisReadyEvent(applicationContext));
    }

    /**
     * find resource by path
     *
     * @param path path
     * @return resources
     */
    @SneakyThrows
    Resource[] findResources(final String path) {
        return new PathMatchingResourcePatternResolver().getResources(path);
    }

    @SneakyThrows
    InputStream resourceInputStream(final Resource resource) {
        return resource.getInputStream();
    }

    byte[] genMapperXml(final ArgGenXml argGenXml) {

        val mapperClass = argGenXml.getMapperClass();
        val doc = genDocument(argGenXml);
        val xPathParser = new XPathParser(
                doc, true, null, argGenXml.getXmlMapperEntityResolver());

        val configuration = argGenXml.getConfiguration();
        val sqlFragments = configuration.getSqlFragments();
        val namespacePrefix = mapperClass.getName() + ".";
        val xmlProcessArg = new XmlGenArg(xPathParser, doc, mapperClass);
        val docEl = doc.getDocumentElement();

        boolean updated = false;

        for (val element : argGenXml.getMapperXmlEls()) {
            var id = namespacePrefix + element.id();
            log.debug("Generate SQL fragment [{}].", id);

            // SQL
            if (element.tag() == MapperXmlEl.Tag.SQL) {
                if (sqlFragments.containsKey(id)) {
                    log.info("SQL fragment [{}] existed, ignore generate.", id);
                    continue;
                }
            }
            // CRUD
            else {
                if (configuration.hasStatement(id, false)) {
                    log.info("SQL statement [{}] existed, ignore generate.", id);
                    continue;
                }

                if (isIncompleteStatement(configuration, id)) {
                    log.info("Incomplete SQL statement [{}] existed, ignore generate.", id);
                    continue;
                }
            }

            docEl.appendChild(xmlEl(element, xmlProcessArg));
            updated = true;
        }

        if (updated) {
            return toBytes(doc);
        }

        return null;
    }

    boolean isIncompleteStatement(
        final Configuration configuration,
        final String id) {

        for (val statementBuilder : configuration.getIncompleteStatements()) {
            val metaObj = SystemMetaObject.forObject(statementBuilder);
            val assistant = (MapperBuilderAssistant) metaObj.getValue("builderAssistant");
            val context = (XNode) metaObj.getValue("context");

            if (id.equals(assistant.getCurrentNamespace() + "." +
                    context.getStringAttribute("id"))) {
                return true;
            }
        }

        return false;
    }

    @SneakyThrows
    Document genDocument(final ArgGenXml arg) {
        val mapperClass = arg.getMapperClass();
        val doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();

        val root = doc.createElement("mapper");
        root.setAttribute("namespace", mapperClass.getName());
        doc.appendChild(root);
        return doc;
    }

    @SneakyThrows
    byte[] toBytes(final Document doc) {
        val transformer = TransformerFactory.newInstance().newTransformer();

        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        val doctype = doc.getImplementation().createDocumentType(
                "DOCTYPE",
                "-//mybatis.org//DTD Mapper 3.0//EN",
                "http://mybatis.org/dtd/mybatis-3-mapper.dtd");

        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

        val baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(baos));
        return baos.toByteArray();
    }

    @SneakyThrows
    XmlContentProvider newProvider(final MapperXmlEl el) {
        return el.contentProvider().newInstance();
    }

    List<MapperXmlEl> getMapperXmlEls(final Class<?> clazz) {
        val interfaces = new LinkedHashSet<Class<?>>(8);
        getAllInterfaces(interfaces, clazz);
        val mapperXmlElsRtn = new ArrayList<MapperXmlEl>(16);

        for (val it : interfaces) {
            for (val mapperXmlEl : it.getAnnotationsByType(MapperXmlEl.class)) {
                mapperXmlElsRtn.add(mapperXmlEl);
            }
        }

        val setEls = new HashSet<String>(mapperXmlElsRtn.size());
        mapperXmlElsRtn.removeIf(e -> !setEls.add(e.tag() + e.id()));

        return mapperXmlElsRtn;
    }

     void getAllInterfaces(final Set<Class<?>> interfaces, final Class<?> clazz) {
        interfaces.add(clazz);
         for (val it : clazz.getInterfaces()) {
            getAllInterfaces(interfaces, it);
         }
     }

    Element xmlEl(final MapperXmlEl element, final XmlGenArg arg) {
        val doc = arg.getDocument();
        val tag = element.tag();
        val elId = element.id();
        val el = doc.createElement(tag.name().toLowerCase());
        el.setAttribute("id", elId);

        // result type
        val resultType = element.resultType();
        if (StrUtils.isNotBlank(resultType)) {
            el.setAttribute("resultType",
                    MapperXmlEl.RESULT_TYPE_ENTITY.equals(resultType) ?
                            arg.getEntityClass().getName() : resultType);
        }

        // insert
        if (tag == MapperXmlEl.Tag.INSERT) {
            val entityClass = arg.getEntityClass();
            val ugkAnnotation =
                    AnnotationUtils.findAnnotation(
                        entityClass, UseGeneratedKeys.class);

            if (ugkAnnotation != null) {
                el.setAttribute("useGeneratedKeys", "true");
                val keyProp = ugkAnnotation.value();
                if (StrUtils.isNotBlank(keyProp)) {
                    el.setAttribute("keyProperty", keyProp);
                }
            }
        }

        val contentProvider = element.contentProvider();

        if (contentProvider != MapperXmlEl.EmptyProvider.class) {
            val content = newProvider(element).content(arg);

            if (content.isText()) {
                el.setTextContent(content.getContent());
                return el;
            }

            content.getElements().forEach(el::appendChild);
            return el;
        }

        val includeEl = doc.createElement("include");
        val include = element.include();
        includeEl.setAttribute("refid",
            StrUtils.isNotBlank(include) ?
                include : "Tigon." + elId);
        el.appendChild(includeEl);
        return el;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class ArgGenXml {
        private final XMLMapperEntityResolver xmlMapperEntityResolver;
        private final Configuration configuration;
        private Class<SuperMapper<?>> mapperClass;
        private List<MapperXmlEl> mapperXmlEls;
    }
}
