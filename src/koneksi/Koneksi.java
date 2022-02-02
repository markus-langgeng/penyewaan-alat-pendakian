/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koneksi;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Langgeng
 */
public class Koneksi {
    private static Connection mysqlkonek;
    public static Connection koneksiDatabase() throws SQLException{
        if(mysqlkonek == null){
            new Driver();
            mysqlkonek =  (Connection)DriverManager.getConnection("jdbc:mysql://localhost:3306/sewa_alat_pendakian", "root", "");
        }
        return mysqlkonek;
    }
}
