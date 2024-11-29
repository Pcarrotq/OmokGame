package omok.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

import omok.admin.AdminScreenMain;
import omok.game.board.frame.GUI;
import omok.game.lobby.Lobby;
import omok.main.function.login.Login;
import omok.main.function.signUp.SignUp;
import omok.member.EditMember;
import omok.member.db.DBConnection;

public class GameStartScreen extends JFrame {
	private JPanel mainPanel;
	private JTextArea weatherTextArea;
	private DBConnection dbConnection = new DBConnection();  // DB 연결 객체
	private String userId = Login.getLoggedInUserId();  // 로그인된 사용자 ID 가져오기
	private static GameStartScreen instance;
	private String loggedInUserId;
	
    public GameStartScreen() {
    	instance = this;
    	
        // JFrame 설정
        setTitle("게임 시작 화면");
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙에 배치

        // 초기 화면 설정
        showLoginScreen();
    }
    
    private void showLoginScreen() {
        // JPanel 생성 및 레이아웃 설정
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Y축으로 구성 요소 배치

        // JLabel: 게임 제목
        JLabel titleLabel = new JLabel("Omok Game", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        panel.add(Box.createVerticalStrut(50)); // 여백 추가
        panel.add(titleLabel);

        // 로그인 버튼
        JButton loginButton = new JButton("로그인");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        loginButton.setMaximumSize(new Dimension(100, 30)); // 버튼 크기 설정
        // 로그인 버튼 액션 리스너
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login login = new Login();
                login.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 로그인 창만 닫힘
                login.setVisible(true);

                // 로그인 성공 시 호출할 콜백 설정
                login.setLoginSuccessCallback(new Runnable() {
                    @Override
                    public void run() {
                        String userId = Login.getLoggedInUserId();
                        if (userId != null) {
                            String status = getUserStatus(userId);
                            if ("BLOCKED".equals(status)) {
                                JOptionPane.showMessageDialog(null, "차단된 유저입니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                            } else if ("DELETED".equals(status)) {
                                JOptionPane.showMessageDialog(null, "삭제된 유저입니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                            } else {
                            	showAdminScreen(); // 정상 상태일 경우 메인 화면 표시
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "로그인 실패", "오류", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            }
        });
        panel.add(Box.createVerticalStrut(20)); // 여백 추가
        panel.add(loginButton);
        
        // 회원가입 버튼
        JButton signUpButton = new JButton("회원가입");
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        signUpButton.setMaximumSize(new Dimension(100, 30)); // 버튼 크기 설정
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // JOptionPane.showMessageDialog(null, "회원가입");
                SignUp signUp = new SignUp();
                signUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 회원가입 창만 닫힘
                signUp.setVisible(true);
            }
        });
        panel.add(Box.createVerticalStrut(20)); // 여백 추가
        panel.add(signUpButton);

        // JButton: 종료 버튼
        JButton exitButton = new JButton("종료");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        exitButton.setMaximumSize(new Dimension(100, 30)); // 버튼 크기 설정
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 프로그램 종료
                System.exit(0);
            }
        });
        panel.add(Box.createVerticalStrut(20)); // 여백 추가
        panel.add(exitButton);

        // 패널을 프레임에 추가
        add(panel);

