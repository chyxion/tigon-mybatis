package me.chyxion.tigon.mybatis.xmlgen.contentprovider;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Element;
import java.util.Collections;
import me.chyxion.tigon.mybatis.xmlgen.XmlGenArg;

/**
 * @author Donghuang
 * @date Feb 21, 2017 17:42:03
 */
public abstract class XmlContentProvider {

    /**
     * sql tag content
     *
     * @param arg arg
     * @return sql tag content
     */
    public abstract Content content(final XmlGenArg arg);

    @Getter
    @Setter
    public static class Content {
        private final boolean text;
        private final String content;
        private final List<Element> elements;

        public Content(final String content) {
            this.text = true;
            this.content = content;
            this.elements = Collections.emptyList();
        }

        public Content(final List<Element> elements) {
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
