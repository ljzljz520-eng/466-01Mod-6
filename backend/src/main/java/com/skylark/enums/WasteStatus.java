package com.skylark.enums;

public enum WasteStatus {
    SUBMITTED("学生已提交"),
    SAFETY_CHECKED("安全员已检查"),
    IN_STORAGE("已入危废暂存间"),
    COLLECTED("已回收"),
    CANCELLED("已取消");

    private final String description;

    WasteStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
