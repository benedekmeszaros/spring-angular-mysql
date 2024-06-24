package com.pmf.awp.project.util;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class BeanCopyUtils {
    public static void copyNonNullProperties(Object source, Object destination, String... ignoreProperties) {
        Set<String> ignorePropertiesSet = getNullPropertyNames(source);
        for (String propertyName : ignoreProperties) {
            ignorePropertiesSet.add(propertyName);
        }
        BeanUtils.copyProperties(source, destination,
                ignorePropertiesSet.toArray(new String[ignorePropertiesSet.size()]));
    }

    public static Set<String> getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                emptyNames.add(pd.getName());
        }
        return emptyNames;
    }
}
