package edu.school.card.frame.component;

import edu.school.card.bean.ConsumeInfo;
import edu.school.card.bean.CreditInfo;
import edu.school.card.bean.SchoolCard;
import edu.school.card.cache.GlobalCache;
import edu.school.card.enums.DelCode;
import edu.school.card.enums.LossCode;
import edu.school.card.frame.util.BeanUtils;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class BaseWindow extends WindowAdapter {

    @Override
    public void windowClosing(WindowEvent e) {
        Window window = e.getWindow();
        window.setVisible(false);
        // 同步数据
        try {
            writeData();
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            System.exit(0);
        }
    }

    private void writeData() throws IOException {
        // 写入校园卡数据
        writeSchoolCard();
        // 写入充值记录
        writeCredit();
        // 写入消费数记录
        writeConsume();
    }

    private void writeConsume() throws IOException{
        if (GlobalCache.getConsumeInfoCache().size() == 0){
            BeanUtils<ConsumeInfo> utils = new BeanUtils<>();
            Map<Long, ConsumeInfo> conMap = utils.getBeanMap("consume.txt", ConsumeInfo.class);
            GlobalCache.setConsumeInfoCache(conMap);
        }
        FileOutputStream fw = new FileOutputStream(BeanUtils.getPathOut("consume.txt"));
        OutputStreamWriter ow = new OutputStreamWriter(fw,"UTF-8");
        Map<Long, ConsumeInfo> consumeInfoCache = GlobalCache.getConsumeInfoCache();
        Set<Long> set = consumeInfoCache.keySet();
        Object[] obj = set.toArray();
        Arrays.sort(obj);
        for (Object key : obj) {
            ConsumeInfo consumeInfo = consumeInfoCache.get(key);
            ow.write(consumeInfo.getConsumeId()+","+consumeInfo.getCardNo()+","+
                    consumeInfo.getConsumeBalance()+","+consumeInfo.getCreateTime()+"\r\n");
        }
        ow.flush();
        ow.close();
        fw.close();
    }

    private void writeCredit() throws IOException  {
        if (GlobalCache.getCreditInfoCache().size() == 0){
            BeanUtils<CreditInfo> utils = new BeanUtils<>();
            GlobalCache.setCreditInfoCache(utils.
                    getBeanMap(
                            "credit.txt",
                            CreditInfo.class));
        }
        FileOutputStream fw = new FileOutputStream(BeanUtils.getPathOut("credit.txt"));
        OutputStreamWriter ow = new OutputStreamWriter(fw,"UTF-8");
        Map<Long, CreditInfo> creditInfoCache = GlobalCache.getCreditInfoCache();
        Set<Long> set = creditInfoCache.keySet();
        Object[] obj = set.toArray();
        Arrays.sort(obj);
        for (Object key : obj) {
            CreditInfo creditInfo = creditInfoCache.get(key);
            ow.write(creditInfo.getCreditId()+","+creditInfo.getCardNo()+","+
                    creditInfo.getCreditBalance()+","+creditInfo.getCreateTime()+"\r\n");
        }
        ow.flush();
        ow.close();
        fw.close();
    }

    private void writeSchoolCard() throws IOException {
        FileOutputStream fw = new FileOutputStream(BeanUtils.getPathOut("schoolcard.txt"));
        OutputStreamWriter ow = new OutputStreamWriter(fw,"UTF-8");
        Map<Long, SchoolCard> schoolCardCache = GlobalCache.getSchoolCardCache();
        Set<Long> set = schoolCardCache.keySet();
        Object[] obj = set.toArray();
        Arrays.sort(obj);
        for (Object key : obj) {
            SchoolCard schoolCard = schoolCardCache.get(key);
            String lossFlag = schoolCard.getLossFlag();
            if (lossFlag.equals(LossCode.YES.getValue())){
                lossFlag = LossCode.YES.getCode()+"";
            }else {
                lossFlag = LossCode.NO.getCode()+"";
            }

            String delFlag = schoolCard.getDelFlag();
            if (delFlag.equals(DelCode.YES.getValue())){
                delFlag = DelCode.YES.getCode()+"";
            }else {
                delFlag = DelCode.NO.getCode()+"";
            }
            ow.write(schoolCard.getCardNo()+","+schoolCard.getCardPwd()+","+
                    schoolCard.getBalance()+","+lossFlag+","+
                    schoolCard.getUserName()+","+delFlag+"\r\n");
        }
        ow.flush();
        ow.close();
        fw.close();
    }
}
