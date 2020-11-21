package me.chyxion.tigon.mybatis.util;

import java.util.function.Supplier;

/**
 * @author Donghuang
 * @date Sep 01, 2020 17:13:04
 */
public class AssertUtils {

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void state(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalStateException(
                messageSupplier != null ? messageSupplier.get() : null);
        }
    }
}
