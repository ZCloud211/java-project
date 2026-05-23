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
    //连消机制
    int comboCount = 0;        // 连消次数
    long lastElimTime = 0;     // 上次消除时间
    BoardPanel boardPanel;
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0, 0, new Color(149, 189, 102),
                width, 0, new Color(146, 188, 95)));
        g2.fillRect(0, 0, width, height);
        // 字体颜色改白色
    }

    public StatusPanel(int width, int height) {
        this.setLayout(null);
        this.setBounds(0, 0, width, height);
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

        statusLabel.setForeground(new Color(255,248,230));
        timeLabel.setForeground(new Color(255,248,230));
        scoreLabel.setForeground(new Color(255,248,230));
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
        scoreLabel.setBounds(width *3/4, time_y, 350, scoreLabelSize.height);

        this.add(statusLabel);
        this.add(timeLabel);
        this.add(scoreLabel);
    }
    public void addScore(int point) {//增加分数
        long now = System.currentTimeMillis();
        if (now - lastElimTime <= 4000) {
            comboCount++;
        }else {
            comboCount = 1;
        }
        lastElimTime = now;
        int earned;
        if (comboCount >= 4) {
            earned = 20;
            scoreLabel.setText("分数：" + score + "  ★连消x" + comboCount + "!");
        } else {
            earned = point; // 普通加10分
            scoreLabel.setText("分数：" + score);
        }
        score += earned;
        scoreLabel.setText("分数：" + score + (comboCount >= 3 ? "  ★连消x" + comboCount : ""));
        Dimension size = scoreLabel.getPreferredSize();
        scoreLabel.setSize(size.width + 50, size.height);
        repaint();
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
        int y = (height - statusLabel.getPreferredSize().height) / 2;
        statusLabel.setBounds((width - 400) / 6, y, 400, statusLabel.getPreferredSize().height + 10);
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
            score = 0;
            scoreLabel.setText("分数：0");
            timeLabel.setText("00:00:00");
            stopTimer();
            setStatus("准备就绪");
            boardPanel.resetBoard();
        }else if (choice == JOptionPane.NO_OPTION) {
            boardPanel.startGame();

            startTimer();
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        scoreLabel.setText("分数：" + score);
    }

    public int getSeconds() {
        return hours * 3600 + minutes * 60 + seconds;
    }

    public void setTime(int totalSeconds) {
        this.hours = totalSeconds / 3600;
        this.minutes = (totalSeconds % 3600) / 60;
        this.seconds = totalSeconds % 60;
        timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    public int getComboCount() {
        return comboCount;
    }

    public void setComboCount(int comboCount) {
        this.comboCount = comboCount;
    }

    public int getMaxCombo() {
        return comboCount;
    }

    public void setMaxCombo(int maxCombo) {
        this.comboCount = maxCombo;
    }

    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    public void updateSize(int width, int height) {//更新大小
        this.width = width;
        this.height = height;
        this.setBounds(0, 0, width, height);
        
        Dimension size = statusLabel.getPreferredSize();
        Dimension timeLabelSize = timeLabel.getPreferredSize();
        Dimension scoreLabelSize = scoreLabel.getPreferredSize();

        int x = (width - size.width) / 6 ;
        int y = (height - size.height) / 2;
        int time_x = (width - timeLabelSize.width) * 1 / 2;
        int time_y = (height - timeLabelSize.height) * 2 /3;
        statusLabel.setBounds(x, y, size.width, size.height);
        timeLabel.setBounds(time_x, time_y, timeLabelSize.width, timeLabelSize.height);
        statusLabel.setBounds(x, y, 300, size.height+10);
        
        repaint();
    }
}