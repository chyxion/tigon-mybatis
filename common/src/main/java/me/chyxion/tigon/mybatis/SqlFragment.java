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
public class SqlFragment implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String sql;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return sql;
    }
}
