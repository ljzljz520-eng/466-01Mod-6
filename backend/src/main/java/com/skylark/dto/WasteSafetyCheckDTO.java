package com.skylark.dto;

import com.skylark.enums.WasteCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WasteSafetyCheckDTO {

    @NotNull(message = "废液桶ID不能为空")
    private Long bucketId;

    @NotBlank(message = "安全员ID不能为空")
    private String officerId;

    @NotBlank(message = "安全员姓名不能为空")
    private String officerName;

    @NotNull(message = "检查结果不能为空")
    private Boolean passed;

    private WasteCategory correctedCategory;

    private String correctionRemark;

    private String remark;
}
