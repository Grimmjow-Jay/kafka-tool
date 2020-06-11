package com.grimmjow.kafkatool.service;

import java.util.List;

/**
 * @author Grimm
 * @since 2020/5/29
 */
public interface ZookeeperService {

    /**
     * 节点的子节点
     *
     * @param root 父节点
     * @return 子节点
     */
    List<String> children(String root);

}
