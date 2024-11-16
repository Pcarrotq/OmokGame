package test.member;

import java.sql.*;
import java.util.*;

public class MemberList {
    public static List<Map<String, Object>> getAllMembers() {
        List<Map<String, Object>> members = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM user_info";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> member = new HashMap<>();
                    member.put("id", rs.getString("id"));
                    member.put("password", rs.getString("password"));
                    member.put("name", rs.getString("name"));
                    member.put("nickname", rs.getString("nickname"));
                    member.put("email", rs.getString("email"));
                    member.put("birth_year", rs.getInt("birth_year"));
                    member.put("birth_month", rs.getInt("birth_month"));
                    member.put("birth_day", rs.getInt("birth_day"));
                    member.put("gender", rs.getString("gender"));
                    member.put("phone_number", rs.getString("phone_number"));
                    member.put("postal_code", rs.getString("postal_code"));
                    member.put("address", rs.getString("address"));
                    member.put("detailed_address", rs.getString("detailed_address"));
                    member.put("profile_image", rs.getBytes("profile_image"));
                    members.add(member);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return members;
    }
}