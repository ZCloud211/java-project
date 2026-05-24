package ui;

import app.Main;
import model.GameBoard;
import model.GameState;
import model.SaveManager;
import model.SaveManager.SaveData;
import model.SessionManager;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    StatusPanel statusPanel;
    BoardPanel boardPanel;
    JButton startButton;
    JButton stopButton;
    JButton resetButton;
    JButton saveButton;
    JButton loadButton;
    JButton themeButton;

    JButton musicBtn;//音乐按钮
    MusicPlayer musicPlayer;

    int offSetX;
    int offSetY;
    int width;
    int height;
    String username;

    public ControlPanel(StatusPanel statusPanel, BoardPanel boardPanel, int offSetX, int offSetY, int width, int height) {
        this(statusPanel, boardPanel, offSetX, offSetY, width, height, "");
    }

    public ControlPanel(StatusPanel statusPanel, BoardPanel boardPanel, int offSetX, int offSetY, int width, int height, String username) {
        this.setLayout(null);
        this.setBounds(offSetX, offSetY, width, height);
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.width = width;
        this.height = height;
        this.statusPanel = statusPanel;
        this.boardPanel = boardPanel;
        this.username = username;

        this.setBackground(new Color(250, 243, 217));

        this.startButton = createStyledButton("开始");
        this.stopButton = createStyledButton("暂停");
        this.resetButton = createStyledButton("重置");
        this.saveButton = createStyledButton("存档");
        this.loadButton = createStyledButton("读档");
        this.themeButton = createStyledButton("主题");

        int btnWidth = 120;
        int btnHeight = 45;
        int totalBtnWidth = btnWidth * 5 + 20 * 4;
        int startX = (width - totalBtnWidth) / 2;
        int y = (height - btnHeight) / 2;

        stopButton.setBounds(startX, y, btnWidth, btnHeight);
        this.add(stopButton);
        stopButton.addActionListener(e -> {
            statusPanel.stopTimer();
            statusPanel.setStatus("已暂停");
            boardPanel.stopGame();
        });

        startButton.setBounds(startX + btnWidth + 20, y, btnWidth, btnHeight);
        this.add(startButton);
        startButton.addActionListener(e -> {
            boardPanel.startGame();
            statusPanel.setStatus("游戏中");
            statusPanel.startTimer();
        });

        resetButton.setBounds(startX + (btnWidth + 20) * 2, y, btnWidth, btnHeight);
        this.add(resetButton);
        resetButton.addActionListener(e -> {
            boardPanel.stopGame();
            statusPanel.stopTimer();
            statusPanel.resetGame();
        });

        saveButton.setBounds(startX + (btnWidth + 20) * 3, y, btnWidth, btnHeight);
        this.add(saveButton);
        saveButton.addActionListener(e -> {
            saveGame();
        });

        loadButton.setBounds(startX + (btnWidth + 20) * 4, y, btnWidth, btnHeight);
        this.add(loadButton);
        loadButton.addActionListener(e -> {
            loadGame();
        });

        musicPlayer = new MusicPlayer();
        musicPlayer.play("resource/bgm.wav");

        musicBtn = createStyledButton("音乐 ON");
        musicBtn.setBounds(startX - (btnWidth + 40),  y, btnWidth+20, btnHeight); // 放最左边
        musicBtn.addActionListener(e -> {
            musicPlayer.toggle();
            musicBtn.setText(musicPlayer.isPlaying() ? "音乐 ON" : "音乐 OFF");
        });
        this.add(musicBtn);

        themeButton.setBounds(startX + (btnWidth + 20) * 5, y, btnWidth, btnHeight);
        this.add(themeButton);
        themeButton.addActionListener(e -> {
            String[] options = {"水果", "动物", "物品"};
            int choice =
                    JOptionPane.showOptionDialog(
                            null,
                            "选择主题",
                            "主题切换",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );
            switch (choice) {
                case 0:
                    boardPanel.loadTheme("fruits");
                    break;
                case 1:
                    boardPanel.loadTheme("animals");
                    break;
                case 2:
                    boardPanel.loadTheme("objects");
                    break;
            }
        });
        this.add(themeButton);
    }

    private void saveGame() {
        String saveUsername = username;
        if (saveUsername == null || saveUsername.isEmpty()) {
            saveUsername = "guest";
        }

        SaveManager saveManager = new SaveManager();
        GameState state = new GameState(
                statusPanel.getScore(),
                statusPanel.getSeconds(),
                statusPanel.getComboCount(),
                statusPanel.getMaxCombo(),
                boardPanel.getModeInt()
        );

        boolean success = saveManager.saveGame(saveUsername, boardPanel.getGameBoard(), state);
        if (success) {
            JOptionPane.showMessageDialog(this, "存档成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "存档失败！", "提示", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadGame() {
        String saveUsername = username;
        if (saveUsername == null || saveUsername.isEmpty()) {
            saveUsername = "guest";
        }

        SaveManager saveManager = new SaveManager();
        if (!saveManager.hasSaveFile(saveUsername, 0)) {
            JOptionPane.showMessageDialog(this, "没有找到存档！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SaveData saveData = saveManager.loadGame(saveUsername, 0);
        if (!saveData.isValid()) {
            JOptionPane.showMessageDialog(this, "存档无效", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boardPanel.setGameBoard(saveData.getBoard());
        GameState state = saveData.getState();
        statusPanel.setScore(state.getScore());
        statusPanel.setTime(state.getRemainingTime());
        statusPanel.setComboCount(state.getComboCount());
        statusPanel.setMaxCombo(state.getMaxCombo());
        statusPanel.setStatus("游戏中");

        JOptionPane.showMessageDialog(this, "读档成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton createStyledButton(String text) {
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
        btn.setFont(new Font("微软雅黑", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
