package com.grimmjow.kafkatool.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Grimm
 * @date 2020/9/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KafkaData<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Topic
     */
    private String topic;

    /**
     * 分区
     */
    private int partition;

    /**
     * offset
     */
    private long offset;

    /**
     * Key
     */
    private K key;

    /**
     * 数据内容
     */
    private V message;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 时间
     */
    @JsonFormat(timezone = "GMT+8")
    private Date date;

    public static <K, V> KafkaData<K, V> convertFromConsumerRecord(ConsumerRecord<K, V> consumerRecord) {
        return new KafkaData<>(
                consumerRecord.topic(),
                consumerRecord.partition(),
                consumerRecord.offset(),
                consumerRecord.key(),
                consumerRecord.value(),
                consumerRecord.timestamp(),
                new Date(consumerRecord.timestamp()));
    }

}