        // 화면 표시
        setVisible(true);
    }
    
    // 메인 화면 구성
    public void showAdminScreen() {
        getContentPane().removeAll(); // 기존 화면 제거
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Y축으로 구성 요소 배치

        // JLabel: 게임 제목
        JLabel titleLabel = new JLabel("Omok Game", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        mainPanel.add(Box.createVerticalStrut(50)); // 여백 추가
        mainPanel.add(titleLabel);

        // Start 버튼
        JButton startButton = new JButton("Start");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setMaximumSize(new Dimension(100, 30));
        startButton.addActionListener(e -> {
            // DB에서 모든 유저의 닉네임 가져오기
            List<String> nicknames = new ArrayList<>();
            String sql = "SELECT nickname FROM user_info";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    nicknames.add(rs.getString("nickname"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB에서 유저 목록을 가져오는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return; // 오류 발생 시 실행 중단
            }

            // 로비 화면 생성 및 초기화
            JFrame lobbyFrame = new JFrame("Lobby");
            lobbyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            lobbyFrame.setSize(900, 600);

            // 로그인된 유저의 닉네임 가져오기
            String loggedInUser = Login.getLoggedInUserId();
            if (loggedInUser == null || loggedInUser.isEmpty()) {
                JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String nickname = DBConnection.getNickname(); // 닉네임 가져오기
            if (nickname == null || nickname.trim().isEmpty()) {
                nickname = "Unknown"; // 닉네임이 없을 경우 기본값 설정
            }

            // Lobby 생성
            Lobby lobby = new Lobby(nickname);
            lobby.updateUserList(nicknames.toArray(new String[0])); // DB에서 가져온 닉네임 리스트 설정
            lobbyFrame.add(lobby.mainPanel);

            // 로비 화면 표시
            lobbyFrame.setVisible(true);

            // 현재 창 닫기
            dispose();
        });
        mainPanel.add(Box.createVerticalStrut(20)); // 여백 추가
        mainPanel.add(startButton);

        // 개인 설정 버튼
        JButton settingsButton = new JButton("개인 설정");
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsButton.setMaximumSize(new Dimension(100, 30));
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 로그인된 사용자 ID를 사용해 EditMember 화면을 연다
                loggedInUserId = Login.getLoggedInUserId();
                if (loggedInUserId != null) {
                    new EditMember(loggedInUserId);  // EditMember 화면을 연다 (로그인된 사용자 정보가 사용됨)
                } else {
                    JOptionPane.showMessageDialog(null, "로그인된 사용자가 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        mainPanel.add(Box.createVerticalStrut(20)); // 여백 추가
        mainPanel.add(settingsButton);

        // 관리자 설정 버튼
        JButton adminSettingsButton = new JButton("관리자 설정");
        adminSettingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminSettingsButton.setMaximumSize(new Dimension(100, 30));
        adminSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 관리자 대시보드 창 열기
                new AdminScreenMain();
            }
        });
        mainPanel.add(Box.createVerticalStrut(20)); // 여백 추가
        mainPanel.add(adminSettingsButton);

        // 로그아웃 버튼
        JButton logoutButton = new JButton("로그아웃");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(100, 30));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 로그아웃 후 로그인 화면으로 전환
                getContentPane().removeAll(); // 기존 화면 제거
                revalidate();  // 레이아웃을 다시 계산
                repaint();  // 화면을 다시 그리기
                showLoginScreen();  // 로그인 화면 다시 표시
            }
        });
        mainPanel.add(Box.createVerticalStrut(20)); // 여백 추가
        mainPanel.add(logoutButton);

        // 종료 버튼
        JButton exitButton = new JButton("종료");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setMaximumSize(new Dimension(100, 30));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 프로그램 종료
                System.exit(0);
            }
        });
        mainPanel.add(Box.createVerticalStrut(20)); // 여백 추가
        mainPanel.add(exitButton);

        // 날씨 정보 표시 영역 (왼쪽 아래에 배치)
        weatherTextArea = new JTextArea(5, 20); // 크기를 줄임 (5행, 20열)
        weatherTextArea.setEditable(false);
        weatherTextArea.setOpaque(false);  // 배경을 투명하게 설정
        weatherTextArea.setBackground(new Color(0, 0, 0, 0));  // 투명한 배경색 설정
        weatherTextArea.setForeground(Color.BLACK);  // 글자 색을 검정으로 설정

        // 날씨 출력 창의 크기 조정
        JScrollPane scrollPane = new JScrollPane(weatherTextArea);
        scrollPane.setPreferredSize(new Dimension(300, 100)); // 날씨 출력 영역 크기 설정
        scrollPane.setOpaque(false);  // 스크롤 창 자체도 투명하게 설정
        scrollPane.getViewport().setOpaque(false);  // 뷰포트도 투명하게 설정

        // 날씨 정보를 왼쪽 아래(SOUTH)에 배치
        JPanel weatherPanel = new JPanel();
        weatherPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // 왼쪽 정렬
        weatherPanel.setOpaque(false);  // 날씨 패널 자체도 투명하게 설정
        weatherPanel.add(scrollPane);

        mainPanel.add(weatherPanel, BorderLayout.SOUTH); // 아래쪽(SOUTH)에 배치

        // 패널을 프레임에 추가
        getContentPane().add(mainPanel);
        revalidate();
        repaint();
        
        fetchWeatherData();
    }
    
    private String getEnglishCityName(String koreanCity) {
        switch (koreanCity) {
            case "강릉시":
                return "Gangneung";
            case "광명시":
                return "Gwangmyeong";
            case "광주시":
                return "Gwangju";
            case "구미시":
                return "Gumi";
            case "군산시":
                return "Gunsan";
            case "군포시":
                return "Gunpo";
            case "김포시":
                return "Gimpo";
            case "김해시":
                return "Gimhae";
            case "남양주시":
                return "Namyangju";
            case "남원시":
                return "Namwon";
            case "논산시":
                return "Nonsan";
            case "대구광역시":
                return "Daegu";
            case "대전광역시":
                return "Daejeon";
            case "동해시":
                return "Donghae";
            case "목포시":
                return "Mokpo";
            case "부산광역시":
                return "Busan";
            case "서울특별시":
                return "Seoul";
            case "세종특별자치시":
                return "Sejong";
            case "속초시":
                return "Sokcho";
            case "수원시":
                return "Suwon";
            case "순천시":
                return "Suncheon";
            case "아산시":
                return "Asan";
            case "안동시":
                return "Andong";
            case "안산시":
                return "Ansan";
            case "안양시":
                return "Anyang";
            case "양산시":
                return "Yangsan";
            case "여수시":
                return "Yeosu";
            case "영주시":
                return "Yeongju";
            case "용인시":
                return "Yongin";
            case "울산광역시":
                return "Ulsan";
            case "의왕시":
                return "Uiwang";
            case "의정부시":
                return "Uijeongbu";
            case "이천시":
                return "Icheon";
            case "인천광역시":
                return "Incheon";
            case "전주시":
                return "Jeonju";
            case "제주시":
                return "Jeju";
            case "제천시":
                return "Jecheon";
            case "진주시":
                return "Jinju";
            case "창원시":
                return "Changwon";
            case "천안시":
                return "Cheonan";
            case "청주시":
                return "Cheongju";
            case "춘천시":
                return "Chuncheon";
            case "충주시":
                return "Chungju";
            case "통영시":
                return "Tongyeong";
            case "파주시":
                return "Paju";
            case "평택시":
                return "Pyeongtaek";
            case "포항시":
                return "Pohang";
            case "화성시":
                return "Hwaseong";
            default:
                return "Seoul"; // 기본값 설정
        }
    }
    
    private String getUserStatus(String userId) {
        String status = null;
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT status FROM user_info WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                status = rs.getString("status");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }
    
    private void fetchWeatherData() {
        new Thread(() -> {
            try {
                String loggedInUserId = Login.getLoggedInUserId();
                System.out.println("로그인된 사용자 ID: " + loggedInUserId);

                if (loggedInUserId == null) {
                    SwingUtilities.invokeLater(() -> weatherTextArea.append("로그인된 사용자가 없습니다.\n"));
                    return;
                }

                Connection conn = dbConnection.getConnection();
                String query = "SELECT postal_code, address, detailed_address FROM user_info WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);

                pstmt.setString(1, loggedInUserId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String address = rs.getString("address");
                    System.out.println("Retrieved address: " + address);

                    // 도시명 추출 로직 개선
                    String city = extractCityName(address);
                    System.out.println("Extracted City: " + city);

                    // 한글 도시명을 영어 도시명으로 변환
                    String englishCity = getEnglishCityName(city);
                    System.out.println("Converted English City: " + englishCity);

                    String apiKey = "0d8f6d74e32882c838851fbd3ecffaf6";
                    String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + englishCity + "&appid=" + apiKey + "&units=metric";
                    System.out.println("Weather API URL: " + urlString);

                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    int status = connection.getResponseCode();
                    if (status == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                        double temperature = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
                        String weatherDescription = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();

                        // 도시명만 출력되도록 UI 업데이트
                        SwingUtilities.invokeLater(() -> {
                            weatherTextArea.setText("");
                            weatherTextArea.append("도시: " + city + "\n");
                            weatherTextArea.append("현재 온도: " + temperature + "°C\n");
                            weatherTextArea.append("날씨 설명: " + weatherDescription + "\n");
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> weatherTextArea.append("Error: " + status + "\n"));
                    }
                    connection.disconnect();
                } else {
                    SwingUtilities.invokeLater(() -> weatherTextArea.append("사용자 정보를 찾을 수 없습니다.\n"));
                    System.out.println("사용자 정보를 찾을 수 없습니다.");
                }

                rs.close();
                pstmt.close();
                conn.close();
            } catch (SQLException | IOException e) {
                SwingUtilities.invokeLater(() -> weatherTextArea.append("Exception: " + e.getMessage() + "\n"));
                e.printStackTrace();
            }
        }).start();
    }
    
    private String extractCityName(String address) {
        String[] addressParts = address.split(" ");
        
        // '경기도 안산시' 같은 경우 '안산시' 추출
        for (String part : addressParts) {
            if (part.endsWith("시")) {
                return part; // 첫 번째로 발견된 "시"가 포함된 단어 반환
            }
        }
        
        // 도시에 대한 정보가 없다면 기본적으로 첫 번째 단어 반환
        return addressParts[0];
    }
    
    public static void showMainScreenStatic() {
        if (instance != null) {
            instance.showAdminScreen();
        }
    }
    
    public JPanel mainPanel() {
        return this.mainPanel;
    }

    public static void main(String[] args) {
        // 시작 화면 생성
        new GameStartScreen();
    }
}