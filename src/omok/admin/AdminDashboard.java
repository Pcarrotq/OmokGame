package omok.admin;

import javax.swing.*;

import omok.main.GameStartScreen;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("관리자 대시보드");
        setSize(700, 500);
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
        addMemberButton.setMaximumSize(new Dimension(150, 30));
        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddMemberDialog();
            }
        });
        add(Box.createVerticalStrut(20));
        add(addMemberButton);

        // 회원 정보 열람 버튼
        JButton viewMemberButton = new JButton("회원 정보 열람");
        viewMemberButton.setAlignmentX(CENTER_ALIGNMENT);
        viewMemberButton.setMaximumSize(new Dimension(150, 30));
        viewMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewMemberDialog();
            }
        });
        add(Box.createVerticalStrut(20));
        add(viewMemberButton);

        // 회원 정보 수정 버튼
        JButton editMemberButton = new JButton("회원 정보 수정");
        editMemberButton.setAlignmentX(CENTER_ALIGNMENT);
        editMemberButton.setMaximumSize(new Dimension(150, 30));
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
        add(Box.createVerticalStrut(20));
        add(editMemberButton);

        // 회원 삭제 버튼
        JButton deleteMemberButton = new JButton("회원 삭제");
        deleteMemberButton.setAlignmentX(CENTER_ALIGNMENT);
        deleteMemberButton.setMaximumSize(new Dimension(150, 30));
        deleteMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DeleteMemberDialog();
            }
        });
        add(Box.createVerticalStrut(20));
        add(deleteMemberButton);
        
        JButton btnCancel = new JButton("취소");
        btnCancel.setAlignmentX(CENTER_ALIGNMENT);
        btnCancel.setMaximumSize(new Dimension(150, 30));
        btnCancel.addActionListener(e -> {
            GameStartScreen.showMainScreenStatic(); // Calls showMainScreen() in GameStartScreen
            this.dispose(); // Close AdminDashboard window
        });
        add(Box.createVerticalStrut(20));
        add(btnCancel);

        setVisible(true);
    }
}