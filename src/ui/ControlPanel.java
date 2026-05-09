package ui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    StatusPanel statusPanel;
    JButton startButton;
    JButton stopButton;
    JButton resetButton;

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
        this.startButton = new JButton("开始 🎮");//开始按钮
        this.statusPanel = statusPanel;
        this.stopButton = new JButton("暂停 ⏸️");//停止按钮
        this.resetButton = new JButton("重置 🔄");//重置按钮

        int btnWidth = 150;
        int btnHeight = 45;
        int x = (width - btnWidth) / 2;
        int y = (height - btnHeight) / 2;
        stopButton.setBounds(x-2*btnWidth, y, btnWidth, btnHeight);
        stopButton.setFont(new Font("微软雅黑", Font.BOLD, 25));
        stopButton.setFocusPainted(false);
        this.add(stopButton);
        this.stopButton.addActionListener(e -> {
            statusPanel.stopTimer();
            statusPanel.setStatus("已暂停");
            statusPanel.setFont(new Font("微软雅黑", Font.BOLD, 30));
            boardPanel.stopGame();//停止游戏
        });

        resetButton.setBounds(x+2*btnWidth, y, btnWidth, btnHeight);
        resetButton.setFont(new Font("微软雅黑", Font.BOLD, 25));
        resetButton.setFocusPainted(false);
        this.add(resetButton);
        this.resetButton.addActionListener(e -> {
            boardPanel.stopGame();
            statusPanel.stopTimer();
            statusPanel.resetGame();//重置游戏
        });

        startButton.setBounds(x, y, btnWidth, btnHeight);
        startButton.setFont(new Font("微软雅黑", Font.BOLD, 25));
        startButton.setFocusPainted(false);
        this.add(startButton);
        this.startButton.addActionListener(e -> {
            boardPanel.startGame();//开始游戏
            statusPanel.setStatus("游戏中");
            statusPanel.startTimer();
            statusPanel.setFont(new Font("微软雅黑", Font.BOLD, 30));

        });
    }

}
