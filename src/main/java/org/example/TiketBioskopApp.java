package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class TiketBioskopApp extends JFrame {

    CardLayout layout = new CardLayout();
    JPanel mainPanel = new JPanel(layout);

    DefaultTableModel model;
    JTable table;

    JTextField tfFilm, tfStudio, tfJam, tfHarga;
    int selectedRow = -1;

    File dataFile = new File("tiket.csv");

    public TiketBioskopApp() {
        setTitle("Aplikasi Tiket Bioskop");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        model = new DefaultTableModel(
                new String[]{"Judul Film", "Studio", "Jam", "Harga"}, 0
        );

        loadData();

        mainPanel.add(dashboardPanel(), "DASH");
        mainPanel.add(listPanel(), "LIST");
        mainPanel.add(inputPanel(), "INPUT");

        add(mainPanel);
        setVisible(true);
    }

    JPanel dashboardPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(25, 25, 25));

        JPanel card = new JPanel(new GridLayout(4, 1, 15, 15));
        card.setBackground(new Color(35, 35, 35));
        card.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel title = new JLabel("CINEMA TICKET", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

        card.add(title);
        card.add(btn("📄 List Tiket", () -> layout.show(mainPanel, "LIST")));
        card.add(btn("➕ Input Tiket", () -> layout.show(mainPanel, "INPUT")));
        card.add(btn("❌ Exit", () -> System.exit(0)));

        p.add(card);
        return p;
    }

    JPanel listPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10)) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(20, 20, 20),
                        0, getHeight(), new Color(100, 0, 0)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(60, 0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(30, 0, 0));
        table.setSelectionBackground(new Color(120, 0, 0));

        table.getSelectionModel().addListSelectionListener(e -> {
            selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tfFilm.setText(model.getValueAt(selectedRow, 0).toString());
                tfStudio.setText(model.getValueAt(selectedRow, 1).toString());
                tfJam.setText(model.getValueAt(selectedRow, 2).toString());
                tfHarga.setText(model.getValueAt(selectedRow, 3).toString());
                layout.show(mainPanel, "INPUT");
            }
        });

        JButton delete = btn("🗑 Delete", () -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data dulu!");
            } else {
                model.removeRow(row);
                saveData();
            }
        });

        JButton back = btn("⬅ Dashboard", () -> layout.show(mainPanel, "DASH"));

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(20, 0, 0));
        bottom.add(delete);
        bottom.add(back);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    JPanel inputPanel() {
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setBackground(new Color(25, 25, 25));

        JPanel form = new JPanel(new GridLayout(6, 2, 12, 12));
        form.setBackground(new Color(40, 40, 40));
        form.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        tfFilm = field();
        tfStudio = field();
        tfJam = field();
        tfHarga = field();

        JButton save = btn("💾 Simpan", () -> {
            if (!validInput()) return;

            model.addRow(new Object[]{
                    tfFilm.getText(),
                    tfStudio.getText(),
                    tfJam.getText(),
                    tfHarga.getText()
            });
            saveData();

            try {
                TiketApiClient.postTiket(
                        tfFilm.getText(),
                        tfStudio.getText(),
                        tfJam.getText(),
                        tfHarga.getText()
                );
            } catch (Exception ignored) {}

            clear();
        });

        JButton update = btn("✏ Update", () -> {
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data dari tabel dulu!");
                return;
            }
            if (!validInput()) return;

            model.setValueAt(tfFilm.getText(), selectedRow, 0);
            model.setValueAt(tfStudio.getText(), selectedRow, 1);
            model.setValueAt(tfJam.getText(), selectedRow, 2);
            model.setValueAt(tfHarga.getText(), selectedRow, 3);

            saveData();

            try {
                TiketApiClient.postTiket(
                        tfFilm.getText(),
                        tfStudio.getText(),
                        tfJam.getText(),
                        tfHarga.getText()
                );
            } catch (Exception ignored) {}

            clear();
            layout.show(mainPanel, "LIST");
        });
        JButton back = btn("⬅ Dashboard", () -> layout.show(mainPanel, "DASH"));

        form.add(label("Judul Film")); form.add(tfFilm);
        form.add(label("Studio")); form.add(tfStudio);
        form.add(label("Jam")); form.add(tfJam);
        form.add(label("Harga")); form.add(tfHarga);
        form.add(save); form.add(update);
        form.add(back); form.add(new JLabel());

        bg.add(form);
        return bg;
    }

    boolean validInput() {
        if (tfFilm.getText().trim().isEmpty() ||
                tfStudio.getText().trim().isEmpty() ||
                tfJam.getText().trim().isEmpty() ||
                tfHarga.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Semua field wajib diisi!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Integer.parseInt(tfHarga.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Harga harus berupa angka!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(dataFile))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                pw.println(
                        model.getValueAt(i, 0) + "," +
                                model.getValueAt(i, 1) + "," +
                                model.getValueAt(i, 2) + "," +
                                model.getValueAt(i, 3)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void loadData() {
        if (!dataFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                model.addRow(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void clear() {
        tfFilm.setText("");
        tfStudio.setText("");
        tfJam.setText("");
        tfHarga.setText("");
        selectedRow = -1;
    }

    JTextField field() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return f;
    }

    JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }

    JButton btn(String t, Runnable r) {
        JButton b = new JButton(t);
        b.setBackground(new Color(200, 30, 50));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.addActionListener(e -> r.run());
        return b;
    }

    static void showLogin() {
        JFrame f = new JFrame("Login");
        f.setSize(350, 220);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.setBackground(new Color(25, 25, 25));

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();

        JButton login = new JButton("Login");
        login.setBackground(new Color(200, 30, 50));
        login.setForeground(Color.WHITE);

        login.addActionListener(e -> {
            if (user.getText().equals("viya") &&
                    new String(pass.getPassword()).equals("keceee")) {

                new TiketBioskopApp();
                f.dispose();
            } else {
                JOptionPane.showMessageDialog(f,
                        "Username atau password salah!",
                        "Login gagal",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(labelStatic("Username")); p.add(user);
        p.add(labelStatic("Password")); p.add(pass);
        p.add(new JLabel()); p.add(login);

        f.add(p);
        f.setVisible(true);
    }

    static JLabel labelStatic(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }

    public static void main(String[] args) {
        showLogin();
    }
}

class TiketApiClient {

    private static final String API_URL = "http://localhost:8080/api/tiket";

    public static void postTiket(String film, String studio, String jam, String harga)
            throws IOException {

        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String json = "{"
                + "\"film\":\"" + film + "\","
                + "\"studio\":\"" + studio + "\","
                + "\"jam\":\"" + jam + "\","
                + "\"harga\":\"" + harga + "\""
                + "}";

        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes());
        os.close();

        conn.getResponseCode();
    }
}