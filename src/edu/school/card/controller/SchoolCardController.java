package edu.school.card.controller;

import edu.school.card.bean.ConsumeInfo;
import edu.school.card.bean.CreditInfo;
import edu.school.card.bean.SchoolCard;
import edu.school.card.cache.GlobalCache;
import edu.school.card.enums.DelCode;
import edu.school.card.enums.LossCode;
import edu.school.card.frame.util.BeanUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @date 2020/11/25
 */
public class SchoolCardController {

    /**
     * 开卡
     * @param pwd
     * @param balance
     * @param username
     */
    public static void insertSchoolCard(String pwd, String balance, String username) {
        SchoolCard schoolCard = new SchoolCard();
        schoolCard.setCardPwd(pwd);
        schoolCard.setBalance(new BigDecimal(balance));
        schoolCard.setUserName(username);
        schoolCard.setDelFlag(DelCode.NO.getCode()+"");
        schoolCard.setLossFlag(LossCode.NO.getCode()+"");
        // 生成用户主键编号
        long cardNo = GlobalCache.getMaxCardNo() + 1;
        schoolCard.setCardNo(cardNo);
        // 向缓存中插入
        // 创建新Map
        HashMap<Long, SchoolCard> copyMap = new HashMap<>();
        copyMap.putAll(GlobalCache.getSchoolCardCache());

        // 转换格式
        String delFlag = schoolCard.getDelFlag();
        if (delFlag.equals(DelCode.YES.getCode()+"")){
            schoolCard.setDelFlag(DelCode.YES.getValue());
        }else {
            schoolCard.setDelFlag(DelCode.NO.getValue());
        }

        String lossFlag = schoolCard.getLossFlag();
        if (lossFlag.equals(LossCode.YES.getCode()+"")){
            schoolCard.setLossFlag(LossCode.YES.getValue());
        }else {
            schoolCard.setLossFlag(LossCode.NO.getValue());
        }

        copyMap.put(cardNo,schoolCard);
        GlobalCache.setSchoolCardCache(copyMap);
    }

    /**
     * 挂失
     * @param cardNo
     */
    public static void lossSchoolCard(String cardNo) {
        HashMap<Long, SchoolCard> map = new HashMap<>();
        Map<Long, SchoolCard> schoolCardCache = GlobalCache.getSchoolCardCache();
        map.putAll(schoolCardCache);

        Set<Long> set = map.keySet();
        for (Long key : set) {
            SchoolCard schoolCard = schoolCardCache.get(key);
            if (schoolCard.getCardNo().toString().equals(cardNo)){
                schoolCard.setLossFlag(LossCode.YES.getValue());
                schoolCardCache.put(key,schoolCard);
                break;
            }
        }
    }

    /**
     * 删卡
     * @param cardNo
     */
    public static void delSchoolCard(String cardNo) {
        HashMap<Long, SchoolCard> map = new HashMap<>();
        Map<Long, SchoolCard> schoolCardCache = GlobalCache.getSchoolCardCache();
        map.putAll(schoolCardCache);

        Set<Long> set = map.keySet();
        for (Long key : set) {
            SchoolCard schoolCard = schoolCardCache.get(key);
            if (schoolCard.getCardNo().toString().equals(cardNo)){
                schoolCard.setDelFlag(DelCode.YES.getValue());
                schoolCardCache.put(key,schoolCard);
                break;
            }
        }
    }

    /**
     * 修改密码
     * @param cardNo
     * @param cardPwd
     */
    public static void updateSchoolCard(String cardNo, String cardPwd) {
        HashMap<Long, SchoolCard> map = new HashMap<>();
        Map<Long, SchoolCard> schoolCardCache = GlobalCache.getSchoolCardCache();
        map.putAll(schoolCardCache);

        Set<Long> set = map.keySet();
        for (Long key : set) {
            SchoolCard schoolCard = schoolCardCache.get(key);
            if (schoolCard.getCardNo().toString().equals(cardNo)){
                if (schoolCard.getLossFlag().equals(LossCode.YES.getValue())){
                    return;
                }
                if (schoolCard.getDelFlag().equals(DelCode.YES.getValue())){
                    return;
                }
                schoolCard.setCardPwd(cardPwd);
                schoolCardCache.put(key,schoolCard);
                return;
            }
        }
    }

    /**
     * 查询校园卡信息
     * @param cardNo
     * @param cardUser
     */
    public static void querySchoolCard(String cardNo, String cardUser) {
        HashMap<Long, SchoolCard> map = new HashMap<>();
        Map<Long, SchoolCard> schoolCardCache = GlobalCache.getSchoolCardCache();
        map.putAll(schoolCardCache);

        Set<Long> set = map.keySet();
        for (Long key : set) {
            SchoolCard schoolCard = schoolCardCache.get(key);
            if (!cardNo.equals("")){
                if (schoolCard.getCardNo().toString().equals(cardNo)){
                    if (schoolCard.getLossFlag().equals(LossCode.YES.getValue())){
                        return;
                    }
                    if (schoolCard.getDelFlag().equals(DelCode.YES.getValue())){
                        return;
                    }
                    GlobalCache.setQueryCurrentSchoolCard(schoolCard);
                    return;
                }
            }

            if (!cardUser.equals("")){
                if (schoolCard.getUserName().toString().equals(cardUser)){
                    if (schoolCard.getLossFlag().equals(LossCode.YES.getValue())){
                        return;
                    }
                    if (schoolCard.getDelFlag().equals(DelCode.YES.getValue())){
                        return;
                    }
                    GlobalCache.setQueryCurrentSchoolCard(schoolCard);
                    return;
                }
            }
        }
    }

