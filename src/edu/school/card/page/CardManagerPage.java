package edu.school.card.page;

import edu.school.card.bean.ConsumeInfo;
import edu.school.card.bean.CreditInfo;
import edu.school.card.bean.SchoolCard;
import edu.school.card.cache.GlobalCache;
import edu.school.card.controller.SchoolCardController;
import edu.school.card.enums.DelCode;
import edu.school.card.frame.component.BaseFrame;
import edu.school.card.frame.component.Layer;
import edu.school.card.frame.util.BeanUtils;
import edu.school.card.frame.util.RegexUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @date 2020/11/24
 */
public class CardManagerPage extends BaseFrame {

    private Panel container;

    private JTable table;

    private String[] titles = {"卡编号","卡密码","卡余额","是否挂失","用户名"};

    /**
     * 开卡
     */
    // 卡编号
    private TextField startCardNoText;
    // 卡密码
    private TextField startCardPwdText;
    // 卡余额
    private TextField startCardYMoneyText;
    // 用户名
    private TextField startCardUserText;
    // 开卡按钮
    private Button startCardBtn;

    /**
     * 挂失卡
     */
    // 卡号
    private TextField disAbleCardNoText;
    // 挂失按钮
    private Button disAbleBtn;

    /**
     * 删除卡
     */
    // 卡号
    private TextField deleteCardNoText;
    // 删除按钮
    private Button deleteBtn;

    /**
     * 卡消费与卡充值
     */
    // 卡号
    private TextField addCardNoText;
    // 余额
    private TextField addYMoneyText;
    // 充值按钮
    private Button addBtn;
    // 消费按钮
    private Button decBtn;

    /**
     * 修改密码
     */
    // 卡号
    private TextField updateCardNoText;
    // 面
    private TextField updateCardPwdText;
    // 修改按钮
    private Button updateBtn;

    /**
     * 消费记录,充值记录
     */
    // 卡号
    private TextField hisCardNoText;
    // 充值按钮
    private Button hisAddBtn;
    // 消费按钮
    private Button hisDecBtn;

    /**
     * 查询
     */
    // 卡号
    private TextField queryCardNoText;
    // 用户名
    private TextField queryCardUserText;
    // 查询按钮
    private Button queryBtn;

    public void start(){
        init("校园卡管理系统",1000,800,true);
        // 创建组件
        createComponents();
    }

    private void createComponents() {
        // 创建容器
        createContainer();
        // 创建表格
        createTable();
        // 创建标题
        createTitle();
        // 后置处理器
        afterProsser();
        // 创建操作栏
        createTools();
        // 创建监听事件
        createListener();
    }

    /**
     * 监听事件
     */
    private void createListener() {
        // 创建开卡监听事件
        createStartCardListener();
        // 创建挂失卡监听事件
        createLossCardListener();
        // 创建删除卡监听事件
        createDelCardListener();
        // 创建修改卡密码监听事件
        createUpdatePwdListener();
        // 创建查询卡信息事件
        createQuerySchoolCardListener();
        // 监听充值
        createInsertBalanceListener();
        // 监听消费
        createDivBalanceListener();
        // 监听充值记录
        createInsertHistory();
        // 监听消费记录
        createDivHistory();
    }

