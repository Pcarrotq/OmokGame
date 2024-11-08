package test.admin;

import javax.swing.*;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("관리자 대시보드");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("관리자 기능 선택");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(titleLabel);

        add(Box.createVerticalStrut(20));  // 간격 추가

        // 회원 추가 버튼
        JButton addMemberButton = new JButton("회원 추가");
        addMemberButton.setAlignmentX(CENTER_ALIGNMENT);
        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddMemberDialog();
            }
        });
        add(addMemberButton);

        // 회원 정보 열람 버튼
        JButton viewMemberButton = new JButton("회원 정보 열람");
        viewMemberButton.setAlignmentX(CENTER_ALIGNMENT);
        viewMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewMemberDialog();
            }
        });
        add(viewMemberButton);

        // 회원 정보 수정 버튼
        JButton editMemberButton = new JButton("회원 정보 수정");
        editMemberButton.setAlignmentX(CENTER_ALIGNMENT);
        editMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ID 입력 창 생성
                String userId = JOptionPane.showInputDialog("수정할 회원의 ID를 입력하세요:");
                
                if (userId != null && !userId.trim().isEmpty()) {
                    // 입력된 ID로 AdminMemberEdit 창 열기
                    new AdminMemberEdit(userId.trim());
                } else {
                    JOptionPane.showMessageDialog(null, "유효한 ID를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(editMemberButton);

        // 회원 삭제 버튼
        JButton deleteMemberButton = new JButton("회원 삭제");
        deleteMemberButton.setAlignmentX(CENTER_ALIGNMENT);
        deleteMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DeleteMemberDialog();
            }
        });
        add(deleteMemberButton);

        setVisible(true);
    }
}