package model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SaveManager {
    // 存档目录名称
    private static final String SAVE_DIR = "saves";
    // 存档文件扩展名
    private static final String SAVE_EXTENSION = ".save";

    /**
     * 存档数据结构类
     * 用于封装读取的存档数据
     */
    public static class SaveData {
        private GameBoard board;      // 棋盘状态
        private GameState state;      // 游戏状态
        private boolean valid;         // 存档是否有效

        public SaveData(GameBoard board, GameState state, boolean valid) {
            this.board = board;
            this.state = state;
            this.valid = valid;
        }

        public GameBoard getBoard() {
            return board;
        }

        public GameState getState() {
            return state;
        }

        public boolean isValid() {
            return valid;
        }
    }

    /**
     * 构造函数：初始化存档管理器
     * 创建存档目录（如果不存在）
     */
    public SaveManager() {
        createSaveDirectory();
    }

    /**
     * 创建存档目录
     * 确保存档文件夹存在
     */
    private void createSaveDirectory() {
        try {
            Path path = Paths.get(SAVE_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==================== 存档功能 ====================

    /**
     * 保存游戏
     * @param username 用户名（用于区分不同玩家的存档）
     * @param board 当前棋盘状态
     * @param state 当前游戏状态
     * @return 是否保存成功
     */
    public boolean saveGame(String username, GameBoard board, GameState state) {
        // 参数校验：非注册用户不能存档
        if (username == null || username.isEmpty()) {
            return false;
        }

        try {
            // 生成存档文件名（用户名_solt0.save）
            String filename = getSaveFilename(username, 0);
            File file = new File(filename);

            // 使用BufferedWriter写入存档文件
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                // 写入用户信息
                bw.write("USER:" + username + "\n");
                // 写入游戏模式
                bw.write("MODE:" + state.getMode() + "\n");
                // 写入当前分数
                bw.write("SCORE:" + state.getScore() + "\n");
                // 写入剩余时间
                bw.write("TIME:" + state.getRemainingTime() + "\n");
                // 写入当前连消数
                bw.write("COMBO:" + state.getComboCount() + "\n");
                // 写入最大连消记录
                bw.write("MAXCOMBO:" + state.getMaxCombo() + "\n");
                // 棋盘数据标记
                bw.write("BOARD:\n");
                // 写入棋盘序列化数据
                bw.write(board.serializeBoard());
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== 读档功能 ====================

    /**
     * 加载游戏存档
     * @param username 当前登录用户名（用于验证是否是本人的存档）
     * @param slot 存档槽位（目前支持1个槽位，索引为0）
     * @return SaveData对象，包含棋盘状态、游戏状态和有效性标志
     */
    public SaveData loadGame(String username, int slot) {
        // 参数校验
        if (username == null || username.isEmpty()) {
            return new SaveData(null, null, false);
        }

        try {
            // 获取存档文件路径
            String filename = getSaveFilename(username, slot);
            File file = new File(filename);

            // 检查存档文件是否存在
            if (!file.exists()) {
                return new SaveData(null, null, false);
            }

            // 用于存储读取的元数据
            Map<String, String> metadata = new HashMap<>();
            // 用于存储棋盘数据
            StringBuilder boardData = new StringBuilder();
            // 是否正在读取棋盘数据
            boolean readingBoard = false;

            // 逐行读取存档文件
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // 遇到BOARD:标记后开始读取棋盘数据
                    if (line.startsWith("BOARD:")) {
                        readingBoard = true;
                        continue;
                    }

                    if (readingBoard) {
                        // 棋盘数据行
                        boardData.append(line).append("\n");
                    } else {
                        // 元数据行，格式为 KEY:VALUE
                        String[] parts = line.split(":", 2);
                        if (parts.length == 2) {
                            metadata.put(parts[0], parts[1]);
                        }
                    }
                }
            }

            // ==================== 用户验证 ====================
            // 确保用户只能读取自己的存档
            String savedUser = metadata.get("USER");
            if (savedUser == null || !savedUser.equals(username)) {
                return new SaveData(null, null, false);
            }

            // ==================== 解析游戏状态 ====================
            int mode = Integer.parseInt(metadata.getOrDefault("MODE", "0"));
            int score = Integer.parseInt(metadata.getOrDefault("SCORE", "0"));
            int time = Integer.parseInt(metadata.getOrDefault("TIME", "300"));
            int combo = Integer.parseInt(metadata.getOrDefault("COMBO", "0"));
            int maxCombo = Integer.parseInt(metadata.getOrDefault("MAXCOMBO", "0"));

            // ==================== 解析棋盘状态 ====================
            String boardStr = boardData.toString().trim();
            GameBoard board = GameBoard.deserializeBoard(boardStr);

            // 如果棋盘解析失败（文件损坏或格式错误），返回无效
            if (board == null) {
                return new SaveData(null, null, false);
            }

            // 重建游戏状态对象
            GameState state = new GameState(score, time, combo, maxCombo, mode);

            // 返回有效的存档数据
            return new SaveData(board, state, true);

        } catch (Exception e) {
            // 发生任何异常（文件损坏、格式错误等），返回无效
            e.printStackTrace();
            return new SaveData(null, null, false);
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 检查指定用户的存档文件是否存在
     */
    public boolean hasSaveFile(String username, int slot) {
        String filename = getSaveFilename(username, slot);
        return new File(filename).exists();
    }

    /**
     * 获取可用存档槽位数量
     */
    public int getAvailableSlots(String username) {
        int count = 0;
        for (int i = 0; i < 1; i++) {
            if (hasSaveFile(username, i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 生成存档文件名
     * 格式：saves/用户名_slot槽位号.save
     * 文件名中的用户名会被处理，移除特殊字符
     */
    private String getSaveFilename(String username, int slot) {
        // 替换用户名中的非法字符（仅保留字母和数字）
        String safeUsername = username.replaceAll("[^a-zA-Z0-9]", "_");
        return SAVE_DIR + File.separator + safeUsername + "_slot" + slot + SAVE_EXTENSION;
    }

    /**
     * 删除指定存档
     */
    public boolean deleteSave(String username, int slot) {
        String filename = getSaveFilename(username, slot);
        File file = new File(filename);
        return file.delete();
    }
}
