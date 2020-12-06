package me.chyxion.tigon.mybatis.util;

import lombok.val;
import java.util.*;
import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Modifier;
import me.chyxion.tigon.mybatis.*;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author Donghuang
 * @date Aug 31, 2020 13:21:19
 */
@Slf4j
public final class EntityUtils {

    public static final String ID = "id";

    /**
     * get primary key name
     * called from tigon-mybatis.xml
     *
     * @param clazz entity class
     * @return primary key name
     */
    public static String primaryKeyName(final Class<?> clazz) {

        if (AnnotationUtils.findAnnotation(clazz, NoPrimaryKey.class) != null) {
            return "!!!NO_PRIMARY_KEY!!!";
        }

        val fields = new ArrayList<Field>(4);

        ReflectionUtils.doWithFields(clazz, fields::add,
            field -> isNormal(field) &&
                    (field.isAnnotationPresent(PrimaryKey.class) ||
                        ID.equalsIgnoreCase(field.getName())));

        AssertUtils.state(!fields.isEmpty(),
            () -> "No primary key found of entity class [" + clazz + "]");
        if (fields.size() == 1) {
            return fields.iterator().next().getName();
        }

        val annoPk = fields.stream()
                .filter(field -> field.isAnnotationPresent(PrimaryKey.class))
                .collect(Collectors.toList());

        AssertUtils.state(annoPk.size() < 2,
            () -> "Multiple @PrimaryKey found in entity class [" + clazz + "]");

        if (annoPk.size() == 1) {
            return annoPk.iterator().next().getName();
        }

        throw new IllegalStateException(
            "Could no decide primary key of entity class [" + clazz + "]");
    }

    /**
     * get primary key value
     * called in tigon-mybatis.xml
     *
     * @param entity entity object
     * @return primary key value
     */
    public static Object primaryKeyValue(final Object entity) {
        val entityClass = entity.getClass();

        if (AnnotationUtils.findAnnotation(entityClass, NoPrimaryKey.class) != null) {
            return "!!!NO_PRIMARY_KEY!!!";
        }

        val field = ReflectionUtils.findField(
                entityClass, primaryKeyName(entityClass));
        field.setAccessible(true);
        return ReflectionUtils.getField(field, entity);
    }

    /**
     * return entity object insert map
     * called in tigon-mybatis.xml
     *
     * @return insert map
     */
    public static Map<String, SqlParam> insertMap(final Object entity) {
        return toMap(entity, false);
    }

    /**
     * return update map
     * called in tigon-mybatis.xml
     *
     * @return update map
     */
    public static Map<String, SqlParam> updateMap(final Object entity) {
        return toMap(entity, true);
    }

    /**
     * to col map
     * called in tigon-mybatis.xml
     *
     * @param map map
     * @return col map
     */
    public static <T> Map<String, T> toColMap(final Map<String, T> map) {
        val mapRtn = new LinkedHashMap<String, T>(map.size());
        for (val kv : map.entrySet()) {
            mapRtn.put(StrUtils.camelToUnderscore(kv.getKey()), kv.getValue());
        }
        return mapRtn;
    }

    /**
     * return entity cols
     * @param clazz entity class
     * @return entity cols
     */
    public static List<String> cols(final Class<?> clazz) {
        val cols = new ArrayList<String>(16);
        ReflectionUtils.doWithFields(clazz,
            field -> cols.add(StrUtils.camelToUnderscore(field.getName())),
            EntityUtils::isNormal);
        return cols;
    }

    static Map<String, SqlParam> toMap(final Object entity, final boolean forUpdate) {
        val entityClass = entity.getClass();
        val mapRtn = new LinkedHashMap<String, SqlParam>();
        ReflectionUtils.doWithFields(entityClass, field -> {
            field.setAccessible(true);

            boolean rawValue = false;
            Object value = null;
            val annoRawValue =
                field.getAnnotation(RawValue.class);

            if (annoRawValue != null) {
                rawValue = true;
                val annoValue = annoRawValue.value();
                if (StrUtils.isNotBlank(annoValue)) {
                    value = annoValue;
                }
            }

            mapRtn.put(StrUtils.camelToUnderscore(field.getName()),
                new SqlParam(rawValue,
                    value != null ?
                    value : ReflectionUtils.getField(field, entity)));
        }, fieldFilter(entity, forUpdate));

        return mapRtn;
    }

    static ReflectionUtils.FieldFilter fieldFilter(
            final Object entity,
            final boolean forUpdate) {

        return field -> {

            if (!isNormal(field)) {
                return false;
            }

            if (forUpdate) {

                // do not update id
                if (ID.equalsIgnoreCase(field.getName())) {
                    return false;
                }

                // do not update field marks @PrimaryKey
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    return false;
                }

                // do not update field marks @NotUpdate
                if (field.isAnnotationPresent(NotUpdate.class)) {
                    return false;
                }

                if (field.isAnnotationPresent(NotUpdateWhenNull.class)) {
                    field.setAccessible(true);
                    if (ReflectionUtils.getField(field, entity) == null) {
                        return false;
                    }
                }
            }

            return true;
        };
    }

    static boolean isNormal(final Field field) {
        val modifiers = field.getModifiers();
        return !Modifier.isTransient(modifiers) &&
                !field.isAnnotationPresent(Transient.class) &&
                !Modifier.isFinal(modifiers) &&
                !Modifier.isPublic(modifiers) &&
                !Modifier.isStatic(modifiers);
    }
}
