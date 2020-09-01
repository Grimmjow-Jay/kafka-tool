package com.grimmjow.kafkatool.vo;

import com.grimmjow.kafkatool.entity.ConsumerTopicOffset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsumerTopicOffsetVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 集群名
     */
    private String clusterName;

    /**
     * 消费者
     */
    private String consumer;

    /**
     * Topic
     */
    private String topic;

    /**
     * 分区
     */
    private Integer partition;

    /**
     * offset
     */
    private Long offset;

    /**
     * logSize
     */
    private Long logSize;

    /**
     * lag
     */
    private Long lag;

    /**
     * 时间戳
     */
    private Long timestamp;

    public static ConsumerTopicOffset convert(ConsumerTopicOffsetVo consumerTopicOffsetVo) {
        return ConsumerTopicOffset.builder()
                .consumer(consumerTopicOffsetVo.getConsumer())
                .topic(consumerTopicOffsetVo.getTopic())
                .partition(consumerTopicOffsetVo.getPartition())
                .offset(consumerTopicOffsetVo.getOffset())
                .logSize(consumerTopicOffsetVo.getLogSize())
                .lag(consumerTopicOffsetVo.getLag())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public void updateLag() {
        if (offset != null && logSize != null) {
            lag = logSize - offset;
        }
    }

}
