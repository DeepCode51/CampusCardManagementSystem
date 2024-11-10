package edu.school.card.cache;

import edu.school.card.bean.ConsumeInfo;
import edu.school.card.bean.CreditInfo;
import edu.school.card.bean.SchoolCard;
import edu.school.card.enums.DelCode;
import edu.school.card.enums.LossCode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GlobalCache {

    private static String warn;

    /**
     * 校园卡数据缓存
     */
    private static Map<Long, SchoolCard> schoolCardCache = new HashMap<>();

    /**
     * 最大的校园卡主键
     */
    private static Long maxCardNo;

    /**
     * 查询的当前条数据
     */
    private static SchoolCard queryCurrentSchoolCard;

    /**
     *  消费数据缓存
     */
    private static Map<Long, ConsumeInfo> consumeInfoCache = new HashMap<>();

    /**
     * 最大消费数据主键
     */
    private static Long maxConsumeId;

    /**
     * 充值数据缓存
     */
    private static Map<Long, CreditInfo> creditInfoCache = new HashMap<>();

    /**
     * 最大充值数据主键
     */
    private static Long maxCreditId;

    public static String getWarn() {
        return warn;
    }

    public static void setWarn(String warn) {
        GlobalCache.warn = warn;
    }

    public static SchoolCard getQueryCurrentSchoolCard() {
        return queryCurrentSchoolCard;
    }

    public static void setQueryCurrentSchoolCard(SchoolCard queryCurrentSchoolCard) {
        GlobalCache.queryCurrentSchoolCard = queryCurrentSchoolCard;
    }

    public static Long getMaxCardNo() {
        return maxCardNo;
    }

    public static void setMaxCardNo(Long maxCardNo) {
        GlobalCache.maxCardNo = maxCardNo;
    }

    public static Long getMaxConsumeId() {
        return maxConsumeId;
    }

    public static void setMaxConsumeId(Long maxConsumeId) {
        GlobalCache.maxConsumeId = maxConsumeId;
    }

    public static Long getMaxCreditId() {
        return maxCreditId;
    }

    public static void setMaxCreditId(Long maxCreditId) {
        GlobalCache.maxCreditId = maxCreditId;
    }

    public static Map<Long, SchoolCard> getSchoolCardCache() {
        return schoolCardCache;
    }

    public static void setSchoolCardCache(Map<Long, SchoolCard> schoolCardCache) {
        GlobalCache.schoolCardCache = schoolCardCache;
        Object[] obj = getMaxKey(schoolCardCache.keySet());
        if (obj.length == 0){
            GlobalCache.setMaxCardNo(0L);
        }else {
            GlobalCache.setMaxCardNo(Long.parseLong(obj[obj.length - 1].toString()));
        }
    }

    private static Object[] getMaxKey(Set<Long> set) {
        Object[] obj = set.toArray();
        Arrays.sort(obj);
        return obj;
    }

    public static Map<Long, ConsumeInfo> getConsumeInfoCache() {
        return consumeInfoCache;
    }

    public static void setConsumeInfoCache(Map<Long, ConsumeInfo> consumeInfoCache) {
        GlobalCache.consumeInfoCache = consumeInfoCache;
        Object[] obj = getMaxKey(consumeInfoCache.keySet());
        if (obj.length == 0){
            GlobalCache.setMaxConsumeId(0L);
        }else {
            GlobalCache.setMaxConsumeId(Long.parseLong(obj[obj.length - 1].toString()));
        }
    }

    public static Map<Long, CreditInfo> getCreditInfoCache() {
        return creditInfoCache;
    }

    public static void setCreditInfoCache(Map<Long, CreditInfo> creditInfoCache) {
        GlobalCache.creditInfoCache = creditInfoCache;
        Object[] obj = getMaxKey(creditInfoCache.keySet());
        if (obj.length == 0){
            GlobalCache.setMaxCreditId(0L);
        }else {
            GlobalCache.setMaxCreditId(Long.parseLong(obj[obj.length - 1].toString()));
        }
    }

    public static Map<Long,SchoolCard> getNotDelSchoolMap(){
        if (schoolCardCache.size() == 0){
            return null;
        }
        HashMap<Long, SchoolCard> map = new HashMap<>();
        Set<Map.Entry<Long, SchoolCard>> entries = schoolCardCache.entrySet();
        for (Map.Entry<Long, SchoolCard> entry : entries) {
            SchoolCard schoolCard = entry.getValue();
            if (schoolCard.getDelFlag().equals(DelCode.YES.getValue())){
                continue;
            }
            map.put(schoolCard.getCardNo(),schoolCard);
        }
        return map;
    }
}
