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


    public GameFrame(String title, int width, int height, String mode, String username) {
        super(title);
        this.setResizable(false);
        this.username = username;

        int frameWidth = 1200;  // 横屏宽度
        int frameHeight = 900;  // 横屏高度
        int boardSize = 600;    // 棋盘大小
        int boardX = (frameWidth - boardSize) / 2; // 棋盘居中X
        int boardY = 110;       // 棋盘Y，留出上方状态栏

        this.statusPanel = new StatusPanel(frameWidth, 100);
        this.setSize(frameWidth, frameHeight);

        BoardPanel boardPanel;

        GameBoard gameBoard;

        if (mode.equals("easy")) {
            gameBoard = GameBoard.generateSimpleBoard();
        } else {
            gameBoard = GameBoard.generateHardBoard();
        }

        boardPanel = new BoardPanel(
                gameBoard,
                boardX,
                boardY,
                boardSize,
                boardSize,
                this.statusPanel,
                mode,
                username
        );

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
        this.controlPanel = new ControlPanel(statusPanel, boardPanel, 0, boardY + boardSize + 15, frameWidth, 80, username);

        this.getContentPane().setBackground(new Color(252, 245, 219));

        this.add(this.statusPanel);
        this.add(this.controlPanel);
        this.add(boardPanel);
        this.setVisible(true);
        SwingUtilities.invokeLater(() -> {
            statusPanel.updateSize(frameWidth, 100);
        });
    }
    private String username;

}