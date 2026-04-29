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
        BoardPanel boardPanel;
        if (mode.equals("easy")) {
            int size = 9;
            Cell[][] board = new Cell[size + 2][size + 2];
            for (int i = 0; i < size + 2; i++) {
                for (int j = 0; j < size + 2; j++) {
                    board[i][j] = new Cell(new Position(i, j), true, 0); /// 边框
                }
            }
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
            boardPanel = new BoardPanel(new GameBoard(size + 2, size + 2, board), 0, 100, 800, 800);
        } else {
            int size = 10;
            Cell[][] board = new Cell[size + 2][size + 2];
            for (int i = 0; i < size + 2; i++) {
                for (int j = 0; j < size + 2; j++) {
                    board[i][j] = new Cell(new Position(i, j), true, 0); /// 边框
                }
            }
            for (int i = 1; i <= 10; i++) {
                for (int j = 1; j <= 10; j++) {
                    board[i][j] = new Cell(new Position(i, j), false, 1);
                }
            }
            boardPanel = new BoardPanel(new GameBoard(size + 2, size + 2, board), 0, 100, 800, 800);
        }
        this.title = title;
        this.width = width;
        this.height = height;
        this.setLayout(null);
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.statusPanel = new StatusPanel(0, 0, 800, 100);
        this.controlPanel = new ControlPanel(statusPanel, 0, 900, 800, 100);
        this.add(this.statusPanel);
        this.add(this.controlPanel);
        this.add(boardPanel);
    }

}
