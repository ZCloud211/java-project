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
    public GameFrame(String title, int width, int height) {
        super(title);
        this.setResizable(false);
        int size = 3;
        Cell[][] board = new Cell[size + 2][size + 2];
        for (int i = 0; i < size + 2; i++) {
            for (int j = 0; j < size + 2; j++) {
                if (i == 0 || i == size + 1 || j == 0 || j == size + 1) {
                    board[i][j] = new Cell(new Position(i, j), true, 0); /// 边框
                }
            }
        }
        for (int i = 1; i <= size; i++) {
            for (int j = 1; j <= size; j++) {
                board[i][j] = new Cell(new Position(i, j), false, 1);
            }
        }
        BoardPanel boardPanel = new BoardPanel(new GameBoard(5, 5, board), 0, 100, 800, 800);
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
