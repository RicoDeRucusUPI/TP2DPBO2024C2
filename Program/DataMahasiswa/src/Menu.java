/*
    ==========================================================================
    Saya Rico Valentino 1909263 mengerjakan (TP2)
    dalam mata kuliah DPBO untuk keberkahanNya maka saya tidak
    melakukan kecurangan seperti yang telah dispesifikasikan.
    ==========================================================================
*/

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.sql.*;

public class Menu extends JFrame{
    public static void main(String[] args) {
        // buat object window
        Menu window = new Menu();

        // atur ukuran window
        window.setSize(480, 560);

        // letakkan window di tengah layar
        window.setLocationRelativeTo(null);

        // isi window
        window.setContentPane(window.mainPanel);

        // ubah warna background
        window.getContentPane().setBackground(Color.white);

        // tampilkan window
        window.setVisible(true);

        // agar program ikut berhenti saat window diclose
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // index baris yang diklik
    private int selectedIndex = -1;
    // untuk id mahasiswa yang di selected
    private String idMahasiswaSelected = null;

    private Database database;
    private JPanel mainPanel;
    private JTextField nimField;
    private JTextField namaField;
    private JTable mahasiswaTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox jenisKelaminComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel nimLabel;
    private JLabel namaLabel;
    private JLabel jenisKelaminLabel;
    private JComboBox nilaiComboBox;
    private JLabel nilaiLabel;

    // constructor
    public Menu() {
        // inisialisasi Database
        database = new Database();
        // isi tabel mahasiswa
        mahasiswaTable.setModel(setTable());
        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        // atur isi combo box
        String[] jenisKelaminData = {"", "Laki-laki", "Perempuan"};
        jenisKelaminComboBox.setModel(new DefaultComboBoxModel(jenisKelaminData));
        String[] nilaiData = {"","A","B","C","D","E"};
        nilaiComboBox.setModel(new DefaultComboBoxModel(nilaiData));
        // sembunyikan button delete
        deleteButton.setVisible(false);
        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });

        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex >= 0) {
                    deleteData();
                }
            }
        });

        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // saat tombol clearForm ditekan
                clearForm();
            }
        });



        // saat salah satu baris tabel ditekan
        mahasiswaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = mahasiswaTable.getSelectedRow();

                // ubah idMahasiswaSelected menjadi id yang dipilih
                idMahasiswaSelected = mahasiswaTable.getModel().getValueAt(selectedIndex, 0).toString();
                // simpan value textfield dan combo box
                String selectedNim = mahasiswaTable.getModel().getValueAt(selectedIndex, 1).toString();
                String selectedNama = mahasiswaTable.getModel().getValueAt(selectedIndex, 2).toString();
                String selectedJenisKelamin = mahasiswaTable.getModel().getValueAt(selectedIndex, 3).toString();
                String selectedNilai = mahasiswaTable.getModel().getValueAt(selectedIndex, 4).toString();
                // ubah isi textfield dan combo box
                nimField.setText(selectedNim);
                namaField.setText(selectedNama);
                jenisKelaminComboBox.setSelectedItem(selectedJenisKelamin);
                nilaiComboBox.setSelectedItem(selectedNilai);

                // ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");

                // tampilkan button delete
                deleteButton.setVisible(true);
            }
        });
    }

    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] column = {"ID", "NIM", "Nama", "Jenis Kelamin", "Nilai"};

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel temp = new DefaultTableModel(null, column);
        try{

            ResultSet resultSet = database.selectQuery("SELECT * FROM mahasiswa");
            while(resultSet.next()) {
                Object[] row = new Object[5];
                row[0] = resultSet.getString("id");
                row[1] = resultSet.getString("nim");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getString("jenis_kelamin"); // corrected typo
                row[4] = resultSet.getString("nilai");
                temp.addRow(row);
            }

        }catch(SQLException e){
            throw new RuntimeException(e);

        }

        return temp;
    }

    public void insertData() {
        // ambil value dari textfield dan combobox
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String nilai = nilaiComboBox.getSelectedItem().toString();

        if (!nim.equals("") && !nama.equals("") && jenisKelamin != "" && nilai != "") {
            try{
                ResultSet resultSet = database.selectQuery("SELECT * FROM mahasiswa WHERE nim = '"+nim+"'");
                if(!resultSet.next()){
                    // tambahkan data ke dalam list
                    database.insertUpdateDeleteQuery("INSERT INTO `mahasiswa` VALUES (NULL, '"+ nim +"', '"+ nama +"', '"+ jenisKelamin +"', '"+ nilai +"');");
                    // update tabel
                    mahasiswaTable.setModel(setTable());

                    // bersihkan form
                    clearForm();

                    // feedback
                    System.out.println("Insert berhasil!");
                    JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
                }else{
                    JOptionPane.showMessageDialog(null, "NIM telah digunakan");
                }
            }catch(SQLException error){
                throw new RuntimeException(error);
            }
        }else{
            JOptionPane.showMessageDialog( null,"Data tidak boleh kosong!");
        }
    }

    public void updateData() {
        // ambil data dari form
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String nilai = nilaiComboBox.getSelectedItem().toString();
        if (!nim.equals("") && !nama.equals("") && jenisKelamin != "" && nilai != "") {
            database.insertUpdateDeleteQuery("UPDATE `mahasiswa` SET `nim` = '"+ nim +"', `nama` = '"+ nama +"', `jenis_kelamin` = '"+ jenisKelamin +"', `nilai` = '"+ nilai + "' WHERE `mahasiswa`.`id` = "+ idMahasiswaSelected +";");

            // update tabel
            mahasiswaTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Update Berhasil!");
            JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
        }else{
            JOptionPane.showMessageDialog( null,"Data tidak boleh kosong!");
        }
    }

    public void deleteData() {
        int choice = JOptionPane.showConfirmDialog(null, "Apakah yakin akan menghapus data", "Delete Data", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            database.insertUpdateDeleteQuery("DELETE FROM `mahasiswa` WHERE `mahasiswa`.`id` = "+ idMahasiswaSelected +";");

            // update tabel
            mahasiswaTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Delete berhasil!");

            JOptionPane.showMessageDialog( null,"Data berhasil dihapus!");
        }
    }

    public void clearForm() {
        // kosongkan semua texfield dan combo box
        nimField.setText("");
        namaField.setText("");
        jenisKelaminComboBox.setSelectedItem("");
        nilaiComboBox.setSelectedItem("");

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
    }
}
