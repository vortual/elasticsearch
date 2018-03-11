package com.itheima;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 瓦力.
 */
@Configuration
public class ElasticSearchConfig {
    @Bean
    public TransportClient esClient() throws UnknownHostException {

        InetSocketTransportAddress master = new InetSocketTransportAddress(
            InetAddress.getByName("localhost"), 9300
//          InetAddress.getByName("10.99.207.76"), 8999
        );
        
        Settings settings = Settings.builder().put("cluster.name","vortual-01").build();

        TransportClient client = new PreBuiltTransportClient(settings);
        
        client.addTransportAddress(master);

        return client;
    }
}
