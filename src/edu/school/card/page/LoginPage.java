package edu.school.card.page;

import edu.school.card.frame.component.BaseFrame;
import edu.school.card.frame.component.Layer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoginPage extends BaseFrame {

    private Panel container;
    private Label userNameLabel;
    private TextField userNameText;
    private Label passwordLabel;
    private TextField passwordText;
    private Button loginButton;
    private Button cancleButton;

    public void start(){
        init("校园卡管理系统",500,400,true);
        createComponents();
    }

    private void createComponents() {
        // 创建容器
        createContainer();
        // 创建标题
        createTitle();
        // 创建用户名
        createUserName();
        // 创建密码
        createPassword();
        // 创建登录按钮
        createLoginButton();
        // 创建取消按钮
        createCancleButton();
        // 后置处理器
        afterProsser();
        // 创建监听器
        createListener();
    }

    private void createListener() {
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 获取用户名
                String userName = userNameText.getText();
                if ("".equals(userName)){
                    // 禁用组件
                    enableComponents(false);
                    // 用户名不能为空
                    Layer.alert("用户名不能为空");
                    Layer.frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            e.getWindow().setVisible(false);
                            enableComponents(true);
                        }
                    });
                    return;
                }
                // 获取密码
                String password = passwordText.getText();
                if ("".equals(password)){
                    // 禁用组件
                    enableComponents(false);
                    // 用户名不能为空
                    Layer.alert("密码不能为空");
                    Layer.frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            e.getWindow().setVisible(false);
                            enableComponents(true);
                        }
                    });
                    return;
                }

                // 登录
                login(userName,password);
            }
        });
    }

    /**
     * 登录
     * @param userName
     * @param password
     */
    public void login(String userName, String password){
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        String path = this.getClass().getClassLoader().getResource("").getPath().substring(1);
        // 使用properties对象加载输入流
        InputStream in = null;
        try {
            in = new FileInputStream(path+"edu/school/card/conf/userInfo.properties");
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //获取key对应的value值
        String username = properties.getProperty("username");
        String pwd = properties.getProperty("password");
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (userName.equals(username)&&password.equals(pwd)){
            // 关闭当前页面
            this.setVisible(false);
            // 打开管理页面
            CardManagerPage cardManagerPage = new CardManagerPage();
            cardManagerPage.start();
            return;
        }
        // 登录失败
        Layer.alert("登录失败,用户名或密码错误");
        enableComponents(false);
        Layer.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().setVisible(false);
                enableComponents(true);
            }
        });
    }

    /**
     * 启用用组件
     */
    private void enableComponents(boolean flag){
        userNameText.setEditable(flag);
        passwordText.setEditable(flag);
        loginButton.setEnabled(flag);
        cancleButton.setEnabled(flag);
    }

    private void createLoginButton() {
        loginButton = new Button("登 录");
        loginButton.setBounds(100,280,100,50);
        loginButton.setFont(new Font("宋体",Font.BOLD,20));
        container.add(loginButton);
    }

    private void createCancleButton() {
        cancleButton = new Button("取 消");
        cancleButton.setBounds(300,280,100,50);
        cancleButton.setFont(new Font("宋体",Font.BOLD,20));
        container.add(cancleButton);
    }

    private void createPassword() {
        passwordLabel = new Label("密     码 : ", Label.RIGHT);
        passwordLabel.setBounds(0,200,200,50);
        passwordLabel.setFont(new Font("宋体",Font.BOLD,20));
        container.add(passwordLabel);

        passwordText = new TextField();
        passwordText.setEchoChar('*');
        passwordText.setBounds(200,210,200,30);
        passwordText.setFont(new Font("宋体",Font.BOLD,20));
        container.add(passwordText);
    }

    private void createUserName() {
        userNameLabel = new Label("用 户 名 : ", Label.RIGHT);
        userNameLabel.setBounds(0,150,200,50);
        userNameLabel.setFont(new Font("宋体",Font.BOLD,20));
        container.add(userNameLabel);

        userNameText = new TextField();
        userNameText.setBounds(200,160,200,30);
        userNameText.setFont(new Font("宋体",Font.BOLD,20));
        container.add(userNameText);
    }

    private void afterProsser() {
        this.add(container);
    }

    private void createTitle() {
        Label title = new Label("校园卡管理系统", Label.CENTER);
        title.setBounds(0,40,500,100);
        title.setFont(new Font("宋体",Font.BOLD,30));
        container.add(title);
    }

    private void createContainer() {
        container = new Panel();
        container.setLayout(null);
        container.setSize(new Dimension(500,400));
        container.setVisible(true);
    }
}
