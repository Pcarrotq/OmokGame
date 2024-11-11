package test.admin;

import javax.swing.*;
import java.awt.event.*;

public class ViewMemberDialog extends JDialog {
    private JTextField idField;
    private JTextArea resultArea;
    private JButton viewButton;

    public ViewMemberDialog() {
        setTitle("회원 정보 열람");
        setSize(300, 300);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        idField = new JTextField(10);
        resultArea = new JTextArea(10, 20);
        resultArea.setEditable(false);

        add(new JLabel("회원 ID:"));
        add(idField);

        viewButton = new JButton("조회");
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewMember();
            }
        });
        add(viewButton);
        add(new JScrollPane(resultArea));

        setVisible(true);
    }

    private void viewMember() {
        // 회원 정보 조회 후 결과를 resultArea에 표시
        resultArea.setText("회원 정보가 조회되었습니다.");
    }
}