package ui;

import app.Main;
import model.*;
import model.Rectangle;

import javax.swing.*;
import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardPanel extends JPanel {
    int offSetX;
    int offSetY;

    float eliminateAlpha = 1.0f;  // 透明度 1.0到0.0
    float eliminateScale = 1.0f;  // 缩放 1.0到0.0
    Cell animCell1 = null;        // 正在消除的格子1
    Cell animCell2 = null;        // 正在消除的格子2

    List<Image> imageList = new ArrayList<>();
    GameBoard gameBoard;
    List<Line> lineList = new ArrayList<>();
    int totalRow;
    int totalCol;
    boolean lineVisible;
    int width;
    int height;
    int cellWidth;
    int cellHeight;
    Position firstSelected = null;
    Position secondSelected = null;
    boolean animating = false;
    boolean gameStarted = false;//开始游戏后才可以点击
    boolean gameWon = false;//游戏胜利标志
    String mode;
    String username;

    private String currentTheme = "fruits";

    public Position getPositionByPoint(int x, int y) {

        int col = x / cellWidth;
        int row = y / cellHeight;
        if (row < 0 || row >= totalRow || col < 0 || col >= totalCol) {
            return null;
        }
        return new Position(row, col);
    }

    public boolean isAdjacent(Position p1, Position p2) {
        int dr = Math.abs(p1.getRow() - p2.getRow());
        int dc = Math.abs(p1.getCol() - p2.getCol());
        return dr + dc == 1;
    }

    public void showLine(List<Line> lines) {
        lineList.clear();
        lineList.addAll(lines);
        lineVisible = true;
        repaint();
    }

    public void clearLine() {
        lineVisible = false;
        lineList.clear();
        repaint();
    }
    public void stopGame() {//停止游戏
        gameStarted = false;
        gameWon = false;
        firstSelected = null;
        secondSelected = null;
    }

    public BoardPanel(GameBoard gameBoard, int offSetX, int offSetY, int width, int height, StatusPanel statusPanel, String mode, String username) {//绘制棋盘,并添加点击事件
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.setBounds(offSetX, offSetY, width, height);
        this.totalRow = gameBoard.getRowCnt();
        this.totalCol = gameBoard.getColCnt();
        this.width = width;
        this.height = height;
        this.setLayout(null);
        this.gameBoard = gameBoard;
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.cellWidth = this.width / totalCol;
        this.cellHeight = this.height / totalRow;
        this.statusPanel = statusPanel;
        this.mode = mode;
        this.username = username;


        loadTheme("fruits");


        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    StatusPanel statusPanel;//状态面板,用于显示游戏状态,时间,分数

    public void loadTheme(String theme) {
        imageList.clear();
        File dir = new File("resource/" + theme);
        File[] files = dir.listFiles();
        Arrays.sort(files);
        for (File file : files) {
            if (file.getName().endsWith(".png")) {
                ImageIcon icon = new ImageIcon(file.getPath());
                imageList.add(icon.getImage());
            }
        }
        currentTheme = theme;
        repaint();
    }

    public void handleClick(int x, int y) {
        if (!gameStarted) {//如果没有点击开始游戏按钮,则不处理点击事件
            return;
        }

        if (animating) {
            return;
        }

        Position pos = getPositionByPoint(x, y);
        if (pos == null) {
            return;
        }

        Cell clickedCell = gameBoard.getCell(pos.getRow(), pos.getCol());
        if (clickedCell == null || clickedCell.isEmpty()) {
            return;
        }

        if (firstSelected == null) {
            gameBoard.clearAllChosen();
            clickedCell.setChosen(true);
            firstSelected = pos;
            repaint();
            return;
        }

        if (firstSelected.equals(pos)) {
            clickedCell.setChosen(false);
            firstSelected = null;
            secondSelected = null;
            repaint();
            return;
        }

        secondSelected = pos;
        Cell secondCell = gameBoard.getCell(secondSelected.getRow(), secondSelected.getCol());

        secondCell.setChosen(true);
        repaint();

        Cell firstCell = gameBoard.getCell(firstSelected.getRow(), firstSelected.getCol());
        
        if (firstCell.getIconIndex() != secondCell.getIconIndex()) {
            gameBoard.clearAllChosen();
            secondCell.setChosen(true);
            firstSelected = secondSelected;
            secondSelected = null;
            statusPanel.setStatus("图案不同，请重新选择");
            repaint();
            return;
        }

        List<Line> connectionPath = gameBoard.getConnectionPath(firstSelected, secondSelected);
        
        if (connectionPath.isEmpty()) {
            gameBoard.clearAllChosen();
            firstSelected = null;
            secondSelected = null;
            statusPanel.setStatus("无法连线，请重新选择");
            repaint();
            return;
        }

        animating = true;
        showLine(connectionPath);

        MusicPlayer.playEffect("resource/eliminate.wav");
        //消除动画
        Timer timer = new Timer(100, e -> {
            animCell1 = gameBoard.getCell(firstSelected.getRow(), firstSelected.getCol());
            animCell2 = gameBoard.getCell(secondSelected.getRow(), secondSelected.getCol());

            eliminateAlpha = 1.0f;
            eliminateScale = 1.0f;

            Timer animTimer = new Timer(16, null);

            animTimer.addActionListener(e2 -> {

                eliminateAlpha -= 0.08f;
                eliminateScale -= 0.08f;

                if (eliminateAlpha <= 0) {

                    eliminateAlpha = 0;
                    eliminateScale = 0;

                    animTimer.stop();

                    // 正式消除
                    animCell1.setEmpty(true);
                    animCell2.setEmpty(true);

                    animCell1.setChosen(false);
                    animCell2.setChosen(false);

                    animCell1 = null;
                    animCell2 = null;

                    lineVisible = false;
                    lineList.clear();

                    firstSelected = null;
                    secondSelected = null;

                    animating = false;

                    eliminateAlpha = 1.0f;
                    eliminateScale = 1.0f;

                    statusPanel.addScore(10);
                    statusPanel.setStatus("游戏中");
                    checkWinCondition();
                }
                repaint();
            });
            animTimer.start();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void startGame() {//开始游戏
        gameStarted = true;
        gameWon = false;
        repaint();
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.totalRow = gameBoard.getRowCnt();
        this.totalCol = gameBoard.getColCnt();
        this.cellWidth = this.width / totalCol;
        this.cellHeight = this.height / totalRow;
        repaint();
    }

    public int getModeInt() {
        return mode.equals("easy") ? GameBoard.MODE_SIMPLE : GameBoard.MODE_HARD;
    }

    public void resetBoard() {

        gameStarted = false;
        gameWon = false;
        firstSelected = null;
        secondSelected = null;
        animating = false;
        lineVisible = false;
        lineList.clear();

        // 根据模式重新生成棋盘
        if (mode.equals("easy")) {
            this.gameBoard = GameBoard.generateSimpleBoard();
        } else {
            this.gameBoard = GameBoard.generateHardBoard();
        }

        this.totalRow = gameBoard.getRowCnt();
        this.totalCol = gameBoard.getColCnt();

        this.cellWidth = this.width / totalCol;
        this.cellHeight = this.height / totalRow;

        repaint();
    }

    public void checkWinCondition() {//检查游戏是否胜利，棋盘没有棋子时，游戏胜利
        for (int i = 0; i < gameBoard.getRowCnt(); i++) {
            for (int j = 0; j < gameBoard.getColCnt(); j++) {
                if (!gameBoard.getCell(i, j).isEmpty()) {
                    return;
                }
            }
        }
        gameWon = true;
        statusPanel.stopTimer();//胜利后暂停时间

        Main.saveScore(username, statusPanel.getScore(), mode);
        // 胜利后加返回按钮
        JButton backBtn = new JButton("返回主界面") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(100, 180, 60));
                } else {
                    g2.setColor(new Color(80, 160, 50));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("微软雅黑", Font.BOLD, 18));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 放在胜利弹窗下方居中
        int btnW = 180;
        int btnH = 45;
        int btnX = (width - btnW) / 2;
        int btnY = height / 2 + 120;
        backBtn.setBounds(btnX, btnY, btnW, btnH);

        this.add(backBtn);
        this.revalidate();
        this.repaint();

        backBtn.addActionListener(e -> {
            // 关闭游戏窗口，重新打开登录界面
            MusicPlayer.stopBgm();//停止背景音乐
            JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            gameFrame.dispose();
            // 重新启动登录界面
            SwingUtilities.invokeLater(() -> Main.main(new String[]{}));
        });
    }

    public Rectangle getRectangle(Position position) {
        int x = position.getCol() * cellWidth;
        int y = position.getRow() * cellHeight;
        return new Rectangle(x, y, cellWidth, cellHeight);
    }

    private void drawEliminateCell(Graphics2D g2, Cell cell) {
        Rectangle rec = getRectangle(cell.getPos());
        int gap = 4;
        int x = rec.getX() + gap;
        int y = rec.getY() + gap;
        int w = rec.getWidth() - gap * 2;
        int h = rec.getHeight() - gap * 2;

        // 根据缩放计算实际位置（从中心缩小）
        int scaledW = (int) (w * eliminateScale);
        int scaledH = (int) (h * eliminateScale);
        int scaledX = x + (w - scaledW) / 2;
        int scaledY = y + (h - scaledH) / 2;

        // 设置透明度
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, eliminateAlpha));
        g2.setComposite(ac);

        // 画格子背景
        g2.setColor(new Color(200, 215, 245));
        g2.fillRoundRect(scaledX, scaledY, scaledW, scaledH, 12, 12);

        // 画图片
        g2.drawImage(
                imageList.get(cell.getIconIndex()),
                scaledX + 4, scaledY + 4, scaledW - 8, scaledH - 8, this
        );

        // 恢复透明度
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    @Override
    protected void paintComponent(Graphics g) {//绘制棋盘
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int gap = 4;

        // 棋盘深色背景
        g2.setColor(new Color(250, 236, 187));
        g2.fillRect(0, 0, width, height);

        for (int i = 0; i < gameBoard.getRowCnt(); i++) {
            for (int j = 0; j < gameBoard.getColCnt(); j++) {
                Rectangle rec = getRectangle(new Position(i, j));
                int x = rec.getX() + gap;
                int y = rec.getY() + gap;
                int w = rec.getWidth() - gap * 2;
                int h = rec.getHeight() - gap * 2;

                if (gameBoard.getCell(i, j).isEmpty()||!gameStarted) {
                    // 空格子：半透明深色圆角,如果没有点开始游戏就只是画背景棋盘，不绘制棋子
                    g2.setColor(new Color(231, 206, 149));
                    g2.fillRoundRect(x, y, w, h, 12, 12);
                } else {
                    // 有图案格子：渐变浅色背景
                    GradientPaint gp = new GradientPaint(
                            x, y, new Color(245, 219, 161),
                            x, y + h, new Color(248, 219, 157)
                    );
                    g2.setPaint(gp);
                    g2.fillRoundRect(x, y, w, h, 12, 12);

                    // 格子边框
                    g2.setColor(new Color(164, 112, 53));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(x, y, w, h, 12, 12);

                    // 画图片，留内边距
                    g2.drawImage(
                            imageList.get(gameBoard.getCell(i, j).getIconIndex()),
                            x + 5, y + 5, w - 10, h - 10, this
                    );

                    // 选中效果
                    if (gameBoard.getCell(i, j).getIsChosen()) {
                        // 蒙层，选中棋子的效果
                        g2.setColor(new Color(93, 147, 227, 80));
                        g2.fillRoundRect(x, y, w, h, 12, 12);
                        // 边框，棋子的边框
                        g2.setColor(new Color(44, 83, 136));
                        g2.setStroke(new BasicStroke(3));
                        g2.drawRoundRect(x, y, w, h, 12, 12);
                    }
                }
            }
        }

        // 连线效果
        if (lineVisible) {
            g2.setColor(new Color(80, 188, 255));
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (Line line : lineList) {
                Rectangle rec1 = getRectangle(line.getCell1().getPos());
                Rectangle rec2 = getRectangle(line.getCell2().getPos());
                g2.drawLine(
                        (int) rec1.getCenterPosition().getX(),
                        (int) rec1.getCenterPosition().getY(),
                        (int) rec2.getCenterPosition().getX(),
                        (int) rec2.getCenterPosition().getY()
                );
            }
        }

        // 胜利画面
        if (gameWon) {
            int panelWidth = width;
            int panelHeight = height;

            // 半透明黑色背景，模糊棋盘的效果
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, panelWidth, panelHeight);

            // 胜利窗口的边框
            int boxWidth = 400;
            int boxHeight = 200;
            int boxX = (panelWidth - boxWidth) / 2;
            int boxY = (panelHeight - boxHeight) / 2;

            g2.setColor(new Color(145, 59, 0));
            g2.setStroke(new BasicStroke(4));
            g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

            // 游戏胜利时的窗口背景
            g2.setColor(new Color(83, 109, 244, 184));
            g2.fillRoundRect(boxX + 5, boxY + 5, boxWidth - 10, boxHeight - 10, 15, 15);

            // 游戏胜利文字
            g2.setColor(new Color(255, 215, 0));
            g2.setFont(new Font("微软雅黑", Font.BOLD, 48));
            FontMetrics fm = g2.getFontMetrics();
            String winText = "★恭喜通关★";
            int textWidth = fm.stringWidth(winText);
            g2.drawString(winText, (panelWidth - textWidth) / 2, boxY + 80);


            // 分数文字
            g2.setColor(new Color(255, 255, 255));
            g2.setFont(new Font("微软雅黑", Font.PLAIN, 24));
            String scoreText = "最终得分: " + statusPanel.getScore();
            textWidth = fm.stringWidth(scoreText);
            g2.drawString(scoreText, (panelWidth - textWidth) / 2, boxY + 130);
        }
        // 画消除动画格子
        if (animCell1 != null && animCell2 != null) {
            drawEliminateCell(g2, animCell1);
            drawEliminateCell(g2, animCell2);
        }

    }
}