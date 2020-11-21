package me.chyxion.tigon.mybatis.xmlgen.contentprovider;

import lombok.val;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Element;
import java.util.Collections;
import me.chyxion.tigon.mybatis.xmlgen.XmlGenArg;
import me.chyxion.tigon.mybatis.xmlgen.annotation.SqlXmlElement;

/**
 * @author Donghuang
 * @date Feb 21, 2017 17:42:03
 */
public abstract class SqlXmlContentProvider {

    /**
     * {@inheritDoc}
     */
    public Element element(final SqlXmlElement element, final XmlGenArg arg) {
        val el = arg.getDocument().createElement("sql");
        el.setAttribute("id", element.id());
        val content = content(arg);

        if (content.isText()) {
            el.setTextContent(content.getContent());
            return el;
        }

        content.getElements().forEach(el::appendChild);
        return el;
    }

    /**
     * sql tag content
     *
     * @param arg arg
     * @return sql tag content
     */
    protected abstract Content content(final XmlGenArg arg);

    @Getter
    @Setter
    protected static class Content {
        private final boolean text;
        private final String content;
        private final List<Element> elements;

        public Content(final String content) {
            this.text = true;
            this.content = content;
            this.elements = Collections.emptyList();
        }

        public Content(List<Element> elements) {
            this.text = false;
            this.content = null;
            this.elements = elements;
        }

        public Content(final Element element) {
            this(new ArrayList<>());
            elements.add(element);
        }
    }
}
