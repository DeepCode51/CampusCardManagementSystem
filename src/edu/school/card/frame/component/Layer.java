package edu.school.card.frame.component;

import edu.school.card.page.LoginPage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Layer {

    public static Frame frame = null;

    public static void alert(String content){
        frame = new Frame();
        frame.setResizable(false);
        frame.setTitle("错误提示");
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setAlwaysOnTop(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().setVisible(false);
            }
        });
        frame.setSize(300,200);

        Panel panel = new Panel();
        panel.setLayout(null);
        panel.setSize(300,200);

        Label label = new Label(content, FlowLayout.CENTER);
        label.setFont(new Font("宋体",Font.BOLD,20));
        label.setSize(300,200);
        panel.add(label);

        panel.setVisible(true);
        frame.add(panel);
    }
}
