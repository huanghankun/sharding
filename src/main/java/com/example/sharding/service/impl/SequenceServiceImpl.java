package com.example.sharding.service.impl;

import com.example.sharding.common.SpringUtil;
import com.example.sharding.mapper.SequenceMapper;
import com.example.sharding.service.SequenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Set;
import java.util.TreeSet;

/**
 * 获取Sequence的服务类
 *
 * @author hhk
 */
@Service
public class SequenceServiceImpl implements SequenceService {

    /**
     * sequence列表
     */
    private Set<Long> sequenceSet = new TreeSet();
    /**
     * 每次向数据库申请sequence的大小
     */
    private static final int INCREMENT = 100;
    @Resource
    private SequenceMapper sequenceMapper;


    /**
     * @param clazz
     * @return
     */
    @Override
    public synchronized Long getNextSequenceByClass(Class clazz) {
        /**
         * 1,当地缓存是否有可用sequence
         */
        if (sequenceSet.isEmpty()) {
            SequenceServiceImpl sequenceService = SpringUtil.getBean(SequenceServiceImpl.class);
            sequenceService.getNextSequence(clazz.getName());
        }
        /**
         * 2,获取下一个Sequence
         */
        Long nextSequence = sequenceSet.iterator().next();
        /**
         * 3,移除已经使用Sequence
         */
        sequenceSet.remove(nextSequence);
        return nextSequence;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void getNextSequence(String sequenceByName) {
        /**
         * 获取下一个sequence
         */
        Long nextSequence = sequenceMapper.getCurrentSequenceByName(sequenceByName);
        /**
         * 2,如果不存在下一个sequence,就初始化一个sequence
         */
        if (nextSequence == null || nextSequence.equals(0)) {
            nextSequence = 1L;
            sequenceMapper.insertSequence(sequenceByName, 0L, 1);
        }
        /**
         * 3,申请一个段的sequence
         */
        sequenceMapper.updateSequence(sequenceByName, INCREMENT);
        /**
         * 4,把申请到的sequence投放到本地缓存
         */
        for (long i = nextSequence; i < nextSequence + INCREMENT; i++) {
            sequenceSet.add(i);
        }

    }
}