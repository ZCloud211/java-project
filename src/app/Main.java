package app;

import model.Cell;
import model.GameBoard;
import model.Position;
import model.SessionManager;
import ui.BoardPanel;
import ui.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static boolean checkLogin(String username, String password) {
        File file = new File("users.txt");
        if (!file.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password))
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isUserExists(String username) {
        File file = new File("users.txt");
        if (!file.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(username)) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean saveUser(String username, String password) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt", true))) {
            bw.write(username + "," + password);
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void showModeSelectionDialog(JFrame parent, String username, boolean saveSession) {
        if (saveSession) SessionManager.saveSession(username);
        String[] options = {"简单模式", "困难模式"};
        int choice = JOptionPane.showOptionDialog(
                parent,
                "欢迎，" + username + "！请选择游戏难度",
                "难度选择",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );
        if (choice == JOptionPane.CLOSED_OPTION) return;
        String mode = (choice == 1) ? "hard" : "easy";
        GameFrame frame = new GameFrame("连连看", 1200, 900, mode, username);
        frame.repaint();
        if (parent != null) parent.dispose();
    }

    public static void saveScore(String username, int score, String mode) {
        if (username.equals("guest")) return;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("scores.txt", true))) {
            bw.write(username + "," + score + "," + mode);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showRankList(JFrame parent) {
        List<String[]> scores = new ArrayList<>();
        File file = new File("scores.txt");
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) scores.add(parts);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        scores.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));

        JDialog dialog = new JDialog(parent, "排行榜", true);
        dialog.setSize(400, 550);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(null);

        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(126, 132, 247),
                        400, 500, new Color(115, 43, 235)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(null);
        dialog.setContentPane(bgPanel);

        JLabel title = new JLabel("排行榜 TOP10", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 28));
        title.setForeground(new Color(255, 215, 0));
        title.setBounds(0, 20, 400, 40);
        bgPanel.add(title);

        JLabel header = new JLabel("  名次        用户名           分数           模式");
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));
        header.setForeground(Color.WHITE);
        header.setBounds(20, 70, 360, 30);
        bgPanel.add(header);

        JSeparator sep = new JSeparator();
        sep.setBounds(20, 100, 360, 2);
        sep.setForeground(Color.WHITE);
        bgPanel.add(sep);

        if (scores.isEmpty()) {
            JLabel empty = new JLabel("暂无记录", SwingConstants.CENTER);
            empty.setFont(new Font("微软雅黑", Font.PLAIN, 20));
            empty.setForeground(Color.WHITE);
            empty.setBounds(0, 200, 400, 40);
            bgPanel.add(empty);
        } else {
            int limit = Math.min(10, scores.size());
            for (int i = 0; i < limit; i++) {
                String name = scores.get(i)[0];
                String score = scores.get(i)[1];
                String mode = scores.get(i).length >= 3 ? scores.get(i)[2] : "-";
                String modeText = mode.equals("easy") ? "简单" : "困难";

                Color rowColor;
                if (i == 0) rowColor = new Color(255, 215, 0);
                else if (i == 1) rowColor = new Color(192, 192, 192);
                else if (i == 2) rowColor = new Color(205, 127, 50);
                else rowColor = Color.WHITE;

                JLabel row = new JLabel(String.format("      %d           %-10s        %s        %s",
                        i + 1, name, score, modeText));
                row.setFont(new Font("微软雅黑", Font.PLAIN, 14));
                row.setForeground(rowColor);
                row.setBounds(20, 110 + i * 35, 360, 30);
                bgPanel.add(row);
            }
        }

        JButton closeBtn = new JButton("关闭") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(39, 155, 219));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBounds(150, 480, 100, 40);
        closeBtn.addActionListener(e -> dialog.dispose());
        bgPanel.add(closeBtn);

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 自动登录
            String autoLoginUser = SessionManager.loadSession();
            if (autoLoginUser != null && isUserExists(autoLoginUser)) {
                showModeSelectionDialog(null, autoLoginUser, false);
                return;
            }

            JFrame login = new JFrame("登录");
            login.setLayout(null);
            login.setSize(400, 380);
            login.setLocationRelativeTo(null);
            login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setPaint(new GradientPaint(0, 0, new Color(126, 132, 247),
                            400, 300, new Color(115, 43, 235)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            panel.setLayout(null);
            login.setContentPane(panel);

            JLabel labelUser = new JLabel("用户名");
            labelUser.setFont(new Font("微软雅黑", Font.BOLD, 16));
            labelUser.setForeground(Color.WHITE);
            labelUser.setLocation(30, 50);
            labelUser.setSize(100, 45);

            JLabel labelPWD = new JLabel("密码");
            labelPWD.setFont(new Font("微软雅黑", Font.BOLD, 16));
            labelPWD.setForeground(Color.WHITE);
            labelPWD.setLocation(30, 100);
            labelPWD.setSize(100, 45);

            JTextField textUser = new JTextField() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            textUser.setOpaque(false);
            textUser.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            textUser.setForeground(Color.WHITE);
            textUser.setCaretColor(Color.WHITE);
            textUser.setFont(new Font("微软雅黑", Font.PLAIN, 15));
            textUser.setLocation(100, 50);
            textUser.setSize(200, 45);
            panel.add(textUser);

            JTextField textPWD = new JTextField() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            textPWD.setOpaque(false);
            textPWD.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            textPWD.setForeground(Color.WHITE);
            textPWD.setCaretColor(Color.WHITE);
            textPWD.setFont(new Font("微软雅黑", Font.PLAIN, 15));
            textPWD.setLocation(100, 100);
            textPWD.setSize(200, 45);
            panel.add(textPWD);

            // 登录注册按钮
            JButton loginBtn = new JButton("登录\\注册") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (getModel().isPressed()) {
                        g2.setPaint(new GradientPaint(0, 0, new Color(36, 144, 204), getWidth(), getHeight(), new Color(36, 144, 204)));
                    } else if (getModel().isRollover()) {
                        g2.setPaint(new GradientPaint(0, 0, new Color(45, 180, 255), getWidth(), getHeight(), new Color(45, 180, 255)));
                    } else {
                        g2.setPaint(new GradientPaint(0, 0, new Color(39, 155, 219), getWidth(), getHeight(), new Color(39, 155, 219)));
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            loginBtn.setFocusPainted(false);
            loginBtn.setBorderPainted(false);
            loginBtn.setContentAreaFilled(false);
            loginBtn.setFont(new Font("微软雅黑", Font.BOLD, 18));
            loginBtn.setForeground(Color.WHITE);
            loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            loginBtn.setBounds(100, 180, 200, 45);
            panel.add(loginBtn);

            loginBtn.addActionListener(e -> {
                String user = textUser.getText().trim();
                String pwd = textPWD.getText().trim();
                if (user.isEmpty() || pwd.isEmpty()) {
                    JOptionPane.showMessageDialog(login, "用户名和密码不能为空！");
                    return;
                }
                if (checkLogin(user, pwd)) {
                    showModeSelectionDialog(login, user, true);
                } else if (isUserExists(user)) {
                    JOptionPane.showMessageDialog(login, "密码错误！");
                } else {
                    int choice = JOptionPane.showConfirmDialog(login,
                            "用户「" + user + "」不存在，是否注册？", "注册提示", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        if (saveUser(user, pwd)) {
                            showModeSelectionDialog(login, user, true);
                        } else {
                            JOptionPane.showMessageDialog(login, "注册失败，请重试！");
                        }
                    }
                }
            });

            // 游客登录按钮
            JButton guestBtn = new JButton("游客登录") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getModel().isRollover() ? new Color(180, 180, 180, 180) : new Color(160, 160, 160, 150));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            guestBtn.setFocusPainted(false);
            guestBtn.setBorderPainted(false);
            guestBtn.setContentAreaFilled(false);
            guestBtn.setFont(new Font("微软雅黑", Font.BOLD, 18));
            guestBtn.setForeground(Color.WHITE);
            guestBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            guestBtn.setBounds(100, 235, 200, 45);
            panel.add(guestBtn);

            guestBtn.addActionListener(e -> {
                String[] options = {"简单模式", "困难模式"};
                int choice = JOptionPane.showOptionDialog(login, "请选择游戏难度", "难度选择",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (choice == JOptionPane.CLOSED_OPTION) return;
                String mode = (choice == 1) ? "hard" : "easy";
                GameFrame frame = new GameFrame("连连看", 1200, 900, mode, "guest");
                frame.repaint();
                login.dispose();
            });

            // 排行榜按钮
            JButton rankBtn = new JButton("排行榜") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getModel().isRollover() ? new Color(255, 180, 0, 180) : new Color(230, 160, 0, 150));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            rankBtn.setFocusPainted(false);
            rankBtn.setBorderPainted(false);
            rankBtn.setContentAreaFilled(false);
            rankBtn.setFont(new Font("微软雅黑", Font.BOLD, 18));
            rankBtn.setForeground(Color.WHITE);
            rankBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            rankBtn.setBounds(100, 290, 200, 40);
            panel.add(rankBtn);
            rankBtn.addActionListener(e -> showRankList(login));

            login.add(loginBtn);
            login.add(labelUser);
            login.add(textUser);
            login.add(labelPWD);
            login.add(textPWD);
            login.setVisible(true);
        });
    }
}