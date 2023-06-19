package com.egrand.sweetplugin.common.utils;


import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * 通用工具
 */
public class OrderUtils {

    private OrderUtils(){}

    /**
     * list按照int排序. 数字越大, 越排在前面
     * @param list list集合
     * @param orderImpl 排序实现
     * @param <T> T
     * @return List
     */
    public static <T> List<T> order(List<T> list, Function<T, Integer> orderImpl){
        if(list == null){
            return null;
        }
        list.sort(Comparator.comparing(orderImpl, Comparator.nullsLast(Comparator.reverseOrder())));
        return list;
    }


    /**
     * 对 OrderPriority 进行排序操作
     * @param order OrderPriority
     * @param <T> 当前操作要被排序的bean
     * @return Comparator
     */
    public static <T> Comparator<T> orderPriority(final Function<T, OrderPriority> order){
        return Comparator.comparing(t -> {
            OrderPriority orderPriority = order.apply(t);
            if(orderPriority == null){
                orderPriority = OrderPriority.getLowPriority();
            }
            return orderPriority.getPriority();
        }, Comparator.nullsLast(Comparator.reverseOrder()));
    }


}
