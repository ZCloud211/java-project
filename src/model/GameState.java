package model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    // 分数
    private int score;
    // 剩余时间（秒）
    private int remainingTime;
    // 当前连消计数
    private int comboCount;
    // 最大连消记录
    private int maxCombo;
    // 操作历史记录（用于显示上一步信息）
    private List<String> actionHistory;
    // 游戏模式（简单/困难）
    private int mode;

    /**
     * 构造函数：初始化默认游戏状态
     */
    public GameState() {
        this.score = 0;
        this.remainingTime = 300;
        this.comboCount = 0;
        this.maxCombo = 0;
        this.actionHistory = new ArrayList<>();
        this.mode = GameBoard.MODE_SIMPLE;
    }

    /**
     * 构造函数：从存档恢复游戏状态
     */
    public GameState(int score, int remainingTime, int comboCount, int maxCombo, int mode) {
        this.score = score;
        this.remainingTime = remainingTime;
        this.comboCount = comboCount;
        this.maxCombo = maxCombo;
        this.actionHistory = new ArrayList<>();
        this.mode = mode;
    }

    // ==================== Getter/Setter方法 ====================

    /**
     * 获取当前分数
     */
    public int getScore() {
        return score;
    }

    /**
     * 设置分数
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * 获取剩余时间
     */
    public int getRemainingTime() {
        return remainingTime;
    }

    /**
     * 设置剩余时间
     */
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    /**
     * 获取当前连消数
     */
    public int getComboCount() {
        return comboCount;
    }

    /**
     * 设置连消数
     */
    public void setComboCount(int comboCount) {
        this.comboCount = comboCount;
        // 更新最大连消记录
        if (comboCount > maxCombo) {
            maxCombo = comboCount;
        }
    }

    /**
     * 获取历史最大连消数
     */
    public int getMaxCombo() {
        return maxCombo;
    }

    /**
     * 设置最大连消数
     */
    public void setMaxCombo(int maxCombo) {
        this.maxCombo = maxCombo;
    }

    /**
     * 获取游戏模式
     */
    public int getMode() {
        return mode;
    }

    /**
     * 设置游戏模式
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * 获取操作历史记录列表
     */
    public List<String> getActionHistory() {
        return actionHistory;
    }

    /**
     * 设置操作历史记录列表
     */
    public void setActionHistory(List<String> actionHistory) {
        this.actionHistory = actionHistory;
    }

    // ==================== 分数计算相关方法 ====================

    /**
     * 添加分数（包含连消奖励）
     * 基础分：每消除一对 +10分
     * 连消奖励：连续消除3对及以上，每对额外+5分
     */
    public void addScore(int baseScore) {
        int bonus = 0;
        // 当连消数>=3时，计算额外奖励
        if (comboCount >= 3) {
            // 额外加分 = (连消数 - 2) * 5
            bonus = (comboCount - 2) * 5;
        }
        int totalScore = baseScore + bonus;
        score += totalScore;

        // 如果有连消奖励，记录到操作历史
        if (comboCount >= 3) {
            addAction("连消×" + comboCount + "，额外+" + bonus + "分");
        }
    }

    /**
     * 增加连消计数（每次成功消除后调用）
     */
    public void incrementCombo() {
        comboCount++;
        if (comboCount > maxCombo) {
            maxCombo = comboCount;
        }
    }

    /**
     * 重置连消计数（消除失败或选择不同图案时调用）
     */
    public void resetCombo() {
        comboCount = 0;
    }

    // ==================== 操作历史相关方法 ====================

    /**
     * 添加操作记录到历史
     * 最多保留最近10条记录
     */
    public void addAction(String action) {
        actionHistory.add(action);
        if (actionHistory.size() > 10) {
            // 移除最早的记录
            actionHistory.remove(0);
        }
    }

    /**
     * 获取最近一次操作记录
     * 用于界面实时显示
     */
    public String getLastAction() {
        if (actionHistory.isEmpty()) {
            return "";
        }
        return actionHistory.get(actionHistory.size() - 1);
    }

    /**
     * 清空操作历史
     */
    public void clearHistory() {
        actionHistory.clear();
    }

    // ==================== 时间相关方法 ====================

    /**
     * 时间递减（每秒游戏循环调用）
     */
    public void decreaseTime() {
        if (remainingTime > 0) {
            remainingTime--;
        }
    }

    /**
     * 检查时间是否耗尽
     */
    public boolean isTimeUp() {
        return remainingTime <= 0;
    }
}
