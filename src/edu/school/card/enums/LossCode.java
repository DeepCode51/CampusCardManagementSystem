package edu.school.card.enums;

/**
 * 挂失枚举类
 * @date 2020/11/24
 */
public enum LossCode {

    /**
     * 挂失
     */
    YES(1,"是"),
    /**
     * 未挂失
     */
    NO(0,"否");

    private String value;

    private int code;

    LossCode(int code, String value) {
        this.value = value;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
