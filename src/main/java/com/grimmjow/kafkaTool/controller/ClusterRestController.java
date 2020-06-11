package com.grimmjow.kafkaTool.controller;

import com.grimmjow.kafkaTool.entity.Cluster;
import com.grimmjow.kafkaTool.entity.KafkaNode;
import com.grimmjow.kafkaTool.entity.response.Empty;
import com.grimmjow.kafkaTool.entity.response.ResponseEntity;
import com.grimmjow.kafkaTool.service.ClusterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@RestController
@RequestMapping("cluster")
public class ClusterRestController {

    private final ClusterService clusterService;

    public ClusterRestController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @GetMapping("/clusters")
    public ResponseEntity<List<Cluster>> clusters() {
        return ResponseEntity.success(clusterService.clusters());
    }

    @PostMapping
    public ResponseEntity<Empty> addCluster(@RequestBody Cluster cluster) {
        clusterService.addCluster(cluster);
        return ResponseEntity.success();
    }

    @DeleteMapping("/{clusterName}")
    public ResponseEntity<Empty> removeCluster(@PathVariable String clusterName) {
        clusterService.removeCluster(clusterName);
        return ResponseEntity.success();
    }

    @GetMapping("/nodes/{clusterName}")
    public ResponseEntity<List<KafkaNode>> listNodes(@PathVariable String clusterName) {
        return ResponseEntity.success(clusterService.listNodes(clusterName));
    }
}