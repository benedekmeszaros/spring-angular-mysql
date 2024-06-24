package com.pmf.awp.project.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExporterUtils {
    public static Map<String, Object> extract(Object object) {
        if (Objects.isNull(object))
            throw new IllegalArgumentException("The provided argumet is null");

        Map<String, Object> map = new HashMap<>();
        Class<?> objClass = object.getClass();

        if (!objClass.isAnnotationPresent(Exporter.class))
            throw new IllegalArgumentException("The object must annotate 'Exporter' interface");

        for (Field field : getAllFields(objClass)) {
            if (!field.isAnnotationPresent(Exclude.class)) {
                field.setAccessible(true);

                try {
                    Object value = field.get(object);
                    if (value != null) {
                        if (value instanceof List<?>) {
                            @SuppressWarnings("unchecked")
                            List<Object> list = (List<Object>) value;
                            List<Object> other = new ArrayList<>();
                            for (Object item : list) {
                                other.add(extract(item));
                            }
                            map.put(field.getName(), other);
                        } else {
                            if (field.isAnnotationPresent(Deep.class)) {
                                value = extract(value);
                                map.put(field.getName(), value);
                            } else if (field.isAnnotationPresent(Brief.class)) {

                                Brief brief = field.getAnnotation(Brief.class);
                                String name = brief.name().equals("") ? field.getName() : brief.name();
                                Field alternative = value.getClass().getDeclaredField(brief.mappedBy());
                                alternative.setAccessible(true);
                                map.put(name, alternative.get(value));
                            } else
                                map.put(field.getName(), value);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (objClass.getAnnotation(Exporter.class).expandandable()) {
            for (Method method : objClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Expand.class)) {
                    Expand expand = method.getAnnotation(Expand.class);
                    try {
                        Object value = method.invoke(object);
                        if (value.getClass().isAnnotationPresent(Exporter.class))
                            value = extract(value);
                        map.put(expand.name().equals("") ? method.getName() : expand.name(), value);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return map;
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            if (clazz.isAnnotationPresent(Exporter.class) || clazz == clazz.getEnclosingClass()) {
                for (Field field : clazz.getDeclaredFields()) {
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}
