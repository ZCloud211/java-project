package ui;

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

    public void showLine(Cell c1, Cell c2) {
        lineList.clear();
        lineList.add(new Line(c1, c2));
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

    public BoardPanel(GameBoard gameBoard, int offSetX, int offSetY, int width, int height, StatusPanel statusPanel, String mode) {//绘制棋盘,并添加点击事件
        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.setBounds(offSetX, offSetY, width, height);
        this.totalRow = gameBoard.getRowCnt();
        this.totalCol = gameBoard.getColCnt();
        this.width = width;
        this.height = height;
        this.setLayout(new GridLayout(this.totalRow, this.totalCol));
        this.gameBoard = gameBoard;
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.cellWidth = this.width / totalCol;
        this.cellHeight = this.height / totalRow;
        this.statusPanel = statusPanel;
        this.mode = mode;

        File dir = new File("resource");
        File[] files = dir.listFiles();
        Arrays.sort(files); //mac上面要排序图片，Windows不影响
        for (File file : files) {
            if (file.getName().endsWith(".png")) {
                ImageIcon icon = new ImageIcon(file.getPath());
                imageList.add(icon.getImage());
            }
        }
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    StatusPanel statusPanel;//状态面板,用于显示游戏状态,时间,分数

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
//消除逻辑，目前只是相同图案消除
        if (gameBoard.getCell(firstSelected.getRow(), firstSelected.getCol()).getIconIndex() ==//底层消除逻辑
                gameBoard.getCell(secondSelected.getRow(), secondSelected.getCol()).getIconIndex()) {//如果是相同图标

            animating = true;
            showLine(
                    gameBoard.getCell(firstSelected.getRow(), firstSelected.getCol()),
                    gameBoard.getCell(secondSelected.getRow(), secondSelected.getCol())
            );//显示消除线
            Timer timer = new Timer(300, e -> {//300ms后删除选中状态
                Cell c1 = gameBoard.getCell(firstSelected.getRow(), firstSelected.getCol());
                Cell c2 = gameBoard.getCell(secondSelected.getRow(), secondSelected.getCol());
                c1.setEmpty(true);
                c2.setEmpty(true);
                c1.setChosen(false);
                c2.setChosen(false);
                lineVisible = false;
                lineList.clear();
                firstSelected = null;
                secondSelected = null;
                animating = false;
                statusPanel.addScore(10);//增加分数，消除一次加10分
                checkWinCondition();
                repaint();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            gameBoard.clearAllChosen();
            secondCell.setChosen(true);
            firstSelected = secondSelected;
            secondSelected = null;
            repaint();
        }
    }

    public void startGame() {//开始游戏
        gameStarted = true;
        gameWon = false;
        repaint();
    }

    public void resetBoard() {//重置棋盘
        gameStarted = false;
        gameWon = false;
        firstSelected = null;
        secondSelected = null;
        animating = false;
        lineVisible = false;
        lineList.clear();

        int size;
        if (mode.equals("easy")) {
            size = 9;
        } else {
            size = 10;
        }

        Cell[][] board = new Cell[size + 2][size + 2];
        for (int i = 0; i < size + 2; i++) {
            for (int j = 0; j < size + 2; j++) {
                board[i][j] = new Cell(new Position(i, j), true, 0);
            }
        }
        if (mode.equals("easy")) {
            for (int i = 1; i <= 4; i++) {
                for (int j = 1; j <= 4; j++) {
                    board[i][j] = new Cell(new Position(i, j), false, 1);
                }
            }
            for (int i = 6; i <= 9; i++) {
                for (int j = 6; j <= 9; j++) {
                    board[i][j] = new Cell(new Position(i, j), false, 1);
                }
            }
        } else {
            for (int i = 1; i <= 10; i++) {
                for (int j = 1; j <= 10; j++) {
                    board[i][j] = new Cell(new Position(i, j), false, 1);
                }
            }
        }

        this.gameBoard = new GameBoard(size + 2, size + 2, board);
        this.totalRow = size + 2;
        this.totalCol = size + 2;
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
    }

    public Rectangle getRectangle(Position position) {
        int x = position.getCol() * cellWidth;
        int y = position.getRow() * cellHeight;
        return new Rectangle(x, y, cellWidth, cellHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {//绘制棋盘
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int gap = 4;

        // 棋盘深色背景
        g2.setColor(new Color(245, 211, 85));
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
                    g2.setColor(new Color(255, 191, 13));
                    g2.fillRoundRect(x, y, w, h, 12, 12);
                } else {
                    // 有图案格子：渐变浅色背景
                    GradientPaint gp = new GradientPaint(
                            x, y, new Color(255, 181, 30),
                            x, y + h, new Color(255, 203, 9)
                    );
                    g2.setPaint(gp);
                    g2.fillRoundRect(x, y, w, h, 12, 12);

                    // 格子边框
                    g2.setColor(new Color(145, 59, 0));
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
    }
}