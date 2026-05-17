package ui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    StatusPanel statusPanel;
    JButton startButton;
    JButton stopButton;
    JButton resetButton;

    JButton musicBtn;//音乐按钮
    MusicPlayer musicPlayer;

    int offSetX;
    int offSetY;
    int width;
    int height;

    public ControlPanel(StatusPanel statusPanel, BoardPanel boardPanel, int offSetX, int offSetY, int width, int height) {
        this.setLayout(null);
        this.setBounds(offSetX, offSetY, width, height);
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.width = width;
        this.height = height;

        this.setBackground(new Color(253, 248, 165));

        this.startButton = createStyledButton("开始");
        this.stopButton = createStyledButton("暂停");
        this.resetButton = createStyledButton("重置");

        int btnWidth = 120;
        int btnHeight = 45;
        int x = (width - btnWidth) / 2;
        int y = (height - btnHeight) / 2;

        stopButton.setBounds(x - 2 * btnWidth, y, btnWidth, btnHeight);
        this.add(stopButton);
        this.stopButton.addActionListener(e -> {
            statusPanel.stopTimer();
            statusPanel.setStatus("已暂停");
            boardPanel.stopGame();
        });

        resetButton.setBounds(x + 2 * btnWidth, y, btnWidth, btnHeight);
        this.add(resetButton);
        this.resetButton.addActionListener(e -> {
            boardPanel.stopGame();
            statusPanel.stopTimer();
            statusPanel.resetGame();
        });

        startButton.setBounds(x, y, btnWidth, btnHeight);
        this.add(startButton);

        this.startButton.addActionListener(e -> {
            boardPanel.startGame();
            statusPanel.setStatus("游戏中");
            statusPanel.startTimer();
        });

        musicPlayer = new MusicPlayer();
        musicPlayer.play("resource/bgm.wav");

        musicBtn = createStyledButton("音乐 ON");
        musicBtn.setBounds(x + 4 * btnWidth, y, btnWidth+20, btnHeight); // 放最右边
        musicBtn.addActionListener(e -> {
            musicPlayer.toggle();
            musicBtn.setText(musicPlayer.isPlaying() ? "音乐 ON" : "音乐 OFF");
        });
        this.add(musicBtn);
    }

    private JButton createStyledButton(String text) {//自定义按钮，目前有三个按钮
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(60, 120, 40),
                            getWidth(), getHeight(), new Color(40, 90, 20)));
                } else if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(100, 180, 60),
                            getWidth(), getHeight(), new Color(70, 150, 40)));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(80, 160, 50),
                            getWidth(), getHeight(), new Color(55, 120, 30)));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 22));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

}
