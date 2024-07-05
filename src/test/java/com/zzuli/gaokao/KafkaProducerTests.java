package com.zzuli.gaokao;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;

@SpringBootTest
public class KafkaProducerTests {


    @Test
    public void producer(){

        HashMap<String, Object> map = new HashMap<>(); // 为生产者配置，并设置序列化
        map.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        KafkaProducer<String,String> producer = new KafkaProducer<>(map);

        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<String, String>("hao","李征明"+i);
            producer.send(record);
        }
        producer.close();
    }



    @Test
    public void consumer(){
        HashMap<String, Object> config = new HashMap<>(); // 为生产者配置，并设置反序列化
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        config.put(ConsumerConfig.GROUP_ID_CONFIG,"hao");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config);
        ArrayList<String> topics = new ArrayList<>();
        topics.add("hao");
        consumer.subscribe(topics);

        for (ConsumerRecord<String, String> record : consumer.poll(1000)) {
            System.out.println(record);
        }
        consumer.close();
    }


}
