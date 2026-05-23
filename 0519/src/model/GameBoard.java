package model;

import java.util.*;

public class GameBoard {
    int rowCnt;
    int colCnt;
    Cell[][] board;

    // 难度配置常量
    public static final int MODE_SIMPLE = 0;  // 简单模式：4×4×2
    public static final int MODE_HARD = 1;    // 困难模式：10×10

    // 简单模式配置
    public static final int SIMPLE_ROWS = 11;
    public static final int SIMPLE_COLS = 11;
    public static final int SIMPLE_PATTERN_COUNT = 5;

    // 困难模式配置
    public static final int HARD_ROWS = 10;
    public static final int HARD_COLS = 10;
    public static final int HARD_PATTERN_COUNT = 12;

    public GameBoard(int rowCnt, int colCnt, Cell[][] border) {
        this.rowCnt = rowCnt;
        this.colCnt = colCnt;
        this.board = border;
    }

   //固定模式生成棋盘，确保存在全消路径
    public static GameBoard generateBoard(int mode) {

        if (mode == MODE_SIMPLE) {
            return generateSimpleBoard();
        }

        if (mode == MODE_HARD) {
            return generateHardBoard();
        }

        return null;
    }

    /*生成保证可全消的图案序列
     * 策略：先生成可逐步消除的配对序列，然后随机打乱
     */
    private static List<Integer> generateSolvableIcons(int totalPairs, int patternCount, Random random) {
        List<Integer> icons = new ArrayList<>();

        // 先生成有序的图案对，确保有解
        List<Integer> pairList = new ArrayList<>();
        for (int i = 0; i < totalPairs; i++) {
            int iconType = (i % patternCount) + 1;  // 循环使用图案类型
            pairList.add(iconType);
            pairList.add(iconType);
        }

        // 随机打乱保证棋盘随机性
        Collections.shuffle(pairList, random);
        icons.addAll(pairList);

        return icons;
    }

    /* 填充简单模式棋盘（两个4×4不相邻区域）
     */
    private static void fillSimpleBoard(Cell[][] board, int rows, int cols, List<Integer> icons, Random random) {
        // 第一个4×4区域：左上角偏移(2,2)
        int iconIndex = 0;
        for (int i = 2; i < 6; i++) {
            for (int j = 2; j < 6; j++) {
                board[i][j] = new Cell(new Position(i, j), false, icons.get(iconIndex++));
            }
        }

        // 第二个4×4区域：右下角偏移，确保不相邻
        int startRow = rows - 6;
        int startCol = cols - 6;
        for (int i = startRow; i < startRow + 4; i++) {
            for (int j = startCol; j < startCol + 4; j++) {
                board[i][j] = new Cell(new Position(i, j), false, icons.get(iconIndex++));
            }
        }
    }

