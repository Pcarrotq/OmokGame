package test.admin.management;

import java.sql.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import test.member.DBConnection;

public class MemberAdd {
    public static void handleAddMember(JTextField idTf, JPasswordField passTf, JTextField nameTf, JTextField nicknameTf,
            JTextField emailLocalTf, JTextField emailDomainTf, JComboBox<String> yearComboBox, JComboBox<String> monthComboBox,
            JComboBox<String> dayComboBox, JComboBox<String> phoneFrontComboBox, JTextField phoneMiddleTf, JTextField phoneBackTf,
            JTextField postalCodeTf, JTextField addressTf, JTextField detailedAddressTf, JRadioButton maleButton, DefaultTableModel tableModel) {

		String id = idTf.getText();
		String password = new String(passTf.getPassword());
		String name = nameTf.getText();
		String nickname = nicknameTf.getText();
		String email = emailLocalTf.getText() + "@" + emailDomainTf.getText();
		String phone = phoneFrontComboBox.getSelectedItem() + phoneMiddleTf.getText() + phoneBackTf.getText();
		String postalCode = postalCodeTf.getText();
		String address = addressTf.getText();
		String detailedAddress = detailedAddressTf.getText();
		int birthYear = Integer.parseInt((String) yearComboBox.getSelectedItem());
		int birthMonth = Integer.parseInt((String) monthComboBox.getSelectedItem());
		int birthDay = Integer.parseInt((String) dayComboBox.getSelectedItem());
		String gender = maleButton.isSelected() ? "M" : "F";
		
		boolean success = addMember(id, password, name, nickname, email, birthYear, birthMonth, birthDay,
		gender, phone, postalCode, address, detailedAddress, null);
	
		if (success) {
			tableModel.addRow(new Object[]{id, name, nickname, email, gender});
			JOptionPane.showMessageDialog(null, "회원 추가 성공!");
		} else {
			JOptionPane.showMessageDialog(null, "회원 추가 실패!", "오류", JOptionPane.ERROR_MESSAGE);
		}
	}
    
    public static boolean addMember(String id, String password, String name, String nickname, String email, int birthYear, int birthMonth, int birthDay, 
            String gender, String phoneNumber, String postalCode, String address, String detailedAddress, byte[] profileImage) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO user_info (id, password, name, nickname, email, birth_year, birth_month, birth_day, gender, phone_number, postal_code, address, "
                       + "detailed_address, profile_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                pstmt.setString(2, password);
                pstmt.setString(3, name);
                pstmt.setString(4, nickname);
                pstmt.setString(5, email);
                pstmt.setInt(6, birthYear);
                pstmt.setInt(7, birthMonth);
                pstmt.setInt(8, birthDay);
                pstmt.setString(9, gender);
                pstmt.setString(10, phoneNumber);
                pstmt.setString(11, postalCode);
                pstmt.setString(12, address);
                pstmt.setString(13, detailedAddress);
                pstmt.setBytes(14, profileImage);

                pstmt.executeUpdate();
                System.out.println("회원 추가 완료: " + id);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}