    private void createDivHistory() {
        hisDecBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String cardNo = hisCardNoText.getText().trim();
                if (alert(cardNo,"卡编号不能为空")){
                    return;
                }
                // 查询编号是否已存在
                if (alert2(cardNo,"卡编号不存在,请重新输入", GlobalCache.getSchoolCardCache())){
                    return;
                }

                // 查询消费记录
                getDivHistory(cardNo);
            }
        });
    }

    private void getDivHistory(String cardNo) {
        int count = 0;
        if (GlobalCache.getConsumeInfoCache().size() == 0){
            BeanUtils<ConsumeInfo> utils = new BeanUtils<>();
            GlobalCache.setConsumeInfoCache(utils.
                    getBeanMap(
                            "consume.txt",
                            ConsumeInfo.class));
        }

        HashMap<Long, ConsumeInfo> map = new HashMap<>();
        // 判断卡号是否已被删除,删除则返回
        Map<Long, SchoolCard> schoolCardCache = GlobalCache.getSchoolCardCache();
        Set<Map.Entry<Long, SchoolCard>> entries = schoolCardCache.entrySet();
        for (Map.Entry<Long, SchoolCard> entry : entries) {
            SchoolCard schoolCard = entry.getValue();
            if (schoolCard.getCardNo().toString().equals(cardNo)){
                if (schoolCard.getDelFlag().equals(DelCode.YES.getValue())){
                    alert("该卡号已被删除,不可充值查询记录");
                    return;
                }

                // 查询
                Map<Long, ConsumeInfo> consumeInfoCache = GlobalCache.getConsumeInfoCache();
                Set<Map.Entry<Long, ConsumeInfo>> es = consumeInfoCache.entrySet();
                for (Map.Entry<Long, ConsumeInfo> e : es) {
                    ConsumeInfo consumeInfo = e.getValue();
                    if (consumeInfo.getCardNo().toString().equals(cardNo)){
                        map.put(Long.parseLong(String.valueOf(count)),consumeInfo);
                        count++;
                    }
                }
                break;
            }
        }


        Object[][] data = new Object[map.size()][4];
        for (int i = 0; i < data.length; i++) {
            Object[] datum = data[i];
            ConsumeInfo consumeInfo = map.get(Long.parseLong(String.valueOf(i)));
            for (int j = 0; j < datum.length; j++) {
                if (j == 0){
                    data[i][j] = consumeInfo.getConsumeId();
                    continue;
                }
                if (j == 1){
                    data[i][j] = consumeInfo.getCardNo();
                    continue;
                }
                if (j == 2){
                    data[i][j] = consumeInfo.getConsumeBalance();
                    continue;
                }
                if (j == 3){
                    data[i][j] = consumeInfo.getCreateTime();
                }
            }
        }

        String[] title = {"消费编号","卡编号","消费金额","消费时间"};
        table.setModel(new DefaultTableModel(data,title));
        clearInsertHistory();
    }

    private void createInsertHistory() {
        hisAddBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String cardNo = hisCardNoText.getText().trim();
                if (alert(cardNo,"卡编号不能为空")){
                    return;
                }
                // 查询编号是否已存在
                if (alert2(cardNo,"卡编号不存在,请重新输入", GlobalCache.getSchoolCardCache())){
                    return;
                }

                // 查询充值记录
                getInsertHistory(cardNo);
            }
        });
    }

    private void getInsertHistory(String cardNo) {
        int count = 0;
        BeanUtils<CreditInfo> utils = new BeanUtils<>();
        if (GlobalCache.getCreditInfoCache().size() == 0){
            GlobalCache.setCreditInfoCache(utils.
                    getBeanMap(
                            "credit.txt",
                            CreditInfo.class));
        }

        HashMap<Long, CreditInfo> map = new HashMap<>();
        // 判断卡号是否已被删除,删除则返回
        Map<Long, SchoolCard> schoolCardCache = GlobalCache.getSchoolCardCache();
        Set<Map.Entry<Long, SchoolCard>> entries = schoolCardCache.entrySet();
        for (Map.Entry<Long, SchoolCard> entry : entries) {
            SchoolCard schoolCard = entry.getValue();
            if (schoolCard.getCardNo().toString().equals(cardNo)){
                if (schoolCard.getDelFlag().equals(DelCode.YES.getValue())){
                    alert("该卡号已被删除,不可充值查询记录");
                    return;
                }

                // 查询
                Map<Long, CreditInfo> creditInfoCache = GlobalCache.getCreditInfoCache();
                Set<Map.Entry<Long, CreditInfo>> es = creditInfoCache.entrySet();
                for (Map.Entry<Long, CreditInfo> e : es) {
                    CreditInfo creditInfo = e.getValue();
                    if (creditInfo.getCardNo().toString().equals(cardNo)){
                        map.put(Long.parseLong(String.valueOf(count)),creditInfo);
                        count++;
                    }
                }
                break;
            }
        }

        Object[][] data = new Object[map.size()][4];
        for (int i = 0; i < data.length; i++) {
            Object[] datum = data[i];
            CreditInfo creditInfo = map.get(Long.parseLong(String.valueOf(i)));
            for (int j = 0; j < datum.length; j++) {
                if (j == 0){
                    data[i][j] = creditInfo.getCreditId();
                    continue;
                }
                if (j == 1){
                    data[i][j] = creditInfo.getCardNo();
                    continue;
                }
                if (j == 2){
                    data[i][j] = creditInfo.getCreditBalance();
                    continue;
                }
                if (j == 3){
                    data[i][j] = creditInfo.getCreateTime();
                }
            }
        }

        String[] title = {"充值编号","卡编号","充值金额","充值时间"};
        table.setModel(new DefaultTableModel(data,title));
        clearInsertHistory();
    }

