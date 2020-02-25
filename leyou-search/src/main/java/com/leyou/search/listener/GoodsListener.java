package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoodsListener {

    @Autowired
    private SearchService searchService;
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "leyou.item.search.queue",durable = "true"),
                    exchange = @Exchange(
                            value = "LEYOU.ITEM.EXCHANGE",
                            ignoreDeclarationExceptions = "ture",
                            durable = "true",
                            type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}))
    public void receiveMQ(Long id) throws IOException {
        if(id==null){
            return;
        }
        this.searchService.creatIndex(id);

    }
}
