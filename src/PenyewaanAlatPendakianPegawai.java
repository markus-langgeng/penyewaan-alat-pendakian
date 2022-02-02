
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import java.awt.HeadlessException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Langgeng
 */
public class PenyewaanAlatPendakianPegawai extends javax.swing.JFrame {

    /**
     * Creates new form FormDataBarang
     */
    public PenyewaanAlatPendakianPegawai() {
        initComponents();
        
        getDataPel();
        getDataBrg();
        getDataSewa();
        getDataPengembalian();
        getDataPendapatan();
        hitungPendapatan();
        dataBaru = true;
        setLocationRelativeTo(this);
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        setTableHeader(tabelBarang);
        setTableHeader(tabelPenyewaan);
        setTableHeader(tabelPengembalian);
        setTableHeader(tabelPendapatan);
        setTableHeader(tabelPelanggan);
        
        usernameHome.setText(username);
        // set nip dan tgl sewa scr otomatis
        inputNipFK.setText(nip);
        inputNipKmblFK.setText(nip);
        Date tglHariIni = new Date();
        inputTglSewa.setDate(tglHariIni);
        inputTglKembaliKmbl.setDate(tglHariIni);
        
        getNamaBrg();
        inputJmlBrg.setText("1");
        inputLamaSewa.setText("1");
        
        resetFormBrg();
        resetFormPel();
        resetFormPengembalian();
        resetFormSewa();
    }
    
