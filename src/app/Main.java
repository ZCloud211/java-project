package app;

import model.Cell;
import model.GameBoard;
import model.Position;
import ui.BoardPanel;
import ui.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {


            JFrame login = new JFrame("登录");
            login.setLayout(null);
            login.setSize(400, 300);
            login.setLocationRelativeTo(null);
            login.setUndecorated(true);
            login.setShape(new RoundRectangle2D.Double(0, 0, login.getWidth(), login.getHeight(), 20, 20));

            JPanel panel = new JPanel(){
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

            JTextField textUser = new JTextField(){
                @Override
                protected void paintComponent(Graphics g) {
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

            JTextField textPWD = new JTextField(){
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

            JButton closeBtn = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    if (getModel().isPressed()) {
                        g2.setColor(new Color(255, 55, 55));
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(255, 78, 31));
                    } else {
                        g2.setColor(new Color(230, 29, 10));
                    }

                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };

            closeBtn.setFocusPainted(false);
            closeBtn.setBorderPainted(false);
            closeBtn.setContentAreaFilled(false);
            closeBtn.setForeground(Color.WHITE);
            closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            closeBtn.setBounds(10, 10, 20, 20);
            panel.add(closeBtn);

            closeBtn.addActionListener(e -> login.dispose());



            JButton loginBtn = new JButton("登录\\注册");

            loginBtn.setFocusable(false);
            loginBtn.setBorderPainted(false);
            loginBtn.setContentAreaFilled(false);

            loginBtn.setFont(new Font("微软雅黑", Font.BOLD, 18));
            loginBtn.setForeground(Color.white);
            loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            loginBtn = new JButton("登录\\注册") {
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
                    super.paintComponent(g); // 画文字
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
                String user = textUser.getText();
                String pwd = textPWD.getText();
                if (user.equals("admin") && pwd.equals("123456")) {
                    GameFrame frame = new GameFrame("连连看", 800, 1000);
                    frame.repaint();
                    login.dispose();
                } else {
                    JOptionPane.showMessageDialog(login, "用户名或密码错误");
                }
            });

            login.add(loginBtn);
            login.add(labelUser);
            login.add(textUser);
            login.add(labelPWD);
            login.add(textPWD);
            login.setVisible(true);
        });
    }
}
