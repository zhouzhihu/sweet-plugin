package com.egrand.sweetplugin.common.utils;

/**
 * 顺序优先级
 */
public class OrderPriority {
    /**
     * 高优先级
     */
    private static final Integer HIGH_PRIORITY = 1000;

    /**
     * 中优先级
     */
    private static final Integer MIDDLE_PRIORITY = 0;

    /**
     * 低优先级
     */
    private static final Integer LOW_PRIORITY = -1000;


    private Integer priority;


    private OrderPriority(Integer priority){
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }

    /**
     * 得到低优先级
     * @return OrderPriority
     */
    public static OrderPriority getLowPriority(){
        return new OrderPriority(LOW_PRIORITY);
    }


    /**
     * 得到中优先级
     * @return OrderPriority
     */
    public static OrderPriority getMiddlePriority(){
        return new OrderPriority(MIDDLE_PRIORITY);
    }


    /**
     * 得到高优先级
     * @return OrderPriority
     */
    public static OrderPriority getHighPriority(){
        return new OrderPriority(HIGH_PRIORITY);
    }


    /**
     * 升优先级.最高只能升到最高优先级别。也就是1000
     * @param number 升级数量
     * @return OrderPriority
     */
    public OrderPriority up(Integer number){
        if(number == null){
            return this;
        }
        if(HIGH_PRIORITY.equals(this.priority) || (this.priority + number) > HIGH_PRIORITY){
            this.priority = HIGH_PRIORITY;
            return this;
        } else {
            this.priority = this.priority + number;
            return this;
        }
    }

    /**
     * 降优先级.最低只能降到最低优先级别。也就是-1000
     * @param number 降级数量
     * @return OrderPriority
     */
    public OrderPriority down(Integer number){
        if(number == null){
            return this;
        }
        if(MIDDLE_PRIORITY.equals(this.priority) || (this.priority - number) < MIDDLE_PRIORITY){
            this.priority = MIDDLE_PRIORITY;
            return this;
        } else {
            this.priority = this.priority - number;
            return this;
        }
    }



}
