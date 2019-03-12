package com.w3engineers.unicef.telemesh.util;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-01-30 at 4:05 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: telemesh.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-01-30 at 4:05 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-01-30 at 4:05 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
//Outside telemesh package any class was not available inside test class that's why putted inside
//telemesh package which skew a bit then our typical package convention

/**
 * Convenient util class to instantiate any object's all the field with random value
 */
public class RandomGenerator {

    private Random random = new Random();

    public <T> T createAndFill(Class<T> clazz) throws Exception {
        T instance = clazz.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            //https://javapapers.com/core-java/java-synthetic-class-method-field/
            if (field != null && !field.isSynthetic() && !field.getType().getName().startsWith("android.os.Parcelable")) {
                field.setAccessible(true);
                Object value = getRandomValueForField(field);
                field.set(instance, value);
            }
        }
        return instance;
    }

    private Object getRandomValueForField(Field field) throws Exception {

        if (field == null)
            return null;

        Class<?> type = field.getType();

        // Note that we must handle the different types here! This is just an
        // example, so this list is not complete! Adapt this to your needs!
        if (type.isEnum()) {
            Object[] enumValues = type.getEnumConstants();
            return enumValues[random.nextInt(enumValues.length)];
        } else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            return random.nextInt();
        } else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
            return random.nextLong();
        } else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
            return random.nextDouble();
        } else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
            return random.nextFloat();
        } else if (type.equals(String.class)) {
            return UUID.randomUUID().toString();
        } else if (type.equals(BigInteger.class)) {
            return BigInteger.valueOf(random.nextInt());
        } else if (type.equals(Boolean.class) || type.getName().equals("boolean")) {
            return random.nextInt() % 2 == 0;
        }
        return createAndFill(type);
    }


}
