package ui;

import javax.swing.*;
import java.awt.*;


public class StatusPanel extends JPanel {
    JLabel statusLabel;
    JLabel timeLabel;
    JLabel scoreLabel;
    int score=0;
    Timer timer;
    int seconds;
    int minutes;
    int hours;
    int offSetX;
    int offSetY;
    int width;
    int height;
    BoardPanel boardPanel;

    public StatusPanel(int offSetX, int offSetY,int width, int height) {
        this.setLayout(null);
        this.setBounds(offSetX, offSetY, width, height);
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.width = width;
        this.height = height;
        statusLabel = new JLabel("准备就绪");//状态标签
        timeLabel = new JLabel("00:00:00");//时间标签
        scoreLabel = new JLabel("分数：0");//分数标签

        timer = new Timer(1000, e -> {
            seconds++;
            if (seconds == 60) {
                minutes++;
                seconds = 0;
                if (minutes == 60) {
                    minutes = 0;
                    hours++;
                }
            }
            timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        });

        statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 30));
        timeLabel.setFont(new Font("微软雅黑", Font.BOLD, 40));
        scoreLabel.setFont(new Font("微软雅黑", Font.BOLD, 25));

        Dimension size = statusLabel.getPreferredSize();
        Dimension timeLabelSize = timeLabel.getPreferredSize();
        Dimension scoreLabelSize = scoreLabel.getPreferredSize();

        int x = (width - size.width) / 6 ;
        int y = (height - size.height) / 2;
        int time_x = (width - timeLabelSize.width) * 1 / 2;
        int time_y = (height - timeLabelSize.height) * 2 /3;
        statusLabel.setBounds(x, y, size.width, size.height);
        timeLabel.setBounds(time_x, time_y, timeLabelSize.width, timeLabelSize.height);
        scoreLabel.setBounds(width - 160, time_y, 250, scoreLabelSize.height);//显示分数

        this.add(statusLabel);
        this.add(timeLabel);
        this.add(scoreLabel);
    }
    public void addScore(int point) {//增加分数
        score += point;
        scoreLabel.setText("分数：" + score);
        Dimension size = scoreLabel.getPreferredSize();
        scoreLabel.setSize(size.width, size.height);
        repaint();
    }

    public void setStatus(String text) {//设置状态
        statusLabel.setText(text);
        Dimension size = statusLabel.getPreferredSize();
        int x = (width - size.width) / 6;
        int y = (height - size.height) / 2;
        statusLabel.setBounds(x, y, size.width, size.height);
        repaint();

    }
    public void startTimer() {
        timer.start();
    }

    public void stopTimer() {
        timer.stop();
    }

    public void resetGame() {
        String[] options = {"是", "否"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "确定要重新开始吗？",
                "重新开始",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (choice == JOptionPane.YES_OPTION) {
            hours = 0;
            minutes = 0;
            seconds = 0;
            addScore(0);
            timeLabel.setText("00:00:00");
            stopTimer();
            addScore(0);
            setStatus("准备就绪");
        }else if (choice == JOptionPane.NO_OPTION) {
            boardPanel.startGame();

            startTimer();
        }
    }

    public int getScore() {
        return score;
    }
}