package test.admin.management;

import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import test.member.DBConnection;

public class MemberView {
    public static void handleViewMember(JTextField idTf) {
        String memberId = idTf.getText();
        String result = viewMember(memberId);
        JOptionPane.showMessageDialog(null, result, "회원 정보", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static String viewMember(String memberId) {
        String result = "";
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM user_info WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, memberId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        result = "ID: " + rs.getString("id") +
                                 "\n이름: " + rs.getString("name") +
                                 "\n닉네임: " + rs.getString("nickname") +
                                 "\n이메일: " + rs.getString("email") +
                                 "\n생년월일: " + rs.getInt("birth_year") + "-" +
                                               rs.getInt("birth_month") + "-" +
                                               rs.getInt("birth_day") +
                                 "\n성별: " + rs.getString("gender") +
                                 "\n전화번호: " + rs.getString("phone_number") +
                                 "\n우편번호: " + rs.getString("postal_code") +
                                 "\n주소: " + rs.getString("address") +
                                 "\n상세 주소: " + rs.getString("detailed_address");
                        System.out.println("회원 조회 완료: " + memberId);
                    } else {
                        result = "해당 회원을 찾을 수 없습니다.";
                        System.out.println("회원 조회 실패: " + memberId);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result = "데이터베이스 오류 발생.";
        }
        return result;
    }
    
    public static Map<String, Object> getMemberDetails(String memberId) {
        Map<String, Object> memberInfo = new HashMap<>();
        String query = "SELECT * FROM user_info WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    memberInfo.put("id", rs.getString("id"));
                    memberInfo.put("name", rs.getString("name"));
                    memberInfo.put("nickname", rs.getString("nickname"));
                    memberInfo.put("password", rs.getString("password"));
                    memberInfo.put("birth_year", rs.getInt("birth_year"));
                    memberInfo.put("birth_month", rs.getInt("birth_month"));
                    memberInfo.put("birth_day", rs.getInt("birth_day"));
                    memberInfo.put("phone_number", rs.getString("phone_number"));
                    memberInfo.put("gender", rs.getString("gender"));
                    memberInfo.put("email", rs.getString("email"));
                    memberInfo.put("postal_code", rs.getString("postal_code"));
                    memberInfo.put("address", rs.getString("address"));
                    memberInfo.put("detailed_address", rs.getString("detailed_address"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return memberInfo;
    }
}