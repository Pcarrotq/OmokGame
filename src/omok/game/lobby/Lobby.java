package omok.game.lobby;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import omok.chat.ChatInventory;
import omok.game.board.frame.GUI;
import omok.game.character.ConectUserInfo;
import omok.game.character.PlayerInfo;
import omok.main.GameStartScreen;
import omok.main.function.login.Login;
import omok.member.db.DBConnection;
import omok.member.UserProfile;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;

import javax.swing.*;

import java.util.Date;
import java.util.List;

public class Lobby extends JFrame {
	private String nickname = DBConnection.getNickname(); // 로그인한 유저의 닉네임을 DB에서 가져옴
	
	public JPanel mainPanel;
	private JTable table;
    private JTextPane chatArea;
    private JTextField chatInput;
    private JComboBox<String> chatTypeDropdown;
    
    private DefaultTableModel tableModel;
    private DefaultTableModel userModel;
    private DefaultListModel<String> userListModel;
    
    private DBConnection dbConnection;
    
    private Socket socket;
    private PrintWriter out;
    String serverAddress = "127.0.0.1"; // 서버 IP 주소
    int serverPort = 8080;            // 서버 포트 번호
    
    private String initializeNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return "Unknown"; // 기본값 설정
        }
        return nickname;
    }
	
	public Lobby(String nickname) {
		this.nickname = initializeNickname(nickname);
		
        try {
            // 서버와 연결
            socket = new Socket("localhost", 8080);
            out = new PrintWriter(socket.getOutputStream(), true);
            startListening();

            // 서버에 닉네임 전송
            out.println("[닉네임]" + nickname);
        } catch (IOException e) {
            e.printStackTrace();
        }
	    
        JFrame frame = new JFrame("Lobby");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        
        dbConnection = new DBConnection();

        // 메인 패널 설정 (전체 레이아웃)
        mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // 테이블 초기화
        String[] columnNames = {"번호", "방 이름", "방장", "인원", "상태"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 수정 불가
            }
        };

        table = new JTable(tableModel); // 필드에 초기화
        table.setRowHeight(30);
        
        // 열 너비 조정
        table.getColumnModel().getColumn(0).setPreferredWidth(30); // "번호"
        table.getColumnModel().getColumn(1).setPreferredWidth(250); // "방 이름"
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // "방장"
        table.getColumnModel().getColumn(3).setPreferredWidth(50); // "인원"
        table.getColumnModel().getColumn(4).setPreferredWidth(50); // "상태"
        
        JScrollPane tableScrollPane = new JScrollPane(table);

        // 오른쪽 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(7, 1, 5, 5)); // 세로 열 7줄, 가로 행 1줄
        buttonPanel.setPreferredSize(new Dimension(120, 0));
        
        JButton createGameButton = new JButton("게임 생성");
        createGameButton.addActionListener(e -> {
            String loggedInUser = Login.getLoggedInUserId();

            if (loggedInUser == null || loggedInUser.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "로그인 후 이용해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String roomName = JOptionPane.showInputDialog("방 이름을 입력하세요:");
            if (roomName != null && !roomName.trim().isEmpty()) {
                out.println("[CREATE_ROOM] " + roomName); // 서버에 방 생성 요청 전송
            }
            
            if (roomName != null && !roomName.trim().isEmpty()) {
                int newRoomNumber = table.getRowCount() + 1;
                tableModel.addRow(new Object[]{newRoomNumber, roomName, loggedInUser, "1/2", "대기 중"});

                JFrame gameFrame = new JFrame("게임 방: " + roomName);
                gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // GUI 생성 로직에 로그 추가
                System.out.println("게임 방 생성됨: " + roomName);
                GUI gameGui = new GUI(roomName, serverAddress, serverPort); // GUI 객체 생성
                gameFrame.add(gameGui);
                gameFrame.setSize(1500, 1000);

                gameFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        removeRoomByTitle(roomName); // 방 삭제 호출
                    }
                });

                SwingUtilities.invokeLater(() -> frame.dispose());
                gameFrame.setVisible(true);
                JOptionPane.showMessageDialog(frame, "새 게임 방이 생성되었습니다!", "생성 성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "유효한 방 이름을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(createGameButton);
        
        JButton joinGameButton = new JButton("게임 입장");
        joinGameButton.addActionListener(e -> {
            // 선택된 행 가져오기
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                // 선택된 행이 없을 경우
                JOptionPane.showMessageDialog(frame, "입장할 방을 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 방 정보 가져오기
            String roomName = (String) tableModel.getValueAt(selectedRow, 1); // 방 이름
            String hostName = (String) tableModel.getValueAt(selectedRow, 2); // 방장 이름
            String status = (String) tableModel.getValueAt(selectedRow, 4); // 방 상태

            // 상태 확인
            if (!"대기 중".equals(status)) {
                JOptionPane.showMessageDialog(frame, "이미 게임이 진행 중인 방입니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 새로운 게임 창 생성 및 방 입장
            JFrame gameFrame = new JFrame("게임 방: " + roomName);
            gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // GUI 추가
            gameFrame.add(new GUI(roomName, serverAddress, serverPort)); // GUI는 방 이름을 인자로 받아 생성
            gameFrame.setSize(1500, 1000);

            // 창 닫힐 때 처리 (필요 시 방 삭제 또는 상태 변경 로직 추가 가능)
            gameFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    // 방 상태를 "대기 중"으로 되돌릴 수 있음 (선택사항)
                }
            });

            // 새로운 게임 창 표시
            gameFrame.setVisible(true);
            JOptionPane.showMessageDialog(frame, roomName + " 방에 입장했습니다.", "입장 성공", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(joinGameButton);
        
        JButton randomMatchButton = new JButton("랜덤 매칭");
        randomMatchButton.addActionListener(e -> {
            // 1. "방을 찾는 중입니다." 메시지 창 표시
            JOptionPane.showMessageDialog(frame, "방을 찾는 중입니다.", "랜덤 매칭", JOptionPane.INFORMATION_MESSAGE);

            // 2. 방 검색 로직 (예제에서는 임의로 방을 찾았다고 가정)
            boolean roomFound = false;
            int searchAttempts = 0;

            while (!roomFound && searchAttempts < 5) { // 최대 5번 시도
                try {
                    Thread.sleep(1000); // 방 검색 시뮬레이션 (1초 대기)
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                // 방이 있다고 가정하는 조건 (여기서는 임의로 설정)
                // 실제 로직에서는 DB 또는 서버와의 통신 필요
                roomFound = tableModel.getRowCount() > 0; // 테이블에 방이 있는 경우
                searchAttempts++;
            }

            // 3. 방을 찾았는지 확인
            if (roomFound) {
                JOptionPane.showMessageDialog(frame, "방을 찾았습니다!", "랜덤 매칭", JOptionPane.INFORMATION_MESSAGE);

                // 4. GUI 화면으로 전환
                int selectedRoom = 0; // 첫 번째 방 선택 (예제)
                String roomName = (String) tableModel.getValueAt(selectedRoom, 1); // 방 이름 가져오기

                JFrame gameFrame = new JFrame("게임 방: " + roomName);
                gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // GUI 추가
                gameFrame.add(new GUI(roomName, serverAddress, serverPort)); // 방 이름 전달
                gameFrame.setSize(1500, 1000);

                // 새로운 게임 창 표시
                gameFrame.setVisible(true);
                JOptionPane.showMessageDialog(frame, roomName + " 방에 입장했습니다.", "입장 성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "방을 찾을 수 없습니다. 다시 시도해주세요.", "랜덤 매칭 실패", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(randomMatchButton);
        
        JButton spectateButton = new JButton("관전");
        spectateButton.addActionListener(e -> {
            // 선택된 행 가져오기
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "관전할 방을 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 방 정보 가져오기
            String roomName = (String) tableModel.getValueAt(selectedRow, 1); // 방 이름

            // 관전 전용 GUI 생성
            JFrame spectatorFrame = new JFrame("관전 중: " + roomName);
            spectatorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            GUI spectatorGUI = new GUI(roomName, serverAddress, serverPort);
            spectatorGUI.setSpectatorMode(true); // 관전 모드 활성화

            spectatorFrame.add(spectatorGUI);
            spectatorFrame.setSize(1500, 1000);

            spectatorFrame.setVisible(true);
            JOptionPane.showMessageDialog(frame, roomName + " 방을 관전 중입니다.", "관전 시작", JOptionPane.INFORMATION_MESSAGE);

            // 관전 채팅 전송
            spectatorGUI.getBtnSend().addActionListener(event -> {
                String message = spectatorGUI.getTxtInput().getText().trim();
                if (!message.isEmpty()) {
                    DBConnection dbConnection = new DBConnection();
                    String spectatorNickname = dbConnection.getNickname(); // 변수 이름 변경
                    spectatorGUI.sendChatMessage(spectatorNickname, message);
                    spectatorGUI.getTxtInput().setText("");
                }
            });
        });
        buttonPanel.add(spectateButton);
        
        JButton myInfoButton = new JButton("내 정보");
        myInfoButton.addActionListener(e -> {
            String loggedInUserId = Login.getLoggedInUserId(); // 로그인된 사용자 ID 가져오기
            if (loggedInUserId == null || loggedInUserId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "로그인 후 이용해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // PlayerInfo 창 호출
            SwingUtilities.invokeLater(() -> new PlayerInfo(loggedInUserId));
        });
        buttonPanel.add(myInfoButton);
        
        JButton chattingButton = new JButton("대화");
        chattingButton.addActionListener(e -> {
            // ChatInventory 화면 열기
            SwingUtilities.invokeLater(ChatInventory::new);
        });
        buttonPanel.add(chattingButton);
        
        JButton exitButton = new JButton("나가기");
        exitButton.addActionListener(e -> {
            // GameStartScreen 인스턴스를 생성
            GameStartScreen mainFrame = new GameStartScreen();

            // showAdminScreen 호출로 Admin Screen 표시
            mainFrame.showAdminScreen();

            // GameStartScreen 창 표시
            mainFrame.setVisible(true);

            // 현재 Lobby 창 닫기
            SwingUtilities.getWindowAncestor(exitButton).dispose();
        });
        buttonPanel.add(exitButton);

        // 채팅 영역
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextPane();
        chatArea.setEditable(false); // 읽기 전용
        chatArea.setPreferredSize(new Dimension(0, 200)); // 높이

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setPreferredSize(new Dimension(0, 200)); // 스크롤 영역 높이

        // 채팅 입력 필드와 드롭다운
        chatTypeDropdown = new JComboBox<>(new String[]{"일반 채팅", "스피커"});
        chatTypeDropdown.setPreferredSize(new Dimension(100, 30));
        chatInput = new JTextField();
        JButton sendButton = new JButton("전송");

        // 전송 버튼 동작 추가
        sendButton.addActionListener(e -> sendMessage());

        // 엔터키로 메시지 전송
        chatInput.addActionListener(e -> sendMessage());

        JPanel chatInputPanel = new JPanel(new BorderLayout());
        JPanel chatInputWithDropdown = new JPanel(new BorderLayout());
        chatInputWithDropdown.add(chatTypeDropdown, BorderLayout.WEST);
        chatInputWithDropdown.add(chatInput, BorderLayout.CENTER);

        chatInputPanel.add(chatInputWithDropdown, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        // 닉네임 리스트 영역 (채팅 오른쪽)
        // 닉네임 리스트 영역 (탭 패널로 수정)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(300, 300));

        // 검색 패널
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(140, 30));
        JButton searchButton = new JButton("검색");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // "접속 유저" 탭
        userModel = new DefaultTableModel(new String[]{"접속 유저"}, 0);
        JTable userTable = new JTable(userModel);
        userTable.setRowHeight(30);
        
        userTable.getColumnModel().getColumn(0).setPreferredWidth(180); // "접속 유저"
        
        JScrollPane userScrollPane = new JScrollPane(userTable);
        tabbedPane.addTab("접속 유저", userScrollPane);

        // "랭킹" 탭
        DefaultTableModel rankModel = new DefaultTableModel(new String[]{"순위", "닉네임", "점수"}, 0);
        JTable rankTable = new JTable(rankModel);
        rankTable.setRowHeight(30);
        
        // 열 너비 조정
        rankTable.getColumnModel().getColumn(0).setPreferredWidth(40); // "순위"
        rankTable.getColumnModel().getColumn(1).setPreferredWidth(160); // "닉네임"
        rankTable.getColumnModel().getColumn(2).setPreferredWidth(70); // "점수"
        
        JScrollPane rankScrollPane = new JScrollPane(rankTable);
        tabbedPane.addTab("랭킹", rankScrollPane);

        // 초기 데이터 로드
        connectUserList(userModel); // 접속 유저 리스트 초기화
        updateRankList(rankModel); // 랭킹 리스트 초기화

        // 검색 기능 연결
        setupSearchFunctionality(searchField, searchButton, userModel);

        // 검색과 탭을 함께 배치
        JPanel tabAndSearchPanel = new JPanel(new BorderLayout());
        tabAndSearchPanel.add(searchPanel, BorderLayout.NORTH);
        tabAndSearchPanel.add(tabbedPane, BorderLayout.CENTER);

        // 채팅과 닉네임 리스트를 함께 배치
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(chatPanel, BorderLayout.CENTER);
        bottomPanel.add(tabAndSearchPanel, BorderLayout.EAST);

        // 상단 영역 (테이블과 버튼)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(tableScrollPane, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // 메인 패널 구성
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void sendMessage() {
	    String message = chatInput.getText().trim();
	    if (message.isEmpty()) {
	        return;
	    }

	    String nickname = Login.getLoggedInUserId();
	    if (nickname == null || nickname.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "로그인 후 사용 가능합니다.", "오류", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    String chatType = (String) chatTypeDropdown.getSelectedItem();
	    String formattedMessage;

	    if ("스피커".equals(chatType)) {
	        formattedMessage = "[스피커] " + nickname + ": " + message;
	    } else {
	        formattedMessage = nickname + ": " + message;
	    }

	    out.println(formattedMessage); // 서버로 메시지 전송
	    chatInput.setText(""); // 입력 필드 초기화
	}
	
	private void startListening() {
	    new Thread(() -> {
	        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
	            String receivedMessage;

	            while ((receivedMessage = in.readLine()) != null) {
	                final String message = receivedMessage.trim();

	                SwingUtilities.invokeLater(() -> {
	                    if (message.startsWith("[ROOM_LIST]")) {
	                        // [ROOM_LIST] 이후 데이터 추출
	                        String roomData = message.length() > 12 ? message.substring(12).trim() : "";
	                        updateRoomList(roomData); // 방 리스트 갱신
	                    } else {
	                        displayMessage(message); // 일반 메시지 출력
	                    }
	                });
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }).start();
	}
	
	private void displayMessage(String message) {
	    try {
	        // "null: " 또는 "[닉네임]" 제거
	        message = message.replace("null: ", "").replaceAll("\\[닉네임\\].*?user[0-9]+", "").trim();
	        
	        // 메시지가 비어 있지 않은 경우만 처리
	        if (message.isEmpty()) return;

	        StyledDocument doc = chatArea.getStyledDocument();
	        SimpleAttributeSet style = new SimpleAttributeSet();

	        if (message.contains("[스피커]")) {
	            StyleConstants.setForeground(style, Color.RED);
	            StyleConstants.setBold(style, true);
	        } else {
	            StyleConstants.setForeground(style, Color.BLACK);
	        }

	        doc.insertString(doc.getLength(), message + "\n", style);
	        chatArea.setCaretPosition(doc.getLength()); // 스크롤을 맨 아래로 이동
	    } catch (BadLocationException e) {
	        e.printStackTrace();
	    }
	}
	
	private void sendMessageToServer(String message) {
	    try {
	        Socket socket = new Socket("localhost", 8080); // 서버와 연결
	        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	        out.println(message); // 서버로 메시지 전송
	        socket.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
    // 방 삭제 메서드
	public void removeRoomByTitle(String roomTitle) {
	    for (int i = 0; i < tableModel.getRowCount(); i++) {
	        if (tableModel.getValueAt(i, 1).equals(roomTitle)) {
	            tableModel.removeRow(i);
	            break;
	        }
	    }
	}
    
    private void createRoom(String roomName) {
        String roomInfo = "0|" + roomName + "|" + nickname + "|1/2|WAITING";
        out.println("[CREATE_ROOM] " + roomInfo); // 서버로 방 생성 메시지 전송
    }

    private void removeRoom(String roomName) {
        out.println("[REMOVE_ROOM] " + roomName); // 서버로 방 삭제 메시지 전송
    }
    
    // 검색 기능 메서드 추가
    private void setupSearchFunctionality(JTextField searchField, JButton searchButton, DefaultListModel<String> userListModel) {
        ActionListener searchAction = e -> {
            String searchQuery = searchField.getText().trim().toLowerCase();
            if (searchQuery.isEmpty()) {
                // 검색어가 비어 있으면 전체 유저 목록 표시
            	updateEntireUserList(userListModel);
            } else {
                // 검색 수행
                searchUserList(searchQuery, userListModel);
            }
        };
        searchButton.addActionListener(searchAction);
        searchField.addActionListener(searchAction); // 엔터키 이벤트 연결
    }

    // 검색 수행 메서드
    private void searchUserList(String query, DefaultListModel<String> userListModel) {
        userListModel.clear(); // 기존 목록 초기화
        DBConnection dbConnection = new DBConnection();
        List<UserProfile> users = dbConnection.getAllUsers();
        for (UserProfile user : users) {
            if (user.getNickname().toLowerCase().contains(query)) {
                userListModel.addElement(user.getNickname()); // 검색 결과 추가
            }
        }

        if (userListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
        }
    }

 // 전체 유저 목록 갱신 메서드
    private void updateEntireUserList(DefaultListModel<String> userListModel) {
        // 예시로 사용할 로컬 유저 리스트
        String[] localUsers = {"user1", "user2", "user3", "user4"};

        // UI 업데이트
        SwingUtilities.invokeLater(() -> {
            userListModel.clear(); // 기존 목록 초기화
            for (String user : localUsers) {
                userListModel.addElement(user); // 닉네임 추가
            }
        });
    }
    
    private void connectUserList(DefaultTableModel userModel) {
        userModel.setRowCount(0); // 기존 데이터 초기화
        List<UserProfile> users = dbConnection.getAllUsers();
        for (UserProfile user : users) {
            userModel.addRow(new Object[]{user.getNickname()});
        }
    }
    
    private void updateRankList(DefaultTableModel rankModel) {
        rankModel.setRowCount(0); // 기존 데이터 초기화
        List<UserProfile> users = dbConnection.getAllUsers();

        // 점수를 기준으로 내림차순 정렬
        users.sort((u1, u2) -> Integer.compare(u2.getScore(), u1.getScore()));

        int rank = 1;
        for (UserProfile user : users) {
            rankModel.addRow(new Object[]{rank++, user.getNickname(), user.getScore()});
        }
    }
    
    private void setupSearchFunctionality(JTextField searchField, JButton searchButton, DefaultTableModel userModel) {
        ActionListener searchAction = e -> {
            String searchQuery = searchField.getText().trim().toLowerCase();
            userModel.setRowCount(0); // 기존 데이터 초기화
            List<UserProfile> users = dbConnection.getAllUsers();

            for (UserProfile user : users) {
                if (user.getNickname().toLowerCase().contains(searchQuery)) {
                    userModel.addRow(new Object[]{user.getNickname()});
                }
            }

            if (userModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            }
        };

        searchButton.addActionListener(searchAction);
        searchField.addActionListener(searchAction); // 엔터키 이벤트 연결
    }

    public void updateUserList(String[] users) {
        SwingUtilities.invokeLater(() -> {
            userModel.setRowCount(0); // 기존 유저 리스트 초기화
            if (users != null) {
                for (String user : users) {
                    if (!user.trim().isEmpty()) { // 빈 문자열 제외
                        userModel.addRow(new Object[]{user});
                    }
                }
            }
        });
    }
    
    private boolean isLocalMessage(String message) {
        // 닉네임 또는 메시지 패턴으로 로컬 메시지 여부 확인
        return message.contains(DBConnection.getNickname());
    }
    
    private void updateRoomList(String roomData) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0); // 기존 데이터 초기화

            if (!roomData.isEmpty()) {
                String[] rooms = roomData.split("\n"); // 각 방 정보 분리
                for (String room : rooms) {
                    String[] details = room.split("\\|"); // 방 번호, 이름, 방장 등 분리
                    if (details.length == 5) { // 유효한 데이터만 추가
                        tableModel.addRow(details);
                    }
                }
            }
        });
    }
}