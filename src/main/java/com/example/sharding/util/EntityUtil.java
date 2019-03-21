package com.example.sharding.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 对象属性的转化
 *
 * @author huanghankun
 */
public class EntityUtil {
    private EntityUtil() {
    }

    /**
     * 将对象属性转化成键值对的list
     *
     * @param <T>
     * @param <T>
     * @param <T>
     * @param
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws Exception
     */
    public static <T> List<T> list2Entity(List<Map<String, Object>> list,
                                          Class<T> clazz) throws Exception {

        List<T> entityList = new ArrayList<T>();
        for (Map<String, Object> map : list) {
            T entity = map2Entity(map, clazz);
            entityList.add(entity);
        }
        return entityList;
    }

    /**
     * 键值对的map--->将对象属性
     *
     * @param map
     * @param clazz
     * @return
     */
    public static <T> T map2Entity(Map<String, Object> map, Class<T> clazz) {

        T entity = null;
        try {
            entity = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e1) {
            e1.printStackTrace();
            return entity;
        }
        for (String key : map.keySet()) {
            Object value = map.get(key);
            Field field = null;
            try {
                field = clazz.getDeclaredField(key);
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
                continue;
            }
            field.setAccessible(true);// 暴力访问，取消age的私有权限。让对象可以访问
            try {
                field.set(entity, value);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
        }
        return entity;
    }

    /**
     * 类属性名称映射成数据库字段名
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T> Map<String, String> entityAttr2TableColumnMap(
            Class<T> clazz) {
        Field[] fields = clazz.getFields();
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            map.put(field.getName(), entityAttrName2ColumnName(field.getName()));
        }
        return map;
    }


    /**
     * 数据表字段名转化为对象属性名
     * 下划线转换驼峰
     *
     * @param columnName
     * @return
     */
    public static String columnName2EntityAttrName(String columnName) {
        if (columnName == null || columnName.trim().equals("")) {
            return "";
        }
        byte[] bs = columnName.toLowerCase().getBytes();
        boolean isDown = false;

        StringBuilder attrName = new StringBuilder();
        for (int i = 0; i < bs.length; i++) {
            byte b = bs[i];
            if (b == 95) {
                isDown = true;
            } else {
                if (isDown) {
                    b -= 32;
                    isDown = false;
                }
                attrName.append((char) b);
            }
        }
        return attrName.toString();
    }

    /**
     * 将对象属性转化成键值对的map
     *
     * @param <T>
     * @param
     * @return
     * @throws Exception
     */
    public static <T> Map<String, Object> entity2Map(Object entity) {
        try {
            if (entity instanceof Map) {
                return (Map<String, Object>) entity;
            }
            Map<String, Object> map = getProperty(entity);
            LinkedHashMap<String, Object> mapNew = new LinkedHashMap<String, Object>();
            for (String key : map.keySet()) {
                Object value = map.get(key);
                if (value == null) {
                    continue;
                }
                if (iSBaseType(value)) {
                    mapNew.put(key, value);
                } else if (value instanceof Collection) {
                    Collection<?> c = (Collection<?>) value;
                    if (c.isEmpty()) {
                        continue;
                    }
                    List<Object> a = new ArrayList<Object>();
                    Iterator<?> iterator = c.iterator();
                    while (iterator.hasNext()) {
                        Object o = iterator.next();
                        if (iSBaseType(o)) {
                            mapNew.put(key, value);
                            a.add(o);
                        } else {
                            a.add(entity2Map(o));
                        }
                    }
                    mapNew.put(key, a);
                } else if (value instanceof Collection || value instanceof Map) {
                    Map<String, Object> m = (Map<String, Object>) value;
                    Map<String, Object> m1 = new HashMap<String, Object>();
                    for (String k : m.keySet()) {
                        Object o = m.get(k);
                        if (iSBaseType(o)) {
                            m1.put(k, o);
                        } else {
                            m1.put(k, entity2Map(o));
                        }
                    }
                    mapNew.put(key, m1);
                } else {
                    mapNew.put(key, entity2Map(value));
                }
            }
            return mapNew;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LinkedHashMap<String, Object>();
    }

    /**
     * 判断是否基础类型
     *
     * @param param
     * @return
     */
    private static boolean iSBaseType(Object param) {
        if (param instanceof Integer || param instanceof String
                || param instanceof Double || param instanceof Float
                || param instanceof Integer || param instanceof Boolean
                || param instanceof Character || param instanceof Byte
                || param instanceof Short || param instanceof Date) {
            return true;
        }
        if (param instanceof Integer[] || param instanceof String[]
                || param instanceof Double[] || param instanceof Float[]
                || param instanceof Integer[] || param instanceof Boolean[]
                || param instanceof Character[] || param instanceof Byte[]
                || param instanceof Short[] || param instanceof Date[]) {
            return true;
        }
        return false;
    }

    /**
     * 对象属性名==>数据表字段名
     *
     * @param
     * @return
     */
    private static String entityAttrName2ColumnName(String key) {
        byte[] c = key.getBytes();
        key = "";
        for (int i = 0; i < c.length; i++) {
            byte b = c[i];
            if (b <= 90 && b >= 65) {
                key += "_" + (char) (b + 32);
            } else {
                key += (char) b;
            }
        }
        return key.toUpperCase();
    }


    /**
     * 获得一个对象各个属性的字节流
     */
    public static Map<String, Object> getProperty(Object entityName) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        Class<?> clszz = entityName.getClass();
        List<Field> list = getFieldList(clszz);
        for (Field f : list) {
            Object value = invokeMethod(entityName, f.getName(), null);
            map.put(f.getName(), value);
        }
        return map;
    }

    /**
     * 获取所有参数（包括父类）
     *
     * @param clszz
     * @return
     */
    public static List<Field> getFieldList(Class<?> clszz) {
        List<Field> fieldList = new ArrayList<Field>();
        Field fields[] = clszz.getDeclaredFields();
        for (Field f : fields) {
            fieldList.add(f);
        }
        if (clszz.getSuperclass() != null) {
            List<Field> list = getFieldList(clszz.getSuperclass());
            fieldList.addAll(list);
        }
        return fieldList;
    }


    /**
     * 获得对象属性的值
     */
    public static Object invokeMethod(Object owner, String methodName,
                                      Object[] args) throws Exception {
        Class<?> ownerClass = owner.getClass();
        methodName = methodName.substring(0, 1).toUpperCase()
                + methodName.substring(1);
        Method method = null;
        try {
            method = ownerClass.getMethod("get" + methodName);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
            return null;
        }
        return method.invoke(owner);
    }

    /**
     *
     * 遍历对象属性值(利用反射实现)，可以在需要对 对象中的每个字段都执行相同的处理时使用
     *
     */
    public static List<String> checkFieldValueNull(Object bean) {
        List<String> varList = new ArrayList<String>();
        if (bean == null) {
            return varList;
        }
        Class<?> cls = bean.getClass();
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            try {
                String fieldGetName = parGetName(field.getName());
                if (!checkGetMet(methods, fieldGetName)) {
                    continue;
                }
                Method fieldGetMet = cls.getMethod(fieldGetName, new Class[] {});
                Object fieldVal = fieldGetMet.invoke(bean, new Object[] {});
                if (fieldVal != null && !"".equals(fieldVal)) {// StringUtils.isEmpty(fieldVal)
                    varList.add(String.valueOf(fieldVal));
                }
            } catch (Exception e) {
                continue;
            }
        }
        return varList;
    }

    /**
     * 拼接某属性的 get方法
     *
     * @param fieldName
     * @return String
     */
    public static String parGetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "get" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                        + fieldName.substring(startIndex + 1);
    }

    /**
     * 判断是否存在某属性的 get方法
     *
     * @param methods
     * @param fieldGetMet
     * @return boolean
     */
    public static boolean checkGetMet(Method[] methods, String fieldGetMet) {
        for (Method met : methods) {
            if (fieldGetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

}
