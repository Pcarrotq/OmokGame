package test.main.account;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.*;

import test.member.db.DBConnection;

public class Login extends JFrame implements ActionListener {
    // GUI 컴포넌트
    JPanel mainPanel;
    JTextField idTextField;
    JPasswordField passTextField;
    JButton loginButton, idSearchBtn, passwordSearchBtn;

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
        
        idSearchBtn = new JButton("아이디 찾기");
        idSearchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findId();
            }
        });
        c.insets = new Insets(0, 5, 0, 0);
        c.gridx = 2;
        c.gridy = 0;
        gridBagidInfo.add(idSearchBtn, c);

        JLabel passLabel = new JLabel("비밀번호 : ");
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
        
        passwordSearchBtn = new JButton("비밀번호 찾기");
        passwordSearchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findPassword();
            }
        });
        c.insets = new Insets(20, 5, 0, 0);
        c.gridx = 2;
        c.gridy = 1;
        gridBagidInfo.add(passwordSearchBtn, c);

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
                        DBConnection.loggedInUserId = id;  // DBConnection에도 설정
                        
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
    
    public static boolean login(String userId, String password) {
        if (isValidCredentials(userId, password)) {
            loggedInUserId = userId;
            return true;
        }
        return false;
    }

    public static void logout() {
        loggedInUserId = null;
    }

    public static String getLoggedInUserId() {
        return loggedInUserId;
    }

    private static boolean isValidCredentials(String userId, String password) {
        // 유효성 검사를 수행 (DB 조회 또는 하드코딩된 값 비교 등)
        return "testUser".equals(userId) && "password123".equals(password);
    }

    public void setLoginSuccessCallback(Runnable callback) {
        this.loginSuccessCallback = callback;
    }
    
    // 아이디 찾기 메소드
    private void findId() {
        String email = JOptionPane.showInputDialog(this, "이메일을 입력하세요:");
        if (email != null && !email.isEmpty()) {
            try {
                String query = "SELECT id FROM user_info WHERE email = ?";
                PreparedStatement pstmt = lp.getConnection().prepareStatement(query);
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    String userId = rs.getString("id");
                    JOptionPane.showMessageDialog(this, "아이디: " + userId);
                } else {
                    JOptionPane.showMessageDialog(this, "해당 이메일로 등록된 아이디가 없습니다.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "오류가 발생했습니다. 다시 시도해주세요.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "이메일을 입력하세요.");
        }
    }
    
    // 비밀번호 찾기 메소드
    private void findPassword() {
        String userId = JOptionPane.showInputDialog(this, "아이디를 입력하세요:");
        if (userId != null && !userId.isEmpty()) {
            try {
                String query = "SELECT password FROM user_info WHERE id = ?";
                PreparedStatement pstmt = lp.getConnection().prepareStatement(query);
                pstmt.setString(1, userId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    String password = rs.getString("password");
                    JOptionPane.showMessageDialog(this, "비밀번호: " + password);
                } else {
                    JOptionPane.showMessageDialog(this, "해당 아이디로 등록된 비밀번호가 없습니다.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "오류가 발생했습니다. 다시 시도해주세요.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "아이디를 입력하세요.");
        }
    }

    // 메인 메서드
    public static void main(String[] args) {
        new Login();
    }
}
