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

        