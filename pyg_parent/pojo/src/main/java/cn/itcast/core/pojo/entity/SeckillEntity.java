package cn.itcast.core.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/*自定义前台封装的秒杀商品类*/
public class SeckillEntity implements Serializable {
    //库存id
    private Long itemId;
    //秒杀价格
    private BigDecimal costPrice;
    //开始时间
    private Date startTime;
    //结束时间
    private Date endTime;
    //秒杀商品数
    private Integer num;
    //剩余库存数
    private Integer stockCount;
    //描述
    private String introduction;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
