package com.grimmjow.kafkatool.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grimmjow.kafkatool.entity.ConsumerTopicOffset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

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

    /**
     * 时间戳对应的时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    public static ConsumerTopicOffset convert(ConsumerTopicOffsetVo consumerTopicOffsetVo) {
        if (consumerTopicOffsetVo == null) {
            return null;
        }
        return ConsumerTopicOffset.builder()
                .clusterName(consumerTopicOffsetVo.getClusterName())
                .consumer(consumerTopicOffsetVo.getConsumer())
                .topic(consumerTopicOffsetVo.getTopic())
                .partition(consumerTopicOffsetVo.getPartition())
                .offset(consumerTopicOffsetVo.getOffset())
                .logSize(consumerTopicOffsetVo.getLogSize())
                .lag(consumerTopicOffsetVo.getLag())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public Date getDate() {
        return timestamp == null ? null : new Date(timestamp);
    }

    public void updateLag() {
        if (offset != null && logSize != null) {
            lag = logSize - offset;
        }
    }

}
