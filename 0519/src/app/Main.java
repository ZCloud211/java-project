package app;

import model.Cell;
import model.GameBoard;
import model.Position;
import ui.BoardPanel;
import ui.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Main {
    private static boolean checkLogin(String username, String password) {
        File file = new File("users.txt"); //创建用户
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2
                        && parts[0].equals(username)
                        && parts[1].equals(password)) {
                    return true;
                }
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
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean saveUser(String username, String password) {//保存用户
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt", true))) {
            bw.write(username + "," + password);
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 保存分数
    public static void saveScore(String username, int score, String mode) {
        if (username.equals("guest")) return; // 游客不保存
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("scores.txt", true))) {
            bw.write(username + "," + score + "," + mode);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 显示排行榜
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

        // 按分数从高到低排序
        scores.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));

        // 创建排行榜窗口
        JDialog dialog = new JDialog(parent, "排行榜", true);
        dialog.setSize(400, 550);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(null);

        // 背景面板
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

        // 标题
        JLabel title = new JLabel("排行榜 TOP10", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 28));
        title.setForeground(new Color(255, 215, 0));
        title.setBounds(0, 20, 400, 40);
        bgPanel.add(title);

        // 表头
        JLabel header = new JLabel("  名次        用户名           分数           模式");
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));
        header.setForeground(Color.WHITE);
        header.setBounds(20, 70, 360, 30);
        bgPanel.add(header);

        // 分割线
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
                if (i == 0) rowColor = new Color(255, 215, 0);//第一名
                else if (i == 1) rowColor = new Color(192, 192, 192);//第二名
                else if (i == 2) rowColor = new Color(205, 127, 50);//第三名
                else rowColor = Color.WHITE;

                JLabel row = new JLabel(String.format("      %d           %-10s        %s        %s",
                        i + 1, name, score, modeText));
                row.setFont(new Font("微软雅黑", Font.PLAIN, 14));
                row.setForeground(rowColor);
                row.setBounds(20, 110 + i * 35, 360, 30);
                bgPanel.add(row);
            }
        }

        // 关闭按钮
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
        closeBtn.setBounds(150, 430, 100, 40);
        closeBtn.addActionListener(e -> dialog.dispose());
        bgPanel.add(closeBtn);

        dialog.setVisible(true);
    }


    public static void main(String[] args) {//登录窗口
        SwingUtilities.invokeLater(() -> {

            JFrame login = new JFrame("登录"); // 登录窗口
            login.setLayout(null);
            login.setSize(400, 380);
            login.setLocationRelativeTo(null);


            JPanel panel = new JPanel(){ // 登录窗口面板
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setPaint(new GradientPaint(0, 0, new Color(126, 132 , 247),
                            400, 300, new Color(115, 43 ,235)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            };

            panel.setLayout(null);
            login.setContentPane(panel);

            JLabel labelUser = new JLabel("用户名");
            JLabel labelPWD = new JLabel("密码");
            labelUser.setFont(new Font("微软雅黑", Font.BOLD, 16));
            labelUser.setForeground(Color.WHITE);

            labelPWD.setFont(new Font("微软雅黑", Font.BOLD, 16));
            labelPWD.setForeground(Color.WHITE);

            labelUser.setLocation(30, 50);
            labelPWD.setLocation(30, 100);
            labelUser.setSize(100, 45);
            labelPWD.setSize(100, 45);

            JTextField textUser = new JTextField(){// 用户名输入框
                @Override
                protected void paintComponent(Graphics g) {//绘制用户名输入框
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 100)); // 半透明白
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); // 圆角
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            textUser.setOpaque(false);           // 关掉默认背景
            textUser.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 内边距
            textUser.setForeground(Color.WHITE); // 输入文字白色
            textUser.setCaretColor(Color.WHITE); // 光标白色
            textUser.setFont(new Font("微软雅黑", Font.PLAIN, 15));
            textUser.setBounds(160, 80, 200, 38);
            panel.add(textUser);

            JTextField textPWD = new JTextField(){// 密码输入框
                @Override
                protected void paintComponent(Graphics g) {//绘制密码输入框
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            textPWD.setOpaque(false);
            textPWD.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 内边距
            textPWD.setForeground(Color.WHITE); // 输入文字白色
            textPWD.setCaretColor(Color.WHITE); // 光标白色
            textPWD.setFont(new Font("微软雅黑", Font.PLAIN, 15));
            textPWD.setBounds(160, 120, 200, 38);
            panel.add(textPWD);

            textUser.setLocation(100, 50);
            textPWD.setLocation(100, 100);
            textUser.setSize(200, 45);
            textPWD.setSize(200, 45);

            JButton loginBtn = new JButton("登录\\注册");// 登录注册按钮

            loginBtn.setFocusable(false);
            loginBtn.setBorderPainted(false);
            loginBtn.setContentAreaFilled(false);

            loginBtn.setFont(new Font("微软雅黑", Font.BOLD, 18));
            loginBtn.setForeground(Color.white);
            loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            //游客登录
            JButton guestBtn = new JButton("游客登录") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (getModel().isRollover()) {
                        g2.setColor(new Color(180, 180, 180, 180));
                    } else {
                        g2.setColor(new Color(160, 160, 160, 150));
                    }
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
            guestBtn.setBounds(100, 235, 200, 45); // 登录按钮下面
            panel.add(guestBtn);

            guestBtn.addActionListener(e -> {
                // 游客模式直接进入
                String[] options = {"简单模式", "困难模式"};
                int choice = JOptionPane.showOptionDialog(
                        login, "请选择游戏难度", "难度选择",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]
                );
                if (choice == JOptionPane.CLOSED_OPTION) {
                    return;
                }
                String mode = (choice == 1) ? "hard" : "easy";
                GameFrame frame = new GameFrame("连连看", 1200, 900, mode,"guest"); // 传游客标识
                frame.repaint();
                login.dispose();
            });

            loginBtn = new JButton("登录\\注册") {// 登录注册按钮
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    if (getModel().isPressed()) {
                        g2.setPaint(new GradientPaint(0, 0, new Color(36, 144, 204),
                                getWidth(), getHeight(), new Color(36, 144, 204)));
                    } else if (getModel().isRollover()) {
                        g2.setPaint(new GradientPaint(0, 0, new Color(45, 180, 255),
                                getWidth(), getHeight(), new Color(45, 180, 255)));
                    } else {
                        g2.setPaint(new GradientPaint(0, 0, new Color(39, 155, 219),
                                getWidth(), getHeight(), new Color(39, 155, 219)));
                    }

                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25); // 圆角
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
                String pwd = new String(textPWD.getText()).trim();

                if (user.isEmpty() || pwd.isEmpty()) {
                    JOptionPane.showMessageDialog(login, "用户名和密码不能为空！");
                    return;
                }

                if (checkLogin(user, pwd)) {//登录成功
                    String[] options = {"简单模式", "困难模式"};
                    int choice = JOptionPane.showOptionDialog(
                            login,
                            "请选择游戏难度",
                            "难度选择",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );
                    if (choice == JOptionPane.CLOSED_OPTION) {
                        return;
                    }
                    String mode = (choice == 1) ? "hard" : "easy";
                    GameFrame frame = new GameFrame("连连看", 1200, 900, mode,user);
                    frame.repaint();
                    login.dispose();

                } else if (isUserExists(user)) {
                    // 用户名存在但密码错误
                    JOptionPane.showMessageDialog(login, "密码错误！");

                } else {
                    // 用户不存在，提示注册
                    int choice = JOptionPane.showConfirmDialog(
                            login,
                            "用户「" + user + "」不存在，是否注册？",
                            "注册提示",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (choice == JOptionPane.YES_OPTION) {
                        if (saveUser(user, pwd)) {
                            String[] options = {"简单模式", "困难模式"};
                            int newChoice  = JOptionPane.showOptionDialog(
                                    login,
                                    "请选择游戏难度",
                                    "难度选择",
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    options,
                                    options[0]
                            );
                            if (newChoice == JOptionPane.CLOSED_OPTION) {
                                return;
                            }
                            String mode = (newChoice == 1) ? "hard" : "easy";
                            GameFrame frame = new GameFrame("连连看", 1200, 900, mode,user);
                            frame.repaint();
                            login.dispose();
                        } else {
                            JOptionPane.showMessageDialog(login, "注册失败，请重试！");
                        }
                    }
                }
            });

            JButton rankBtn = new JButton("排行榜") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (getModel().isRollover()) {
                        g2.setColor(new Color(255, 180, 0, 180));
                    } else {
                        g2.setColor(new Color(230, 160, 0, 150));
                    }
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
            login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}