    /** 填充困难模式棋盘（10×10）
     */
    private static void fillHardBoard(Cell[][] board, int rows, int cols, List<Integer> icons, Random random) {
        int iconIndex = 0;
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < cols - 1; j++) {
                board[i][j] = new Cell(new Position(i, j), false, icons.get(iconIndex++));
            }
        }
    }

    public int getRowCnt() {
        return rowCnt;
    }

    public int getColCnt() {
        return colCnt;
    }

    public Cell getCell(int row, int col) {
        return board[row][col];
    }

    public void clearAllChosen() {
        for (int i = 0; i < rowCnt; i++) {
            for (int j = 0; j < colCnt; j++) {
                board[i][j].setChosen(false);
            }
        }
    }

    /**
     * 判断两个位置是否可以连通（最多2次转折）
     */
    public boolean canConnect(Position p1, Position p2) {
        if (p1.equals(p2)) return false;

        Cell cell1 = getCell(p1.getRow(), p1.getCol());
        Cell cell2 = getCell(p2.getRow(), p2.getCol());

        // 图案不同或为空不能连通
        if (cell1.isEmpty() || cell2.isEmpty()) return false;
        if (cell1.getIconIndex() != cell2.getIconIndex()) return false;

        // 0次转折：直线连接
        if (isLineEmpty(p1, p2)) return true;

        // 1次转折
        Position corner1 = new Position(p1.getRow(), p2.getCol());
        Position corner2 = new Position(p2.getRow(), p1.getCol());

        if (getCell(corner1.getRow(), corner1.getCol()).isEmpty()
                && isLineEmpty(p1, corner1) && isLineEmpty(corner1, p2)) {
            return true;
        }

        if (getCell(corner2.getRow(), corner2.getCol()).isEmpty()
                && isLineEmpty(p1, corner2) && isLineEmpty(corner2, p2)) {
            return true;
        }

        // 2次转折：遍历行
        for (int row = 0; row < rowCnt; row++) {
            Position turn1 = new Position(row, p1.getCol());
            Position turn2 = new Position(row, p2.getCol());

            if (row == p1.getRow() || row == p2.getRow()) continue;

            if (getCell(turn1.getRow(), turn1.getCol()).isEmpty()
                    && getCell(turn2.getRow(), turn2.getCol()).isEmpty()
                    && isLineEmpty(p1, turn1) && isLineEmpty(turn1, turn2) && isLineEmpty(turn2, p2)) {
                return true;
            }
        }

        // 2次转折：遍历列
        for (int col = 0; col < colCnt; col++) {
            Position turn1 = new Position(p1.getRow(), col);
            Position turn2 = new Position(p2.getRow(), col);

            if (col == p1.getCol() || col == p2.getCol()) continue;

            if (getCell(turn1.getRow(), turn1.getCol()).isEmpty()
                    && getCell(turn2.getRow(), turn2.getCol()).isEmpty()
                    && isLineEmpty(p1, turn1) && isLineEmpty(turn1, turn2) && isLineEmpty(turn2, p2)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断两点之间的直线路径是否为空（不含端点）
     */
    private boolean isLineEmpty(Position p1, Position p2) {
        if (p1.getRow() == p2.getRow()) {
            // 同一行
            int minCol = Math.min(p1.getCol(), p2.getCol());
            int maxCol = Math.max(p1.getCol(), p2.getCol());
            for (int col = minCol + 1; col < maxCol; col++) {
                if (!getCell(p1.getRow(), col).isEmpty()) {
                    return false;
                }
            }
            return true;
        } else if (p1.getCol() == p2.getCol()) {
            // 同一列
            int minRow = Math.min(p1.getRow(), p2.getRow());
            int maxRow = Math.max(p1.getRow(), p2.getRow());
            for (int row = minRow + 1; row < maxRow; row++) {
                if (!getCell(row, p1.getCol()).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return false;  // 不在同一行或列
    }

    /**
     * 获取连线路径（用于绘制）
     */
    public List<Line> getConnectionPath(Position p1, Position p2) {
        List<Line> path = new ArrayList<>();

        if (!canConnect(p1, p2)) return path;

        Cell cell1 = getCell(p1.getRow(), p1.getCol());
        Cell cell2 = getCell(p2.getRow(), p2.getCol());

        // 0次转折
        if (isLineEmpty(p1, p2)) {
            path.add(new Line(cell1, cell2));
            return path;
        }

        // 1次转折
        Position corner1 = new Position(p1.getRow(), p2.getCol());
        if (getCell(corner1.getRow(), corner1.getCol()).isEmpty()
                && isLineEmpty(p1, corner1) && isLineEmpty(corner1, p2)) {
            path.add(new Line(cell1, getCell(corner1.getRow(), corner1.getCol())));
            path.add(new Line(getCell(corner1.getRow(), corner1.getCol()), cell2));
            return path;
        }

        Position corner2 = new Position(p2.getRow(), p1.getCol());
        if (getCell(corner2.getRow(), corner2.getCol()).isEmpty()
                && isLineEmpty(p1, corner2) && isLineEmpty(corner2, p2)) {
            path.add(new Line(cell1, getCell(corner2.getRow(), corner2.getCol())));
            path.add(new Line(getCell(corner2.getRow(), corner2.getCol()), cell2));
            return path;
        }

        // 2次转折：找行
        for (int row = 0; row < rowCnt; row++) {
            Position turn1 = new Position(row, p1.getCol());
            Position turn2 = new Position(row, p2.getCol());

            if (row == p1.getRow() || row == p2.getRow()) continue;

            if (getCell(turn1.getRow(), turn1.getCol()).isEmpty()
                    && getCell(turn2.getRow(), turn2.getCol()).isEmpty()
                    && isLineEmpty(p1, turn1) && isLineEmpty(turn1, turn2) && isLineEmpty(turn2, p2)) {
                path.add(new Line(cell1, getCell(turn1.getRow(), turn1.getCol())));
                path.add(new Line(getCell(turn1.getRow(), turn1.getCol()), getCell(turn2.getRow(), turn2.getCol())));
                path.add(new Line(getCell(turn2.getRow(), turn2.getCol()), cell2));
                return path;
            }
        }

        // 2次转折：找列
        for (int col = 0; col < colCnt; col++) {
            Position turn1 = new Position(p1.getRow(), col);
            Position turn2 = new Position(p2.getRow(), col);

            if (col == p1.getCol() || col == p2.getCol()) continue;

            if (getCell(turn1.getRow(), turn1.getCol()).isEmpty()
                    && getCell(turn2.getRow(), turn2.getCol()).isEmpty()
                    && isLineEmpty(p1, turn1) && isLineEmpty(turn1, turn2) && isLineEmpty(turn2, p2)) {
                path.add(new Line(cell1, getCell(turn1.getRow(), turn1.getCol())));
                path.add(new Line(getCell(turn1.getRow(), turn1.getCol()), getCell(turn2.getRow(), turn2.getCol())));
                path.add(new Line(getCell(turn2.getRow(), turn2.getCol()), cell2));
                return path;
            }
        }

        return path;
    }

    /**
     * 检查棋盘是否存在可消除对
     */
    public boolean hasValidPair() {
        List<Position> cells = new ArrayList<>();
        for (int i = 0; i < rowCnt; i++) {
            for (int j = 0; j < colCnt; j++) {
                if (!board[i][j].isEmpty()) {
                    cells.add(new Position(i, j));
                }
            }
        }

        for (int i = 0; i < cells.size(); i++) {
            for (int j = i + 1; j < cells.size(); j++) {
                if (canConnect(cells.get(i), cells.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取剩余可消除对数
     */
    public int getRemainingPairs() {
        int count = 0;
        for (int i = 0; i < rowCnt; i++) {
            for (int j = 0; j < colCnt; j++) {
                if (!board[i][j].isEmpty()) {
                    count++;
                }
            }
        }
        return count / 2;
    }

    /**
     * 消除两个格子
     */
    public void clearCells(Position p1, Position p2) {
        getCell(p1.getRow(), p1.getCol()).setEmpty(true);
        getCell(p2.getRow(), p2.getCol()).setEmpty(true);
    }

    /**
     * 将棋盘状态序列化为字符串
     */
    public String serializeBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append(rowCnt).append(",").append(colCnt).append("\n");
        
        for (int i = 0; i < rowCnt; i++) {
            for (int j = 0; j < colCnt; j++) {
                Cell cell = board[i][j];
                sb.append(cell.isEmpty() ? 0 : cell.getIconIndex());
                if (j < colCnt - 1) {
                    sb.append(" ");
                }
            }
            if (i < rowCnt - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 从字符串反序列化棋盘状态
     */
    public static GameBoard deserializeBoard(String data) {
        try {
            String[] lines = data.split("\n");
            if (lines.length < 1) {
                return null;
            }
            
            String[] dims = lines[0].split(",");
            int rows = Integer.parseInt(dims[0].trim());
            int cols = Integer.parseInt(dims[1].trim());
            
            Cell[][] board = new Cell[rows][cols];
            
            for (int i = 0; i < rows; i++) {
                if (i + 1 >= lines.length) {
                    return null;
                }
                String[] cells = lines[i + 1].split(" ");
                for (int j = 0; j < cols; j++) {
                    if (j >= cells.length) {
                        return null;
                    }
                    int iconIndex = Integer.parseInt(cells[j].trim());
                    board[i][j] = new Cell(new Position(i, j), iconIndex == 0, iconIndex);
                }
            }
            
            return new GameBoard(rows, cols, board);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查棋盘是否为空（所有格子都已消除）
     */
    public boolean isBoardEmpty() {
        for (int i = 0; i < rowCnt; i++) {
            for (int j = 0; j < colCnt; j++) {
                if (!board[i][j].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }



     //手动生成简单模式棋盘
    public static GameBoard generateSimpleBoard() {
        int size = 11;
        Cell[][] board = new Cell[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = new Cell(new Position(i, j), true, 0);
            }
        }

        int[][] leftTop = {
                {1, 4, 2, 5},
                {3, 1, 5, 2},
                {4, 3, 1, 1},
                {5, 4, 3, 2}
        };
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board[i + 1][j + 1] = new Cell(
                        new Position(i + 1, j + 1), false, leftTop[i][j]
                );
            }
        }

        int[][] rightBottom = {
                {2, 5, 4, 1},
                {3, 2, 1, 4},
                {5, 3, 1, 1},
                {4, 5, 3, 2}
        };
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board[i + 6][j + 6] = new Cell(
                        new Position(i + 6, j + 6), false, rightBottom[i][j]
                );
            }
        }

        return new GameBoard(size, size, board);
    }

    //手动生成困难模式棋盘
    public static GameBoard generateHardBoard() {

        int size = 12;
        Cell[][] board = new Cell[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] =
                        new Cell(
                                new Position(i, j),
                                true,
                                0
                        );
            }
        }

        int[][] hardBoard = {
                {1, 4, 2, 5, 3, 6, 7, 8, 9, 10},
                {11, 12, 1, 7, 4, 2, 5, 5, 6, 8},
                {9, 10, 11, 12, 2, 5, 3, 6, 1, 4},
                {7, 8, 9, 10, 11, 12, 4, 4, 5, 3},
                {6, 1, 4, 7, 8, 9, 10, 11, 12, 2},

                {5, 3, 6, 8, 1, 4, 7, 9, 2, 10},
                {11, 5, 3, 6, 12, 2, 8, 1, 4, 7},
                {9, 10, 11, 12, 5, 3, 6, 2, 8, 1},
                {4, 7, 9, 10, 11, 12, 5, 3, 6, 2},
                {8, 1, 4, 7, 9, 10, 11, 12, 5, 3}
        };

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board[i + 1][j + 1] =
                        new Cell(
                                new Position(i + 1, j + 1),
                                false,
                                hardBoard[i][j]
                        );
            }
        }

        return new GameBoard(size, size, board);
    }

}