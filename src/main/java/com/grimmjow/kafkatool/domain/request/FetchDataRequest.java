package com.grimmjow.kafkatool.domain.request;

import lombok.Data;

/**
 * @author Grimm
 * @date 2020/9/9
 */
@Data
public class FetchDataRequest {

    /**
     * 分区
     */
    private Integer partition;

    /**
     * 开始Offset
     */
    private Long startOffset;

    /**
     * 结束Offset
     */
    private Long endOffset;

}
