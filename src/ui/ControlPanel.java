package ui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    StatusPanel statusPanel;
    JButton startButton;
    int offSetX;
    int offSetY;
    int width;
    int height;
    public ControlPanel(StatusPanel statusPanel, BoardPanel boardPanel, int offSetX, int offSetY,int width, int height) {
        this.setLayout(null);
        this.setBounds(offSetX, offSetY, width, height);
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.width = width;
        this.height = height;
        this.startButton = new JButton("开始");//开始按钮
        this.statusPanel = statusPanel;
        int btnWidth = 150;
        int btnHeight = 35;
        int x = (width - btnWidth) / 2;
        int y = (height - btnHeight) / 2;
        startButton.setBounds(x, y, btnWidth, btnHeight);
        startButton.setFont(new Font("微软雅黑", Font.BOLD, 25));
        startButton.setFocusPainted(false);
        this.add(startButton);
        this.startButton.addActionListener(e -> {
            boardPanel.startGame();//开始游戏
            statusPanel.setStatus("游戏中");
            statusPanel.startTimer();
            statusPanel.setFont(new Font("微软雅黑", Font.BOLD, 25));

        });
    }

}
