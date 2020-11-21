package me.chyxion.tigon.mybatis;

import lombok.val;
import java.util.*;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.function.Consumer;
import me.chyxion.tigon.mybatis.util.StrUtils;
import static me.chyxion.tigon.mybatis.Criterion.Type.*;

/**
 * @author Donghuang
 * @date May 12, 2015 3:00:40 PM
 */
public class Search implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * order
     */
    public enum Order {
        ASC,
        DESC
    }

    private final List<Criterion> criteria = new LinkedList<>();
    private String table;
    private Integer offset;
    private Integer limit;
    private final Map<String, String> orders = new LinkedHashMap<>();
    private final Map<String, Object> attrs = new HashMap<>();
    private static final Map<Criterion.Type, Consumer<ProcArg>> PROCESSORS;

    static {
        PROCESSORS = new HashMap<>(16);

        PROCESSORS.put(EQ, arg -> arg.addSql(arg.getCol() + " = ").addParam());
        PROCESSORS.put(NE, arg -> arg.addSql(arg.getCol() + " <> ").addParam());
        PROCESSORS.put(LIKE, arg -> arg.addSql(arg.getCol() + " like ").addParam());
        PROCESSORS.put(NOT_LIKE, arg -> arg.addSql(arg.getCol() + " not like ").addParam());
        PROCESSORS.put(LT, arg -> arg.addSql(arg.getCol() + " < ").addParam());
        PROCESSORS.put(LTE, arg -> arg.addSql(arg.getCol() + " <= ").addParam());
        PROCESSORS.put(GT, arg -> arg.addSql(arg.getCol() + " > ").addParam());
        PROCESSORS.put(GTE, arg -> arg.addSql(arg.getCol() + " >= ").addParam());
        PROCESSORS.put(IS_NULL, arg -> arg.addSql(arg.getCol() + " is null"));
        PROCESSORS.put(IS_NOT_NULL, arg -> arg.addSql(arg.getCol() + " is not null"));
        PROCESSORS.put(BETWEEN, arg -> {
            val valIt = arg.getCriterion()
                .getValues().iterator();
            arg.addSql(arg.getCol() + " between ")
                .addParam(valIt.next())
                .addSql(" and ")
                .addParam(valIt.next());
        });
        PROCESSORS.put(NOT_BETWEEN, arg -> {
            val valIt = arg.getCriterion()
                .getValues().iterator();
            arg.addSql(arg.getCol() + " not between ")
                .addParam(valIt.next())
                .addSql(" and ")
                .addParam(valIt.next());
        });
        PROCESSORS.put(IN, arg ->
            arg.addSql(arg.getCol() + " in ").addParamList());
        PROCESSORS.put(NOT_IN, arg ->
            arg.addSql(arg.getCol() + " not in ").addParamList());
        PROCESSORS.put(AND, arg -> arg.addSubsearch());
        PROCESSORS.put(OR, arg -> arg.addSubsearch());
        PROCESSORS.put(BUILDER, arg ->
            ((Consumer<ProcArg>) arg.getCriterion().getAttr()).accept(arg));
    }

    /**
     * default construct
     */
    public Search() {
    }
    
    /**
     * @param value id value
     */
    public Search(final Object value) {
        eq("id", value);
    }

    /**
     * construct by eq
     * @param col col name
     * @param value value
     */
    public Search(final String col, final Object value) {
        eq(col, value);
    }

    /**
     * construct by eq
     * @param col col name
     * @param values values
     */
    public Search(final String col, final Collection<?> values) {
        in(col, values);
    }

    /**
     * construct by eq
     *
     * @param col col name
     * @param values values
     */
    public Search(final String col, final Object[] values) {
        in(col, values);
    }

    /**
     * set table
     * @param table table
     * @return this
     */
    public Search table(final String table) {
        this.table = table;
        return this;
    }

    /**
     * clear criteria
     * @return search
     */
    public Search clearCriteria() {
        criteria.clear();
        return this;
    }

    /**
     * clear orders
     * @return search
     */
    public Search clearOrders() {
        orders.clear();
        return this;
    }

    /**
     * col eq
     *
     * @param col col name
     * @param value value
     * @return this
     */
    public Search eq(final String col, final Object value) {
        if (value instanceof Collection) {
            in(col, (Collection<?>) value);
        }
        else if (value != null && value.getClass().isArray()) {
            // may primitive, (Object[]) causes exception
            in(col, arrayToList(value));
        }
        else {
            criteria.add(new Criterion(EQ, col, value));
        }

        return this;
    }

    /**
     * col is null
     *
     * @param col col name
     * @return this
     */
    public Search isNull(final String col) {
        return eq(col, null);
    }

    /**
     * col in values array
     *
     * @param col col name
     * @param values values
     * @return this
     */
    public Search in(final String col, final Object[] values) {
        criteria.add(new Criterion(IN, col, values));
        return this;
    }

    /**
     * col in values list
     *
     * @param col col name
     * @param values values
     * @return this
     */
    public Search in(final String col, final Collection<?> values) {
        criteria.add(new Criterion(IN, col, values));
        return this;
    }

    /**
     * col like value
     *
     * @param col col name
     * @param value value
     * @return this
     */
    public Search like(final String col, final String value) {
        criteria.add(new Criterion(LIKE, col, value));
        return this;
    }

    /**
     * col like value
     *
     * @param col col name
     * @param value value
     * @param wrapValue wrap value with %
     * @return this
     */
    public Search like(final String col, final String value, final boolean wrapValue) {
        return like(col, wrapValue ? "%" + value + "%" : value);
    }

    /**
     * col not like value
     *
     * @param col col name
     * @param value value
     * @return this
     */
    public Search notLike(final String col, final String value) {
        criteria.add(new Criterion(NOT_LIKE, col, value));
        return this;
    }

    /**
     * col not like value
     *
     * @param col col name
     * @param value value
     * @param wrapValue wrap value with %
     * @return this
     */
    public Search notLike(final String col, final String value, final boolean wrapValue) {
        return notLike(col, wrapValue ? "%" + value + "%" : value);
    }

    /**
     * col contains value
     *
     * @see #like
     * @param col col name
     * @param value value
     * @return this
     */
    public Search contains(final String col, final String value) {
        return like(col, value, true);
    }

    /**
     * col not contains value
     *
     * @see #notLike
     * @param col col name
     * @param value value
     * @return this
     */
    public Search notContains(final String col, final String value) {
        return notLike(col, value, true);
    }

    /**
     * col starts with value
     *
     * @see #like
     * @param col col name
     * @param value value
     * @return this
     */
    public Search startsWith(final String col, final String value) {
        return like(col, value + "%");
    }

    /**
     * col not starts with value
     *
     * @see #notLike
     * @param col col name
     * @param value value
     * @return this
     */
    public Search notStartsWith(final String col, final String value) {
        return notLike(col, value + "%");
    }

    /**
     * col ends with value
     *
     * @see #like(String, String)
     * @param col col name
     * @param value value
     * @return this
     */
    public Search endsWith(final String col, final String value) {
        return like(col, "%" + value);
    }

    /**
     * col not ends with value
     *
     * @see #notLike(String, String)
     * @param col col name
     * @param value value
     * @return this
     */
    public Search notEndsWith(final String col, final String value) {
        return notLike(col, "%" + value);
    }

    /**
     * col between value1 and value2
     *
     * @param col col name
     * @param bottom bottom value
     * @param top top value
     * @return this
     */
    public Search between(final String col, final Object bottom, final Object top) {
        criteria.add(new Criterion(
            BETWEEN,
            col, 
            Arrays.asList(bottom, top)));
        return this;
    }

    /**
     * col not between value1 and value2
     *
     * @param col col name
     * @param bottom bottom value
     * @param top top value
     * @return this
     */
    public Search notBetween(final String col, final Object bottom, final Object top) {
        criteria.add(new Criterion(
            NOT_BETWEEN,
            col,
            Arrays.asList(bottom, top)));
        return this;
    }

    /**
     * col not eq value
     *
     * @param col col name
     * @param value value
     * @return this
     */
    public Search ne(final String col, final Object value) {
        criteria.add(new Criterion(NE, col, value));
        return this;
    }

    /**
     * col is not null
     *
     * @param col col
     * @return search
     */
    public Search notNull(final String col) {
        criteria.add(new Criterion(IS_NOT_NULL, col, (Object) null));
        return this;
    }

    /**
     * col is not not in values
     *
     * @param col col name
     * @param values values
     * @return this
     */
    public Search notIn(final String col, final Object[] values) {
        return notIn(col, Arrays.asList(values));
    }
    
    /**
     * col is not not in values
     *
     * @param col col name
     * @param values values
     * @return this
     */
    public Search notIn(final String col, final Collection<?> values) {
        criteria.add(new Criterion(NOT_IN, col, values));
        return this;
    }
    
    /**
     * col is less than value
     *
     * @param col col name
     * @param value value
     * @return this
     */
    public Search lt(final String col, final Object value) {
        criteria.add(new Criterion(LT, col, value));
        return this;
    }

    /**
     * col is less than value or equals value
     *
     * @param col col name
     * @param value value
     * @return this
     */
    public Search lte(final String col, final Object value) {
        criteria.add(new Criterion(LTE, col, value));
        return this;
    }

    /**
     * col is greater than value
     *
     * @param col col name
     * @param value value
     * @return this
     */
    public Search gt(final String col, final Object value) {
        criteria.add(new Criterion(GT, col, value));
        return this;
    }

    /**
     * col is greater than value or equals value
     *
     * @param col col name
     * @param value value
     * @return this
     */
    public Search gte(final String col, final Object value) {
        criteria.add(new Criterion(GTE, col, value));
        return this;
    }

    /**
     * and another search
     *
     * @param search search
     * @return this
     */
    public Search and(final Search search) {
        if (StrUtils.isBlank(search.table)) {
            search.table = table;
        }
        criteria.add(new Criterion(AND, search));
        return this;
    }

    /**
     * or another search
     *
     * @param search search
     * @return this
     */
    public Search or(final Search search) {
        if (StrUtils.isBlank(search.table)) {
            search.table = table;
        }
        criteria.add(new Criterion(OR, search));
        return this;
    }

    /**
     * or col eq val
     *
     * @param col col
     * @param value value
     * @return this
     */
    public Search or(final String col, final Object value) {
        return or(new Search(col, value).table(table));
    }

    /**
     * build custom search criterion
     *
     * @param builder criterion builder
     * @return this
     */
    public Search build(final Consumer<ProcArg> builder) {
        criteria.add(new Criterion(BUILDER, builder));
        return this;
    }

    /**
     * order by col asc
     *
     * @param col col
     * @return this
     */
    public Search asc(final String col) {
        return orderBy(col, Order.ASC);
    }

    /**
     * order by col desc
     *
     * @param col col name
     * @return this
     */
    public Search desc(final String col) {
        return orderBy(col, Order.DESC);
    }

    /**
     * order by
     *
     * @param col col name
     * @param order order
     * @return this
     */
    public Search orderBy(final String col, final Order order) {
        orders.put(ProcArg.col(table, col), order.name());
        return this;
    }

    /**
     * set offset
     *
     * @param offset offset
     * @return this
     */
    public Search offset(final Integer offset) {
        this.offset = offset;
        return this;
    }

    /**
     * get offset
     *
     * @return offset
     */
    public Integer offset() {
        return offset;
    }

    /**
     * set limit
     *
     * @param limit limit
     * @return this
     */
    public Search limit(final Integer limit) {
        this.limit = limit;
        return this;
    }

    /**
     * get limit
     *
     * @return limit
     */
    public Integer limit() {
        return limit;
    }

    /**
     * @return orders
     */
    public Map<String, String> orders() {
        val ordersRtn = new LinkedHashMap<String, String>(orders.size());
        val hasTable = StrUtils.isNotBlank(table);

        for (val order : orders.entrySet()) {
            val col = order.getKey();
            ordersRtn.put(hasTable && !col.contains(".") ?
                table + "." + col : col, order.getValue());
        }

        return ordersRtn;
    }

    /**
     * assemble search to sql and param list
     *
     * @return sql and param list
     */
    public List<Object> assemble() {
        return assemble(false);
    }

    /**
     * @return true if has criterion
     */
    public boolean hasCriterion() {
        return !criteria.isEmpty();
    }

    /**
     * @param col col
     * @return true if has col criterion
     */
    public boolean hasCriterion(final String col) {
        for (val criterion : criteria) {
            if (col.equals(criterion.getCol())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if has no criterion
     */
    public boolean hasNoCriterion() {
        return criteria.isEmpty();
    }

    /**
     * @return true if has order
     */
    public boolean hasOrder() {
        return !orders.isEmpty();
    }

    /**
     * @return true if has no order
     */
    public boolean hasNoOrder() {
        return orders.isEmpty();
    }

    /**
     * check attrs data
     * @param name name
     * @return true if has attr
     */
    public boolean hasAttr(final String name) {
        return attrs.containsKey(name);
    }

    /**
     * check attr val is true
     * @param name attr name
     * @return true if attr is true
     */
    public boolean trueAttr(final String name) {
        val val = attr(name);
        return (val instanceof Boolean) && (Boolean) val;
    }

    /**
     * get attrs data
     * @param name name
     * @param <T> data type
     * @return data
     */
    public <T> T getAttr(final String name) {
        return attr(name);
    }

    /**
     * get attrs data
     * @param name name
     * @param <T> data type
     * @return data
     */
    @SuppressWarnings("unchecked")
    public <T> T attr(final String name) {
        return (T) attrs.get(name);
    }

    /**
     * set attr
     * @param name name
     * @param value value
     * @return this
     */
    public Search setAttr(final String name, final Object value) {
        return attr(name, value);
    }

    /**
     * set attr
     * @param name name
     * @param value value
     * @return this
     */
    public Search attr(final String name, final Object value) {
        attrs.put(name, value);
        return this;
    }

    // --
    // private methods

    /**
     * get table
     * @return table
     */
    String table() {
        return this.table;
    }

    List<Object> assemble(final boolean subSearch) {
        val result = new LinkedList<Object>();
        val arg = new ProcArg(table, result);

        for (val criterion : criteria) {
            arg.setCriterion(criterion);
            val type = criterion.getType();

            if (type == OR) {
                if (arg.isHasPrevCol()) {
                    arg.addSql(" or ");
                }
                else {
                    arg.setHasPrevOrCol(true);
                }
            }
            // and
            else {
                if (arg.isHasPrevOrCol()) {
                    arg.addSql(" or ");
                    arg.setHasPrevOrCol(false);
                }
                else if (arg.isHasPrevCol()) {
                    arg.addSql(" and ");
                }
            }

            PROCESSORS.get(type).accept(arg);

            if (!arg.isHasPrevCol()) {
                arg.setHasPrevCol(true);
            }
        }

        if (subSearch && criteria.size() > 1) {
            result.add(0, new SqlFragment("("));
            result.add(new SqlFragment(")"));
        }
        return result;
    }

    List<Object> arrayToList(final Object array) {
        val length = Array.getLength(array);
        val list = new ArrayList<>(length);
        for (int i = 0; i < length; ++i) {
            list.add(Array.get(array, i));
        }
        return list;
    }
}