//    /**
//     * 获取开卡用户信息
//     */
//    private Object[][] getCreditMap() {
//        BeanUtils<SchoolCard> utils = new BeanUtils<>();
//        if (GlobalCache.getSchoolCardCache().size() == 0){
//            GlobalCache.setSchoolCardCache(utils.
//                    getBeanMap(
//                            "schoolcard.txt",
//                            SchoolCard.class));
//        }
//        Object[][] data = getObjects();
//        return data;
//    }

    private void clearInsertHistory() {
        hisCardNoText.setText(null);
    }

    private void createDivBalanceListener() {
        decBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String cardNo = addCardNoText.getText().trim();
                if (alert(cardNo,"卡编号不能为空")){
                    return;
                }
                // 查询编号是否已存在
                if (alert2(cardNo,"卡编号不存在,请重新输入", GlobalCache.getSchoolCardCache())){
                    return;
                }

                String balance = addYMoneyText.getText();
                if (alert(balance,"卡余额不能为空")){
                    return;
                }
                if (balance.contains("-")){
                    alert("消费不能为负数");
                    return;
                }

                try {
                    SchoolCardController.divBalance(cardNo,balance);
                }catch (Exception ex){
                    ex.printStackTrace();
                    alert("警告!!! 请输入正确的金额");
                    return;
                }

                if (GlobalCache.getWarn() != null){
                    alert(GlobalCache.getWarn());
                    return;
                }

                // 创建表格
                table.setModel(new DefaultTableModel(getSchoolCardMap(),titles));
                // 清除数据
                clearInsertBalance();
            }
        });
    }

    private void createInsertBalanceListener() {
        addBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String cardNo = addCardNoText.getText().trim();
                if (alert(cardNo,"卡编号不能为空")){
                    return;
                }
                // 查询编号是否已存在
                if (alert2(cardNo,"卡编号不存在,请重新输入", GlobalCache.getSchoolCardCache())){
                    return;
                }

                String balance = addYMoneyText.getText();
                if (alert(balance,"卡余额不能为空")){
                    return;
                }
                if (balance.contains("-")){
                    alert("充值不能为负数");
                    return;
                }

                try {
                    SchoolCardController.insertBalance(cardNo,balance);
                }catch (Exception ex){
                    ex.printStackTrace();
                    alert("警告!!! 请输入正确的金额");
                    return;
                }

                // 创建表格
                table.setModel(new DefaultTableModel(getSchoolCardMap(),titles));
                // 清除数据
                clearInsertBalance();
            }
        });
    }

    private void clearInsertBalance() {
        addCardNoText.setText(null);
        addYMoneyText.setText(null);
    }

    private void createQuerySchoolCardListener() {
        queryBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String cardNo = queryCardNoText.getText();
                String cardUser = queryCardUserText.getText();

                if (cardNo.equals("")&&cardUser.equals("")){
                    // 显示全部
                    // 创建表格
                    table.setModel(new DefaultTableModel(getSchoolCardMap(),titles));
                    // 清除数据
                    clearQueryCard();
                    return;
                }else if ((!cardNo.equals(""))&&cardUser.equals("")){
                    // 编号不为空时
                    // 查询编号是否已存在
                    if (alert2(cardNo,"卡编号不存在,请重新输入", GlobalCache.getSchoolCardCache())){
                        return;
                    }

                }else if (cardNo.equals("")&&(!cardUser.equals(""))){
                    // 用户不为空时
                    // 查询用户名是否已存在
                    if (alertNoUser(cardUser,"卡用户名不存在,请修改用户名", GlobalCache.getSchoolCardCache())){
                        return;
                    }
                }
                // 查询校园卡信息
                try {
                    SchoolCardController.querySchoolCard(cardNo,cardUser);
                }catch (Exception ex){
                    ex.printStackTrace();
                    alert("警告!!!出现异常了");
                    return;
                }

                if (GlobalCache.getQueryCurrentSchoolCard() == null){
                    alert("查无此人!!!");
                    return;
                }

                // 创建表格
                table.setModel(new DefaultTableModel(getQueryCurrentSchoolCard(),titles));
                // 清除数据
                clearQueryCard();
            }
        });
    }

    private Object[][] getQueryCurrentSchoolCard() {
        SchoolCard schoolCard = GlobalCache.getQueryCurrentSchoolCard();
        Object[][] data = new Object[1][5];
        for (int i = 0; i < data.length; i++) {
            Object[] datum = data[i];
            for (int j = 0; j < datum.length; j++) {
                if (j == 0){
                    data[i][j] = schoolCard.getCardNo();
                    continue;
                }
                if (j == 1){
                    data[i][j] = schoolCard.getCardPwd();
                    continue;
                }
                if (j == 2){
                    data[i][j] = schoolCard.getBalance();
                    continue;
                }
                if (j == 3){
                    data[i][j] = schoolCard.getLossFlag();
                    continue;
                }
                if (j == 4){
                    data[i][j] = schoolCard.getUserName();
                }
            }
        }
        return data;
    }

    private void clearQueryCard() {
        queryCardUserText.setText(null);
        queryCardNoText.setText(null);
    }

    private void createUpdatePwdListener() {
        updateBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 获取卡用户名
                String cardNo = updateCardNoText.getText().trim();
                if (alert(cardNo,"卡编号不能为空")){
                    return;
                }
                // 查询用户名是否已存在
                if (alert2(cardNo,"卡编号不存在,请重新输入", GlobalCache.getSchoolCardCache())){
                    return;
                }

                // 获取密码
                String cardPwd = updateCardPwdText.getText().trim();
                if (alert(cardPwd, "卡密码不能为空")){
                    return;
                }
                if (alert(cardPwd, "卡密码长度不能小于6位",6)){
                    return;
                }
                if (alert("卡密码只能为数字或英文", RegexUtils.isNumberOrEnglish(cardPwd))){
                    return;
                }

                // 修改卡密码
                try {
                    SchoolCardController.updateSchoolCard(cardNo,cardPwd);
                }catch (Exception ex){
                    ex.printStackTrace();
                    alert("警告!!!出现异常了");
                    return;
                }

                // 创建表格
                table.setModel(new DefaultTableModel(getSchoolCardMap(),titles));
                // 清除数据
                clearUpdateCard();
            }
        });
    }

    private void clearUpdateCard() {
        updateCardNoText.setText(null);
        updateCardPwdText.setText(null);
    }

    /**
     * 删除
     */
    private void createDelCardListener() {
        deleteBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String cardNo = deleteCardNoText.getText().trim();
                if (alert(cardNo, "卡编号不能为空")){
                    return;
                }
                if (alert2(cardNo, "卡编号不存在,请重新输入",GlobalCache.getSchoolCardCache())){
                    return;
                }

                // 删除
                try {
                    SchoolCardController.delSchoolCard(cardNo);
                }catch (Exception ex){
                    ex.printStackTrace();
                    alert("警告!!!出现异常了");
                    return;
                }

                // 创建表格
                table.setModel(new DefaultTableModel(getSchoolCardMap(),titles));
                // 清除数据
                clearDelCard();
            }
        });
    }

    private void clearDelCard() {
        deleteCardNoText.setText(null);
    }

    /**
     * 挂失
     */
    private void createLossCardListener() {
        disAbleBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String cardNo = disAbleCardNoText.getText().trim();
                if (alert(cardNo, "卡编号不能为空")){
                    return;
                }
                if (alert2(cardNo, "卡编号不存在,请重新输入",GlobalCache.getSchoolCardCache())){
                    return;
                }

                // 挂失
                try {
                    SchoolCardController.lossSchoolCard(cardNo);
                }catch (Exception ex){
                    ex.printStackTrace();
                    alert("警告!!!出现异常了");
                    return;
                }

                // 创建表格
                table.setModel(new DefaultTableModel(getSchoolCardMap(),titles));
                // 清除数据
                clearLossCard();
            }
        });
    }

    private void clearLossCard() {
        disAbleCardNoText.setText(null);
    }

    /**
     * 开卡
     */
    private void createStartCardListener() {
        startCardBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 获取卡密码
                String pwd = startCardPwdText.getText().trim();
                if (alert(pwd, "卡密码不能为空")){
                    return;
                }
                if (alert(pwd, "卡密码长度不能小于6位",6)){
                    return;
                }
                if (alert("卡密码只能为数字或英文", RegexUtils.isNumberOrEnglish(pwd))){
                    return;
                }
                // 获取卡余额
                String balance = startCardYMoneyText.getText().trim();
                if (alert(balance,"卡余额不能为空")){
                    return;
                }
                // 获取卡用户名
                String username = startCardUserText.getText().trim();
                if (alert(username,"卡用户名不能为空")){
                    return;
                }

                // 查询用户名是否已存在
                if (alert(username,"卡用户名已存在,请修改用户名", GlobalCache.getSchoolCardCache())){
                    return;
                }

                // 插入校园卡数据
                try {
                    SchoolCardController.insertSchoolCard(pwd,balance,username);
                }catch (Exception ex){
                    alert("警告!!!出现异常了");
                    return;
                }
                // 创建表格
                table.setModel(new DefaultTableModel(getSchoolCardMap(),titles));
                // 清除数据
                clearStartCard();
            }
        });
    }

    private void clearStartCard() {
        startCardPwdText.setText(null);
        startCardYMoneyText.setText(null);
        startCardUserText.setText(null);
    }

    public boolean alert(String value,String content){
        if ("".equals(value)){
            // 禁用组件
            enableComponents(false);
            // 用户名不能为空
            Layer.alert(content);
            Layer.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    e.getWindow().setVisible(false);
                    enableComponents(true);
                }
            });
            return true;
        }
        return false;
    }

    public void alert(String content){
        // 禁用组件
        enableComponents(false);
        // 用户名不能为空
        Layer.alert(content);
        Layer.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().setVisible(false);
                enableComponents(true);
            }
        });
    }

    public boolean alert(String value,String content,int length){
        if (value.length() < length){
            // 禁用组件
            enableComponents(false);
            // 用户名不能为空
            Layer.alert(content);
            Layer.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    e.getWindow().setVisible(false);
                    enableComponents(true);
                }
            });
            return true;
        }
        return false;
    }

    public boolean alert(String value,String content, Map<Long,SchoolCard> map){
        boolean flag = false;
        Set<Map.Entry<Long, SchoolCard>> entries = map.entrySet();
        for (Map.Entry<Long, SchoolCard> entry : entries) {
            SchoolCard schoolCard = entry.getValue();
            if (schoolCard.getUserName().equals(value)){
                flag = true;
                break;
            }
        }

        if (flag){
            // 禁用组件
            enableComponents(false);
            // 用户名不能为空
            Layer.alert(content);
            Layer.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    e.getWindow().setVisible(false);
                    enableComponents(true);
                }
            });
            return true;
        }
        return false;
    }

    public boolean alertNoUser(String value,String content, Map<Long,SchoolCard> map){
        boolean flag = true;
        Set<Map.Entry<Long, SchoolCard>> entries = map.entrySet();
        for (Map.Entry<Long, SchoolCard> entry : entries) {
            SchoolCard schoolCard = entry.getValue();
            if (schoolCard.getUserName().equals(value)){
                flag = false;
                break;
            }
        }

        if (flag){
            // 禁用组件
            enableComponents(false);
            // 用户名不能为空
            Layer.alert(content);
            Layer.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    e.getWindow().setVisible(false);
                    enableComponents(true);
                }
            });
            return true;
        }
        return false;
    }

    public boolean alert2(String value,String content, Map<Long,SchoolCard> map){
        boolean flag = false;
        Set<Map.Entry<Long, SchoolCard>> entries = map.entrySet();
        for (Map.Entry<Long, SchoolCard> entry : entries) {
            SchoolCard schoolCard = entry.getValue();
            if (schoolCard.getCardNo().toString().equals(value)){
                flag = true;
                break;
            }
        }

        if (!flag){
            // 禁用组件
            enableComponents(false);
            // 用户名不能为空
            Layer.alert(content);
            Layer.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    e.getWindow().setVisible(false);
                    enableComponents(true);
                }
            });
            return true;
        }
        return false;
    }

    public boolean alert(String content,boolean flag){
        if (!flag){
            // 禁用组件
            enableComponents(false);
            // 用户名不能为空
            Layer.alert(content);
            Layer.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    e.getWindow().setVisible(false);
                    enableComponents(true);
                }
            });
            return true;
        }
        return false;
    }

    private void enableComponents(boolean flag) {
        startCardPwdText.setEnabled(flag);
        startCardYMoneyText.setEnabled(flag);
        startCardUserText.setEnabled(flag);
        startCardBtn.setEnabled(flag);

        disAbleCardNoText.setEnabled(flag);
        disAbleBtn.setEnabled(flag);

        deleteCardNoText.setEnabled(flag);
        deleteBtn.setEnabled(flag);

        addCardNoText.setEnabled(flag);
        addYMoneyText.setEnabled(flag);
        addBtn.setEnabled(flag);
        decBtn.setEnabled(flag);

        updateCardNoText.setEnabled(flag);
        updateCardPwdText.setEnabled(flag);
        updateBtn.setEnabled(flag);

        hisCardNoText.setEnabled(flag);
        hisAddBtn.setEnabled(flag);
        hisDecBtn.setEnabled(flag);

        queryCardNoText.setEnabled(flag);
        queryCardUserText.setEnabled(flag);
        queryBtn.setEnabled(flag);
    }

    private void createTools() {
        // 创建卡编号标签
        createLabel();
        // 创建开卡行
        createCardFirstRow();
        // 创建挂失行
        createCardSecondRow();
        // 创建删除行
        createCardThirdRow();
        // 创建消费充值行
        createCardFourRow();
        // 创建修改密码行
        createCardFifthRow();
        // 创建充值消费记录行
        createCardSixRow();
        // 创建查询行
        createCardSevenRow();
    }

    private void createCardSevenRow() {
        queryCardNoText = new TextField();
        queryCardNoText.setBounds(10,660,160,30);
        queryCardNoText.setFont(new Font("宋体",Font.BOLD,16));

        queryCardUserText = new TextField();
        queryCardUserText.setBounds(610,660,160,30);
        queryCardUserText.setFont(new Font("宋体",Font.BOLD,16));

        queryBtn = new Button("查询");
        queryBtn.setBounds(825,660,160,30);
        queryBtn.setFont(new Font("宋体",Font.BOLD,16));

        container.add(queryBtn);
        container.add(queryCardNoText);
        container.add(queryCardUserText);
    }

    private void createCardSixRow() {
        hisCardNoText = new TextField();
        hisCardNoText.setBounds(10,620,160,30);
        hisCardNoText.setFont(new Font("宋体",Font.BOLD,16));

        hisAddBtn = new Button("充值记录");
        hisAddBtn.setBounds(610,620,160,30);
        hisAddBtn.setFont(new Font("宋体",Font.BOLD,16));

        hisDecBtn = new Button("消费记录");
        hisDecBtn.setBounds(825,620,160,30);
        hisDecBtn.setFont(new Font("宋体",Font.BOLD,16));

        container.add(hisDecBtn);
        container.add(hisAddBtn);
        container.add(hisCardNoText);
    }

    private void createCardFifthRow() {
        updateCardNoText = new TextField();
        updateCardNoText.setBounds(10,580,160,30);
        updateCardNoText.setFont(new Font("宋体",Font.BOLD,16));

        updateCardPwdText = new TextField();
        updateCardPwdText.setBounds(210,580,160,30);
        updateCardPwdText.setFont(new Font("宋体",Font.BOLD,16));

        updateBtn = new Button("修改密码");
        updateBtn.setBounds(825,580,160,30);
        updateBtn.setFont(new Font("宋体",Font.BOLD,16));

        container.add(updateBtn);
        container.add(updateCardNoText);
        container.add(updateCardPwdText);
    }

    private void createCardFourRow() {
        addCardNoText = new TextField();
        addCardNoText.setBounds(10,540,160,30);
        addCardNoText.setFont(new Font("宋体",Font.BOLD,16));

        addYMoneyText = new TextField();
        addYMoneyText.setBounds(410,540,160,30);
        addYMoneyText.setFont(new Font("宋体",Font.BOLD,16));

        addBtn = new Button("卡充值");
        addBtn.setBounds(610,540,160,30);
        addBtn.setFont(new Font("宋体",Font.BOLD,16));

        decBtn = new Button("卡消费");
        decBtn.setBounds(825,540,160,30);
        decBtn.setFont(new Font("宋体",Font.BOLD,16));

        container.add(addCardNoText);
        container.add(addYMoneyText);
        container.add(addBtn);
        container.add(decBtn);
    }

    private void createCardThirdRow() {
        deleteCardNoText = new TextField();
        deleteCardNoText.setBounds(10,500,160,30);
        deleteCardNoText.setFont(new Font("宋体",Font.BOLD,16));

        deleteBtn = new Button("删除");
        deleteBtn.setBounds(825,500,160,30);
        deleteBtn.setFont(new Font("宋体",Font.BOLD,16));

        container.add(deleteBtn);
        container.add(deleteCardNoText);
    }

    /**
     * 挂失
     */
    private void createCardSecondRow() {
        disAbleCardNoText = new TextField();
        disAbleCardNoText.setBounds(10,460,160,30);
        disAbleCardNoText.setFont(new Font("宋体",Font.BOLD,16));

        disAbleBtn = new Button("挂失");
        disAbleBtn.setBounds(825,460,160,30);
        disAbleBtn.setFont(new Font("宋体",Font.BOLD,16));
        container.add(disAbleBtn);
        container.add(disAbleCardNoText);
    }

    /**
     * 开卡
     */
    private void createCardFirstRow() {
        startCardNoText = new TextField();
        startCardNoText.setBounds(10,420,160,30);
        startCardNoText.setFont(new Font("宋体",Font.BOLD,16));

        startCardPwdText = new TextField();
        startCardPwdText.setBounds(210,420,160,30);
        startCardPwdText.setFont(new Font("宋体",Font.BOLD,16));

        startCardYMoneyText = new TextField();
        startCardYMoneyText.setBounds(410,420,160,30);
        startCardYMoneyText.setFont(new Font("宋体",Font.BOLD,16));

        startCardUserText = new TextField();
        startCardUserText.setBounds(610,420,160,30);
        startCardUserText.setFont(new Font("宋体",Font.BOLD,16));

        startCardBtn = new Button("开卡");
        startCardBtn.setBounds(825,420,160,30);
        startCardBtn.setFont(new Font("宋体",Font.BOLD,16));
        container.add(startCardBtn);

        container.add(startCardNoText);
        container.add(startCardPwdText);
        container.add(startCardYMoneyText);
        container.add(startCardUserText);
        container.add(startCardBtn);
    }

    private void createLabel() {
        Label cardNo = new Label("卡编号", Label.RIGHT);
        cardNo.setBounds(50,380,65,40);
        cardNo.setFont(new Font("宋体",Font.BOLD,16));

        Label cardPwd = new Label("卡密码", Label.RIGHT);
        cardPwd.setBounds(250,380,65,40);
        cardPwd.setFont(new Font("宋体",Font.BOLD,16));

        Label cardYMoney = new Label("卡余额", Label.RIGHT);
        cardYMoney.setBounds(450,380,65,40);
        cardYMoney.setFont(new Font("宋体",Font.BOLD,16));

        Label cardUser = new Label("用户名", Label.RIGHT);
        cardUser.setBounds(650,380,65,40);
        cardUser.setFont(new Font("宋体",Font.BOLD,16));

        Label cardOper = new Label("操作", Label.RIGHT);
        cardOper.setBounds(860,380,65,40);
        cardOper.setFont(new Font("宋体",Font.BOLD,16));

        container.add(cardNo);
        container.add(cardPwd);
        container.add(cardYMoney);
        container.add(cardUser);
        container.add(cardOper);
    }

    private void createTable() {
        Object[][] datas = getSchoolCardMap();;
//        titles = {"卡编号","卡密码","卡余额","是否挂失","用户名"};
        table = new JTable(datas, titles);
        table.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
        table.setVisible(true);
        table.setBounds(0,0,980,300);
        table.getTableHeader().setPreferredSize(new Dimension(table.getWidth(),50));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("宋体",Font.BOLD,16));
        table.setFont(new Font("宋体",Font.BOLD,16));
        table.setEnabled(false);

        JScrollPane panel = new JScrollPane();
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
        panel.setBounds(10,80,980,300);
        panel.setVisible(true);

        panel.setViewportView(table);
        container.add(panel);
        container.setBackground(table.getTableHeader().getBackground());
    }

    /**
     * 获取开卡用户信息
     */
    private Object[][] getSchoolCardMap() {
        BeanUtils<SchoolCard> utils = new BeanUtils<>();
        if (GlobalCache.getSchoolCardCache().size() == 0){
            GlobalCache.setSchoolCardCache(utils.
                    getBeanMap(
                            "schoolcard.txt",
                            SchoolCard.class));
        }
        Object[][] data = getObjects();
        return data;
    }

    private Object[][] getObjects() {
        Map<Long, SchoolCard> notDelSchoolMap = GlobalCache.getNotDelSchoolMap();
        Object[][] data = new Object[notDelSchoolMap.size()][5];
        // 数据重新整理
        HashMap<Long, SchoolCard> map = getDistinctSchoolCardMap(notDelSchoolMap);
        for (int i = 0; i < data.length; i++) {
            Object[] datum = data[i];
            SchoolCard schoolCard = map.get(Long.parseLong(String.valueOf(i)));
            for (int j = 0; j < datum.length; j++) {
                if (j == 0){
                    data[i][j] = schoolCard.getCardNo();
                    continue;
                }
                if (j == 1){
                    data[i][j] = schoolCard.getCardPwd();
                    continue;
                }
                if (j == 2){
                    data[i][j] = schoolCard.getBalance();
                    continue;
                }
                if (j == 3){
                    data[i][j] = schoolCard.getLossFlag();
                    continue;
                }
                if (j == 4){
                    data[i][j] = schoolCard.getUserName();
                }
            }
        }
        return data;
    }

    private HashMap<Long, SchoolCard> getDistinctSchoolCardMap(Map<Long, SchoolCard> schoolCardCache) {
        HashMap<Long, SchoolCard> map = new HashMap<>();
        Set<Long> set = schoolCardCache.keySet();
        Object[] obj = set.toArray();
        Arrays.sort(obj);
        long count = 0;
        for (int i = 0; i < obj.length; i++) {
            Object key = obj[i];
            map.put(count,schoolCardCache.get(key));
            count ++;
        }
        return map;
    }

    private void createContainer() {
        container = new Panel();
        container.setLayout(null);
        container.setSize(new Dimension(1000,800));
        container.setVisible(true);
    }

    private void afterProsser() {
        this.add(container);
    }

    private void createTitle() {
        Label title = new Label("校园卡列表", Label.CENTER);
        title.setBounds(0,20,1000,60);
        title.setFont(new Font("宋体",Font.BOLD,25));
        container.add(title);
    }
}
