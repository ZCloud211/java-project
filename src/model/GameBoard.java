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

    /**
     * 填充困难模式棋盘（10×10）
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
    private static final int[][][][] simpleBoards = {
            {
                    {{1, 4, 2, 5}, {3, 1, 5, 2}, {4, 3, 1, 1}, {5, 4, 3, 2}},
                    {{2, 5, 4, 1}, {3, 2, 1, 4}, {5, 3, 1, 1}, {4, 5, 3, 2}}
            },
            {
                    {{1, 3, 2, 5}, {4, 1, 3, 1}, {5, 2, 3, 2}, {1, 4, 2, 3}},
                    {{2, 1, 5, 3}, {3, 4, 2, 2}, {1, 3, 1, 4}, {5, 2, 3, 1}}
            },
            {
                    {{1, 2, 3, 4}, {5, 1, 2, 3}, {4, 5, 1, 2}, {3, 1, 2, 3}},
                    {{2, 3, 5, 1}, {4, 2, 3, 2}, {1, 5, 4, 3}, {3, 1, 2, 1}}
            },
            {
                    {{4, 1, 2, 3}, {3, 5, 1, 2}, {5, 3, 2, 1}, {2, 1, 3, 4}},
                    {{1, 2, 4, 3}, {2, 3, 1, 5}, {4, 2, 1, 3}, {3, 1, 5, 2}}
            },
            {
                    {{3, 5, 1, 2}, {1, 2, 4, 3}, {5, 3, 2, 1}, {2, 1, 3, 4}},
                    {{4, 1, 3, 2}, {2, 5, 1, 3}, {3, 2, 4, 5}, {1, 3, 2, 1}}
            }
    };

    public static GameBoard generateSimpleBoard() {
        Random random = new Random();
        int boardIndex = random.nextInt(simpleBoards.length);
        int[][][] selectedBoard = simpleBoards[boardIndex];

        int size = 11;
        Cell[][] board = new Cell[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                board[i][j] = new Cell(new Position(i, j), true, 0);

        int[][] leftTop = selectedBoard[0];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                board[i + 1][j + 1] = new Cell(new Position(i + 1, j + 1), false, leftTop[i][j]);

        int[][] rightBottom = selectedBoard[1];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                board[i + 6][j + 6] = new Cell(new Position(i + 6, j + 6), false, rightBottom[i][j]);

        return new GameBoard(size, size, board);
    }

    private static final int[][][] hardBoards = {
            {
                    {11, 2, 11, 12, 12, 5, 6, 9, 3, 12},
                    {7, 5, 9, 10, 1, 8, 9, 10, 11, 2},
                    {4, 5, 9, 11, 12, 6, 3, 7, 2, 8},
                    {6, 10, 6, 3, 1, 1, 10, 10, 9, 2},
                    {11, 7, 10, 4, 2, 6, 12, 9, 11, 5},
                    {5, 3, 6, 3, 12, 3, 7, 12, 8, 2},
                    {4, 9, 3, 4, 10, 11, 5, 11, 11, 1},
                    {4, 8, 8, 7, 7, 6, 7, 2, 11, 10},
                    {12, 7, 8, 12, 6, 4, 4, 4, 9, 5},
                    {5, 2, 1, 12, 1, 1, 3, 1, 8, 8},
            },
// Board 2
            {
                    {11, 12, 7, 3, 12, 9, 4, 5, 8, 12},
                    {7, 12, 11, 10, 10, 10, 6, 7, 11, 8},
                    {8, 6, 6, 2, 6, 2, 4, 4, 12, 2},
                    {2, 5, 11, 3, 3, 1, 5, 5, 5, 7},
                    {3, 8, 1, 3, 4, 10, 12, 2, 1, 3},
                    {9, 10, 12, 9, 9, 2, 1, 4, 12, 10},
                    {6, 11, 8, 10, 9, 9, 11, 10, 6, 4},
                    {11, 7, 5, 8, 4, 11, 7, 1, 7, 11},
                    {4, 1, 2, 6, 3, 6, 9, 3, 5, 11},
                    {1, 5, 8, 12, 12, 1, 9, 7, 2, 8},
            },
// Board 3
            {
                    {2, 9, 5, 5, 10, 5, 3, 7, 3, 1},
                    {10, 5, 5, 12, 11, 7, 2, 2, 11, 7},
                    {2, 3, 3, 12, 4, 9, 6, 12, 10, 10},
                    {4, 3, 9, 9, 6, 12, 11, 3, 9, 12},
                    {6, 11, 2, 1, 4, 3, 12, 8, 1, 10},
                    {11, 5, 8, 1, 9, 8, 1, 7, 6, 12},
                    {4, 4, 11, 10, 11, 10, 9, 7, 8, 6},
                    {5, 3, 7, 11, 4, 1, 4, 12, 7, 2},
                    {8, 10, 7, 1, 11, 2, 4, 8, 12, 8},
                    {6, 6, 9, 1, 8, 6, 2, 11, 5, 12},
            },
// Board 4
            {
                    {12, 7, 7, 12, 11, 9, 9, 5, 12, 2},
                    {9, 6, 11, 2, 7, 11, 5, 1, 4, 12},
                    {9, 4, 11, 3, 6, 4, 3, 6, 3, 6},
                    {2, 7, 3, 2, 4, 11, 5, 10, 10, 8},
                    {11, 2, 5, 1, 8, 1, 6, 8, 12, 3},
                    {12, 1, 3, 8, 10, 3, 1, 7, 4, 11},
                    {4, 8, 1, 12, 11, 9, 7, 12, 12, 2},
                    {6, 5, 5, 8, 3, 1, 8, 10, 5, 11},
                    {12, 4, 10, 5, 10, 9, 7, 10, 11, 9},
                    {8, 9, 7, 6, 4, 2, 2, 6, 10, 1},
            },
// Board 5
            {
                    {4, 12, 9, 6, 7, 10, 7, 7, 2, 1},
                    {4, 9, 2, 6, 12, 11, 12, 10, 2, 3},
                    {12, 1, 8, 6, 5, 9, 8, 5, 7, 9},
                    {10, 7, 9, 11, 1, 10, 10, 8, 3, 10},
                    {7, 7, 5, 6, 1, 5, 3, 3, 8, 4},
                    {2, 12, 4, 8, 12, 11, 11, 3, 7, 11},
                    {6, 4, 12, 12, 3, 11, 6, 3, 11, 3},
                    {6, 11, 11, 9, 2, 9, 1, 12, 1, 8},
                    {1, 2, 12, 10, 5, 2, 6, 8, 11, 2},
                    {10, 9, 4, 4, 4, 5, 8, 1, 5, 5},
            }
    };

    public static GameBoard generateHardBoard() {
        Random random = new Random();
        int boardIndex = random.nextInt(hardBoards.length);
        int[][] selectedBoard = hardBoards[boardIndex];

        int size = 12;
        Cell[][] board = new Cell[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                board[i][j] = new Cell(new Position(i, j), true, 0);

        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                board[i+1][j+1] = new Cell(new Position(i+1, j+1), false, selectedBoard[i][j]);

        return new GameBoard(size, size, board);
    }
}