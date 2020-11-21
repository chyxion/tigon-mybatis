package me.chyxion.tigon.mybatis;

import lombok.Getter;
import java.io.Serializable;
import lombok.RequiredArgsConstructor;

/**
 * @author Donghuang
 * @date 2017/2/6 9:48
 */
@Getter
@RequiredArgsConstructor
public class SqlParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean raw;
    private final Object value;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SQL param [" + value + "], raw [" + raw + "].";
    }
}
