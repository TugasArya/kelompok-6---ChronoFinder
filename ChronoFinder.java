import javax.swing.*;
import java.util.*;

public class ChronoFinder {
    // Array untuk nama hari
    private static final String[] HARI = {"Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
    
    // Array untuk doomsday dates setiap bulan (tahun biasa)
    private static final int[] DOOMSDAY_REGULAR = {3, 28, 14, 4, 9, 6, 11, 8, 5, 10, 7, 12};
    
    // Array untuk doomsday dates setiap bulan (tahun kabisat)
    private static final int[] DOOMSDAY_LEAP = {4, 29, 14, 4, 9, 6, 11, 8, 5, 10, 7, 12};
    
    // Queue untuk riwayat perhitungan
    private final ArrayDeque<String> riwayatPerhitungan = new ArrayDeque<>();
    
    // Stack untuk undo functionality
    private final Stack<String> stackUndo = new Stack<>();
    
    // ArrayList untuk menyimpan hasil perhitungan
    private final ArrayList<String> semuaPerhitungan = new ArrayList<>();

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, """
                                              SELAMAT DATANG DI CHRONOFINDER 
                                                                  by kelompok 6
                                            
                                    Program sederhana yang berfungsi menentukan hari 
                                            berdasarkan tanggal dari input user""",
            "ChronoFinder - Time Explorer", 
            JOptionPane.INFORMATION_MESSAGE);
        
