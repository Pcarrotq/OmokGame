package test.admin.management;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import test.member.db.DBConnection;

public class MemberDelete {
    public static void handleDeleteMember(JTextField idTf, DefaultTableModel tableModel) {
        String memberId = idTf.getText();

        boolean success = deleteMember(memberId);

        if (success) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(memberId)) {
                    tableModel.removeRow(i);
                    break;
                }
            }
            JOptionPane.showMessageDialog(null, "회원 삭제 성공!");
        } else {
            JOptionPane.showMessageDialog(null, "회원 삭제 실패!", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static boolean deleteMember(String memberId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM user_info WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, memberId);

                int rowsDeleted = pstmt.executeUpdate();
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