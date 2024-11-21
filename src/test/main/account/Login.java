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
    JComboBox<String> searchOptions = new JComboBox<>(new String[]{"이메일", "전화번호", "이름"});

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
        
        JPanel searchPanel = new JPanel();
        
        idSearchBtn = new JButton("아이디 찾기");
        idSearchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createIdSearchUI();
            }
        });
        c.insets = new Insets(0, 5, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        searchPanel.add(idSearchBtn);
        
        passwordSearchBtn = new JButton("비밀번호 찾기");
        passwordSearchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPasswordSearchUI();
            }
        });
        c.insets = new Insets(20, 5, 0, 0);
        c.gridx = 0;
        c.gridy = 1;
        searchPanel.add(passwordSearchBtn);

        loginButton = new JButton("로그인");
        loginButton.addActionListener(this);
        
        JPanel buttonPanel = new JPanel();
        
        buttonPanel.add(loginButton);

        mainPanel.add(centerPanel);
        mainPanel.add(gridBagidInfo);
        mainPanel.add(buttonPanel);
        mainPanel.add(searchPanel);

        add(mainPanel);
        
        setVisible(true); // 화면에 프레임을 표시
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
    private void searchId(String selectedOption, String inputValue) {
        try {
            String query = null;
            switch (selectedOption) {
                case "이메일":
                    query = "SELECT id FROM user_info WHERE email = ?";
                    break;
                case "전화번호":
                    query = "SELECT id FROM user_info WHERE phone_number = ?";
                    break;
                case "이름":
                    query = "SELECT id FROM user_info WHERE name = ?";
                    break;
            }

            PreparedStatement pstmt = lp.getConnection().prepareStatement(query);
            pstmt.setString(1, inputValue);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String userId = rs.getString("id");
                JOptionPane.showMessageDialog(null, "아이디: " + userId);
            } else {
                JOptionPane.showMessageDialog(null, "해당 정보로 등록된 아이디가 없습니다.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "오류가 발생했습니다. 다시 시도해주세요.");
        }
    }
    
    // 비밀번호 찾기 메소드
    private void searchPassword(String selectedOption, String inputValue) {
        try {
            String query = null;
            switch (selectedOption) {
                case "이메일":
                    query = "SELECT password FROM user_info WHERE email = ?";
                    break;
                case "전화번호":
                    query = "SELECT password FROM user_info WHERE phone_number = ?";
                    break;
                case "이름":
                    query = "SELECT password FROM user_info WHERE name = ?";
                    break;
            }

            PreparedStatement pstmt = lp.getConnection().prepareStatement(query);
            pstmt.setString(1, inputValue);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String password = rs.getString("password");
                JOptionPane.showMessageDialog(null, "비밀번호: " + password);
            } else {
                JOptionPane.showMessageDialog(null, "해당 정보로 등록된 비밀번호가 없습니다.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "오류가 발생했습니다. 다시 시도해주세요.");
        }
    }
    
 // 아이디 찾기 UI 패널 구성
    private void createIdSearchUI() {
        JFrame frame = new JFrame("아이디 찾기");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 메인 패널 설정
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // BorderLayout으로 간격 추가
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 패널 외부 여백

        // 상단 제목 패널
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("아이디 찾기");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titlePanel.add(titleLabel);

        // 중앙 입력 패널
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 컴포넌트 간 여백
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 검색 옵션 라벨 및 콤보박스
        JLabel optionLabel = new JLabel("검색 옵션:");
        optionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(optionLabel, gbc);

        JComboBox<String> comboBox = new JComboBox<>(new String[]{"이메일", "전화번호", "이름"});
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputPanel.add(comboBox, gbc);

        // 입력 필드 라벨 및 텍스트 필드
        JLabel inputLabel = new JLabel("입력:");
        inputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        inputPanel.add(inputLabel, gbc);

        JTextField inputField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(inputField, gbc);

        // 확인 버튼
        JButton searchButton = new JButton("확인");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOption = (String) comboBox.getSelectedItem();
                String inputValue = inputField.getText();

                if (inputValue.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, selectedOption + "을(를) 입력하세요.");
                } else {
                    searchId(selectedOption, inputValue); // 아이디 검색 로직 호출
                }
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        inputPanel.add(searchButton, gbc);

        // 패널 구성
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // 비밀번호 찾기 UI 패널 구성
    private void createPasswordSearchUI() {
        JFrame frame = new JFrame("비밀번호 찾기");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 메인 패널 설정
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 외부 여백 설정

        // 상단 제목 패널
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("비밀번호 찾기");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titlePanel.add(titleLabel);

        // 중앙 입력 패널
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 컴포넌트 간 여백
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 검색 옵션 라벨 및 콤보박스
        JLabel optionLabel = new JLabel("검색 옵션:");
        optionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(optionLabel, gbc);

        JComboBox<String> comboBox = new JComboBox<>(new String[]{"이메일", "전화번호", "이름"});
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputPanel.add(comboBox, gbc);

        // 입력 필드 라벨 및 텍스트 필드
        JLabel inputLabel = new JLabel("입력:");
        inputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        inputPanel.add(inputLabel, gbc);

        JTextField inputField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(inputField, gbc);

        // 확인 버튼
        JButton searchButton = new JButton("확인");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOption = (String) comboBox.getSelectedItem();
                String inputValue = inputField.getText();

                if (inputValue.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, selectedOption + "을(를) 입력하세요.");
                } else {
                    searchPassword(selectedOption, inputValue); // 비밀번호 검색 로직 호출
                }
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        inputPanel.add(searchButton, gbc);

        // 패널 구성
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // 메인 메서드
    public static void main(String[] args) {
        new Login();
    }
}