    public boolean dataBaru;
    public static String nip;
    public static String username;
    // getData utk semua tabel
    private void getDataPel(){ //TO DO:tambahi utk nik
        try{
            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery("SELECT "
                    + "`id_pel` AS `ID Pelanggan`, "
                    + "`nik` AS `NIK`, "
                    + "`nama_pel` AS `Nama Pelanggan`, "
                    + "`no_telp` AS `No. Telp`, "
                    + "`alamat` AS `Alamat`, "
                    + "`tgl_lahir` AS `Tanggal Lahir`, "
                    + "`kota` AS `Kota` "
                    + "FROM pelanggan;");
            tabelPelanggan.setModel(DbUtils.resultSetToTableModel(sql));
        }catch(Exception e){
            
        }
    }
    private void getDataBrg(){
        try{
            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery("SELECT "
                    + "`id_brg` AS `ID Barang`, "
                    + "`nm_brg` AS `Nama Barang`, "
                    + "`harga_brg` AS `Harga Sewa`, "
                    + "`stok` AS `Stok Barang` "
                    + "FROM barang;");
            tabelBarang.setModel(DbUtils.resultSetToTableModel(sql));
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
    private void getDataSewa(){
        try{
            java.sql.Connection conn = (java.sql.Connection) koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery("SELECT "
                    + "`id_sewa` AS `ID Penyewaan`, "
                    + "`nip` AS `NIP`, "
                    + "`id_pel` AS `ID Pelanggan`, "
                    + "`id_brg` AS `ID Barang`, "
                    + "`jml_brg` AS `Jumlah Barang`, "
                    + "`tgl_sewa` AS `Tanggal Sewa`, "
                    + "`lama_sewa` AS `Lama Sewa`, "
                    + "`tgl_kembali` AS `Tanggal Kembali`, "
                    + "`harga` AS `Harga` "
                    + "FROM penyewaan;");
            tabelPenyewaan.setModel(DbUtils.resultSetToTableModel(sql));
            
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
    private void getDataPengembalian(){
        try{
            java.sql.Connection conn = (java.sql.Connection) koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery("SELECT "
                    + "`id_kmbl` AS `ID Pengembalian`, "
                    + "`id_sewa` AS `ID Penyewaan`, "
                    + "`nip` AS `NIP`, "
                    + "`tgl_sewa` AS `Tanggal Sewa`, "
                    + "`lama_sewa` AS `Lama Sewa`, "
                    + "`tgl_kmbl` AS `Tanggal Kembali`, "
                    + "`id_brg` AS `ID Barang`, "
                    + "`harga` AS `Harga Sewa`, "
                    + "`denda` AS `Denda`, "
                    + "`total_harga` AS `Total Harga` "
                    + "FROM pengembalian;");
            tabelPengembalian.setModel(DbUtils.resultSetToTableModel(sql));
            
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
    private void getDataPendapatan(){
        try{
            java.sql.Connection conn = (java.sql.Connection) koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery(
                    "SELECT "
                    + "s.id_sewa AS `ID Penyewaan`, s.harga AS `Harga`, k.denda AS `Denda`, k.total_harga AS `Total Harga`, "
                    + "CASE "
                    + "WHEN k.total_harga IS NOT NULL then k.total_harga "
                    + "ELSE s.harga "
                    + "END AS `Harga Akhir` "
                    + "FROM penyewaan s "
                    + "LEFT JOIN pengembalian k "
                    + "ON k.id_sewa = s.id_sewa");
            tabelPendapatan.setModel(DbUtils.resultSetToTableModel(sql));            
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
    private void hitungPendapatan(){
        try{
            java.sql.Connection conn = (java.sql.Connection) koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery("SELECT "
                    + "SUM("
                    + "CASE "
                    + "WHEN k.total_harga IS NOT NULL THEN k.total_harga "
                    + "ELSE s.harga "
                    + "END"
                    + ") AS jmlHargaAkhir "
                    + "FROM penyewaan s "
                    + "LEFT JOIN pengembalian k "
                    + "ON k.id_sewa = s.id_sewa"
                    + "; ");
            if(sql.next()){
                String jmlHrgAkhir = sql.getString("jmlHargaAkhir");
                labelTotalPendapatan.setText(jmlHrgAkhir);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
//    private void hitungDenda(){
//        try{
//            String idSewa = inputIdSewaKmblFK.getText();
//            SimpleDateFormat formatTgl = new SimpleDateFormat("yyyy-MM-dd");
//            String tglKmbl2 = String.valueOf(formatTgl.format(inputTglKembaliKmbl.getDate()));
//            java.sql.Connection conn = (java.sql.Connection) koneksi.Koneksi.koneksiDatabase();
//            java.sql.Statement stmt = conn.createStatement();
//            java.sql.ResultSet sql = stmt.executeQuery("SELECT "
//                    + "s.* , DATEDIFF("+tglKmbl2+", s.tgl_kembali) AS selisih "
//                    + "FROM penyewaan s "
//                    + "WHERE id_sewa = '"+idSewa+"';");
//            if(sql.next()){
//                int selisihTgl = Integer.valueOf(sql.getString("selisih"));
//                int hargaSewa = Integer.valueOf(sql.getString("harga"));
//                int denda = 5000 * selisihTgl;
//                String dendaString =  String.valueOf(denda);
//                inputDendaKmbl.setText(dendaString);
//                //set harga akhir
//                int hrgAkhir = hargaSewa + denda;
//                String hrgAkhirString =  String.valueOf(hrgAkhir);
//                inputTotHrgKmbl.setText(hrgAkhirString);
//            }
//        }catch(Exception e){
//            JOptionPane.showMessageDialog(null, e);
//        }
//    }
    private void getNamaBrg(){
        try {
            java.sql.Connection conn = (java.sql.Connection) koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet hasilNama = stmt.executeQuery("SELECT nm_brg FROM barang;");
            while(hasilNama.next()){
                inputNmBrg.addItem(hasilNama.getString("nm_brg"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    private void getIdBrg(){
        try {
            String brgDipilih = inputNmBrg.getSelectedItem().toString();
            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet hasilIdBrg = stmt.executeQuery("SELECT * FROM barang WHERE nm_brg = '"+brgDipilih+"';");
            if(hasilIdBrg.next()){
                inputIdBrgFK.setText(hasilIdBrg.getString("id_brg"));
                inputStokBrgSewa.setText(hasilIdBrg.getString("stok"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    // reset form utk semua form 
    private void resetFormPel(){
        inputIdPel.setText("");
        inputNikPel.setText("");
        inputNamaPel.setText("");
        inputNoTelpPel.setText("");
        inputAlamatPel.setText("");
        inputTglLahirPel.setCalendar(null);
        inputKotaPel.setText("");
        dataBaru = true;
    }
    private void resetFormBrg(){
        inputIdBrg.setText("");
        inputNamaBrg.setText("");
        inputHargaBrg.setText("");
        inputStokBrg.setText("");
        dataBaru = true;
    }
    private void resetFormSewa(){
        inputIdSewa.setText("");
        inputNipFK.setText(nip);
        inputIdPelFK.setText("");
        inputNmBrg.setSelectedIndex(0);
        inputJmlBrg.setText("1");
        Date tglHariIni = new Date();
        inputTglSewa.setDate(tglHariIni);
        inputLamaSewa.setText("1");
        inputTglKembali.setCalendar(null);
        inputHrgSewa.setText("");
        dataBaru = true;
    }
    private void resetFormPengembalian(){
        inputIdSewaKmblFK.setText("");
        inputNipKmblFK.setText(nip);
        inputTglSewaKmbl.setCalendar(null);
        inputLamaSewaKmbl.setText("");
        Date tglHariIni = new Date();
        inputTglKembaliKmbl.setDate(tglHariIni);
        inputIdBrgKmblFK.setText("");
        inputHargaSewaKmbl.setText("");
        inputDendaKmbl.setText("");
        inputTotHrgKmbl.setText("");
        dataBaru = true;
    }
        
    void setTabColor(JPanel panel, JPanel indikator, JLabel nama_tab) {
        panel.setBackground(new Color(235,152,152));
        indikator.setBackground(new Color(78,23,23));
        nama_tab.setForeground(new Color(78,23,23));
    }
    
    void resetTabColor(JPanel panel, JPanel indikator, JLabel nama_tab) {
        panel.setBackground(new Color(200,79,79));
        indikator.setBackground(new Color(200,79,79));
        nama_tab.setForeground(new Color(240,240,240));
    }
    
    void setTableHeader(JTable tabel) {
        tabel.getTableHeader().setFont(new Font("Seqoe UI", Font.BOLD, 18));
        tabel.getTableHeader().setBackground(new Color(235,152,152));
        tabel.getTableHeader().setForeground(new Color(78,23,23));
        tabel.getTableHeader().setOpaque(false);
    }
    
    void setHargaSewa(){
        try {
            String brgDipilih = inputNmBrg.getSelectedItem().toString();
            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet hasilHrgBrg = stmt.executeQuery("SELECT harga_brg FROM barang WHERE nm_brg = '"+brgDipilih+"';");
            if(hasilHrgBrg.next()){
                int jmlBrg = Integer.valueOf(inputJmlBrg.getText());
                int lamaSewa = Integer.valueOf(inputLamaSewa.getText());
                int hargaBrg = Integer.valueOf(hasilHrgBrg.getString("harga_brg"));
                int hrgSewa = hargaBrg * jmlBrg * lamaSewa;
                String hargaSewa = String.valueOf(hrgSewa);
                inputHrgSewa.setText(hargaSewa);
            }
        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null, e);
        }
//        String hargaSewa = 
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        side_bar = new javax.swing.JPanel();
        tabBeranda = new javax.swing.JPanel();
        labelTabBeranda = new javax.swing.JLabel();
        indikatorBeranda = new javax.swing.JPanel();
        tabBrg = new javax.swing.JPanel();
        labelTabBrg = new javax.swing.JLabel();
        indikatorTabBrg = new javax.swing.JPanel();
        tabSewa = new javax.swing.JPanel();
        labelTabSewa = new javax.swing.JLabel();
        indikatorTabSewa = new javax.swing.JPanel();
        tabKembali = new javax.swing.JPanel();
        labelTabKembali = new javax.swing.JLabel();
        indikatorTabKembali = new javax.swing.JPanel();
        tabPendapatan = new javax.swing.JPanel();
        labelTabPendapatan = new javax.swing.JLabel();
        indikatorTabPendapatan = new javax.swing.JPanel();
        tabLogout = new javax.swing.JPanel();
        labelTabLogout = new javax.swing.JLabel();
        indikatorTabLogout = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        tabPel = new javax.swing.JPanel();
        labelTabPel = new javax.swing.JLabel();
        indikatorTabPel = new javax.swing.JPanel();
        main_content = new javax.swing.JPanel();
        Header = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        main = new javax.swing.JPanel();
        beranda = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        usernameHome = new javax.swing.JLabel();
        dataPel = new javax.swing.JPanel();
        formDataPel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        inputIdPel = new javax.swing.JTextField();
        inputNamaPel = new javax.swing.JTextField();
        inputNoTelpPel = new javax.swing.JTextField();
        inputAlamatPel = new javax.swing.JTextField();
        btnInsertPel = new javax.swing.JButton();
        btnUpdatePel = new javax.swing.JButton();
        btnDeletePel = new javax.swing.JButton();
        btnResetPel = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        inputTglLahirPel = new com.toedter.calendar.JDateChooser();
        jLabel38 = new javax.swing.JLabel();
        inputKotaPel = new javax.swing.JTextField();
        inputNikPel = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabelPelanggan = new javax.swing.JTable();
        jLabel36 = new javax.swing.JLabel();
        inputCariPel = new javax.swing.JTextField();
        btnCariPel = new javax.swing.JButton();
        dataBrg = new javax.swing.JPanel();
        form_data_brg = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        inputIdBrg = new javax.swing.JTextField();
        inputNamaBrg = new javax.swing.JTextField();
        inputHargaBrg = new javax.swing.JTextField();
        inputStokBrg = new javax.swing.JTextField();
        btnInsertBrg = new javax.swing.JButton();
        btnUpdateBrg = new javax.swing.JButton();
        btnDeleteBrg = new javax.swing.JButton();
        btnResetBrg = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelBarang = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        inputCariBrg = new javax.swing.JTextField();
        btnCariBrg = new javax.swing.JButton();
        dataSewa = new javax.swing.JPanel();
        form_data_sewa = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        inputIdSewa = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        inputIdPelFK = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        inputJmlBrg = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        inputTglSewa = new com.toedter.calendar.JDateChooser();
        jLabel16 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        inputLamaSewa = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        inputTglKembali = new com.toedter.calendar.JDateChooser();
        btnResetSewa = new javax.swing.JButton();
        btnInsertSewa = new javax.swing.JButton();
        btnUpdateSewa = new javax.swing.JButton();
        btnDeleteSewa = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        inputHrgSewa = new javax.swing.JTextField();
        btnCetakDataSewa = new javax.swing.JButton();
        jLabel35 = new javax.swing.JLabel();
        inputNipFK = new javax.swing.JTextField();
        inputNmBrg = new javax.swing.JComboBox<>();
        inputIdBrgFK = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        inputStokBrgSewa = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelPenyewaan = new javax.swing.JTable();
        inputCariSewa = new javax.swing.JTextField();
        btnCariSewa = new javax.swing.JButton();
        dataKembali = new javax.swing.JPanel();
        formDataKembali = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        inputIdSewaKmblFK = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        inputIdBrgKmblFK = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        inputDendaKmbl = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        inputTglSewaKmbl = new com.toedter.calendar.JDateChooser();
        jLabel25 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        inputLamaSewaKmbl = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        inputTglKembaliKmbl = new com.toedter.calendar.JDateChooser();
        btnResetKembali = new javax.swing.JButton();
        btnIsertKmbl = new javax.swing.JButton();
        btnUpdateKmbl = new javax.swing.JButton();
        btnDeleteKmbl = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        inputTotHrgKmbl = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        inputHargaSewaKmbl = new javax.swing.JTextField();
        btnCetakKmbl = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        inputNipKmblFK = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabelPengembalian = new javax.swing.JTable();
        inputCariKmbl = new javax.swing.JTextField();
        btnCariKmbl = new javax.swing.JButton();
        pendapatan = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabelPendapatan = new javax.swing.JTable();
        labelTotalPendapatan = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        side_bar.setBackground(new java.awt.Color(200, 79, 79));
        side_bar.setPreferredSize(new java.awt.Dimension(300, 1080));

        tabBeranda.setBackground(new java.awt.Color(235, 152, 152));
        tabBeranda.setPreferredSize(new java.awt.Dimension(280, 60));
        tabBeranda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabBerandaMouseClicked(evt);
            }
        });
        tabBeranda.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTabBeranda.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        labelTabBeranda.setForeground(new java.awt.Color(78, 23, 23));
        labelTabBeranda.setText("Beranda");
        tabBeranda.add(labelTabBeranda, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 14, -1, -1));

        indikatorBeranda.setBackground(new java.awt.Color(78, 23, 23));
        indikatorBeranda.setPreferredSize(new java.awt.Dimension(15, 60));

        javax.swing.GroupLayout indikatorBerandaLayout = new javax.swing.GroupLayout(indikatorBeranda);
        indikatorBeranda.setLayout(indikatorBerandaLayout);
        indikatorBerandaLayout.setHorizontalGroup(
            indikatorBerandaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        indikatorBerandaLayout.setVerticalGroup(
            indikatorBerandaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        tabBeranda.add(indikatorBeranda, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 60));

        tabBrg.setBackground(new java.awt.Color(200, 79, 79));
        tabBrg.setPreferredSize(new java.awt.Dimension(280, 60));
        tabBrg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabBrgMouseClicked(evt);
            }
        });
        tabBrg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTabBrg.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        labelTabBrg.setForeground(new java.awt.Color(247, 231, 231));
        labelTabBrg.setText("Data Barang");
        tabBrg.add(labelTabBrg, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 14, -1, -1));

        indikatorTabBrg.setBackground(new java.awt.Color(200, 79, 79));
        indikatorTabBrg.setPreferredSize(new java.awt.Dimension(15, 60));

        javax.swing.GroupLayout indikatorTabBrgLayout = new javax.swing.GroupLayout(indikatorTabBrg);
        indikatorTabBrg.setLayout(indikatorTabBrgLayout);
        indikatorTabBrgLayout.setHorizontalGroup(
            indikatorTabBrgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        indikatorTabBrgLayout.setVerticalGroup(
            indikatorTabBrgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        tabBrg.add(indikatorTabBrg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 60));

        tabSewa.setBackground(new java.awt.Color(200, 79, 79));
        tabSewa.setPreferredSize(new java.awt.Dimension(250, 60));
        tabSewa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabSewaMouseClicked(evt);
            }
        });
        tabSewa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTabSewa.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        labelTabSewa.setForeground(new java.awt.Color(247, 231, 231));
        labelTabSewa.setText("Data Penyewaan");
        tabSewa.add(labelTabSewa, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 14, -1, -1));

        indikatorTabSewa.setBackground(new java.awt.Color(200, 79, 79));
        indikatorTabSewa.setPreferredSize(new java.awt.Dimension(15, 60));

        javax.swing.GroupLayout indikatorTabSewaLayout = new javax.swing.GroupLayout(indikatorTabSewa);
        indikatorTabSewa.setLayout(indikatorTabSewaLayout);
        indikatorTabSewaLayout.setHorizontalGroup(
            indikatorTabSewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        indikatorTabSewaLayout.setVerticalGroup(
            indikatorTabSewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        tabSewa.add(indikatorTabSewa, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 60));

        tabKembali.setBackground(new java.awt.Color(200, 79, 79));
        tabKembali.setPreferredSize(new java.awt.Dimension(250, 60));
        tabKembali.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabKembaliMouseClicked(evt);
            }
        });
        tabKembali.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTabKembali.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        labelTabKembali.setForeground(new java.awt.Color(247, 231, 231));
        labelTabKembali.setText("Data Pengembalian");
        tabKembali.add(labelTabKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 14, -1, -1));

        indikatorTabKembali.setBackground(new java.awt.Color(200, 79, 79));
        indikatorTabKembali.setPreferredSize(new java.awt.Dimension(15, 60));

        javax.swing.GroupLayout indikatorTabKembaliLayout = new javax.swing.GroupLayout(indikatorTabKembali);
        indikatorTabKembali.setLayout(indikatorTabKembaliLayout);
        indikatorTabKembaliLayout.setHorizontalGroup(
            indikatorTabKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        indikatorTabKembaliLayout.setVerticalGroup(
            indikatorTabKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        tabKembali.add(indikatorTabKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 60));

        tabPendapatan.setBackground(new java.awt.Color(200, 79, 79));
        tabPendapatan.setPreferredSize(new java.awt.Dimension(250, 60));
        tabPendapatan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabPendapatanMouseClicked(evt);
            }
        });
        tabPendapatan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTabPendapatan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        labelTabPendapatan.setForeground(new java.awt.Color(247, 231, 231));
        labelTabPendapatan.setText("Pendapatan");
        tabPendapatan.add(labelTabPendapatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 14, -1, -1));

        indikatorTabPendapatan.setBackground(new java.awt.Color(200, 79, 79));
        indikatorTabPendapatan.setPreferredSize(new java.awt.Dimension(15, 60));

        javax.swing.GroupLayout indikatorTabPendapatanLayout = new javax.swing.GroupLayout(indikatorTabPendapatan);
        indikatorTabPendapatan.setLayout(indikatorTabPendapatanLayout);
        indikatorTabPendapatanLayout.setHorizontalGroup(
            indikatorTabPendapatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        indikatorTabPendapatanLayout.setVerticalGroup(
            indikatorTabPendapatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        tabPendapatan.add(indikatorTabPendapatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 60));

        tabLogout.setBackground(new java.awt.Color(200, 79, 79));
        tabLogout.setPreferredSize(new java.awt.Dimension(250, 60));
        tabLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabLogoutMouseClicked(evt);
            }
        });
        tabLogout.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTabLogout.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        labelTabLogout.setForeground(new java.awt.Color(247, 231, 231));
        labelTabLogout.setText("Logout");
        tabLogout.add(labelTabLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 14, -1, -1));

        indikatorTabLogout.setBackground(new java.awt.Color(200, 79, 79));
        indikatorTabLogout.setPreferredSize(new java.awt.Dimension(15, 60));

        javax.swing.GroupLayout indikatorTabLogoutLayout = new javax.swing.GroupLayout(indikatorTabLogout);
        indikatorTabLogout.setLayout(indikatorTabLogoutLayout);
        indikatorTabLogoutLayout.setHorizontalGroup(
            indikatorTabLogoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        indikatorTabLogoutLayout.setVerticalGroup(
            indikatorTabLogoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        tabLogout.add(indikatorTabLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 60));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gambar/1635909092146 size150.png"))); // NOI18N

        tabPel.setBackground(new java.awt.Color(200, 79, 79));
        tabPel.setPreferredSize(new java.awt.Dimension(280, 60));
        tabPel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabPelMouseClicked(evt);
            }
        });
        tabPel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTabPel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        labelTabPel.setForeground(new java.awt.Color(247, 231, 231));
        labelTabPel.setText("Data Pelanggan");
        tabPel.add(labelTabPel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 14, -1, -1));

        indikatorTabPel.setBackground(new java.awt.Color(200, 79, 79));
        indikatorTabPel.setPreferredSize(new java.awt.Dimension(15, 60));

        javax.swing.GroupLayout indikatorTabPelLayout = new javax.swing.GroupLayout(indikatorTabPel);
        indikatorTabPel.setLayout(indikatorTabPelLayout);
        indikatorTabPelLayout.setHorizontalGroup(
            indikatorTabPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        indikatorTabPelLayout.setVerticalGroup(
            indikatorTabPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        tabPel.add(indikatorTabPel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 60));

        javax.swing.GroupLayout side_barLayout = new javax.swing.GroupLayout(side_bar);
        side_bar.setLayout(side_barLayout);
        side_barLayout.setHorizontalGroup(
            side_barLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabBeranda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabBrg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabSewa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabKembali, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabPendapatan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(side_barLayout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(tabPel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        side_barLayout.setVerticalGroup(
            side_barLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(side_barLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(0, 0, 0)
                .addComponent(tabBeranda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(tabPel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(tabBrg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(tabSewa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(tabKembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(tabPendapatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 424, Short.MAX_VALUE)
                .addComponent(tabLogout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70))
        );

        getContentPane().add(side_bar, java.awt.BorderLayout.WEST);

        main_content.setLayout(new java.awt.BorderLayout());

        Header.setBackground(new java.awt.Color(255, 255, 255));
        Header.setPreferredSize(new java.awt.Dimension(1620, 100));
        Header.setLayout(new javax.swing.BoxLayout(Header, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel4.setOpaque(false);
        jPanel4.setPreferredSize(new java.awt.Dimension(2246, 20));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1620, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 26, Short.MAX_VALUE)
        );

        Header.add(jPanel4);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel5.setText("RENTAL OUTDOOR");
        jLabel5.setAlignmentX(0.5F);
        Header.add(jLabel5);

        jPanel5.setOpaque(false);
        jPanel5.setPreferredSize(new java.awt.Dimension(2246, 20));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1620, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 26, Short.MAX_VALUE)
        );

        Header.add(jPanel5);

        main_content.add(Header, java.awt.BorderLayout.PAGE_START);

        jScrollPane3.setBorder(null);
        jScrollPane3.setPreferredSize(new java.awt.Dimension(1620, 100));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.BorderLayout());

        main.setBackground(new java.awt.Color(255, 255, 255));
        main.setPreferredSize(new java.awt.Dimension(1620, 980));
        main.setLayout(new java.awt.CardLayout());

        beranda.setBackground(new java.awt.Color(255, 255, 255));
        beranda.setPreferredSize(new java.awt.Dimension(1620, 980));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 30)); // NOI18N
        jLabel21.setText("Selamat datang, ");

        usernameHome.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        usernameHome.setText("Username");

        javax.swing.GroupLayout berandaLayout = new javax.swing.GroupLayout(beranda);
        beranda.setLayout(berandaLayout);
        berandaLayout.setHorizontalGroup(
            berandaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(berandaLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameHome)
                .addContainerGap(1192, Short.MAX_VALUE))
        );
        berandaLayout.setVerticalGroup(
            berandaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(berandaLayout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(berandaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(usernameHome))
                .addContainerGap(871, Short.MAX_VALUE))
        );

        main.add(beranda, "beranda");

        dataPel.setBackground(new java.awt.Color(255, 255, 255));
        dataPel.setPreferredSize(new java.awt.Dimension(1620, 980));

        formDataPel.setBackground(new java.awt.Color(255, 255, 255));
        formDataPel.setOpaque(false);
        formDataPel.setPreferredSize(new java.awt.Dimension(500, 750));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel4.setText("Id Pelanggan");

        jLabel32.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel32.setText("Nama");

        jLabel33.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel33.setText("No. Telp");

        jLabel34.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel34.setText("Alamat");

        inputIdPel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        inputNamaPel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        inputNoTelpPel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        inputAlamatPel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        btnInsertPel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnInsertPel.setText("Insert");
        btnInsertPel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertPelActionPerformed(evt);
            }
        });

        btnUpdatePel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnUpdatePel.setText("Update");
        btnUpdatePel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdatePelActionPerformed(evt);
            }
        });

        btnDeletePel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnDeletePel.setText("Delete");
        btnDeletePel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletePelActionPerformed(evt);
            }
        });

        btnResetPel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnResetPel.setText("Reset");
        btnResetPel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetPelActionPerformed(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel37.setText("Tanggal Lahir");

        inputTglLahirPel.setDateFormatString("yyyy-MM-dd");
        inputTglLahirPel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        inputTglLahirPel.setPreferredSize(new java.awt.Dimension(119, 33));

        jLabel38.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel38.setText("Kota");

        inputKotaPel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        inputNikPel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        jLabel40.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel40.setText("NIK");

        javax.swing.GroupLayout formDataPelLayout = new javax.swing.GroupLayout(formDataPel);
        formDataPel.setLayout(formDataPelLayout);
        formDataPelLayout.setHorizontalGroup(
            formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formDataPelLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40)
                    .addGroup(formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel37)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formDataPelLayout.createSequentialGroup()
                            .addComponent(btnResetPel)
                            .addGap(39, 39, 39)
                            .addComponent(btnInsertPel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnUpdatePel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnDeletePel))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formDataPelLayout.createSequentialGroup()
                            .addGroup(formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel32)
                                .addComponent(jLabel4)
                                .addComponent(jLabel33)
                                .addComponent(jLabel34))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(inputIdPel, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                .addComponent(inputNamaPel)
                                .addComponent(inputNoTelpPel)
                                .addComponent(inputAlamatPel)
                                .addComponent(inputTglLahirPel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(inputNikPel)))
                        .addGroup(formDataPelLayout.createSequentialGroup()
                            .addComponent(jLabel38)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inputKotaPel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        formDataPelLayout.setVerticalGroup(
            formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formDataPelLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(inputIdPel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(inputNikPel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(inputNamaPel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(inputNoTelpPel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(inputAlamatPel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel37)
                    .addComponent(inputTglLahirPel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(inputKotaPel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(formDataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInsertPel)
                    .addComponent(btnUpdatePel)
                    .addComponent(btnDeletePel)
                    .addComponent(btnResetPel))
                .addContainerGap(61, Short.MAX_VALUE))
        );

        tabelPelanggan.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tabelPelanggan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id Pelanggan", "Nama Pelanggan", "No. Telp", "Alamat", "Tanggal Lahir", "Kota"
            }
        ));
        tabelPelanggan.setRowHeight(30);
        tabelPelanggan.setSelectionBackground(new java.awt.Color(174, 203, 248));
        tabelPelanggan.getTableHeader().setReorderingAllowed(false);
        tabelPelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelPelangganMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(tabelPelanggan);

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel36.setText("Kelola Data Pelanggan");

        inputCariPel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        btnCariPel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnCariPel.setText("Cari");
        btnCariPel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariPelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dataPelLayout = new javax.swing.GroupLayout(dataPel);
        dataPel.setLayout(dataPelLayout);
        dataPelLayout.setHorizontalGroup(
            dataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPelLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(dataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36)
                    .addGroup(dataPelLayout.createSequentialGroup()
                        .addComponent(formDataPel, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addGroup(dataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dataPelLayout.createSequentialGroup()
                                .addComponent(inputCariPel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCariPel))
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 858, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(165, Short.MAX_VALUE))
        );
        dataPelLayout.setVerticalGroup(
            dataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPelLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel36)
                .addGroup(dataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dataPelLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(dataPelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inputCariPel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCariPel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dataPelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(formDataPel, javax.swing.GroupLayout.PREFERRED_SIZE, 497, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(357, Short.MAX_VALUE))
        );

        main.add(dataPel, "data_pel");

        dataBrg.setBackground(new java.awt.Color(255, 255, 255));
        dataBrg.setPreferredSize(new java.awt.Dimension(1620, 980));

        form_data_brg.setBackground(new java.awt.Color(255, 255, 255));
        form_data_brg.setOpaque(false);
        form_data_brg.setPreferredSize(new java.awt.Dimension(500, 750));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel1.setText("Id Barang");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel6.setText("Nama Barang");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel7.setText("Harga Sewa");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel8.setText("Stok Barang");

        inputIdBrg.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        inputNamaBrg.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        inputHargaBrg.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        inputStokBrg.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        btnInsertBrg.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnInsertBrg.setText("Insert");
        btnInsertBrg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertBrgActionPerformed(evt);
            }
        });

        btnUpdateBrg.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnUpdateBrg.setText("Update");
        btnUpdateBrg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateBrgActionPerformed(evt);
            }
        });

        btnDeleteBrg.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnDeleteBrg.setText("Delete");
        btnDeleteBrg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteBrgActionPerformed(evt);
            }
        });

        btnResetBrg.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnResetBrg.setText("Reset");
        btnResetBrg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetBrgActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout form_data_brgLayout = new javax.swing.GroupLayout(form_data_brg);
        form_data_brg.setLayout(form_data_brgLayout);
        form_data_brgLayout.setHorizontalGroup(
            form_data_brgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(form_data_brgLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(form_data_brgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(form_data_brgLayout.createSequentialGroup()
                        .addComponent(btnResetBrg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                        .addComponent(btnInsertBrg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdateBrg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteBrg))
                    .addGroup(form_data_brgLayout.createSequentialGroup()
                        .addGroup(form_data_brgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel1)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addGroup(form_data_brgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(inputIdBrg, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .addComponent(inputNamaBrg)
                            .addComponent(inputHargaBrg)
                            .addComponent(inputStokBrg))))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        form_data_brgLayout.setVerticalGroup(
            form_data_brgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(form_data_brgLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(form_data_brgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(inputIdBrg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(form_data_brgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(inputNamaBrg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(form_data_brgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(inputHargaBrg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(form_data_brgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(inputStokBrg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(form_data_brgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInsertBrg)
                    .addComponent(btnUpdateBrg)
                    .addComponent(btnDeleteBrg)
                    .addComponent(btnResetBrg))
                .addContainerGap())
        );

        tabelBarang.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tabelBarang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id Barang", "Nama Barang", "Harga Sewa", "Stok Barang"
            }
        ));
        tabelBarang.setRowHeight(30);
        tabelBarang.setSelectionBackground(new java.awt.Color(174, 203, 248));
        tabelBarang.getTableHeader().setReorderingAllowed(false);
        tabelBarang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelBarangMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelBarang);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel9.setText("Kelola Data Barang");

        inputCariBrg.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        btnCariBrg.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnCariBrg.setText("Cari");
        btnCariBrg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariBrgActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dataBrgLayout = new javax.swing.GroupLayout(dataBrg);
        dataBrg.setLayout(dataBrgLayout);
        dataBrgLayout.setHorizontalGroup(
            dataBrgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataBrgLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(dataBrgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(dataBrgLayout.createSequentialGroup()
                        .addComponent(form_data_brg, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addGroup(dataBrgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(dataBrgLayout.createSequentialGroup()
                                .addComponent(inputCariBrg, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCariBrg)))))
                .addContainerGap(323, Short.MAX_VALUE))
        );
        dataBrgLayout.setVerticalGroup(
            dataBrgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataBrgLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel9)
                .addGroup(dataBrgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dataBrgLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(form_data_brg, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dataBrgLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(dataBrgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inputCariBrg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCariBrg))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(357, Short.MAX_VALUE))
        );

        main.add(dataBrg, "data_brg");

        dataSewa.setBackground(new java.awt.Color(255, 255, 255));
        dataSewa.setPreferredSize(new java.awt.Dimension(1620, 980));

        form_data_sewa.setOpaque(false);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel11.setText("Id Penyewaan");

        inputIdSewa.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel12.setText("Id Pelanggan");

        inputIdPelFK.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel13.setText("Barang");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel14.setText("Jumlah Barang");

        inputJmlBrg.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        inputJmlBrg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputJmlBrgKeyReleased(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel15.setText("Tanggal Sewa");

        inputTglSewa.setDateFormatString("yyyy-MM-dd");
        inputTglSewa.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel16.setText("Lama Sewa");

        jPanel1.setOpaque(false);

        inputLamaSewa.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        inputLamaSewa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputLamaSewaKeyReleased(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel17.setText("hari");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(inputLamaSewa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(inputLamaSewa)
                .addComponent(jLabel17))
        );

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel18.setText("Tanggal Kembali");

        inputTglKembali.setDateFormatString("yyyy-MM-dd");
        inputTglKembali.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        btnResetSewa.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnResetSewa.setText("Reset");
        btnResetSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetSewaActionPerformed(evt);
            }
        });

        btnInsertSewa.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnInsertSewa.setText("Insert");
        btnInsertSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertSewaActionPerformed(evt);
            }
        });

        btnUpdateSewa.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnUpdateSewa.setText("Update");
        btnUpdateSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateSewaActionPerformed(evt);
            }
        });

        btnDeleteSewa.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnDeleteSewa.setText("Delete");
        btnDeleteSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSewaActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel19.setText("Harga ");

        inputHrgSewa.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        btnCetakDataSewa.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnCetakDataSewa.setText("Cetak");
        btnCetakDataSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakDataSewaActionPerformed(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel35.setText("NIP");

        inputNipFK.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        inputNipFK.setEnabled(false);

        inputNmBrg.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        inputNmBrg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputNmBrgActionPerformed(evt);
            }
        });

        inputIdBrgFK.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        inputIdBrgFK.setText("Id Barang");

        jLabel31.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel31.setText("Id Barang");

        jLabel39.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel39.setText("Stok");

        inputStokBrgSewa.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        inputStokBrgSewa.setText("Stok");

        javax.swing.GroupLayout form_data_sewaLayout = new javax.swing.GroupLayout(form_data_sewa);
        form_data_sewa.setLayout(form_data_sewaLayout);
        form_data_sewaLayout.setHorizontalGroup(
            form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(form_data_sewaLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCetakDataSewa)
                    .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(form_data_sewaLayout.createSequentialGroup()
                            .addComponent(jLabel19)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inputHrgSewa, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(form_data_sewaLayout.createSequentialGroup()
                            .addComponent(jLabel18)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(inputTglKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, form_data_sewaLayout.createSequentialGroup()
                            .addComponent(btnResetSewa)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnInsertSewa)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnUpdateSewa)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnDeleteSewa))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, form_data_sewaLayout.createSequentialGroup()
                            .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel12)
                                .addComponent(jLabel11)
                                .addComponent(jLabel13)
                                .addComponent(jLabel14)
                                .addComponent(jLabel15)
                                .addComponent(jLabel16)
                                .addComponent(jLabel35)
                                .addComponent(jLabel31)
                                .addComponent(jLabel39))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(inputIdBrgFK)
                                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(inputNipFK)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(inputTglSewa, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                    .addComponent(inputIdSewa)
                                    .addComponent(inputIdPelFK)
                                    .addComponent(inputJmlBrg, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                    .addComponent(inputNmBrg, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(inputStokBrgSewa)))))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        form_data_sewaLayout.setVerticalGroup(
            form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(form_data_sewaLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(inputIdSewa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(inputNipFK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(inputIdPelFK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(inputNmBrg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputIdBrgFK)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(inputStokBrgSewa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(inputJmlBrg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(inputTglSewa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(inputTglKembali, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputHrgSewa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addGap(18, 18, 18)
                .addGroup(form_data_sewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInsertSewa)
                    .addComponent(btnUpdateSewa)
                    .addComponent(btnDeleteSewa)
                    .addComponent(btnResetSewa))
                .addGap(18, 18, 18)
                .addComponent(btnCetakDataSewa)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel10.setText("Kelola Data Penyewaan");

        tabelPenyewaan.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tabelPenyewaan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id Penyewaan", "Id Pegawai", "Id Pelanggan", "Id Barang", "Jumlah Barang", "Tanggal Sewa", "Lama Sewa", "Tanggal kembali", "Harga Sewa"
            }
        ));
        tabelPenyewaan.setRowHeight(30);
        tabelPenyewaan.setSelectionBackground(new java.awt.Color(174, 203, 248));
        tabelPenyewaan.getTableHeader().setReorderingAllowed(false);
        tabelPenyewaan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelPenyewaanMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabelPenyewaan);

        inputCariSewa.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        btnCariSewa.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnCariSewa.setText("Cari");
        btnCariSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariSewaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dataSewaLayout = new javax.swing.GroupLayout(dataSewa);
        dataSewa.setLayout(dataSewaLayout);
        dataSewaLayout.setHorizontalGroup(
            dataSewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataSewaLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(dataSewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(dataSewaLayout.createSequentialGroup()
                        .addComponent(form_data_sewa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addGroup(dataSewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 950, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(dataSewaLayout.createSequentialGroup()
                                .addComponent(inputCariSewa, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCariSewa)))))
                .addContainerGap(62, Short.MAX_VALUE))
        );
        dataSewaLayout.setVerticalGroup(
            dataSewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataSewaLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel10)
                .addGroup(dataSewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dataSewaLayout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(dataSewaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inputCariSewa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCariSewa))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dataSewaLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(form_data_sewa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(189, Short.MAX_VALUE))
        );

        main.add(dataSewa, "data_sewa");

        dataKembali.setBackground(new java.awt.Color(255, 255, 255));
        dataKembali.setPreferredSize(new java.awt.Dimension(1620, 980));

        formDataKembali.setOpaque(false);

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel20.setText("Id Penyewaan");

        inputIdSewaKmblFK.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        inputIdSewaKmblFK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputIdSewaKmblFKKeyReleased(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel22.setText("Id Barang");

        inputIdBrgKmblFK.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        inputIdBrgKmblFK.setEnabled(false);

        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel23.setText("Denda");

        inputDendaKmbl.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        inputDendaKmbl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputDendaKmblKeyReleased(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel24.setText("Tanggal Sewa");

        inputTglSewaKmbl.setDateFormatString("yyyy-MM-dd");
        inputTglSewaKmbl.setEnabled(false);
        inputTglSewaKmbl.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel25.setText("Lama Sewa");

        jPanel3.setOpaque(false);

        inputLamaSewaKmbl.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        inputLamaSewaKmbl.setEnabled(false);

        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel26.setText("hari");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(inputLamaSewaKmbl, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26)
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(inputLamaSewaKmbl)
                .addComponent(jLabel26))
        );

        jLabel27.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel27.setText("Tanggal Kembali");

        inputTglKembaliKmbl.setDateFormatString("yyyy-MM-dd");
        inputTglKembaliKmbl.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        inputTglKembaliKmbl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputTglKembaliKmblKeyReleased(evt);
            }
        });

        btnResetKembali.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnResetKembali.setText("Reset");
        btnResetKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetKembaliActionPerformed(evt);
            }
        });

        btnIsertKmbl.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnIsertKmbl.setText("Insert");
        btnIsertKmbl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIsertKmblActionPerformed(evt);
            }
        });

        btnUpdateKmbl.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnUpdateKmbl.setText("Update");
        btnUpdateKmbl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateKmblActionPerformed(evt);
            }
        });

        btnDeleteKmbl.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnDeleteKmbl.setText("Delete");
        btnDeleteKmbl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteKmblActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel28.setText("Total Harga ");

        inputTotHrgKmbl.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        jLabel30.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel30.setText("Harga");

        inputHargaSewaKmbl.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        inputHargaSewaKmbl.setEnabled(false);

        btnCetakKmbl.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnCetakKmbl.setText("Cetak");
        btnCetakKmbl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakKmblActionPerformed(evt);
            }
        });

        jLabel41.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel41.setText("NIP");

        inputNipKmblFK.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        inputNipKmblFK.setEnabled(false);

        javax.swing.GroupLayout formDataKembaliLayout = new javax.swing.GroupLayout(formDataKembali);
        formDataKembali.setLayout(formDataKembaliLayout);
        formDataKembaliLayout.setHorizontalGroup(
            formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formDataKembaliLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formDataKembaliLayout.createSequentialGroup()
                        .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formDataKembaliLayout.createSequentialGroup()
                                    .addComponent(btnResetKembali)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnIsertKmbl)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnUpdateKmbl)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(btnDeleteKmbl)
                                        .addComponent(btnCetakKmbl)))
                                .addGroup(formDataKembaliLayout.createSequentialGroup()
                                    .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel20)
                                        .addComponent(jLabel41))
                                    .addGap(45, 45, 45)
                                    .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(inputNipKmblFK, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(inputIdSewaKmblFK, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel30)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23)
                            .addComponent(jLabel28)
                            .addGroup(formDataKembaliLayout.createSequentialGroup()
                                .addGap(179, 179, 179)
                                .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(inputTotHrgKmbl, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(inputDendaKmbl, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(inputHargaSewaKmbl, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(inputIdBrgKmblFK, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(formDataKembaliLayout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addGap(18, 18, 18)
                                .addComponent(inputTglKembaliKmbl, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(32, Short.MAX_VALUE))
                    .addGroup(formDataKembaliLayout.createSequentialGroup()
                        .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(jLabel25))
                        .addGap(45, 45, 45)
                        .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inputTglSewaKmbl, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        formDataKembaliLayout.setVerticalGroup(
            formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formDataKembaliLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(inputIdSewaKmblFK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputNipKmblFK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(inputTglSewaKmbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(inputTglKembaliKmbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formDataKembaliLayout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(19, 19, 19)
                        .addComponent(jLabel30)
                        .addGap(19, 19, 19)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel28)
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formDataKembaliLayout.createSequentialGroup()
                        .addComponent(inputIdBrgKmblFK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(inputHargaSewaKmbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(inputDendaKmbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(inputTotHrgKmbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(formDataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnIsertKmbl)
                    .addComponent(btnUpdateKmbl)
                    .addComponent(btnDeleteKmbl)
                    .addComponent(btnResetKembali))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCetakKmbl)
                .addContainerGap(62, Short.MAX_VALUE))
        );

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel29.setText("Kelola Data Pengembalian");

        tabelPengembalian.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tabelPengembalian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id Penyewaan", "Id Pegawai", "Tanggal Sewa", "Lama Sewa", "Tanggal Kembali", "Id Barang", "Harga", "Denda", "Total Harga"
            }
        ));
        tabelPengembalian.setRowHeight(30);
        tabelPengembalian.setSelectionBackground(new java.awt.Color(174, 203, 248));
        tabelPengembalian.getTableHeader().setReorderingAllowed(false);
        tabelPengembalian.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelPengembalianMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tabelPengembalian);

        inputCariKmbl.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        btnCariKmbl.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        btnCariKmbl.setText("Cari");
        btnCariKmbl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariKmblActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dataKembaliLayout = new javax.swing.GroupLayout(dataKembali);
        dataKembali.setLayout(dataKembaliLayout);
        dataKembaliLayout.setHorizontalGroup(
            dataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataKembaliLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(dataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29)
                    .addGroup(dataKembaliLayout.createSequentialGroup()
                        .addComponent(formDataKembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addGroup(dataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 950, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(dataKembaliLayout.createSequentialGroup()
                                .addComponent(inputCariKmbl, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCariKmbl)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dataKembaliLayout.setVerticalGroup(
            dataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataKembaliLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel29)
                .addGroup(dataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dataKembaliLayout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addGroup(dataKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inputCariKmbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCariKmbl))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dataKembaliLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(formDataKembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(241, Short.MAX_VALUE))
        );

        main.add(dataKembali, "data_kembali");

        pendapatan.setBackground(new java.awt.Color(255, 255, 255));
        pendapatan.setPreferredSize(new java.awt.Dimension(1620, 980));
        pendapatan.setLayout(new java.awt.BorderLayout());

        jPanel9.setOpaque(false);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setText("Total Pendapatan : Rp ");

        tabelPendapatan.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tabelPendapatan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id Penyewaan", "Harga Sewa", "Denda", "Total Harga"
            }
        ));
        tabelPendapatan.setOpaque(false);
        tabelPendapatan.setRowHeight(30);
        tabelPendapatan.setSelectionBackground(new java.awt.Color(174, 203, 248));
        tabelPendapatan.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(tabelPendapatan);

        labelTotalPendapatan.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        labelTotalPendapatan.setText("(Total harga akhir)");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, 0)
                        .addComponent(labelTotalPendapatan, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1381, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(labelTotalPendapatan))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(439, Short.MAX_VALUE))
        );

        pendapatan.add(jPanel9, java.awt.BorderLayout.CENTER);

        jPanel10.setOpaque(false);
        jPanel10.setPreferredSize(new java.awt.Dimension(100, 980));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 980, Short.MAX_VALUE)
        );

        pendapatan.add(jPanel10, java.awt.BorderLayout.LINE_START);

        jPanel11.setOpaque(false);
        jPanel11.setPreferredSize(new java.awt.Dimension(100, 980));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 980, Short.MAX_VALUE)
        );

        pendapatan.add(jPanel11, java.awt.BorderLayout.LINE_END);

        main.add(pendapatan, "pendapatan");

        jPanel2.add(main, java.awt.BorderLayout.CENTER);

        jScrollPane3.setViewportView(jPanel2);

        main_content.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        getContentPane().add(main_content, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabBrgMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabBrgMouseClicked
        CardLayout card = (CardLayout)main.getLayout();
        card.show(main, "data_brg");
        setTabColor(tabBrg, indikatorTabBrg, labelTabBrg);
        resetTabColor(tabSewa, indikatorTabSewa, labelTabSewa);
        resetTabColor(tabKembali, indikatorTabKembali, labelTabKembali);
        resetTabColor(tabBeranda, indikatorBeranda, labelTabBeranda);
        resetTabColor(tabPendapatan, indikatorTabPendapatan, labelTabPendapatan);
        resetTabColor(tabLogout, indikatorTabLogout, labelTabLogout);
        resetTabColor(tabPel, indikatorTabPel, labelTabPel);
        
        getDataBrg();
    }//GEN-LAST:event_tabBrgMouseClicked

    private void tabSewaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabSewaMouseClicked
        CardLayout card = (CardLayout)main.getLayout();
        card.show(main, "data_sewa");
        
        setTabColor(tabSewa, indikatorTabSewa, labelTabSewa);
        resetTabColor(tabBrg, indikatorTabBrg, labelTabBrg);
        resetTabColor(tabKembali, indikatorTabKembali, labelTabKembali);
        resetTabColor(tabBeranda, indikatorBeranda, labelTabBeranda);
        resetTabColor(tabPendapatan, indikatorTabPendapatan, labelTabPendapatan);
        resetTabColor(tabLogout, indikatorTabLogout, labelTabLogout);
        resetTabColor(tabPel, indikatorTabPel, labelTabPel);
        
        getIdBrg();
    }//GEN-LAST:event_tabSewaMouseClicked

    private void tabKembaliMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabKembaliMouseClicked
        CardLayout card = (CardLayout)main.getLayout();
        card.show(main, "data_kembali");
        
        setTabColor(tabKembali, indikatorTabKembali, labelTabKembali);
        resetTabColor(tabBrg, indikatorTabBrg, labelTabBrg);
        resetTabColor(tabSewa, indikatorTabSewa, labelTabSewa);
        resetTabColor(tabBeranda, indikatorBeranda, labelTabBeranda);
        resetTabColor(tabPendapatan, indikatorTabPendapatan, labelTabPendapatan);
        resetTabColor(tabLogout, indikatorTabLogout, labelTabLogout);
        resetTabColor(tabPel, indikatorTabPel, labelTabPel);
        
        resetFormPengembalian();
    }//GEN-LAST:event_tabKembaliMouseClicked

    private void btnUpdateBrgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateBrgActionPerformed
        // TODO add your handling code here:
        dataBaru = false;
        try{
            String sql = "UPDATE barang SET "
                    + "id_brg='"+inputIdBrg.getText()+"'"
                    + ", nm_brg='"+inputNamaBrg.getText()+"'"
                    + ", harga_brg='"+inputHargaBrg.getText()+"'"
                    + ", stok='"+inputStokBrg.getText()+"'"
                    + " WHERE id_brg='"+inputIdBrg.getText()+"';";

            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.execute();
            inputIdBrg.requestFocus();
            JOptionPane.showMessageDialog(null, "Perubahan data berhasil");
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, e);
        }
        getDataBrg();
        resetFormBrg();
        
    }//GEN-LAST:event_btnUpdateBrgActionPerformed

    private void btnDeleteBrgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteBrgActionPerformed
        // TODO add your handling code here:
        try{
            String idBrg = inputIdBrg.getText();
            String sql = "DELETE FROM barang WHERE id_brg='"+idBrg+"'";
            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.PreparedStatement pStmt =conn.prepareStatement(sql);
            pStmt.execute();
            JOptionPane.showMessageDialog(null, "Data akan dihapus?");
            dataBaru=true;
            resetFormBrg(); 
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, e);
        }
        getDataBrg();
        
    }//GEN-LAST:event_btnDeleteBrgActionPerformed

    private void btnResetBrgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetBrgActionPerformed
        resetFormBrg();
    }//GEN-LAST:event_btnResetBrgActionPerformed

    private void btnResetSewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetSewaActionPerformed
        // TODO add your handling code here:
        resetFormSewa();
    }//GEN-LAST:event_btnResetSewaActionPerformed

    private void btnUpdateSewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateSewaActionPerformed
        // TODO add your handling code here:
        if(dataBaru == false){
            //utk tanggal
            SimpleDateFormat formatTgl = new SimpleDateFormat("yyyy-MM-dd");
            String tglSewa = String.valueOf(formatTgl.format(inputTglSewa.getDate()));
            String tglKembali = String.valueOf(formatTgl.format(inputTglKembali.getDate()));
            try{
                String sql = "UPDATE penyewaan SET "
                        + "id_sewa='"+inputIdSewa.getText()+"'"
                        + ", nip='"+inputNipFK.getText()+"'"
                        + ", id_pel='"+inputIdPelFK.getText()+"'"
                        + ", id_brg='"+inputIdBrgFK.getText()+"'"
                        + ", jml_brg='"+inputJmlBrg.getText()+"'"
                        + ", tgl_sewa='"+tglSewa+"'"
                        + ", lama_sewa='"+inputLamaSewa.getText()+"'"
                        + ", tgl_kembali='"+tglKembali+"'"
                        + ", harga='"+inputHrgSewa.getText()+"'"
                        + " WHERE id_sewa='"+inputIdSewa.getText()+"';";

                java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
                java.sql.PreparedStatement pStmt = conn.prepareStatement(sql);
                pStmt.execute();
                inputIdSewa.requestFocus();
                JOptionPane.showMessageDialog(null, "Perubahan data berhasil");
            }catch(SQLException | HeadlessException e){
                JOptionPane.showMessageDialog(null, e);
            }
            getDataSewa();
            resetFormSewa();
        }
        
    }//GEN-LAST:event_btnUpdateSewaActionPerformed

    private void btnDeleteSewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteSewaActionPerformed
        try{
            String idSewa = inputIdSewa.getText();
            String sql = "DELETE FROM penyewaan WHERE id_sewa='"+idSewa+"'";
            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.PreparedStatement pStmt =conn.prepareStatement(sql);
            pStmt.execute();
            JOptionPane.showMessageDialog(null, "Data akan dihapus?");
            dataBaru=true;
            resetFormSewa(); 
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, e);
        }
        getDataSewa();
    }//GEN-LAST:event_btnDeleteSewaActionPerformed

    private void btnCariSewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariSewaActionPerformed
        
        try{
            String keyword = inputCariSewa.getText();

            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery("SELECT "
                    + "`id_sewa` AS `ID Penyewaan`,"
                    + "`nip` AS `NIP`, "
                    + "`id_pel` AS `ID Pelanggan`, "
                    + "`id_brg` AS `ID Barang`, "
                    + "`jml_brg` AS `Jumlah Barang`, "
                    + "`tgl_sewa` AS `Tanggal Sewa`, "
                    + "`lama_sewa` AS `Lama Sewa`, "
                    + "`tgl_kembali` AS `Tanggal Kembali`, "
                    + "`harga` AS `Harga Sewa` "
                    + " FROM penyewaan WHERE "
                    + "id_sewa LIKE '%"+keyword+"%' "
                    + "OR nip LIKE '%"+keyword+"%' "
                    + "OR id_pel LIKE '%"+keyword+"%' "
                    + "OR id_brg LIKE '%"+keyword+"%' "
                    + "OR jml_brg LIKE '%"+keyword+"%' "
                    + "OR tgl_sewa LIKE '%"+keyword+"%' "
                    + "OR lama_sewa LIKE '%"+keyword+"%' "
                    + "OR tgl_kembali LIKE '%"+keyword+"%' "
                    + "OR harga LIKE '%"+keyword+"%';");

            tabelPenyewaan.setModel(DbUtils.resultSetToTableModel(sql));
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, e);
        }    
    }//GEN-LAST:event_btnCariSewaActionPerformed

    private void btnCariBrgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariBrgActionPerformed
        try{
            String keyword = inputCariBrg.getText();

            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery("SELECT "
                    + "`id_brg` AS `ID Barang`,"
                    + "`nm_brg` AS `Nama Barang`, "
                    + "`harga_brg` AS `Harga Sewa`, "
                    + "`stok` AS `Stok` "
                    + " FROM barang WHERE "
                    + "id_brg LIKE '%"+keyword+"%' "
                    + "OR nm_brg LIKE '%"+keyword+"%' "
                    + "OR harga_brg LIKE '%"+keyword+"%' "
                    + "OR stok LIKE '%"+keyword+"%';");

            tabelBarang.setModel(DbUtils.resultSetToTableModel(sql));
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, e);
        }        
    }//GEN-LAST:event_btnCariBrgActionPerformed

    private void btnResetKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetKembaliActionPerformed
        resetFormPengembalian();
    }//GEN-LAST:event_btnResetKembaliActionPerformed

    private void btnUpdateKmblActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateKmblActionPerformed
        // TODO add your handling code here:
        if(dataBaru == false){
            //utk tanggal
            SimpleDateFormat formatTgl = new SimpleDateFormat("yyyy-MM-dd");
            String tglSewaKmbl = String.valueOf(formatTgl.format(inputTglSewaKmbl.getDate()));
            String tglKembaliKmbl = String.valueOf(formatTgl.format(inputTglKembaliKmbl.getDate()));
            try{
                String sql = "UPDATE pengembalian SET "
                        + "id_sewa='"+inputIdSewaKmblFK.getText()+"'"
                        + ",tgl_sewa='"+tglSewaKmbl+"'"
                        + ",nip='"+inputNipKmblFK.getText()+"'"
                        + ",lama_sewa='"+inputLamaSewaKmbl.getText()+"'"
                        + ",tgl_kmbl='"+tglKembaliKmbl+"'"
                        + ",id_brg='"+inputIdBrgKmblFK.getText()+"'"
                        + ",harga='"+inputHargaSewaKmbl.getText()+"'"
                        + ",denda='"+inputDendaKmbl.getText()+"'"
                        + ",total_harga='"+inputTotHrgKmbl.getText()+"'"
                        + " WHERE id_sewa='"+inputIdSewaKmblFK.getText()+"';";

                java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
                java.sql.PreparedStatement pStmt = conn.prepareStatement(sql);
                pStmt.execute();
                inputIdSewaKmblFK.requestFocus();
                JOptionPane.showMessageDialog(null, "Perubahan data berhasil");
            }catch(SQLException | HeadlessException e){
                JOptionPane.showMessageDialog(null, e);
            }
            getDataPengembalian();
            resetFormPengembalian();
        }
        
        
    }//GEN-LAST:event_btnUpdateKmblActionPerformed

    private void btnDeleteKmblActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteKmblActionPerformed
        // TODO add your handling code here:
        try{
            String sql = "DELETE FROM pengembalian WHERE id_sewa='"+inputIdSewaKmblFK.getText()+"'";
            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.PreparedStatement pStmt =conn.prepareStatement(sql);
            pStmt.execute();
            JOptionPane.showMessageDialog(null, "Data akan dihapus?");
            dataBaru=true;
            resetFormPengembalian(); 
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, e);
        }
        getDataPengembalian(); 
        resetFormPengembalian();
    }//GEN-LAST:event_btnDeleteKmblActionPerformed

    private void btnCariKmblActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariKmblActionPerformed
        // TODO add your handling code here:
        try{
            String keyword = inputCariKmbl.getText();

            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery("SELECT "
                    + "`id_kmbl` AS `ID Pengembalian`,"
                    + "`id_sewa` AS `ID Penyewaan`, "
                    + "`nip` AS `NIP`, "
                    + "`tgl_sewa` AS `Tanggal Sewa`, "
                    + "`lama_sewa` AS `Lama Sewa`, "
                    + "`tgl_kmbl` AS `Tanggal Kembali`, "
                    + "`id_brg` AS `ID Barang`, "
                    + "`harga` AS `Harga Sewa`, "
                    + "`denda` AS `Denda`, "
                    + "`total_harga` AS `Total Harga` "
                    + " FROM pengembalian WHERE "
                    
                    + "id_kmbl LIKE '%"+keyword+"%'"
                    + "OR id_sewa LIKE '%"+keyword+"%'"
                    + "OR nip LIKE '%"+keyword+"%'"
                    + "OR tgl_sewa LIKE '%"+keyword+"%'"
                    + "OR lama_sewa LIKE '%"+keyword+"%'"
                    + "OR tgl_kmbl LIKE '%"+keyword+"%'"
                    + "OR id_brg LIKE '%"+keyword+"%'"
                    + "OR harga LIKE '%"+keyword+"%'"
                    + "OR denda LIKE '%"+keyword+"%'"
                    + "OR total_harga LIKE '%"+keyword+"%';");

            tabelPengembalian.setModel(DbUtils.resultSetToTableModel(sql));
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, e);
        }
        
    }//GEN-LAST:event_btnCariKmblActionPerformed

    private void tabBerandaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabBerandaMouseClicked
        CardLayout card = (CardLayout)main.getLayout();
        card.show(main, "beranda");
        
        setTabColor(tabBeranda, indikatorBeranda, labelTabBeranda);
        resetTabColor(tabBrg, indikatorTabBrg, labelTabBrg);
        resetTabColor(tabSewa, indikatorTabSewa, labelTabSewa);
        resetTabColor(tabKembali, indikatorTabKembali, labelTabKembali);
        resetTabColor(tabPendapatan, indikatorTabPendapatan, labelTabPendapatan);
        resetTabColor(tabLogout, indikatorTabLogout, labelTabLogout);
        resetTabColor(tabPel, indikatorTabPel, labelTabPel);
    }//GEN-LAST:event_tabBerandaMouseClicked

    private void btnCetakKmblActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakKmblActionPerformed
        // TODO add your handling code here:
        if(dataBaru==false){
            try{
                java.sql.Connection conn = (java.sql.Connection) koneksi.Koneksi.koneksiDatabase();

                try{
                    String path ="D:\\Documents\\LANGGENG\\kls xi\\tugas & materi kls xi\\PBO\\tugas akhir\\PenyewaanAlatPendakian\\src\\cetak\\strukPengembalian.jasper";
                    HashMap param = new HashMap();
                    param.put("kode2", inputIdSewaKmblFK.getText());

                    JasperReport reports = (JasperReport) JRLoader.loadObjectFromFile(path);
                    JasperPrint JPrint = JasperFillManager.fillReport(reports, param, conn);

                    JasperViewer jviewer = new JasperViewer(JPrint, false);
                    jviewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    jviewer.setVisible(true);

                }catch(JRException jRException){
                    JOptionPane.showMessageDialog(null, jRException);
                }

            }catch(Exception e){
                JOptionPane.showMessageDialog(null, e);
            }
        }
        
    }//GEN-LAST:event_btnCetakKmblActionPerformed

    private void btnCetakDataSewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakDataSewaActionPerformed
        // TODO add your handling code here:            
        if(dataBaru==false){
            try{
                java.sql.Connection conn = (java.sql.Connection) koneksi.Koneksi.koneksiDatabase();

                try{
                    String path ="D:\\Documents\\LANGGENG\\kls xi\\tugas & materi kls xi\\PBO\\"
                            + "tugas akhir\\PenyewaanAlatPendakian\\src\\cetak\\strukPenyewaan.jasper";                
                    HashMap param = new HashMap();
                    param.put("kode", inputIdSewa.getText());

                    JasperReport reports = (JasperReport) JRLoader.loadObjectFromFile(path);
                    JasperPrint JPrint = JasperFillManager.fillReport(reports, param, conn);

                    JasperViewer jviewer = new JasperViewer(JPrint, false);
                    jviewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    jviewer.setVisible(true);

                }catch(JRException jRException){
                    JOptionPane.showMessageDialog(null, jRException);
                }

            }catch(Exception e){
                JOptionPane.showMessageDialog(null, e);
            }
        }
        
    }//GEN-LAST:event_btnCetakDataSewaActionPerformed

    private void tabPendapatanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabPendapatanMouseClicked
        CardLayout card = (CardLayout)main.getLayout();
        card.show(main, "pendapatan");
        
        setTabColor(tabPendapatan, indikatorTabPendapatan, labelTabPendapatan);
        resetTabColor(tabBrg, indikatorTabBrg, labelTabBrg);
        resetTabColor(tabSewa, indikatorTabSewa, labelTabSewa);
        resetTabColor(tabKembali, indikatorTabKembali, labelTabKembali);
        resetTabColor(tabBeranda, indikatorBeranda, labelTabBeranda);
        resetTabColor(tabLogout, indikatorTabLogout, labelTabLogout);
        resetTabColor(tabPel, indikatorTabPel, labelTabPel);
        
        getDataPendapatan();
        hitungPendapatan();
    }//GEN-LAST:event_tabPendapatanMouseClicked

    private void tabLogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabLogoutMouseClicked
        setTabColor(tabLogout, indikatorTabLogout, labelTabLogout);
        resetTabColor(tabBrg, indikatorTabBrg, labelTabBrg);
        resetTabColor(tabSewa, indikatorTabSewa, labelTabSewa);
        resetTabColor(tabKembali, indikatorTabKembali, labelTabKembali);
        resetTabColor(tabBeranda, indikatorBeranda, labelTabBeranda);
        resetTabColor(tabPendapatan, indikatorTabPendapatan, labelTabPendapatan);
        resetTabColor(tabPel, indikatorTabPel, labelTabPel);
        
        new Login().show();
        this.dispose();
    }//GEN-LAST:event_tabLogoutMouseClicked

    private void tabPelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabPelMouseClicked
        CardLayout card = (CardLayout)main.getLayout();
        card.show(main, "data_pel");
        
        setTabColor(tabPel, indikatorTabPel, labelTabPel);
        resetTabColor(tabBrg, indikatorTabBrg, labelTabBrg);
        resetTabColor(tabSewa, indikatorTabSewa, labelTabSewa);
        resetTabColor(tabKembali, indikatorTabKembali, labelTabKembali);
        resetTabColor(tabBeranda, indikatorBeranda, labelTabBeranda);
        resetTabColor(tabLogout, indikatorTabLogout, labelTabLogout);
        resetTabColor(tabPendapatan, indikatorTabPendapatan, labelTabPendapatan);
    }//GEN-LAST:event_tabPelMouseClicked

    private void btnUpdatePelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdatePelActionPerformed
        dataBaru = false;
        //utk tanggal
        SimpleDateFormat formatTgl = new SimpleDateFormat("yyyy-MM-dd");
        String tglLahirPel = String.valueOf(formatTgl.format(inputTglLahirPel.getDate()));
        try{
            String sql = "UPDATE pelanggan SET "
                    + "id_pel='"+inputIdPel.getText()+"'"
                    + ", nik='"+inputNikPel.getText()+"'" //TO DO: tambahi utk nik
                    + ", nama_pel='"+inputNamaPel.getText()+"'"
                    + ", no_telp='"+inputNoTelpPel.getText()+"'"
                    + ", alamat='"+inputAlamatPel.getText()+"'"
                    + ", tgl_lahir='"+tglLahirPel+"'"
                    + ", kota='"+inputKotaPel.getText()+"'"
                    + " WHERE id_pel='"+inputIdPel.getText()+"';";

            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.execute();
            inputIdPel.requestFocus();
            JOptionPane.showMessageDialog(null, "Perubahan data berhasil");
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, e);
        }
        getDataPel();
        resetFormPel();
    }//GEN-LAST:event_btnUpdatePelActionPerformed

    private void btnDeletePelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletePelActionPerformed
        try{
            String idPel = inputIdPel.getText();
            String sql = "DELETE FROM pelanggan WHERE id_pel='"+idPel+"'";
            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.PreparedStatement pStmt =conn.prepareStatement(sql);
            pStmt.execute();
            JOptionPane.showMessageDialog(null, "Data akan dihapus?");
            dataBaru=true;
            resetFormPel(); 
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, e);
        }
        getDataPel();
    }//GEN-LAST:event_btnDeletePelActionPerformed

    private void btnResetPelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetPelActionPerformed
        resetFormPel();
    }//GEN-LAST:event_btnResetPelActionPerformed

    private void btnCariPelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariPelActionPerformed
        try{
            String keyword = inputCariPel.getText();

            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery("SELECT "
                    + "`id_pel` AS `ID Pelanggan`,"
                    + "`nama_pel` AS `Nama Pelanggan`, "
                    + "`alamat` AS `Alamat`, "
                    + "`tgl_lahir` AS `Tanggal Lahir`, "
                    + "`kota` AS `Kota` "
                    + " FROM pelanggan WHERE "
                    + "id_pel LIKE '%"+keyword+"%'"
                    + "OR nama_pel LIKE '%"+keyword+"%'"
                    + "OR alamat LIKE '%"+keyword+"%'"
                    + "OR tgl_lahir LIKE '%"+keyword+"%'"
                    + "OR kota LIKE '%"+keyword+"%';");

            tabelPelanggan.setModel(DbUtils.resultSetToTableModel(sql));
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_btnCariPelActionPerformed

    private void tabelBarangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelBarangMouseClicked
        // TODO add your handling code here:
        dataBaru = false;
        try{
           int row = tabelBarang.getSelectedRow();
           String tabelKlik = (tabelBarang.getValueAt(row, 0)).toString();
           java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
           java.sql.Statement stmt = conn.createStatement();
           java.sql.ResultSet sql = stmt.executeQuery("SELECT * FROM barang WHERE id_brg='"+tabelKlik+"';");
           
           if(sql.next()){
               String idBrg = sql.getString("id_brg");
               String namaBrg = sql.getString("nm_brg");
               String hargaBrg = sql.getString("harga_brg");
               String stokBrg = sql.getString("stok");
               
               inputIdBrg.setText(idBrg);
               inputNamaBrg.setText(namaBrg);
               inputHargaBrg.setText(hargaBrg);
               inputStokBrg.setText(stokBrg);
           }
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, e);
        }
        
    }//GEN-LAST:event_tabelBarangMouseClicked

    private void tabelPelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelPelangganMouseClicked
        dataBaru = false;
        try{ 
            int row = tabelPelanggan.getSelectedRow();
            String tabelKlik = (tabelPelanggan.getValueAt(row, 0)).toString();
            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery("SELECT * FROM pelanggan WHERE id_pel='"+tabelKlik+"';");
            
            if(sql.next()){
                String idPel = sql.getString("id_pel");
                String nik = sql.getString("nik"); //TO DO: tambahi utk nik
                String namaPel = sql.getString("nama_pel");
                String noTelpPel = sql.getString("no_telp");
                String alamatPel = sql.getString("alamat");
                Date tglLahirPel = sql.getDate("tgl_lahir");
                String kotaPel = sql.getString("kota");
                
                inputIdPel.setText(idPel);
                inputNikPel.setText(nik);
                inputNamaPel.setText(namaPel);
                inputNoTelpPel.setText(noTelpPel);
                inputAlamatPel.setText(alamatPel);
                inputTglLahirPel.setDate(tglLahirPel);
                inputKotaPel.setText(kotaPel);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_tabelPelangganMouseClicked

    private void tabelPenyewaanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelPenyewaanMouseClicked
        // TODO add your handling code here:
        dataBaru = false;
        try{ 
            int row = tabelPenyewaan.getSelectedRow();
            String tabelKlik = (tabelPenyewaan.getValueAt(row, 0)).toString();
            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery("SELECT * FROM penyewaan WHERE id_sewa='"+tabelKlik+"';");
            
            if(sql.next()){
                String idSewa = sql.getString("id_sewa");
                String nip = sql.getString("nip");
                String idPel = sql.getString("id_pel");
                String idBrg = sql.getString("id_brg");
                String jmlBrg = sql.getString("jml_brg");
                Date tglSewa = sql.getDate("tgl_sewa");
                String lamaSewa = sql.getString("lama_sewa");
                Date tglKembali = sql.getDate("tgl_kembali");
                String harga = sql.getString("harga");
                
                inputIdSewa.setText(idSewa);
                inputNipFK.setText(nip);
                inputIdPelFK.setText(idPel);
                inputIdBrgFK.setText(idBrg);
                inputJmlBrg.setText(jmlBrg);
                inputTglSewa.setDate(tglSewa);
                inputLamaSewa.setText(lamaSewa);
                inputTglKembali.setDate(tglKembali);
                inputHrgSewa.setText(harga);
                
                try {
                    java.sql.Statement stmt2 = conn.createStatement();
                    java.sql.ResultSet setNmBrg = stmt2.executeQuery("SELECT nm_brg FROM barang WHERE id_brg='"+idBrg+"';");
                    if(setNmBrg.next()){
                        inputNmBrg.setSelectedItem(setNmBrg.getString("nm_brg"));
                    }
                } catch (Exception e) {
                }
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_tabelPenyewaanMouseClicked

    private void tabelPengembalianMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelPengembalianMouseClicked
        // TODO add your handling code here:
        dataBaru = false;
        try{ 
            int row = tabelPengembalian.getSelectedRow();
            String tabelKlik = (tabelPengembalian.getValueAt(row, 0)).toString();
            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery("SELECT * FROM pengembalian WHERE id_kmbl='"+tabelKlik+"';");
            
            if(sql.next()){
                String idSewa = sql.getString("id_sewa");
                String nipKmbl = sql.getString("nip");
                Date tglSewa = sql.getDate("tgl_sewa");
                String lamaSewa = sql.getString("lama_sewa");
                Date tglKembali = sql.getDate("tgl_kmbl");
                String idBrg = sql.getString("id_brg");
                String harga = sql.getString("harga");
                String denda = sql.getString("denda");
                String hargaTot = sql.getString("total_harga");
                
                inputIdSewaKmblFK.setText(idSewa);
                inputNipKmblFK.setText(nipKmbl);
                inputTglSewaKmbl.setDate(tglSewa);
                inputLamaSewaKmbl.setText(lamaSewa);
                inputTglKembaliKmbl.setDate(tglKembali);
                inputIdBrgKmblFK.setText(idBrg);
                inputHargaSewaKmbl.setText(harga);
                inputDendaKmbl.setText(denda);
                inputTotHrgKmbl.setText(hargaTot);
                
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
        
    }//GEN-LAST:event_tabelPengembalianMouseClicked

    private void btnInsertPelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertPelActionPerformed
        // TODO add your handling code here:
        if(dataBaru == true){
            //utk tanggal
            SimpleDateFormat formatTgl = new SimpleDateFormat("yyyy-MM-dd");
            String tglLahirPel = String.valueOf(formatTgl.format(inputTglLahirPel.getDate()));
            
            try{
                String sql = "INSERT INTO pelanggan VALUES ("
                        + "'"+inputIdPel.getText()+"'"
                        + ",'"+inputNikPel.getText()+"'"//TO DO: tambahi utk nik
                        + ",'"+inputNamaPel.getText()+"'"
                        + ",'"+inputNoTelpPel.getText()+"'"
                        + ",'"+inputAlamatPel.getText()+"'"
                        + ",'"+tglLahirPel+"'"
                        + ",'"+inputKotaPel.getText()+"'"
                        + ");";
                java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
                java.sql.PreparedStatement pStmt = conn.prepareStatement(sql);
                pStmt.execute();
                inputIdPel.requestFocus();
                JOptionPane.showMessageDialog(null, "Data berhasil dimasukkan");
            }catch(SQLException | HeadlessException e){
                JOptionPane.showMessageDialog(null, e);
            }
            getDataPel();
            resetFormPel();
        }
    }//GEN-LAST:event_btnInsertPelActionPerformed

    private void btnInsertBrgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertBrgActionPerformed
        // TODO add your handling code here:
        if(dataBaru == true){
            try{
                String sql = "INSERT INTO barang VALUES ("
                        + "'"+inputIdBrg.getText()+"'"
                        + ",'"+inputNamaBrg.getText()+"'"
                        + ", "+inputHargaBrg.getText()
                        + ", "+inputStokBrg.getText()
                        + ");";
                java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
                java.sql.PreparedStatement pStmt = conn.prepareStatement(sql);
                pStmt.execute();
                inputIdBrg.requestFocus();
                JOptionPane.showMessageDialog(null, "Data berhasil dimasukkan");
            }catch(SQLException | HeadlessException e){
                JOptionPane.showMessageDialog(null, e);
            }
            getDataBrg();
            resetFormBrg();
        }
    }//GEN-LAST:event_btnInsertBrgActionPerformed

    private void btnInsertSewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertSewaActionPerformed
        if(dataBaru == true){
            //utk tanggal
            SimpleDateFormat formatTgl = new SimpleDateFormat("yyyy-MM-dd");
            String tglSewa = String.valueOf(formatTgl.format(inputTglSewa.getDate()));
            String tglKembali = String.valueOf(formatTgl.format(inputTglKembali.getDate()));
            try{
                String sql = "INSERT INTO penyewaan VALUES ("
                        + "'"+inputIdSewa.getText()+"'"
                        + ", '"+inputNipFK.getText()+"'"
                        + ", '"+inputIdPelFK.getText()+"'"
                        + ", '"+inputIdBrgFK.getText()+"'"
                        + ", '"+inputJmlBrg.getText()+"'"
                        + ", '"+tglSewa+"'"
                        + ", '"+inputLamaSewa.getText()+"'"
                        + ", '"+tglKembali+"'"
                        + ", '"+inputHrgSewa.getText()+"'"
                        + ");";
                java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
                java.sql.PreparedStatement pStmt = conn.prepareStatement(sql);
                pStmt.execute();
                inputIdSewa.requestFocus();
                JOptionPane.showMessageDialog(null, "Data berhasil dimasukkan");
            }catch(SQLException | HeadlessException e){
                JOptionPane.showMessageDialog(null, e);
            }
            getDataSewa();
            resetFormSewa();
        }
        
    }//GEN-LAST:event_btnInsertSewaActionPerformed

    private void inputLamaSewaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputLamaSewaKeyReleased
        int tglSewa = Integer.valueOf(inputLamaSewa.getText());
        Date tglHariIni = inputTglSewa.getDate();
        
        Calendar c = Calendar.getInstance();
        c.setTime(tglHariIni);
        c.add(Calendar.DATE, tglSewa);
        Date tglKembali = c.getTime();
        inputTglKembali.setDate(tglKembali);
        setHargaSewa();
    }//GEN-LAST:event_inputLamaSewaKeyReleased

    private void btnIsertKmblActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIsertKmblActionPerformed
        // TODO add your handling code here:
        if(dataBaru == true){
            //utk tanggal
            SimpleDateFormat formatTgl = new SimpleDateFormat("yyyy-MM-dd");
            String tglSewaKmbl = String.valueOf(formatTgl.format(inputTglSewaKmbl.getDate()));
            String tglKembaliKmbl = String.valueOf(formatTgl.format(inputTglKembaliKmbl.getDate()));
            try{
                String sql = "INSERT INTO pengembalian VALUES ("
                        + "NULL"
                        + ",'"+inputIdSewaKmblFK.getText()+"'"
                        + ",'"+inputNipKmblFK.getText()+"'"
                        + ", '"+tglSewaKmbl+"'"
                        + ", '"+inputLamaSewaKmbl.getText()+"'"
                        + ", '"+tglKembaliKmbl+"'"
                        + ", '"+inputIdBrgKmblFK.getText()+"'"
                        + ", '"+inputHargaSewaKmbl.getText()+"'"
                        + ", '"+inputDendaKmbl.getText()+"'"
                        + ", '"+inputTotHrgKmbl.getText()+"'"
                        + ");";
                java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
                java.sql.PreparedStatement pStmt = conn.prepareStatement(sql);
                pStmt.execute();
                inputIdSewaKmblFK.requestFocus();
                JOptionPane.showMessageDialog(null, "Data berhasil dimasukkan");
            }catch(SQLException | HeadlessException e){
                JOptionPane.showMessageDialog(null, e);
            }
            getDataPengembalian();
            resetFormPengembalian();
        }
        
        
    }//GEN-LAST:event_btnIsertKmblActionPerformed

    private void inputIdSewaKmblFKKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputIdSewaKmblFKKeyReleased
        // TODO add your handling code here:
        try{
            java.sql.Connection conn = (java.sql.Connection)koneksi.Koneksi.koneksiDatabase();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet sql = stmt.executeQuery("SELECT * FROM penyewaan WHERE id_sewa='"+inputIdSewaKmblFK.getText()+"';");
            
            if(sql.next()){
                String nip = sql.getString("nip");
                Date tglSewa = sql.getDate("tgl_sewa");
                String lamaSewa = sql.getString("lama_sewa");
                String idBrg = sql.getString("id_brg");
                String harga = sql.getString("harga");
                
                inputNipKmblFK.setText(nip);
                inputTglSewaKmbl.setDate(tglSewa);
                inputLamaSewaKmbl.setText(lamaSewa);
                inputIdBrgKmblFK.setText(idBrg);
                inputHargaSewaKmbl.setText(harga);
                
//                hitungDenda();
            }
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null, e);
        }
        
    }//GEN-LAST:event_inputIdSewaKmblFKKeyReleased

    private void inputDendaKmblKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputDendaKmblKeyReleased
        // TODO add your handling code here:
        
        int hrgSewa = Integer.valueOf(inputHargaSewaKmbl.getText()) ;
        int Denda = Integer.valueOf(inputDendaKmbl.getText());
        String totalHrg= String.valueOf(hrgSewa+Denda);
        inputTotHrgKmbl.setText(totalHrg);
        
    }//GEN-LAST:event_inputDendaKmblKeyReleased

    private void inputNmBrgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputNmBrgActionPerformed
        // TODO add your handling code here:
        getIdBrg();
    }//GEN-LAST:event_inputNmBrgActionPerformed

    private void inputJmlBrgKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputJmlBrgKeyReleased
        // TODO add your handling code here:
        setHargaSewa();
        int jmlStok = Integer.valueOf(inputStokBrgSewa.getText());
        int jmlBrg = Integer.valueOf(inputJmlBrg.getText());
        //cek apakah jml barang yg disewa melebihi brg yg tersedia tau tidak
        if(jmlStok < jmlBrg){
            JOptionPane.showMessageDialog(null, "Jumlah stok tidak cukup");
            inputJmlBrg.setText("1");
        }
    }//GEN-LAST:event_inputJmlBrgKeyReleased

    private void inputTglKembaliKmblKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputTglKembaliKmblKeyReleased
        // TODO add your handling code here:
