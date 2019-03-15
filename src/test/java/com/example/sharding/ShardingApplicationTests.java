package com.example.sharding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShardingApplicationTests {

    @Test
    public void contextLoads() {
        String strs[]={"BB","AA","DD","CC","aA","Aa","aa","BBB"};
        List<String> list = Arrays.asList(strs);
        Arrays.sort(strs);
        for( String str:strs){
            System.out.println(str);
        }
        System.out.println("###############");
        Collections.sort(list, (String a, String b) -> -1);
        for( String str:list){
            System.out.println(str);
        }
    }

}
