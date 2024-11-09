package omok.admin;

import javax.swing.*;
import java.awt.event.*;

public class DeleteMemberDialog extends JDialog {
    private JTextField idField;
    private JButton deleteButton;

    public DeleteMemberDialog() {
        setTitle("회원 삭제");
        setSize(300, 150);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        idField = new JTextField(10);
        add(new JLabel("삭제할 회원 ID:"));
        add(idField);

        deleteButton = new JButton("삭제");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMember();
            }
        });
        add(deleteButton);

        setVisible(true);
    }

    private void deleteMember() {
        // 회원 ID를 기반으로 데이터베이스에서 회원 삭제
        JOptionPane.showMessageDialog(this, "회원이 삭제되었습니다.");
        dispose();
    }
}