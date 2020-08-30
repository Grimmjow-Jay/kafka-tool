package com.grimmjow.kafkatool.controller;

import com.grimmjow.kafkatool.domain.Cluster;
import com.grimmjow.kafkatool.domain.KafkaNode;
import com.grimmjow.kafkatool.domain.response.Empty;
import com.grimmjow.kafkatool.domain.response.ResponseEntity;
import com.grimmjow.kafkatool.service.ClusterService;
import com.grimmjow.kafkatool.vo.ClusterVo;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
    public ResponseEntity<List<ClusterVo>> clusters() {
        return ResponseEntity.success(clusterService.clusters());
    }

    @PostMapping
    public ResponseEntity<Empty> addCluster(@Valid @RequestBody Cluster cluster) {
        clusterService.addCluster(cluster);
        return ResponseEntity.success();
    }

    @DeleteMapping("/{clusterName}")
    public ResponseEntity<Empty> removeCluster(@NotBlank(message = "集群名不能为空") @PathVariable String clusterName) {
        clusterService.removeCluster(clusterName);
        return ResponseEntity.success();
    }

    @GetMapping("/nodes/{clusterName}")
    public ResponseEntity<List<KafkaNode>> listNodes(@NotBlank(message = "集群名不能为空") @PathVariable String clusterName) {
        return ResponseEntity.success(clusterService.listNodes(clusterName));
    }

    @PutMapping("reconnect/{clusterName}")
    public ResponseEntity<Empty> reconnect(@NotBlank(message = "集群名不能为空") @PathVariable String clusterName) {
        clusterService.reconnect(clusterName);
        return ResponseEntity.success();
    }
}
