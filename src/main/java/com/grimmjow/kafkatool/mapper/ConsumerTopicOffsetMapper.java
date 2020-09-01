package com.grimmjow.kafkatool.mapper;

import com.grimmjow.kafkatool.entity.ConsumerTopicOffset;
import com.grimmjow.kafkatool.vo.ConsumerTopicOffsetVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Grimm
 * @date 2020/9/1
 */
@Repository
public interface ConsumerTopicOffsetMapper {

    /**
     * 保存ConsumerTopicOffset记录
     *
     * @param consumerTopicOffset ConsumerTopicOffset记录
     * @return 保存成功的数量
     */
    int save(@Param("consumerTopicOffset") ConsumerTopicOffset consumerTopicOffset);

    /**
     * 批量保存ConsumerTopicOffset记录
     *
     * @param consumerTopicOffsetList ConsumerTopicOffset记录
     * @return 保存成功的数量
     */
    int saveList(@Param("consumerTopicOffsetList") List<ConsumerTopicOffset> consumerTopicOffsetList);

    /**
     * 查询ConsumerTopicOffset记录
     *
     * @param startTime   最小时间
     * @param endTime     最大时间
     * @param clusterName 集群名
     * @param consumer    consumer
     * @param topic       topic
     * @return ConsumerTopicOffset记录
     */
    List<ConsumerTopicOffsetVo> list(@Param("startTime") long startTime,
                                     @Param("endTime") long endTime,
                                     @Param("clusterName") String clusterName,
                                     @Param("consumer") String consumer,
                                     @Param("topic") String topic);

    /**
     * 按间隔分组查询指定分区offset最大的ConsumerTopicOffset记录
     *
     * @param startTime   最小时间
     * @param endTime     最大时间
     * @param clusterName 集群名
     * @param consumer    消费者名
     * @param topic       topic
     * @param interval    时间间隔
     * @param partition   partition
     * @return ConsumerTopicOffset记录
     */
    List<ConsumerTopicOffsetVo> listByInterval(@Param("startTime") long startTime,
                                               @Param("endTime") long endTime,
                                               @Param("clusterName") String clusterName,
                                               @Param("consumer") String consumer,
                                               @Param("topic") String topic,
                                               @Param("interval") long interval,
                                               @Param("partition") int partition);

    /**
     * 按间隔分组查询所有分区offset最大的ConsumerTopicOffset记录
     *
     * @param startTime   最小时间
     * @param endTime     最大时间
     * @param clusterName 集群名
     * @param consumer    消费者名
     * @param topic       topic
     * @param interval    时间间隔
     * @return ConsumerTopicOffset记录
     */
    List<ConsumerTopicOffsetVo> listByIntervalIgnorePartition(@Param("startTime") long startTime,
                                                              @Param("endTime") long endTime,
                                                              @Param("clusterName") String clusterName,
                                                              @Param("consumer") String consumer,
                                                              @Param("topic") String topic,
                                                              @Param("interval") long interval);

}
