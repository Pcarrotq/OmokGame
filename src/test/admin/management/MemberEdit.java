package test.admin.management;

import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import test.member.db.DBConnection;

public class MemberEdit {
	public static void handleEditMember(JTextField idTf, JTextField nameTf, JTextField nicknameTf, JTextField emailLocalTf, JTextField emailDomainTf,
			JComboBox<String> phoneFrontComboBox, JTextField phoneMiddleTf, JTextField phoneBackTf, JTextField addressTf, JTextField detailedAddressTf,
			JComboBox<String> yearComboBox, JComboBox<String> monthComboBox, JComboBox<String> dayComboBox, JRadioButton maleButton, DefaultTableModel tableModel) {

		String id = idTf.getText();
		String name = nameTf.getText();
		String nickname = nicknameTf.getText();
		String email = emailLocalTf.getText() + "@" + emailDomainTf.getText();
		String phoneNumber = phoneFrontComboBox.getSelectedItem().toString() + "-" +
		 phoneMiddleTf.getText() + "-" + phoneBackTf.getText();
		String address = addressTf.getText();
		String detailedAddress = detailedAddressTf.getText();
		String birthYear = yearComboBox.getSelectedItem().toString();
		String birthMonth = monthComboBox.getSelectedItem().toString();
		String birthDay = dayComboBox.getSelectedItem().toString();
		String gender = maleButton.isSelected() ? "M" : "F";
		
		// 데이터베이스 업데이트
		boolean updateSuccessful = updateMemberInDatabase(id, name, nickname, email, phoneNumber, address, detailedAddress, birthYear, birthMonth, birthDay, gender);
	
		if (updateSuccessful) {
			// 테이블 데이터 업데이트
			int rowCount = tableModel.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				if (tableModel.getValueAt(i, 0).equals(id)) {
					tableModel.setValueAt(name, i, 1); // 이름 업데이트
					tableModel.setValueAt(nickname, i, 2); // 닉네임 업데이트
					tableModel.setValueAt(email, i, 3); // 이메일 업데이트
					tableModel.setValueAt(gender.equals("M") ? "남" : "여", i, 4); // 성별 업데이트
					break;
				}
			}
			JOptionPane.showMessageDialog(null, "회원 정보가 성공적으로 수정되었습니다.");
		} else {
			JOptionPane.showMessageDialog(null, "회원 정보 수정 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
		}
	}
    
    public static boolean editMember(String memberId, String newName, String newEmail, String newPhoneNumber, String newAddress) {
        try (Connection conn = DBConnection.getConnection()) {
            // 회원 정보 확인
            String checkSql = "SELECT * FROM user_info WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, memberId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("수정 대상 회원을 찾을 수 없음: " + memberId);
                        return false;
                    }
                }
            }

            // 회원 정보 수정
            String sql = "UPDATE user_info SET name = ?, email = ?, phone_number = ?, address = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newName);
                pstmt.setString(2, newEmail);
                pstmt.setString(3, newPhoneNumber);
                pstmt.setString(4, newAddress);
                pstmt.setString(5, memberId);

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("회원 수정 완료: " + memberId);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    private static boolean updateMemberInDatabase(String id, String name, String nickname, String email, String phoneNumber, String address, String detailedAddress,
            String birthYear, String birthMonth, String birthDay, String gender) {
		String query = "UPDATE user_info SET name = ?, nickname = ?, email = ?, phone_number = ?, postal_code = ?, " +
		"address = ?, detailed_address = ?, birth_year = ?, birth_month = ?, birth_day = ?, gender = ? WHERE id = ?";
		
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			// 모든 매개변수를 설정합니다.
			pstmt.setString(1, name);                 // name
			pstmt.setString(2, nickname);             // nickname
			pstmt.setString(3, email);                // email
			pstmt.setString(4, phoneNumber);          // phone_number
			pstmt.setString(5, "");                   // postal_code (예: 빈 문자열로 처리)
			pstmt.setString(6, address);              // address
			pstmt.setString(7, detailedAddress);      // detailed_address
			pstmt.setInt(8, Integer.parseInt(birthYear)); // birth_year
			pstmt.setInt(9, Integer.parseInt(birthMonth)); // birth_month
			pstmt.setInt(10, Integer.parseInt(birthDay)); // birth_day
			pstmt.setString(11, gender);              // gender
			pstmt.setString(12, id);                  // WHERE id = ?
			
			int rowsUpdated = pstmt.executeUpdate();  // SQL 실행
			return rowsUpdated > 0;                   // 업데이트 성공 여부 반환
		} catch (SQLException ex) {
			ex.printStackTrace();                     // 오류 로그 출력
			return false;                             // 실패 시 false 반환
		}
    }
}