package test.admin.management;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import test.member.db.DBConnection;

public class MemberDelete {
    public static void handleDeleteMember(JTextField idTf, DefaultTableModel tableModel) {
        String memberId = idTf.getText();

        boolean success = softDeleteMember(memberId);

        if (success) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(memberId)) {
                    tableModel.removeRow(i);
                    break;
                }
            }
            JOptionPane.showMessageDialog(null, "회원이 삭제 데이터 테이블로 이동되었습니다.");
        } else {
            JOptionPane.showMessageDialog(null, "회원 삭제 실패!", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static boolean softDeleteMember(String memberId) {
        try (Connection conn = DBConnection.getConnection()) {
            // user_info 테이블에서 데이터를 deleted_users 테이블로 복사
            String copySql = "INSERT INTO deleted_users (id, name, nickname, email, deleted_date) "
                           + "SELECT id, name, nickname, email, CURRENT_TIMESTAMP FROM user_info WHERE id = ?";
            try (PreparedStatement copyStmt = conn.prepareStatement(copySql)) {
                copyStmt.setString(1, memberId);
                copyStmt.executeUpdate();
            }

            // user_info 테이블에서 데이터 삭제
            String deleteSql = "DELETE FROM user_info WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, memberId);
                int rowsDeleted = deleteStmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("회원 삭제 완료: " + memberId);
                    return true;
                } else {
                    System.out.println("삭제 대상 회원을 찾을 수 없음: " + memberId);
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}