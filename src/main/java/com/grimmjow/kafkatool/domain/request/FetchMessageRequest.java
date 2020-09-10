package com.grimmjow.kafkatool.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Grimm
 * @date 2020/9/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchMessageRequest {

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
