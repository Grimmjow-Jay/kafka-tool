package com.grimmjow.kafkaTool.test;

import com.google.common.collect.Lists;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Properties;

/**
 * @author Grimm
 * @since 2020/5/26
 */
public class ConsumerTest {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "192.168.154.100:9092");
        props.put(CommonClientConfigs.GROUP_ID_CONFIG, "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "100000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//        consumer.subscribe(Lists.newArrayList("Jay_test"));
        consumer.assign(Lists.newArrayList(new TopicPartition("Jay_test", 0)));
        while (true) {
            ConsumerRecords<String, String> records;
            try {
                records = consumer.poll(Duration.ofMillis(100));
            } catch (Exception e) {
                break;
            }
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            }
        }
        consumer.close();
    }
}
