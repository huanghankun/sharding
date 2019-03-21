package com.example.sharding.service.impl;

import com.example.sharding.common.SpringUtil;
import com.example.sharding.mapper.SequenceMapper;
import com.example.sharding.service.SequenceService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;

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
//    @Resource
//    private SequenceServiceImpl sequenceService;


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
        for (Long i = nextSequence; i < nextSequence + INCREMENT; i++) {
            sequenceSet.add(i);
        }

    }


    /**
     * Desc :  @Async所修饰的函数不要定义为static类型，否则异步调用不会生效
     *  这里通过返回Future<T>来返回异步调用的结果，实现异步回调
     * User : RICK
     * Time : 2017/8/29 10:30
     */

    /**
     *
     * 任务一
     *
     *
     * @return
     * @throws Exception
     */
    @Async
    @Override
    public Future<String> doTaskOne() throws Exception{
        System.out.println("开始做任务一");
        long start = System.currentTimeMillis();
        Thread.sleep(new Random().nextInt(10000));
        long end = System.currentTimeMillis();
        System.out.println("完成任务一，耗时：" + (end - start) + "毫秒");
        return new AsyncResult<>("test1 is done!");
    }

    /**
     * 任务二;
     *
     * @return
     * @throws Exception
     */
    @Async
    @Override
    public Future<String> doTaskTwo() throws Exception {
        System.out.println("开始做任务二");
        long start = System.currentTimeMillis();
        Thread.sleep(new Random().nextInt(10000));
        long end = System.currentTimeMillis();
        System.out.println("完成任务二，耗时：" + (end - start) + "毫秒");
        return new AsyncResult<>("test2 is done!");
    }

    /**
     * 任务3;
     *
     * @return
     * @throws Exception
     */
    @Async
    @Override
    public Future<String> doTaskThree() throws Exception {
        System.out.println("开始做任务三");
        long start = System.currentTimeMillis();
        Thread.sleep(new Random().nextInt(10000));
        long end = System.currentTimeMillis();
        System.out.println("完成任务三，耗时：" + (end - start) + "毫秒");
        return new AsyncResult<>("test3 is done!");
    }
}