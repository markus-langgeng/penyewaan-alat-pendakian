-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 31 Jan 2022 pada 05.57
-- Versi server: 10.4.21-MariaDB
-- Versi PHP: 8.0.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `sewa_alat_pendakian`
--
CREATE DATABASE IF NOT EXISTS `sewa_alat_pendakian` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `sewa_alat_pendakian`;

-- --------------------------------------------------------

--
-- Struktur dari tabel `barang`
--

CREATE TABLE `barang` (
  `id_brg` varchar(10) NOT NULL,
  `nm_brg` varchar(50) NOT NULL,
  `harga_brg` int(11) NOT NULL,
  `stok` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data untuk tabel `barang`
--

INSERT INTO `barang` (`id_brg`, `nm_brg`, `harga_brg`, `stok`) VALUES
('BRG01', 'Tenda', 25000, 5),
('BRG02', 'Sleeping Bag', 10000, 2),
('BRG03', 'Tas Gunung', 10000, 10),
('BRG04', 'Day Pack', 25000, 10),
('BRG05', 'Jaket', 15000, 10),
('BRG06', 'Sepatu', 20000, 10);

-- --------------------------------------------------------

--
-- Struktur dari tabel `pegawai`
--

CREATE TABLE `pegawai` (
  `nip` varchar(10) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `nm_lkp` varchar(50) NOT NULL,
  `alamat` varchar(100) NOT NULL,
  `no_telp` varchar(20) NOT NULL,
  `jk` enum('L','P') NOT NULL,
  `tgl_lahir` date NOT NULL,
  `level` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data untuk tabel `pegawai`
--

INSERT INTO `pegawai` (`nip`, `username`, `password`, `nm_lkp`, `alamat`, `no_telp`, `jk`, `tgl_lahir`, `level`) VALUES
('P000', 'admin', 'admin', 'Langgeng', 'Jl. Arwana No.20', '085234748512', 'L', '2005-11-09', 'admin'),
('P001', 'pegawai1', 'pegawai1', 'Markus', 'Jl. Kelapa No.17', '085723485925', 'P', '2005-11-14', 'pegawai'),
('P002', 'pegawai2', 'pegawai2', 'Saputra', 'Jl. Pelangi No.12', '082374538467', 'L', '2000-01-05', 'pegawai'),
('P003', 'pegawai3', 'pegawai3', 'Budi Gunawan', 'Jl. Melati No.10', '084372123', 'L', '2000-12-01', 'pegawai');

-- --------------------------------------------------------

--
-- Struktur dari tabel `pelanggan`
--

CREATE TABLE `pelanggan` (
  `id_pel` varchar(10) NOT NULL,
  `nik` varchar(20) NOT NULL,
  `nama_pel` varchar(50) NOT NULL,
  `no_telp` varchar(20) NOT NULL,
  `alamat` varchar(50) NOT NULL,
  `tgl_lahir` date NOT NULL,
  `kota` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data untuk tabel `pelanggan`
--

INSERT INTO `pelanggan` (`id_pel`, `nik`, `nama_pel`, `no_telp`, `alamat`, `tgl_lahir`, `kota`) VALUES
('PEL1', '0167124794720953', 'Nita Flores', '1-302-439-2517', '676-5063 Nisl Road', '2020-11-10', 'Malang'),
('PEL2', '0263654794720953', 'Rebecca Kramer', '(118) 698-5661', '973-3767 Blandit Av.', '2000-11-10', 'Manizales'),
('PEL3', '017812479220543', 'Anisa Rahma', '083214342', 'Jl. Merpati No. 6', '2000-12-04', 'Malang'),
('PEL4', '0623424763420953', 'Faisal Putra', '03129847364', 'Jl. Sudirman No.12', '2002-05-15', 'Malang'),
('PEL5', '043702348720953', 'Siti Kurniawan', '093294', 'Jl. Kamboja No.5', '2001-12-04', 'Malang');

-- --------------------------------------------------------

--
-- Struktur dari tabel `pengembalian`
--

CREATE TABLE `pengembalian` (
  `id_kmbl` int(11) NOT NULL,
  `id_sewa` varchar(10) NOT NULL,
  `nip` varchar(10) NOT NULL,
  `tgl_sewa` date NOT NULL,
  `lama_sewa` int(11) NOT NULL,
  `tgl_kmbl` date NOT NULL,
  `id_brg` varchar(10) NOT NULL,
  `harga` int(11) NOT NULL,
  `denda` int(11) NOT NULL,
  `total_harga` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data untuk tabel `pengembalian`
--

INSERT INTO `pengembalian` (`id_kmbl`, `id_sewa`, `nip`, `tgl_sewa`, `lama_sewa`, `tgl_kmbl`, `id_brg`, `harga`, `denda`, `total_harga`) VALUES
(1, 'S001', 'P001', '2021-12-02', 1, '2021-12-03', 'BRG04', 75000, 0, 75000),
(2, 'S002', 'P001', '2021-12-03', 2, '2021-12-06', 'BRG03', 40000, 5000, 45000);

--
-- Trigger `pengembalian`
--
DELIMITER $$
CREATE TRIGGER `trg_tambah_stok` BEFORE INSERT ON `pengembalian` FOR EACH ROW BEGIN
	SET @x = (SELECT s.jml_brg 
    FROM penyewaan s 
    WHERE s.id_sewa = NEW.id_sewa);
    UPDATE barang SET
    stok = stok + @x
    WHERE barang.id_brg = NEW.id_brg;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Struktur dari tabel `penyewaan`
--

CREATE TABLE `penyewaan` (
  `id_sewa` varchar(10) NOT NULL,
  `nip` varchar(10) DEFAULT NULL,
  `id_pel` varchar(10) DEFAULT NULL,
  `id_brg` varchar(10) DEFAULT NULL,
  `jml_brg` int(11) DEFAULT NULL,
  `tgl_sewa` date DEFAULT NULL,
  `lama_sewa` int(11) DEFAULT NULL,
  `tgl_kembali` date DEFAULT NULL,
  `harga` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data untuk tabel `penyewaan`
--

INSERT INTO `penyewaan` (`id_sewa`, `nip`, `id_pel`, `id_brg`, `jml_brg`, `tgl_sewa`, `lama_sewa`, `tgl_kembali`, `harga`) VALUES
('S001', 'P001', 'PEL1', 'BRG04', 3, '2021-12-02', 1, '2021-12-03', 75000),
('S002', 'P001', 'PEL2', 'BRG03', 2, '2021-12-03', 2, '2021-12-05', 40000);

--
-- Trigger `penyewaan`
--
DELIMITER $$
CREATE TRIGGER `trg_kurangi_stok` BEFORE INSERT ON `penyewaan` FOR EACH ROW BEGIN
	UPDATE barang SET 
    stok = stok - NEW.jml_brg
    WHERE barang.id_brg = NEW.id_brg;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_ubah_stok` BEFORE UPDATE ON `penyewaan` FOR EACH ROW BEGIN
	IF (NEW.jml_brg > OLD.jml_brg) THEN
    	UPDATE barang SET 
        stok = stok - (NEW.jml_brg - OLD.jml_brg)
        WHERE barang.id_brg = NEW.id_brg;
	ELSEIF (NEW.jml_brg < OLD.jml_brg) THEN
    	UPDATE barang SET
        stok = stok + (OLD.jml_brg - NEW.jml_brg)
        WHERE barang.id_brg = NEW.id_brg;
	END IF;
END
$$
DELIMITER ;

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `barang`
--
ALTER TABLE `barang`
  ADD PRIMARY KEY (`id_brg`);

--
-- Indeks untuk tabel `pegawai`
--
ALTER TABLE `pegawai`
  ADD PRIMARY KEY (`nip`);

--
-- Indeks untuk tabel `pelanggan`
--
ALTER TABLE `pelanggan`
  ADD PRIMARY KEY (`id_pel`);

--
-- Indeks untuk tabel `pengembalian`
--
ALTER TABLE `pengembalian`
  ADD PRIMARY KEY (`id_kmbl`),
  ADD KEY `id_sewa` (`id_sewa`),
  ADD KEY `nip` (`nip`),
  ADD KEY `id_brg` (`id_brg`);

--
-- Indeks untuk tabel `penyewaan`
--
ALTER TABLE `penyewaan`
  ADD PRIMARY KEY (`id_sewa`),
  ADD KEY `nip` (`nip`),
  ADD KEY `id_pel` (`id_pel`),
  ADD KEY `id_brg` (`id_brg`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `pengembalian`
--
ALTER TABLE `pengembalian`
  MODIFY `id_kmbl` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `pengembalian`
--
ALTER TABLE `pengembalian`
  ADD CONSTRAINT `pengembalian_ibfk_1` FOREIGN KEY (`id_sewa`) REFERENCES `penyewaan` (`id_sewa`),
  ADD CONSTRAINT `pengembalian_ibfk_2` FOREIGN KEY (`nip`) REFERENCES `pegawai` (`nip`),
  ADD CONSTRAINT `pengembalian_ibfk_3` FOREIGN KEY (`id_brg`) REFERENCES `barang` (`id_brg`);

--
-- Ketidakleluasaan untuk tabel `penyewaan`
--
ALTER TABLE `penyewaan`
  ADD CONSTRAINT `penyewaan_ibfk_1` FOREIGN KEY (`nip`) REFERENCES `pegawai` (`nip`),
  ADD CONSTRAINT `penyewaan_ibfk_2` FOREIGN KEY (`id_pel`) REFERENCES `pelanggan` (`id_pel`),
  ADD CONSTRAINT `penyewaan_ibfk_3` FOREIGN KEY (`id_brg`) REFERENCES `barang` (`id_brg`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
