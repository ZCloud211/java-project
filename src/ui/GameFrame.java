package ui;

import model.Cell;
import model.GameBoard;
import model.Position;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame{
    int width;
    int height;
    String title;
    StatusPanel statusPanel;
    ControlPanel controlPanel;


    public GameFrame(String title, int width, int height, String mode) {
        super(title);
        this.setResizable(false);

        int frameWidth = 1200;  // 横屏宽度
        int frameHeight = 900;  // 横屏高度
        int boardSize = 600;    // 棋盘大小
        int boardX = (frameWidth - boardSize) / 2; // 棋盘居中X
        int boardY = 100;       // 棋盘Y，留出上方状态栏

        this.statusPanel = new StatusPanel(frameWidth, 100);
        this.setSize(frameWidth, frameHeight);

        BoardPanel boardPanel;
        if (mode.equals("easy")) {
            int size = 9;
            Cell[][] board = new Cell[size + 2][size + 2];
            for (int i = 0; i < size + 2; i++)
                for (int j = 0; j < size + 2; j++)
                    board[i][j] = new Cell(new Position(i, j), true, 0);
            for (int i = 1; i <= 4; i++)
                for (int j = 1; j <= 4; j++)
                    board[i][j] = new Cell(new Position(i, j), false, 1);
            for (int i = 6; i <= 9; i++)
                for (int j = 6; j <= 9; j++)
                    board[i][j] = new Cell(new Position(i, j), false, 1);
            boardPanel = new BoardPanel(new GameBoard(size + 2, size + 2, board), boardX, boardY, boardSize, boardSize, this.statusPanel, mode);
        } else {
            int size = 10;
            Cell[][] board = new Cell[size + 2][size + 2];
            for (int i = 0; i < size + 2; i++)
                for (int j = 0; j < size + 2; j++)
                    board[i][j] = new Cell(new Position(i, j), true, 0);
            for (int i = 1; i <= 10; i++)
                for (int j = 1; j <= 10; j++)
                    board[i][j] = new Cell(new Position(i, j), false, 1);
            boardPanel = new BoardPanel(new GameBoard(size + 2, size + 2, board), boardX, boardY, boardSize, boardSize, this.statusPanel, mode);
        }
        this.controlPanel = new ControlPanel(statusPanel, boardPanel, 0, boardY + boardSize + 10, frameWidth, 80);
        this.statusPanel.setBoardPanel(boardPanel);

        this.title = title;
        this.width = frameWidth;
        this.height = frameHeight;
        this.setLayout(null);
        this.setSize(frameWidth, frameHeight);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 状态栏在上，宽度铺满
        this.statusPanel.setBounds(0, 0, frameWidth, 100);

        // 按钮栏在下，宽度铺满
        this.controlPanel = new ControlPanel(statusPanel, boardPanel, 0, boardY + boardSize, frameWidth, 100);

        this.getContentPane().setBackground(new Color(255, 248, 220));

        this.add(this.statusPanel);
        this.add(this.controlPanel);
        this.add(boardPanel);
        this.setVisible(true);
        SwingUtilities.invokeLater(() -> {
            statusPanel.updateSize(frameWidth, 100);
        });
    }

}