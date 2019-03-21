package com.example.sharding.mapper;


import com.example.sharding.entity.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Created by Kane on 2018/1/17.
 */
public interface SequenceMapper {

    /**
     * 通过序列名称获取当前值
     * @param sequenceName
     * @return
     */
    Long getCurrentSequenceByName(@Param("sequenceName") String sequenceName);

    /**
     * 插入新序列
     * @param sequenceName
     * @param currentSequence
     * @param increment
     * @return
     */
    Long insertSequence(@Param("sequenceName") String sequenceName,
                           @Param("currentSequence") Long currentSequence,
                           @Param("increment") Integer increment);

    /**
     * 更新新序列
     * @param sequenceName
     * @return
     */
    Long updateSequence(@Param("sequenceName") String sequenceName,@Param("increment") Integer increment);
}
