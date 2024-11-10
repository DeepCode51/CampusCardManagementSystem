package edu.school.card.bean;

import edu.school.card.anon.ChangeValue;

import java.math.BigDecimal;
import java.util.Date;

public class CreditInfo {

    /**
     * 充值记录主键
     */
    @ChangeValue(index = 0)
    private Long creditId;

    /**
     * 校园卡编号
     */
    @ChangeValue(index = 1)
    private Long cardNo;

    /**
     * 充值
     */
    @ChangeValue(index = 2)
    private BigDecimal creditBalance;

    /**
     * 充值时间
     */
    @ChangeValue(index = 3)
    private String createTime;

    public Long getCreditId() {
        return creditId;
    }

    public void setCreditId(Long creditId) {
        this.creditId = creditId;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public BigDecimal getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(BigDecimal creditBalance) {
        this.creditBalance = creditBalance;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "CreditInfo{" +
                "creditId=" + creditId +
                ", cardNo=" + cardNo +
                ", creditBalance=" + creditBalance +
                ", createTime=" + createTime +
                '}';
    }
}
