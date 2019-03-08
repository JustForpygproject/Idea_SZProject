package cn.itcast.core.controller;

import cn.itcast.core.common.ExcelUtil;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.ExcelService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import cn.itcast.core.pojo.user.User;

@RestController
@RequestMapping
public class ExcelController {

    @Reference
    private ExcelService excelService;


    @RequestMapping("exportWordData")
    public void exportExcelData(HttpServletRequest request, HttpServletResponse response) {

        // 定义表的标题
        String title = "订单导出数据";
        //查询所有用户
        List<Order> orderList = excelService.findOrderList();

        //定义表的列名
        String[] rowsName = new String[] { "order_id", "payment", "payment_type", "status", "create_time", "update_time"};
        //定义表的内容
        List<Object[]> dataList = new ArrayList<Object[]>();
        if (orderList!=null){
            for (Order order : orderList) {
                Long orderId = order.getOrderId();
                BigDecimal payment = order.getPayment();
                String paymentType = order.getPaymentType();
                Date createTime = order.getCreateTime();
                Date updateTime = order.getUpdateTime();
                String status = order.getStatus();
                Object[] objs = new Object[6];
                objs[0] = orderId;
                objs[1] = payment;
                objs[2] = paymentType;
                objs[3] = status;
                objs[4] = createTime;
                objs[5] = updateTime;

                dataList.add(objs);
            }
        }
        // 创建ExportExcel对象
        ExcelUtil excelUtil = new ExcelUtil();

        try {
            String fileName = new String("订单数据导出.xlsx".getBytes("UTF-8"), "iso-8859-1");    //生成word文件的文件名
            excelUtil.exportExcel(title, rowsName, dataList, fileName, response);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @RequestMapping("exportWordData1")
    public void exportExcelData1(HttpServletRequest request, HttpServletResponse response) {

        // 定义表的标题
        String title = "用户导出数据";
        //查询所有用户
        List<User> userList =excelService.findAll();

        //定义表的列名
        String[] rowsName = new String[] { "id", "username", "password", "phone", "created", "updated"};
        //定义表的内容
        List<Object[]> dataList = new ArrayList<Object[]>();
        if (userList!=null){
            for (User user : userList) {
                String password = user.getPassword();
                Long id = user.getId();
                String username = user.getUsername();
                Date created = user.getCreated();
                Date updated = user.getUpdated();
                String phone = user.getPhone();
                Object[] objs = new Object[6];
                objs[0] = id;
                objs[1] = username;
                objs[2] = password;
                objs[3] = phone;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String date = df.format(new Date());
                objs[4] = created;
                objs[5] = updated;

                dataList.add(objs);
            }
        }
        // 创建ExportExcel对象
        ExcelUtil excelUtil = new ExcelUtil();

        try {
            String fileName = new String("用户数据导出.xlsx".getBytes("UTF-8"), "iso-8859-1");    //生成word文件的文件名
            excelUtil.exportExcel(title, rowsName, dataList, fileName, response);

        } catch (Exception e) {
            e.printStackTrace();
        }

    } @RequestMapping("exportWordData2")
    public void exportExcelData2(HttpServletRequest request, HttpServletResponse response) {

        // 定义表的标题
        String title = "商品导出数据";
        //查询所有用户
        List<Goods> goodsList = excelService.findGoodsList();
        //定义表的列名
        String[] rowsName = new String[] { "id", "seller_id", "goods_name", "audit-status", "brand_id", "caption"};
        //定义表的内容
        List<Object[]> dataList = new ArrayList<Object[]>();
        if (goodsList!=null){
            for (Goods goods : goodsList) {
                Long id = goods.getId();
                String sellerId = goods.getSellerId();
                String auditStatus = goods.getAuditStatus();
                Long brandId = goods.getBrandId();
                String goodsName = goods.getGoodsName();
                String caption = goods.getCaption();
                Object[] objs = new Object[6];
                objs[0] = id;
                objs[1] = sellerId;
                objs[2] = auditStatus;
                objs[3] = brandId;
                objs[4] = goodsName;
                objs[5] = caption;

                dataList.add(objs);
            }
        }
        // 创建ExportExcel对象
        ExcelUtil excelUtil = new ExcelUtil();

        try {
            String fileName = new String("商品数据导出.xlsx".getBytes("UTF-8"), "iso-8859-1");    //生成word文件的文件名
            excelUtil.exportExcel(title, rowsName, dataList, fileName, response);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
