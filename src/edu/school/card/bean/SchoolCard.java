package edu.school.card.bean;

import edu.school.card.anon.ChangeValue;

import java.math.BigDecimal;

/**
 * @author wujiayao
 * @date 2020/11/24
 */
public class SchoolCard {

    /**
     * 卡号
     */
    @ChangeValue(index = 0)
    private Long cardNo;

    /**
     * 用户名
     */
    @ChangeValue(index = 4)
    private String userName;

    /**
     * 密码
     */
    @ChangeValue(index = 1)
    private String cardPwd;

    /**
     * 余额
     */
    @ChangeValue(index = 2)
    private BigDecimal balance;

    /**
     * 是否挂失
     */
    @ChangeValue(index = 3,pattern = "1=是,0=否")
    private String lossFlag;

    /**
     * 是否删除
     */
    @ChangeValue(index = 5,pattern = "1=是,0=否")
    private String delFlag;

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCardPwd() {
        return cardPwd;
    }

    public void setCardPwd(String cardPwd) {
        this.cardPwd = cardPwd;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getLossFlag() {
        return lossFlag;
    }

    public void setLossFlag(String lossFlag) {
        this.lossFlag = lossFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return "SchoolCard{" +
                "cardNo=" + cardNo +
                ", userName='" + userName + '\'' +
                ", cardPwd='" + cardPwd + '\'' +
                ", balance=" + balance +
                ", lossFlag='" + lossFlag + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