//        hitungDenda();
    }//GEN-LAST:event_inputTglKembaliKmblKeyReleased
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PenyewaanAlatPendakianPegawai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PenyewaanAlatPendakianPegawai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PenyewaanAlatPendakianPegawai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PenyewaanAlatPendakianPegawai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PenyewaanAlatPendakianPegawai().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Header;
    private javax.swing.JPanel beranda;
    private javax.swing.JButton btnCariBrg;
    private javax.swing.JButton btnCariKmbl;
    private javax.swing.JButton btnCariPel;
    private javax.swing.JButton btnCariSewa;
    private javax.swing.JButton btnCetakDataSewa;
    private javax.swing.JButton btnCetakKmbl;
    private javax.swing.JButton btnDeleteBrg;
    private javax.swing.JButton btnDeleteKmbl;
    private javax.swing.JButton btnDeletePel;
    private javax.swing.JButton btnDeleteSewa;
    private javax.swing.JButton btnInsertBrg;
    private javax.swing.JButton btnInsertPel;
    private javax.swing.JButton btnInsertSewa;
    private javax.swing.JButton btnIsertKmbl;
    private javax.swing.JButton btnResetBrg;
    private javax.swing.JButton btnResetKembali;
    private javax.swing.JButton btnResetPel;
    private javax.swing.JButton btnResetSewa;
    private javax.swing.JButton btnUpdateBrg;
    private javax.swing.JButton btnUpdateKmbl;
    private javax.swing.JButton btnUpdatePel;
    private javax.swing.JButton btnUpdateSewa;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel dataBrg;
    private javax.swing.JPanel dataKembali;
    private javax.swing.JPanel dataPel;
    private javax.swing.JPanel dataSewa;
    private javax.swing.JPanel formDataKembali;
    private javax.swing.JPanel formDataPel;
    private javax.swing.JPanel form_data_brg;
    private javax.swing.JPanel form_data_sewa;
    private javax.swing.JPanel indikatorBeranda;
    private javax.swing.JPanel indikatorTabBrg;
    private javax.swing.JPanel indikatorTabKembali;
    private javax.swing.JPanel indikatorTabLogout;
    private javax.swing.JPanel indikatorTabPel;
    private javax.swing.JPanel indikatorTabPendapatan;
    private javax.swing.JPanel indikatorTabSewa;
    private javax.swing.JTextField inputAlamatPel;
    private javax.swing.JTextField inputCariBrg;
    private javax.swing.JTextField inputCariKmbl;
    private javax.swing.JTextField inputCariPel;
    private javax.swing.JTextField inputCariSewa;
    private javax.swing.JTextField inputDendaKmbl;
    private javax.swing.JTextField inputHargaBrg;
    private javax.swing.JTextField inputHargaSewaKmbl;
    private javax.swing.JTextField inputHrgSewa;
    private javax.swing.JTextField inputIdBrg;
    private javax.swing.JLabel inputIdBrgFK;
    private javax.swing.JTextField inputIdBrgKmblFK;
    private javax.swing.JTextField inputIdPel;
    private javax.swing.JTextField inputIdPelFK;
    private javax.swing.JTextField inputIdSewa;
    private javax.swing.JTextField inputIdSewaKmblFK;
    private javax.swing.JTextField inputJmlBrg;
    private javax.swing.JTextField inputKotaPel;
    private javax.swing.JTextField inputLamaSewa;
    private javax.swing.JTextField inputLamaSewaKmbl;
    private javax.swing.JTextField inputNamaBrg;
    private javax.swing.JTextField inputNamaPel;
    private javax.swing.JTextField inputNikPel;
    private javax.swing.JTextField inputNipFK;
    private javax.swing.JTextField inputNipKmblFK;
    private javax.swing.JComboBox<String> inputNmBrg;
    private javax.swing.JTextField inputNoTelpPel;
    private javax.swing.JTextField inputStokBrg;
    private javax.swing.JLabel inputStokBrgSewa;
    private com.toedter.calendar.JDateChooser inputTglKembali;
    private com.toedter.calendar.JDateChooser inputTglKembaliKmbl;
    private com.toedter.calendar.JDateChooser inputTglLahirPel;
    private com.toedter.calendar.JDateChooser inputTglSewa;
    private com.toedter.calendar.JDateChooser inputTglSewaKmbl;
    private javax.swing.JTextField inputTotHrgKmbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel labelTabBeranda;
    private javax.swing.JLabel labelTabBrg;
    private javax.swing.JLabel labelTabKembali;
    private javax.swing.JLabel labelTabLogout;
    private javax.swing.JLabel labelTabPel;
    private javax.swing.JLabel labelTabPendapatan;
    private javax.swing.JLabel labelTabSewa;
    private javax.swing.JLabel labelTotalPendapatan;
    private javax.swing.JPanel main;
    private javax.swing.JPanel main_content;
    private javax.swing.JPanel pendapatan;
    private javax.swing.JPanel side_bar;
    private javax.swing.JPanel tabBeranda;
    private javax.swing.JPanel tabBrg;
    private javax.swing.JPanel tabKembali;
    private javax.swing.JPanel tabLogout;
    private javax.swing.JPanel tabPel;
    private javax.swing.JPanel tabPendapatan;
    private javax.swing.JPanel tabSewa;
    private javax.swing.JTable tabelBarang;
    private javax.swing.JTable tabelPelanggan;
    private javax.swing.JTable tabelPendapatan;
    private javax.swing.JTable tabelPengembalian;
    private javax.swing.JTable tabelPenyewaan;
    private javax.swing.JLabel usernameHome;
    // End of variables declaration//GEN-END:variables
}
