package com.example.sharding.service.impl;

import com.example.sharding.common.SpringUtil;
import com.example.sharding.mapper.SequenceMapper;
import com.example.sharding.service.SequenceService;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by Kane on 2018/1/17.
 * @author hhk
 */
@Service
public class SequenceServiceImpl implements SequenceService {


    @Resource
    private SequenceMapper sequenceMapper;


    @Override
    public synchronized Long getNextSequenceByClass(Class clazz) {
        SequenceServiceImpl sequenceService = SpringUtil.getBean(SequenceServiceImpl.class);
        return sequenceService.getNextSequence(clazz.getName());
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public Long getNextSequence(String sequenceByName){
        Long nextSequence = sequenceMapper.getCurrentSequenceByName(sequenceByName);
        if(nextSequence==null||nextSequence.equals(0)){
            nextSequence = 1L;
            sequenceMapper.insertSequence(sequenceByName,0L,1);
        }
        sequenceMapper.updateSequence(sequenceByName);
        return nextSequence;
    }
}