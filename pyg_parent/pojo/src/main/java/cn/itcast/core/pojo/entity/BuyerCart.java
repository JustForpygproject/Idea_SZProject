package cn.itcast.core.pojo.entity;

import cn.itcast.core.pojo.order.OrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * 自定义封装类, 购物车对象
 */
public class BuyerCart implements Serializable{

    //购物项集合, 购物明细集合
    private List<OrderItem> orderItemList;
    //卖家名称
    private String sellerName;
    //卖家id
    private String sellerId;


    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}
