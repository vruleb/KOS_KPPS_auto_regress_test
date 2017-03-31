package org.redsys.testapp.applogic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vyacheslav.vrubel on 22.03.2017.
 */
public class KPPSDatabaseManager {

    private Connection conn = null;

    public KPPSDatabaseManager() {
    }

    public void connect(String host, int port, String user, String pass) throws SQLException {
        String url = "jdbc:postgresql://" + host + ":" + port + "/fri_kpps";
        conn = DriverManager.getConnection(url, user, pass);
    }

    public void closeConnection() throws SQLException {
        conn.close();
    }

    public List<String> getLastMessageUUID(int range) {
        try {
            Statement st = conn.createStatement();
            String sql = "SELECT in_msg_proc_guid FROM kpps.arc_in_message ORDER BY in_msg_dtcreate DESC FETCH FIRST " + range + "ROWS ONLY";
            ResultSet rs = st.executeQuery(sql);

            List<String> messageUUID = new ArrayList<String>();
            while (rs.next()) {
                messageUUID.add(rs.getString(1));
            }
            return messageUUID;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getLogUUID(String procUUID, int operTypeId) {
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = "SELECT in_msg_log_guid FROM kpps.arc_in_message_log WHERE in_msg_proc_guid = '"
                    + procUUID + "' AND oper_type_id = " + operTypeId;
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

    public String getErrorCode(String logUUID) {
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = "SELECT error_code_id FROM kpps.arc_in_message_log WHERE in_msg_log_guid = '"
                    + logUUID + "'";
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

    public String getLog(String logUUID, int reqResp) {
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql, reqRespField;
            switch (reqResp) {
                case 0:
                    sql = reqRespField = "in_msg_log_request";
                    break;
                case 1:
                    sql = reqRespField = "in_msg_log_response";
                    break;
                default:
                    return null;
            }
            sql = "SELECT " + reqRespField + " FROM kpps.arc_in_message_log WHERE in_msg_log_guid = '"
                    + logUUID + "'";
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

    public int getProcStatus(String procUUID) {
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = "SELECT process_status_id FROM kpps.arc_in_message_log WHERE in_msg_proc_guid = '"
                    + procUUID + "'";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;

        } catch (SQLException e) {
            e.printStackTrace();
            return -2;
        }
    }

}
