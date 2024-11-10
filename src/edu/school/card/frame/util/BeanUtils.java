package edu.school.card.frame.util;

import edu.school.card.anon.ChangeValue;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * 创建bean工具类
 * @date 2020/11/24
 */
public class BeanUtils<T> {

    public Map<Long,T> getBeanMap(String pathName, Class<T> clazz){
        String pathOut = getPathOut(pathName);
        HashMap<Long, T> map = new HashMap<>();
        BufferedReader br = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
            fis = new FileInputStream(pathOut);
            isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null){
                createBean(line, clazz, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();
                isr.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static String getPathOut(String pathName) {
        String path = BeanUtils.class.getClassLoader().getResource("").getPath().substring(1);
        String[] paths = path.split("/");
        String pathOut = "";
        for (int i = 0; i < paths.length; i++) {
            int count = paths.length - 4;
            if (i < count){
                pathOut += paths[i]+"/";
            }
        }
        pathOut = pathOut.substring(0, pathOut.length() - 1)+"/CampusCardManagementSystem/src/edu/school/card/conf/"+pathName;
        return pathOut;
    }

    public Vector<T> getBeanVector(String path, Class<T> clazz){
        Vector<T> vector = new Vector<>();
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null){
                T bean = createBean(line, clazz);
                vector.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vector;
    }

    private T createBean(String line, Class<T> clazz) {
        String[] lines = line.split(",");
        Field[] fields = clazz.getDeclaredFields();
        T obj = null;
        try {
            obj = clazz.newInstance();
            for (Field field : fields) {
                ChangeValue anon = field.getAnnotation(ChangeValue.class);
                if (anon == null){
                    continue;
                }
                int index = anon.index();
                String val = lines[index];
                field.setAccessible(true);
                String pattern = anon.pattern();
                if ("".equals(pattern)){
                    changeType(field,obj,val);
                }else {
                    String[] ps = pattern.split(",");
                    for (String p : ps) {
                        String[] pv = p.split("=");
                        if (val.equals(pv[0])){
                            val = pv[1];
                            changeType(field,obj,val);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private void createBean(String line, Class<T> clazz, HashMap<Long, T> map) {
        String[] lines = line.split(",");
        Field[] fields = clazz.getDeclaredFields();
        T obj = null;
        Long key = 0L;
        try {
            obj = clazz.newInstance();
            for (Field field : fields) {
                ChangeValue anon = field.getAnnotation(ChangeValue.class);
                if (anon == null){
                    continue;
                }
                int index = anon.index();
                String val = lines[index];
                if (index == 0){
                    key = Long.parseLong(val);
                }
                field.setAccessible(true);
                String pattern = anon.pattern();
                if ("".equals(pattern)){
                    changeType(field,obj,val);
                }else {
                    String[] ps = pattern.split(",");
                    for (String p : ps) {
                        String[] pv = p.split("=");
                        if (val.equals(pv[0])){
                            val = pv[1];
                            changeType(field,obj,val);
                        }
                    }
                }
            }
            map.put(key,obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeType(Field field, T obj, String val) throws IllegalAccessException {
        Class<?> type = field.getType();
        String typeName = type.getName();
        if (typeName.contains("String")){
            field.set(obj,val);
        }else if (typeName.contains("Long")){
            field.set(obj,Long.parseLong(val));
        }else if (typeName.contains("BigDecimal")){
            field.set(obj,new BigDecimal(val));
        }
    }
}
