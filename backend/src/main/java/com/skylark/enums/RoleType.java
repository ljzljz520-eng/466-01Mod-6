package com.skylark.enums;

public enum RoleType {
    STUDENT("学生"),
    SAFETY_OFFICER("安全员"),
    RECYCLER("回收人员"),
    ADMIN("管理员");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
