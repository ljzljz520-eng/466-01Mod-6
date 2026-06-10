package com.skylark.enums;

public enum WasteCategory {
    ACID("酸性废液"),
    ALKALI("碱性废液"),
    ORGANIC("有机废液"),
    HEAVY_METAL("重金属废液"),
    TOXIC("有毒废液"),
    OXIDIZING("氧化性废液"),
    REDUCING("还原性废液"),
    OTHER("其他废液");

    private final String description;

    WasteCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
