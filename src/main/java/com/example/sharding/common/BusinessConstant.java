package com.example.sharding.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> Description: 公共业务常量类BusinessConstant <p>
 *
 * @author hhk
 * @date 2018年2月25日
 */
public class BusinessConstant {


    /**
     * 状态
     *
     * @author hhk
     */
    public static class OrderStatus {
        /**
         * 保存
         */
        public static final Integer SAVED = 1;

        /**
         * 作废
         */
        public static final Integer CANCEED = 2;

        /**
         * 提交审批
         */
        public static final Integer APPROVING = 3;

        /**
         * 审批通过
         */
        public static final Integer APPROVED = 4;

        /**
         * 审批拒绝
         */
        public static final Integer APPROVE_REFUSED = 5;

        /**
         * 待受理
         */
        public static final Integer UNACCEPTED = 6;

        /**
         * 已受理
         */
        public static final Integer ACCEPTED = 7;

        /**
         * 受理拒绝
         */
        public static final Integer ACCEPT_REFUSE = 8;

        /**
         * 办理成功
         */
        public static final Integer HANDED = 9;

        /**
         * 办理退回
         */
        public static final Integer HANDE_REFUSE = 10;

        /**
         * 已锁定
         */
        public static final Integer OCKED = 11;

        /**
         * 网银退回
         */
        public static final Integer GCEB_REFUSE = 12;

        /**
         * 待处理
         */
        public static final Integer UNHANDE = 13;

        /**
         * 退款申请单驳回
         **/
        public static final Integer REFUND_REJECT = 14;

        /**
         * 退款申请撤销修改
         **/
        public static final Integer REFUND_CANCE = 15;

        /**
         * 已执行
         */
        public static final Integer EXECUTION = 16;

        /**
         * 待确认
         */
        public static final Integer UNCONFIRM = 17;

        /**
         * 已确认
         */
        public static final Integer CONFIRMED = 18;

        /**
         * 确认拒绝
         */
        public static final Integer CONFIRMED_REFUSE = 19;

        /**
         * 询价中
         */
        public static final Integer ENQUIRING = 20;

        /**
         * 询价失败
         */
        public static final Integer INQUIRY_FAIURE = 21;

        /**
         * 外围撤销
         */
        public static final Integer STOCKIN_REVOCATION = 22;

        /**
         * 付款成功
         */
        public static final Integer PAYMENT_SUCCESS = 23;

        /**
         * 付款失败
         */
        public static final Integer PAYMENT_FAI = 24;

        /**
         * 支付成功(待核销)
         */
        public static final Integer WRITE_OFF = 25;

        /**
         * 已注销
         */
        public static final Integer CANCE = 26;

        /**
         * 生成付款单
         */
        public static final Integer GENERATING_PAYMENT = 27;

        /**
         * 自动确认
         */
        public static final Integer AUTO_CONFIRMED = 28;

        /**
         * 状态流转
         */
        public static Map<Integer, List> getTransitionMap() {
            Map<Integer, List> transitionMap = new HashMap<>();
            //保存->保存,审批决绝->保存
            transitionMap.put(SAVED, Arrays.asList(SAVED,APPROVE_REFUSED));
            //保存->提交审批
            transitionMap.put(APPROVING, Arrays.asList(SAVED));
            //提交审批->审批通过
            transitionMap.put(APPROVED, Arrays.asList(APPROVING));
            //提交审批->审批拒绝
            transitionMap.put(APPROVE_REFUSED, Arrays.asList(APPROVING));
            //提交拒绝->作废，保存->作废
            transitionMap.put(CANCEED, Arrays.asList(SAVED,APPROVE_REFUSED));
            return transitionMap;
        }
    }


}
