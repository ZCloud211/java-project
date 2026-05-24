package model;

import java.io.*;

public class SessionManager {
    private static final String SESSION_FILE = "session.txt";

    public static void saveSession(String username) {
        if (username == null || username.isEmpty() || "guest".equals(username)) return;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SESSION_FILE))) {
            bw.write(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadSession() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) return null;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String username = br.readLine();
            if (username != null && !username.trim().isEmpty() && !"guest".equals(username.trim())) {
                return username.trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearSession() {
        File file = new File(SESSION_FILE);
        if (file.exists()) file.delete();
    }
}