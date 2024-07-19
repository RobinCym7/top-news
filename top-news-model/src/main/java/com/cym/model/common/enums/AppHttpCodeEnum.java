package com.cym.model.common.enums;

public enum AppHttpCodeEnum {

    // 成功段0
    SUCCESS(200,"操作成功"),
    // 登录段1~50
    NEED_LOGIN(1,"需要登录后操作"),
    LOGIN_PASSWORD_ERROR(2,"密码错误"),
    // TOKEN50~100
    TOKEN_INVALID(50,"无效的TOKEN"),
    TOKEN_EXPIRE(51,"TOKEN已过期"),
    TOKEN_REQUIRE(52,"TOKEN是必须的"),
    // SIGN验签 100~120
    SIGN_INVALID(100,"无效的SIGN"),
    SIG_TIMEOUT(101,"SIGN已过期"),
    // 参数错误 500~1000
    PARAM_REQUIRE(500,"缺少参数"),
    PARAM_INVALID(501,"无效参数"),
    PARAM_IMAGE_FORMAT_ERROR(502,"图片格式有误"),
    CHANNEL_USED(503,"频道已被引用"),
    CHANNEL_DELETE_FAILED(504,"频道删除失败"),
    SERVER_ERROR(503,"服务器内部错误"),
    // 数据错误 1000~2000
    DATA_EXIST(1000,"数据已经存在"),
    CHANNEL_EXIST(1001,"频道已经存在"),
    SENSITIVE_EXIST(1002,"敏感词已经存在"),
    AP_USER_DATA_NOT_EXIST(1003,"ApUser数据不存在"),
    USER_DATA_NOT_EXIST(1004,"用户不存在"),
    DATA_NOT_EXIST(1005,"数据不存在"),
    ARTICLE_NOT_EXIST(1006,"文章不存在"),
    AUTHOR_ID_GET_FAIL(1007, "作者ID获取失败"),

    // 数据错误 3000~3500
    NO_OPERATOR_AUTH(3000,"无权限操作"),
    NEED_ADMIND(3001,"需要管理员权限"),
    // 素材错误 4000~5000
    MATERIAL_REFERENCE_FAIL(4000, "素材失效"),
    MATERIAL_AUDIT_FAIL(4001, "内容审核失败");

    int code;
    String errorMessage;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
