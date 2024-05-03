package com.example.compose;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class RelectionUtil {

    public static void changeFinalValue(Object object, Field field, Object newValue, boolean isSafeThread) {

        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            // 如果field为private,则需要使用该方法使其可被访问
            Field modifersField = null;
            try {
                modifersField = Field.class.getDeclaredField("modifiers");
            } catch (Throwable e) {
                e.printStackTrace();
            }
            try {
                modifersField = Field.class.getDeclaredField("accessFlags");
            } catch (Throwable e) {
                e.printStackTrace();
            }
            modifersField.setAccessible(true);

            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers)) {
                // 把指定的field中的final修饰符去掉
                modifersField.setInt(field, modifiers & ~Modifier.FINAL);
            }

            field.set(object, newValue); // 为指定field设置新值
            if (isSafeThread) {  //如果要考虑线程安全，建议还原
                if (Modifier.isFinal(modifiers)) {
                    modifersField.setInt(field, modifiers | Modifier.FINAL);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
