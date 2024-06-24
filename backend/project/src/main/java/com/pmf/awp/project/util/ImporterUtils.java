package com.pmf.awp.project.util;

import java.lang.reflect.Field;
import java.util.Map;

public class ImporterUtils {
    public static <T> void copy(Map<String, Object> source, T target) {
        Class<?> targetClass = target.getClass();

        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            try {
                Field field = targetClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, fieldValue);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }
}
