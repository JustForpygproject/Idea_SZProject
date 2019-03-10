package cn.itcast.core.util;

import cn.itcast.core.common.Common;
import cn.itcast.core.pojo.good.Brand;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbUtil {
    /**
     * @param sql
     */
    public static void insert(String sql, Brand brand) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(Common.URL, Common.USERNAME, Common.PASSWORD);
            ps = conn.prepareStatement(sql);
            ps.setString(1, String.valueOf(brand.getId()));
            ps.setString(2, brand.getName());
            ps.setString(3, brand.getFirstChar());
            boolean flag = ps.execute();
            if(!flag){
                System.out.println("Save data : Id = " + brand.getId() + " , Name = " + brand.getName() + ", First_Char = " + brand.getFirstChar()+ " succeed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List selectOne(String sql, Brand brand) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List list = new ArrayList();
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(Common.URL, Common.USERNAME, Common.PASSWORD);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("id").equals(brand.getId()) || rs.getString("name").equals(brand.getName())|| rs.getString("first_char").equals(brand.getFirstChar())){
                    list.add(1);
                }else{
                    list.add(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }


    public static ResultSet selectAll(String sql) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(Common.DRIVER);
            conn = DriverManager.getConnection(Common.URL, Common.USERNAME, Common.PASSWORD);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return rs;
    }
}
