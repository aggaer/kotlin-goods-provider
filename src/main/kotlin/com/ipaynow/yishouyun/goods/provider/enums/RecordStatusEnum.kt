package com.ipaynow.yishouyun.goods.provider.enums

enum class RecordStatusEnum(val statusName: String, val code: Int) {
    DELETED("已删除", -1),
    NORMAL("正常", 0);
}