        ChronoFinder program = new ChronoFinder();
        program.jalankanProgram();
    }

    public void jalankanProgram() {
        while (true) {
            String[] options = {"Cari Hari dari Tanggal", "Lihat Riwayat", 
                              "Lihat Semua Perhitungan", "Undo Terakhir", "Keluar"};
            
            int pilihan = JOptionPane.showOptionDialog(null, """
                                                             CHRONOFINDER - MAIN MENU
                                                             Pilih fitur yang ingin digunakan:""",
                "ChronoFinder Navigator",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
            
            // Percabangan menggunakan switch case untuk menu
            switch (pilihan) {
                case 0 -> hitungHariDariTanggalGUI();
                case 1 -> tampilkanRiwayatQueueGUI();
                case 2 -> tampilkanSemuaPerhitunganGUI();
                case 3 -> undoPerhitunganGUI();
                case 4, -1 -> {
                    // Handle window close
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Apakah Anda yakin ingin keluar dari ChronoFinder?",
                            "Konfirmasi Keluar",
                            JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(null,
                                "Terima kasih telah menggunakan ChronoFinder!",
                                "Sampai Jumpa",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
                default -> {
                }
            }
        }
    }

    private void hitungHariDariTanggalGUI() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        
        JTextField dayField = new JTextField(5);
        JTextField monthField = new JTextField(5);
        JTextField yearField = new JTextField(5);
        
        inputPanel.add(new JLabel("MASUKKAN TANGGAL"));
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(new JLabel("Tanggal (1-31):"));
        inputPanel.add(dayField);
        inputPanel.add(new JLabel("Bulan (1-12):"));
        inputPanel.add(monthField);
        inputPanel.add(new JLabel("Tahun:"));
        inputPanel.add(yearField);
        
        int result = JOptionPane.showConfirmDialog(null, inputPanel,
            "ChronoFinder - Input Tanggal",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int tanggal = Integer.parseInt(dayField.getText());
                int bulan = Integer.parseInt(monthField.getText());
                int tahun = Integer.parseInt(yearField.getText());
                
                // Validasi input menggunakan percabangan
                if (!isTanggalValid(tanggal, bulan, tahun)) {
                    JOptionPane.showMessageDialog(null, """
                                                        TANGGAL TIDAK VALID!
                                                        
                                                        Pastikan:
                                                        Tanggal antara 1-31
                                                        Bulan antara 1-12
                                                        Tahun valid
                                                        Tanggal sesuai dengan bulan""",
                        "Error - Tanggal Invalid",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String hasil = hitungHari(tanggal, bulan, tahun);
                String output = String.format("%02d-%02d-%d adalah hari %s", 
                    tanggal, bulan, tahun, hasil);
                
                JOptionPane.showMessageDialog(null,
                    """
                    HASIL PERHITUNGAN
                    
                    Tanggal: """ + String.format("%02d-%02d-%d", tanggal, bulan, tahun) + "\n" +
                    "Hari: " + hasil + "\n\n" +
                    "Doomsday Algorithm berhasil diaplikasikan!",
                    "ChronoFinder - Result",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Simpan ke struktur data
                simpanKeStrukturData(output);
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, """
                                                    INPUT TIDAK VALID!
                                                    
                                                    Pastikan Anda memasukkan angka yang benar.""",
                    "Error - Format Invalid",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void tampilkanRiwayatQueueGUI() {
        if (riwayatPerhitungan.isEmpty()) {
            JOptionPane.showMessageDialog(null, """
                                                RIWAYAT MASIH KOSONG
                                                
                                                Belum ada perhitungan yang disimpan.
                                                Gunakan fitur 'Cari Hari dari Tanggal' terlebih dahulu.""",
                "ChronoFinder - Riwayat",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("5 RIWAYAT TERAKHIR (Queue - FIFO)\n\n");
        
        int counter = 1;
        // Looping - Iterasi melalui Queue
        for (String riwayat : riwayatPerhitungan) {
            sb.append(counter).append(". ").append(riwayat).append("\n");
            counter++;
        }
        
        sb.append("\nQueue menggunakan sistem First-In-First-Out");
        
        JOptionPane.showMessageDialog(null,
            sb.toString(),
            "ChronoFinder - Riwayat Terbaru",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void tampilkanSemuaPerhitunganGUI() {
        if (semuaPerhitungan.isEmpty()) {
            JOptionPane.showMessageDialog(null, """
                                                DATA MASIH KOSONG
                                                
                                                Belum ada perhitungan yang tersimpan.""",
                "ChronoFinder - Arsip",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("SEMUA PERHITUNGAN (ArrayList)\n\n");
        
        // Looping - Iterasi melalui ArrayList dengan index
        for (int i = 0; i < semuaPerhitungan.size(); i++) {
            sb.append(i + 1).append(". ").append(semuaPerhitungan.get(i)).append("\n");
        }
        
        sb.append("\nTotal: ").append(semuaPerhitungan.size()).append(" perhitungan");
        
        // Create scrollable text area for large data
        JTextArea textArea = new JTextArea(sb.toString(), 15, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        JOptionPane.showMessageDialog(null,
            scrollPane,
            "ChronoFinder - Arsip Lengkap",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void undoPerhitunganGUI() {
        if (stackUndo.isEmpty()) {
            JOptionPane.showMessageDialog(null, """
                                                TIDAK ADA DATA UNTUK DI-UNDO
                                                
                                                Stack undo sedang kosong.
                                                Lakukan perhitungan terlebih dahulu.""",
                "ChronoFinder - Undo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String terakhir = stackUndo.peek(); // Lihat tanpa menghapus dulu
        
        int confirm = JOptionPane.showConfirmDialog(null,
            """
            KONFIRMASI UNDO
            
            Data yang akan dihapus:
            """ +
            terakhir + "\n\n" +
            "Apakah Anda yakin ingin melanjutkan?",
            "ChronoFinder - Konfirmasi Undo",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // STACK - Pop operation (LIFO)
            String dataTerhapus = stackUndo.pop();
            
            // Hapus dari semua struktur data
            semuaPerhitungan.remove(dataTerhapus);
            riwayatPerhitungan.remove(dataTerhapus);
            
            JOptionPane.showMessageDialog(null,
                """
                UNDO BERHASIL
                
                Data terakhir telah dihapus:
                """ +
                dataTerhapus + "\n\n" +
                "Stack menggunakan sistem Last-In-First-Out",
                "ChronoFinder - Undo Completed",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void simpanKeStrukturData(String output) {
        // Simpan ke Queue (riwayat)
        riwayatPerhitungan.add(output);
        
        // Simpan ke Stack (untuk undo)
        stackUndo.push(output);
        
        // Simpan ke ArrayList
        semuaPerhitungan.add(output);
        
        // Jika queue sudah lebih dari 5, hapus yang paling lama
        if (riwayatPerhitungan.size() > 5) {
            String removed = riwayatPerhitungan.poll();
            System.out.println("Data tertua dihapus dari queue: " + removed);
        }
    }

    // methods   
    
    private String hitungHari(int tanggal, int bulan, int tahun) {
        // Step 1: Cari anchor day untuk abad
        int anchorDay = cariAnchorDay(tahun);
        
        // Step 2: Cari doomsday untuk tahun tersebut
        int doomsdayTahun = cariDoomsdayTahun(tahun, anchorDay);
        
        // Step 3: Cari doomsday date untuk bulan tersebut
        int doomsdayBulan = cariDoomsdayBulan(bulan, isTahunKabisat(tahun));
        
        // Step 4: Hitung selisih dan cari hari
        int selisih = (tanggal - doomsdayBulan) % 7;
        int indexHari = (doomsdayTahun + selisih + 7) % 7;
        
        return HARI[indexHari];
    }

    private int cariAnchorDay(int tahun) {
        int abad = tahun / 100;
        
        // Percabangan menggunakan switch case untuk anchor day
        return switch (abad % 4) {
            case 0 -> 2; // 1600, 2000, etc.
            case 1 -> 0; // 1700, 2100, etc.
            case 2 -> 5; // 1800, 2200, etc.
            case 3 -> 3; // 1900, 2300, etc.
            default -> 2;
        }; 
    }

    private int cariDoomsdayTahun(int tahun, int anchorDay) {
        int duaDigitAkhir = tahun % 100;
        
        // Looping untuk menghitung jumlah tahun kabisat
        int tahunKabisat = duaDigitAkhir / 4;
        
        int total = (duaDigitAkhir + tahunKabisat) % 7;
        return (anchorDay + total) % 7;
    }

    private int cariDoomsdayBulan(int bulan, boolean kabisat) {
        // Menggunakan array untuk doomsday dates
        int[] doomsdayDates = kabisat ? DOOMSDAY_LEAP : DOOMSDAY_REGULAR;
        
        // Validasi index array
        if (bulan >= 1 && bulan <= 12) {
            return doomsdayDates[bulan - 1];
        }
        return 0;
    }

    private boolean isTahunKabisat(int tahun) {
        // Percabangan untuk menentukan tahun kabisat
        if (tahun % 400 == 0) return true;
        if (tahun % 100 == 0) return false;
        return tahun % 4 == 0;
    }

    private boolean isTanggalValid(int tanggal, int bulan, int tahun) {
        if (bulan < 1 || bulan > 12) return false;
        if (tahun < 0) return false;
        
        // Array untuk jumlah hari setiap bulan
        int[] hariPerBulan = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        
        // Adjust untuk tahun kabisat
        if (isTahunKabisat(tahun)) {
            hariPerBulan[1] = 29;
        }
        
        // Validasi tanggal
        return tanggal >= 1 && tanggal <= hariPerBulan[bulan - 1];
    }
}