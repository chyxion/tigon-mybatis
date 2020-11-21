package me.chyxion.tigon.mybatis.xmlgen.contentprovider;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import me.chyxion.tigon.mybatis.xmlgen.XmlGenArg;

/**
 * @author Donghuang
 * @date Feb 21, 2017 17:42:03
 */
@Slf4j
public class TableXmlContentProvider extends SqlXmlContentProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public Content content(final XmlGenArg arg) {
        val tableName = arg.getTable();
        log.info("Table content [{}] generated.", tableName);
        return new Content(tableName);
    }
}
