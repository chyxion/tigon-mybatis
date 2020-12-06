package me.chyxion.tigon.mybatis;

import me.chyxion.tigon.mybatis.xmlgen.annotation.MapperXmlEl;
import static me.chyxion.tigon.mybatis.xmlgen.annotation.MapperXmlEl.Tag.SQL;
import me.chyxion.tigon.mybatis.xmlgen.contentprovider.ColsXmlContentProvider;
import me.chyxion.tigon.mybatis.xmlgen.contentprovider.PrimaryKeyXmlContentProvider;
import me.chyxion.tigon.mybatis.xmlgen.contentprovider.TableXmlContentProvider;

/**
 * @author Donghuang
 * @date Dec 13, 2018 19:08:58
 */
@MapperXmlEl(tag = SQL, id = "table", contentProvider = TableXmlContentProvider.class)
@MapperXmlEl(tag = SQL, id = "primaryKey", contentProvider = PrimaryKeyXmlContentProvider.class)
@MapperXmlEl(tag = SQL, id = "cols", contentProvider = ColsXmlContentProvider.class)
public interface SuperMapper<Enity> {
    String PARAM_MODEL_KEY = "model";
    String PARAM_MODELS_KEY = "models";
    String PARAM_SEARCH_KEY = "s";
    String PARAM_COL_KEY = "col";
    String PARAM_COLS_KEY = "cols";
}
