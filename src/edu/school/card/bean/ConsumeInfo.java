package edu.school.card.bean;

import edu.school.card.anon.ChangeValue;

import java.math.BigDecimal;
import java.util.Date;

public class ConsumeInfo {

    /**
     * 充值记录主键
     */
    @ChangeValue(index = 0)
    private Long consumeId;

    /**
     * 校园卡编号
     */
    @ChangeValue(index = 1)
    private Long cardNo;

    /**
     * 消费
     */
    @ChangeValue(index = 2)
    private BigDecimal consumeBalance;

    /**
     * 消费时间
     */
    @ChangeValue(index = 3)
    private String createTime;

    public Long getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(Long consumeId) {
        this.consumeId = consumeId;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public BigDecimal getConsumeBalance() {
        return consumeBalance;
    }

    public void setConsumeBalance(BigDecimal consumeBalance) {
        this.consumeBalance = consumeBalance;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ConsumeInfo{" +
                "consumeId=" + consumeId +
                ", cardNo=" + cardNo +
                ", consumeBalance=" + consumeBalance +
                ", createTime=" + createTime +
                '}';
    }
}
