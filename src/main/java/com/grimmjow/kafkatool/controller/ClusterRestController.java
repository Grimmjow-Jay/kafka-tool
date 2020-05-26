package com.grimmjow.kafkatool.controller;

import com.grimmjow.kafkatool.entity.Cluster;
import com.grimmjow.kafkatool.entity.KafkaNode;
import com.grimmjow.kafkatool.entity.response.ResponseEntity;
import com.grimmjow.kafkatool.service.ClusterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("cluster")
public class ClusterRestController {

    private final ClusterService clusterService;

    public ClusterRestController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @RequestMapping("list")
    public ResponseEntity<List<Cluster>> clusters() {
        return ResponseEntity.success(clusterService.clusters());
    }

    @PostMapping
    public ResponseEntity<Boolean> addCluster(@RequestBody Cluster cluster) {
        clusterService.addCluster(cluster);
        return ResponseEntity.success(true);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> removeCluster(@RequestParam("clusterName") String clusterName) {
        clusterService.removeCluster(clusterName);
        return ResponseEntity.success(true);
    }

    @GetMapping("/nodes")
    public ResponseEntity<List<KafkaNode>> listNodes(@RequestParam("clusterName") String clusterName) {
        return ResponseEntity.success(clusterService.listNodes(clusterName));
    }
}
