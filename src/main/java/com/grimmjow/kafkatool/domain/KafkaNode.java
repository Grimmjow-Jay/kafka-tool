package com.grimmjow.kafkatool.domain;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.Node;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaNode implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String idString;
    private String host;
    private int port;
    private String rack;

    public static KafkaNode convert(Node node) {
        if (node == null) {
            return null;
        }
        return KafkaNode.builder()
                .id(node.id())
                .host(node.host())
                .port(node.port())
                .rack(node.rack())
                .idString(node.idString())
                .build();
    }

    public static List<KafkaNode> convert(Collection<Node> nodes) {
        List<KafkaNode> kafkaNodes = Lists.newArrayList();
        if (CollectionUtils.isEmpty(nodes)) {
            return kafkaNodes;
        }
        nodes.forEach(node -> kafkaNodes.add(convert(node)));
        return kafkaNodes;
    }
}
