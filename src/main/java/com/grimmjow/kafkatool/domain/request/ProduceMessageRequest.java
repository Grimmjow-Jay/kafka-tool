package com.grimmjow.kafkatool.domain.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Grimm
 * @date 2020/9/10
 */
@Data
public class ProduceMessageRequest {

    private String key;

    @NotBlank(message = "消息不能为空")
    private String message;

}
