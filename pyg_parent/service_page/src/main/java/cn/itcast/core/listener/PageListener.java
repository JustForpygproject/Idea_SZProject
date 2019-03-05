package cn.itcast.core.listener;

import cn.itcast.core.service.CmsService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Map;

/**
 * 自定义监听器
 * 监听来自于消息服务器发送来的消息, 接收到商品id后, 根据商品id到数据库查询商品详细数据
 * 然后根据模板, 生成静态化页面, 供portal系统访问商品详情页面使用
 */
public class PageListener implements MessageListener {

    @Autowired
    private CmsService cmsService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage)message;

        try {
            //1. 接收消息
            String goodsId = activeMQTextMessage.getText();
            //2. 根据商品id查询商品详细数据
            Map<String, Object> rootMap = cmsService.findGoods(Long.parseLong(goodsId));
            //3. 生成静态化页面
            cmsService.createStaticPage(Long.parseLong(goodsId), rootMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
