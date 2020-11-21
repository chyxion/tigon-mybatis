package me.chyxion.tigon.mybatis.xmlgen.contentprovider;

import lombok.extern.slf4j.Slf4j;
import me.chyxion.tigon.mybatis.xmlgen.XmlGenArg;

/**
 * @author Donghuang
 * @date Jan 09, 2020 18:05:43
 */
@Slf4j
public class ColsXmlContentProvider extends SqlXmlContentProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public Content content(final XmlGenArg arg) {
        return new Content(arg.cols());
    }
}
