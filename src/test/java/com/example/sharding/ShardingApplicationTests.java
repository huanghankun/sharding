package com.example.sharding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShardingApplicationTests {

    @Test
    public void contextLoads() {
        String strs[] = {"BB", "AA", "DD", "CC", "aA", "Aa", "aa", "BBB"};
        List<String> list = Arrays.asList(strs);
        Arrays.sort(strs);
        for (String str : strs) {
            System.out.println(str);
        }
        System.out.println("###############");
        Comparator c = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return 6;
            }

        };
        Collections.sort(list, (String a, String b) -> -1);
        for (String str : list) {
            System.out.println(str);
        }
    }


    /**
     * 测试
     * 在Integer加载之后这个内部类的cache数组里边就初始化了-128到127之间的值
     */
    @Test
    public void test2() {
        for (int i = -200; i < 200; i++) {
            Integer a = i;
            Integer b = i;
            Integer c = new Integer(1);

            System.out.println(i + " : " + (a == b));
        }
    }

    /**
     * BigDecimal测试
     */
    @Test
    public void bigDecimalTest() {
        //直接使用double类型数据进行运算
        float a = 0.05f, b = 0.01f;
        double c = a + b;
        System.out.println(c);
        //使用BigDecimal的double参数的构造器
        BigDecimal bd1 = new BigDecimal(a);
        BigDecimal bd2 = new BigDecimal(b);
        System.out.println(bd1.add(bd2));
        //使用BigDecimal的String参数的构造器
        BigDecimal bd3 = new BigDecimal(Float.toString(a));
        BigDecimal bd4 = new BigDecimal(Float.toString(b));
        System.out.println(bd3.add(bd4));
    }


}
