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

    public BoardPanel(GameBoard gameBoard, int offSetX, int offSetY, int width, int height, StatusPanel statusPanel) {//绘制棋盘,并添加点击事件
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
                repaint();
                statusPanel.addScore(10);//增加分数
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
        repaint();
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
        g2.setColor(new Color(30, 45, 80));
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
                    g2.setColor(new Color(20, 35, 65));
                    g2.fillRoundRect(x, y, w, h, 12, 12);
                } else {
                    // 有图案格子：渐变浅色背景
                    GradientPaint gp = new GradientPaint(
                            x, y, new Color(220, 230, 255),
                            x, y + h, new Color(180, 200, 240)
                    );
                    g2.setPaint(gp);
                    g2.fillRoundRect(x, y, w, h, 12, 12);

                    // 格子边框
                    g2.setColor(new Color(150, 170, 220));
                    g2.setStroke(new BasicStroke(1));
                    g2.drawRoundRect(x, y, w, h, 12, 12);

                    // 画图片，留内边距
                    g2.drawImage(
                            imageList.get(gameBoard.getCell(i, j).getIconIndex()),
                            x + 5, y + 5, w - 10, h - 10, this
                    );

                    // 选中效果
                    if (gameBoard.getCell(i, j).getIsChosen()) {
                        // 黄色蒙层
                        g2.setColor(new Color(255, 220, 0, 80));
                        g2.fillRoundRect(x, y, w, h, 12, 12);
                        // 黄色边框
                        g2.setColor(new Color(255, 200, 0));
                        g2.setStroke(new BasicStroke(3));
                        g2.drawRoundRect(x, y, w, h, 12, 12);
                    }
                }
            }
        }

        // 连线
        if (lineVisible) {
            g2.setColor(new Color(255, 80, 80));
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
    }
}