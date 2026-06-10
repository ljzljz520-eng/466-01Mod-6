package com.skylark.dto;

import com.skylark.enums.WasteCategory;
import com.skylark.enums.WasteStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WasteTraceQueryDTO {

    private String bucketNo;

    private WasteCategory category;

    private WasteStatus status;

    private String studentId;

    private String studentName;

    private String experimentName;

    private String labRoom;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Boolean categoryCorrected;

    private Integer pageNum = 1;

    private Integer pageSize = 20;
}
