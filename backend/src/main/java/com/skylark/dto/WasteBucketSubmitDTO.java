package com.skylark.dto;

import com.skylark.enums.WasteCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WasteBucketSubmitDTO {

    @NotBlank(message = "桶号不能为空")
    private String bucketNo;

    @NotNull(message = "废液类别不能为空")
    private WasteCategory category;

    @NotNull(message = "重量不能为空")
    @Positive(message = "重量必须大于0")
    private BigDecimal weight;

    @NotNull(message = "是否含特殊成分不能为空")
    private Boolean hasSpecialIngredients;

    private String specialIngredientsRemark;

    @NotBlank(message = "实验名称不能为空")
    private String experimentName;

    @NotNull(message = "实验时间不能为空")
    private LocalDateTime experimentTime;

    @NotBlank(message = "实验室房间号不能为空")
    private String labRoom;

    @NotBlank(message = "学生ID不能为空")
    private String studentId;

    @NotBlank(message = "学生姓名不能为空")
    private String studentName;

    private String studentContact;
}
