package test.admin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddMemberDialog extends JDialog {
    private JTextField idField, nameField, nicknameField, emailField;
    private JButton addButton;

    public AddMemberDialog() {
        setTitle("회원 추가");
        setSize(300, 200);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        idField = new JTextField(10);
        nameField = new JTextField(10);
        nicknameField = new JTextField(10);
        emailField = new JTextField(10);

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
        // 데이터베이스에 회원 추가 로직
        // 회원 정보 추가 후 다이얼로그 닫기
        JOptionPane.showMessageDialog(this, "회원이 추가되었습니다.");
        dispose();
    }
}