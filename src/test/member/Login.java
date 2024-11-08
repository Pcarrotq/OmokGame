package test.member;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends JFrame implements ActionListener {
    // GUI 컴포넌트
    JPanel mainPanel;
    JTextField idTextField;
    JPasswordField passTextField;
    JButton loginButton;

    Font font = new Font("회원가입", Font.BOLD, 40);
    
    private DBConnection lp; // 데이터베이스 연결 객체
    
    private Runnable loginSuccessCallback;
    // 정적 필드 추가 - 로그인한 사용자 ID 저장
    private static String loggedInUserId;

    // 생성자
    public Login() {
        // JFrame 설정
        setTitle("Login Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 닫기 버튼 동작 설정
        setSize(1000, 700); // 프레임 크기 설정

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 1)); // 패널 레이아웃 설정
        
        JPanel centerPanel = new JPanel();
        JLabel loginLabel = new JLabel("로그인 화면");
        loginLabel.setFont(font);
        centerPanel.add(loginLabel);
        
        JPanel gridBagidInfo = new JPanel(new GridBagLayout());
        gridBagidInfo.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        GridBagConstraints c = new GridBagConstraints();

        JLabel idLabel = new JLabel(" 아이디 : ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        gridBagidInfo.add(idLabel, c);

        idTextField = new JTextField(15);
        c.insets = new Insets(0, 5, 0, 0);
        c.gridx = 1;
        c.gridy = 0;
        gridBagidInfo.add(idTextField, c);

        JLabel passLabel = new JLabel(" 비밀번호 : ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(20, 0, 0, 0);
        gridBagidInfo.add(passLabel, c);

        passTextField = new JPasswordField(15);
        c.insets = new Insets(20, 5, 0, 0);
        c.gridx = 1;
        c.gridy = 1;
        gridBagidInfo.add(passTextField, c);

        // 로그인 버튼 추가
        loginButton = new JButton("로그인");
        loginButton.addActionListener(this); // 액션 리스너 추가
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton); // 버튼을 패널에 추가
        
        // 패널을 mainPanel에 추가
        mainPanel.add(centerPanel);
        mainPanel.add(gridBagidInfo);
        mainPanel.add(buttonPanel); // 버튼 패널 추가

        // JFrame에 mainPanel 추가
        add(mainPanel);
        
        // 화면에 프레임을 표시
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String id = idTextField.getText();
            String pass = new String(passTextField.getPassword());

            lp = new DBConnection();
            try (Connection conn = lp.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM user_info WHERE id = ?")) {
                 
                pstmt.setString(1, id);
                ResultSet rset = pstmt.executeQuery();

                if (rset.next()) {
                    String dbPassword = rset.getString("password");
                    if (dbPassword.equals(pass)) {
                        JOptionPane.showMessageDialog(this, "로그인 성공", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);

                        // 로그인 성공 시 로그인한 사용자 ID를 저장
                        loggedInUserId = id;
                        
                        // 로그인된 사용자 ID 로그로 확인
                        System.out.println("로그인된 사용자 ID: " + loggedInUserId);

                        dispose();

                        if (loginSuccessCallback != null) {
                            loginSuccessCallback.run(); // GameStartScreen으로 돌아가기
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "로그인 실패", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "로그인 실패", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "로그인 실패", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // 정적 메서드: 로그인된 사용자 ID 반환
    public static String getLoggedInUserId() {
        return loggedInUserId;
    }

    public void setLoginSuccessCallback(Runnable callback) {
        this.loginSuccessCallback = callback;
    }

    // 메인 메서드
    public static void main(String[] args) {
        new Login();
    }
}
