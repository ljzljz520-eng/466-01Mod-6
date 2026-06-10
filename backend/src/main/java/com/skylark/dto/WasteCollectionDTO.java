package com.skylark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WasteCollectionDTO {

    @NotBlank(message = "二维码不能为空")
    private String qrCode;

    @NotBlank(message = "回收人员ID不能为空")
    private String recyclerId;

    @NotBlank(message = "回收人员姓名不能为空")
    private String recyclerName;
}
