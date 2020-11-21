package me.chyxion.tigon.mybatis;

import lombok.*;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Stream;
import me.chyxion.tigon.mybatis.util.StrUtils;

/**
 * Search process argument, may use in Search#build
 *
 * @author Donghuang
 * @date 2017/1/23 16:58
 */
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class ProcArg implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String table;

    /**
     * search assemble result
     */
    @NonNull
    private final List<Object> result;

    private Criterion criterion;
    private boolean hasPrevCol;
    private boolean hasPrevOrCol;

    /**
     * add SQL
     * @param sql sql
     * @return this
     */
    public ProcArg addSql(final String sql) {
        result.add(new SqlFragment(sql));
        return this;
    }

    /**
     * add value
     *
     * @return this
     */
    ProcArg addParam() {
        return addParam(getValue());
    }

    /**
     * add value
     *
     * @param value value
     * @return this
     */
    public ProcArg addParam(final Object value) {
        result.add(value);
        return this;
    }

    /**
     * add (1, 2, 3)
     *
     * @return this
     */
    ProcArg addParamList() {
        return addParamList(criterion.getValues());
    }

    /**
     * add param list
     *
     * (1, 2, 3)
     *
     * @return this
     */
    public ProcArg addParamList(final Object param, final Object ... params) {
        val paramsList = new ArrayList<>(params.length + 1);
        paramsList.add(param);
        Stream.of(params).forEach(paramsList::add);
        return addParamList(paramsList);
    }

    /**
     * add param list
     *
     * (1, 2, 3)
     *
     * @return this
     */
    public ProcArg addParamList(final Collection<?> values) {
        addSql("(");
        val size = values.size();
        int i = 0;
        for (val value : values) {
            addParam(value);
            if (++i < size) {
                addSql(", ");
            }
        }
        addSql(")");
        return this;
    }

    /**
     * add subsearch and(search) or(search)
     *
     * @return this
     */
    ProcArg addSubsearch() {
        final Search search = criterion.getAttr();
        if (StrUtils.isBlank(search.table())) {
            search.table(table);
        }
        result.addAll(search.assemble(true));
        return this;
    }

    /**
     * get criterion col
     *
     * @return col
     */
    String getCol() {
        return col(table, criterion.getCol());
    }

    /**
     * get criterion value
     *
     * @param <T> value type
     * @return value
     */
    <T> T getValue() {
        return criterion.getValue();
    }

    static String col(final String table, final String col0) {
        val col = StrUtils.camelToUnderscore(col0);
        return StrUtils.isNotBlank(table) &&
                !col.contains(".") ?
                table + "." + col : col;
    }
}
