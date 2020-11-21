package me.chyxion.tigon.mybatis;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Donghuang
 * @date Sep 06, 2020 14:10:51
 */
@Slf4j
public class SqliteUrlFactory {

    /**
     * SQLite URL
     *
     * @return SQLite URL
     */
    public static String url() {
        final String url = SqliteUrlFactory.class
            .getResource("/tigon-mybatis.db").getFile();
        log.info("SQLite url [{}].", url);
        return "jdbc:sqlite:" + url;
    }
}
