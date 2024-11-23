package test.admin.management;

import java.sql.*;
import test.member.db.DBConnection;

public class MemberBlock {
    public static boolean blockMember(String memberId, String reason) {
        try (Connection conn = DBConnection.getConnection()) {
            // 원본 테이블에서 회원 정보를 가져와 차단 테이블로 복사
            String copySql = "INSERT INTO blocked_users (id, name, nickname, email, reason) "
                           + "SELECT id, name, nickname, email, ? FROM user_info WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(copySql)) {
                pstmt.setString(1, reason);
                pstmt.setString(2, memberId);
                pstmt.executeUpdate();
            }

            // 원본 테이블에서 회원 삭제
            String deleteSql = "DELETE FROM user_info WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setString(1, memberId);
                pstmt.executeUpdate();
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}