    /**
     * 充值
     * @param cardNo
     * @param balance
     */
    public static void insertBalance(String cardNo, String balance) {
        if (GlobalCache.getCreditInfoCache().size() == 0){
            BeanUtils<CreditInfo> utils = new BeanUtils<>();
            GlobalCache.setCreditInfoCache(utils.
                    getBeanMap(
                            "credit.txt",
                            CreditInfo.class));
        }
        HashMap<Long, SchoolCard> map = new HashMap<>();
        Map<Long, SchoolCard> schoolCardCache = GlobalCache.getSchoolCardCache();
        map.putAll(schoolCardCache);

        Set<Long> set = map.keySet();
        for (Long key : set) {
            SchoolCard schoolCard = schoolCardCache.get(key);
            if (schoolCard.getCardNo().toString().equals(cardNo)){
                if (schoolCard.getLossFlag().equals(LossCode.YES.getValue())){
                    return;
                }
                if (schoolCard.getDelFlag().equals(DelCode.YES.getValue())){
                    return;
                }

                BigDecimal balance1 = schoolCard.getBalance();
                BigDecimal bigDecimal = balance1.add(new BigDecimal(balance));
                schoolCard.setBalance(bigDecimal);
                schoolCardCache.put(key,schoolCard);

                // 插入充值数据
                insertCredit(balance, schoolCard);
                return;
            }
        }
    }

    /**
     * 向充值表写入数据
     * @param balance
     * @param schoolCard
     */
    private static void insertCredit(String balance, SchoolCard schoolCard) {
        CreditInfo creditInfo = new CreditInfo();
        Long maxCreditId = GlobalCache.getMaxCreditId();
        if (maxCreditId == null){
            creditInfo.setCreditId(1L);
        }else {
            creditInfo.setCreditId(maxCreditId+1);
        }
        creditInfo.setCardNo(schoolCard.getCardNo());
        creditInfo.setCreditBalance(new BigDecimal(balance));
        creditInfo.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        // 向缓存中插入
        // 创建新Map
        HashMap<Long, CreditInfo> copyMap = new HashMap<>();
        copyMap.putAll(GlobalCache.getCreditInfoCache());
        copyMap.put(creditInfo.getCreditId(),creditInfo);
        GlobalCache.setCreditInfoCache(copyMap);
    }

    /**
     * 消费
     * @param cardNo
     * @param balance
     */
    public static void divBalance(String cardNo, String balance) {
        if (GlobalCache.getConsumeInfoCache().size() == 0){
            BeanUtils<ConsumeInfo> utils = new BeanUtils<>();
            Map<Long, ConsumeInfo> conMap = utils.getBeanMap("consume.txt", ConsumeInfo.class);
            GlobalCache.setConsumeInfoCache(conMap);
        }
        HashMap<Long, SchoolCard> map = new HashMap<>();
        Map<Long, SchoolCard> schoolCardCache = GlobalCache.getSchoolCardCache();
        map.putAll(schoolCardCache);

        Set<Long> set = map.keySet();
        for (Long key : set) {
            SchoolCard schoolCard = schoolCardCache.get(key);
            if (schoolCard.getCardNo().toString().equals(cardNo)){
                if (schoolCard.getLossFlag().equals(LossCode.YES.getValue())){
                    return;
                }
                if (schoolCard.getDelFlag().equals(DelCode.YES.getValue())){
                    return;
                }

                BigDecimal balance1 = schoolCard.getBalance();
                BigDecimal bigDecimal = balance1.subtract(new BigDecimal(balance));
                // 消费超过余额,返回
                if (bigDecimal.compareTo(BigDecimal.ZERO) == -1){
                    GlobalCache.setWarn("本次消费超出卡中余额,请先充值");
                    return;
                }
                schoolCard.setBalance(bigDecimal);
                schoolCardCache.put(key,schoolCard);

                // 向消费表写入数据
                insertConsume(balance, schoolCard);
                return;
            }
        }
    }

    private static void insertConsume(String balance, SchoolCard schoolCard) {
        ConsumeInfo consumeInfo = new ConsumeInfo();
        Long maxConsumeId = GlobalCache.getMaxConsumeId();
        if (maxConsumeId == null){
            consumeInfo.setConsumeId(1L);
        }else {
            consumeInfo.setConsumeId(maxConsumeId+1);
        }
        consumeInfo.setCardNo(schoolCard.getCardNo());
        consumeInfo.setConsumeBalance(new BigDecimal(balance));
        consumeInfo.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        // 向缓存中插入
        // 创建新Map
        HashMap<Long, ConsumeInfo> copyMap = new HashMap<>();
        copyMap.putAll(GlobalCache.getConsumeInfoCache());
        copyMap.put(consumeInfo.getConsumeId(),consumeInfo);
        GlobalCache.setConsumeInfoCache(copyMap);
    }
}
