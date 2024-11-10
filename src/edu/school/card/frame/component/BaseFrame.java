package edu.school.card.frame.component;

import java.awt.*;

public class BaseFrame extends Frame {

    public void init(String title, int width, int height, boolean visible){
        super.setLayout(null);
        super.setTitle(title);
        super.setSize(width,height);
        super.setVisible(visible);
        super.setResizable(false);
        super.setLocationRelativeTo(null);
        super.addWindowListener(new BaseWindow());
    }

}
