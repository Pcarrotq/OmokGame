package test.admin;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.event.*;
import java.sql.*;

public class AddMemberDialog extends JDialog {
    private JTextField idField, passwordField, birthDateField, nameField, nicknameField, genderField, emailField;
    private JTextField phoneField, postalCodeField, addressField, detailedAddressField;
    private JButton addButton;
    private MemberDAO memberDAO;

    public AddMemberDialog() {
        setTitle("회원 추가");
        setSize(300, 200);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        memberDAO = new MemberDAO();
        
        idField = new JTextField(10);
        nameField = new JTextField(10);
        nicknameField = new JTextField(10);
        emailField = new JTextField(10);
        birthDateField = new JTextField(10);
        genderField = new JTextField(10);
        phoneField = new JTextField(10);
        postalCodeField = new JTextField(10);
        addressField = new JTextField(10);
        detailedAddressField = new JTextField(10);

        addField("ID", idField);
        addField("이름", nameField);
        addField("닉네임", nicknameField);
        addField("이메일", emailField);

        addButton = new JButton("회원 추가");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMember();
            }
        });
        add(addButton);

        setVisible(true);
    }

    private void addField(String labelText, JTextField textField) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(labelText));
        panel.add(textField);
        add(panel);
    }

    private void addMember() {
		
		// UI에서 입력 값을 가져와 Member 객체 생성
        Member newMember = new Member(
                idField.getText(),
                passwordField.getText(),
                nameField.getText(),
                nicknameField.getText(),
                emailField.getText(),
                Date.valueOf(birthDateField.getText()),  // 형식에 맞게 변환
                genderField.getText(),
                phoneField.getText(),
                postalCodeField.getText(),
                addressField.getText(),
                detailedAddressField.getText(),
                null  // profileImage는 예제로 생략
        );

        // memberDAO를 통해 데이터베이스에 추가
        boolean isAdded = memberDAO.addMember(newMember);
        if (isAdded) {
            JOptionPane.showMessageDialog(this, "Member added successfully!");
            dispose();  // 다이얼로그 닫기
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add member.");
        }
    }
}