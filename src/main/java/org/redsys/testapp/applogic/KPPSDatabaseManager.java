package org.redsys.testapp.applogic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by vyacheslav.vrubel on 22.03.2017.
 */
public class KPPSDatabaseManager {

    public static final int GET_REQUEST = 1;
    public static final int ACT = 2;
    public static final int INTERNAL = 3;
    public static final int SEND_RESPONSE = 4;
    public static final int ADDITIONAL_INTERNAL = 10;

    private Connection conn = null;

    public KPPSDatabaseManager(String host, int port, String user, String pass) {
        String url = "jdbc:postgresql://" + host + ":" + 5432 + "/fri_kpps";
        try {
            conn = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConn() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLastMessageUUID(int range) {
        try {
            Statement st = conn.createStatement();
            String sql = "SELECT in_msg_proc_guid FROM kpps.in_message ORDER BY in_msg_dtcreate DESC FETCH FIRST " + range + "ROWS ONLY";
            ResultSet rs = st.executeQuery(sql);

            List<String> messageUUID = new ArrayList<String>();
            while(rs.next()) {
                messageUUID.add(rs.getString(1));
            }
            return messageUUID;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getLogByOperTypeId(String UUID, int operTypeId) {
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = "SELECT in_msg_log_guid FROM kpps.in_message_log WHERE in_msg_proc_guid = '"
                    + UUID + "' AND oper_type_id = " + operTypeId;
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getLastLogsByOperTypeId(int range, int operTypeId) {
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = "SELECT * FROM kpps.in_message INNER JOIN kpss.;
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
