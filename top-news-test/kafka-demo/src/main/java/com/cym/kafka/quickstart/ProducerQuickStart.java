package com.cym.kafka.quickstart;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ProducerQuickStart {

    public static void main(String[] args) {
        // 设置kafka相关设置
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "101.126.76.60:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // 创建生产者
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        // 发送消息
        ProducerRecord<String, String> record = new ProducerRecord<String, String>("topic-first", "key-01", "hello kafka1!");
        producer.send(record);
        producer.close();

    }

}
