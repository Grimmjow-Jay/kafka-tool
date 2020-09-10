package com.grimmjow.kafkatool.domain.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Grimm
 * @date 2020/9/10
 */
@Data
public class EditOffsetRequest {

    @NotNull(message = "Topic不能为空")
    private String topic;

    @NotNull(message = "分区不能为空")
    private Integer partition;

    @NotNull(message = "offset不能为空")
    @Min(value = 0L, message = "Offset有误")
    private Long offset;

}
