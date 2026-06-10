package com.skylark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WasteStorageDTO {

    @NotNull(message = "废液桶ID不能为空")
    private Long bucketId;

    @NotBlank(message = "操作人员ID不能为空")
    private String operatorId;

    @NotBlank(message = "操作人员姓名不能为空")
    private String operatorName;
}
