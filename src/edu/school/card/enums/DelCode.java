package edu.school.card.enums;

/**
 * @author wujiayao
 * @date 2020/11/24
 */
public enum DelCode {

    /**
     * 已删
     */
    YES(1,"是"),
    /**
     * 未删
     */
    NO(0,"否");

    private String value;

    private int code;

    DelCode(int code, String value) {
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
