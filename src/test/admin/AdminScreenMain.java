package test.admin;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.util.*;
import java.util.List;

import test.member.*;
import test.admin.*;
import test.admin.management.*;
import test.api.ApiExplorer;
import test.member.Login;

public class AdminScreenMain extends JFrame {
    JPanel memberListPanel, memberEditPanel;
    JTable memberTable;
    JTextField searchField;
    DefaultTableModel tableModel;
    TableRowSorter<DefaultTableModel> rowSorter;
    
    JTextField nameTf, idTf, nicknameTf, emailLocalTf, emailDomainTf, phoneMiddleTf, phoneBackTf, postalCodeTf, detailedAddressTf, addressTf;
    JPasswordField passTf, passReTf;
    JComboBox<String> yearComboBox, monthComboBox, dayComboBox, phoneFrontComboBox, emailDomainComboBox;
    JRadioButton maleButton, femaleButton;
    ButtonGroup genderGroup;
    JLabel imageLabel;
    BufferedImage profileImage;
    JButton saveButton, cancelButton, changeProfileButton, idCheckButton, nicknameButton, postalCodeBtn;
    
    DBConnection dbConnection;

    public AdminScreenMain() {
        setTitle("관리자");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 1000);
        setLayout(new GridLayout(1, 2, 10, 10));

        dbConnection = new DBConnection();

        memberListPanel = new JPanel(new BorderLayout());
        memberListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel tableTitle = new JLabel("회원 정보", SwingConstants.CENTER);
        tableTitle.setFont(new Font("SanSerif", Font.BOLD, 30));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel searchLabel = new JLabel("검색: ");
        searchField = new JTextField();
        searchField.setToolTipText("ID 또는 이름으로 검색");

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel titleAndSearchPanel = new JPanel(new BorderLayout());
        titleAndSearchPanel.add(tableTitle, BorderLayout.NORTH);
        titleAndSearchPanel.add(searchPanel, BorderLayout.SOUTH);

        memberListPanel.add(titleAndSearchPanel, BorderLayout.NORTH);

        String[] columnNames = {"아이디", "이름", "닉네임", "이메일", "성별"};
        tableModel = new DefaultTableModel(columnNames, 0);
        memberTable = new JTable(tableModel);
        
        // 테이블 선택 이벤트 리스너 추가
        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // 선택이 완료되었을 때만 처리
                int selectedRow = memberTable.getSelectedRow();
                if (selectedRow != -1) {
                    // 선택된 행의 데이터를 가져옴
                    Object id = memberTable.getValueAt(selectedRow, 0);
                    Object name = memberTable.getValueAt(selectedRow, 1);
                    Object nickname = memberTable.getValueAt(selectedRow, 2);
                    Object email = memberTable.getValueAt(selectedRow, 3);
                    Object gender = memberTable.getValueAt(selectedRow, 4);
                    
                    // 콘솔로 선택 데이터 확인
                    System.out.println("선택된 데이터 - 아이디: " + id + ", 이름: " + name + ", 닉네임: " + nickname + ", 이메일: " + email + ", 성별: " + gender);
                }
            }
        });

        rowSorter = new TableRowSorter<>(tableModel);
        memberTable.setRowSorter(rowSorter);
        
        loadTableData();

        memberTable.setShowGrid(true);
        memberTable.setGridColor(Color.LIGHT_GRAY);
        memberTable.setIntercellSpacing(new Dimension(1, 1));
        memberTable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(null);
        memberListPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel memberButtonPanel = new JPanel();
        memberButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
      
        JButton addButton = new JButton("추가"); // 회원 추가 버튼
        JButton openButton = new JButton("열람"); // 회원 정보 열람 버튼
        JButton editButton = new JButton("수정"); // 회원 정보 수정 버튼
        JButton deleteButton = new JButton("삭제"); // 회원 삭제 버튼
        
        addButton.addActionListener(e -> MemberAdd.handleAddMember(idTf, passTf, nameTf, nicknameTf, emailLocalTf, emailDomainTf,
                yearComboBox, monthComboBox, dayComboBox, phoneFrontComboBox, phoneMiddleTf, phoneBackTf,
                postalCodeTf, addressTf, detailedAddressTf, maleButton, tableModel));
        
        openButton.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "열람할 데이터를 선택하세요.");
                return;
            }

            // 선택된 데이터의 ID 가져오기
            Object id = memberTable.getValueAt(selectedRow, 0);
            if (id == null) {
                JOptionPane.showMessageDialog(this, "선택된 데이터의 ID를 가져올 수 없습니다.");
                return;
            }

            // 데이터베이스에서 해당 ID로 상세 정보 조회
            Map<String, Object> memberInfo = MemberView.getMemberDetails(id.toString());
            if (memberInfo == null || memberInfo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "회원 정보를 가져올 수 없습니다.");
                return;
            }

            // 필드 초기화 및 데이터 설정
            idTf.setText(memberInfo.getOrDefault("id", "").toString());
            nameTf.setText(memberInfo.getOrDefault("name", "").toString());
            nicknameTf.setText(memberInfo.getOrDefault("nickname", "").toString());
            passTf.setText(memberInfo.getOrDefault("password", "").toString());
            passReTf.setText(memberInfo.getOrDefault("password", "").toString()); // 비밀번호 확인도 동일
            yearComboBox.setSelectedItem(memberInfo.getOrDefault("birth_year", "").toString());
            monthComboBox.setSelectedItem(memberInfo.getOrDefault("birth_month", "").toString());
            dayComboBox.setSelectedItem(memberInfo.getOrDefault("birth_day", "").toString());

            // 전화번호 처리 (형식: 010-1234-5678)
            String phoneNumber = memberInfo.getOrDefault("phone_number", "").toString();
            if (!phoneNumber.isEmpty()) {
                System.out.println("가져온 전화번호: " + phoneNumber); // 디버깅용
                if (phoneNumber.length() == 11) { // 11자리 숫자인 경우
                    phoneFrontComboBox.setSelectedItem(phoneNumber.substring(0, 3)); // 첫 3자리
                    phoneMiddleTf.setText(phoneNumber.substring(3, 7)); // 중간 4자리
                    phoneBackTf.setText(phoneNumber.substring(7)); // 마지막 4자리
                } else {
                    System.out.println("전화번호 형식이 올바르지 않습니다: " + phoneNumber);
                }
            }

            // 성별 설정
            String gender = memberInfo.getOrDefault("gender", "").toString();
            if ("M".equals(gender)) {
                maleButton.setSelected(true);
            } else if ("F".equals(gender)) {
                femaleButton.setSelected(true);
            }

            // 이메일 처리
            String email = memberInfo.getOrDefault("email", "").toString();
            if (email.contains("@")) {
                String[] emailParts = email.split("@");
                emailLocalTf.setText(emailParts[0]);
                emailDomainTf.setText(emailParts[1]);
            }

            // 주소 및 상세 주소 처리
            postalCodeTf.setText(memberInfo.getOrDefault("postal_code", "").toString());
            addressTf.setText(memberInfo.getOrDefault("address", "").toString());
            detailedAddressTf.setText(memberInfo.getOrDefault("detailed_address", "").toString());

            // memberEditPanel 표시
            memberEditPanel.setVisible(true);
        });
        
        editButton.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "수정할 데이터를 선택하세요.");
                return;
            }

            MemberEdit.handleEditMember(idTf, nameTf, nicknameTf, emailLocalTf, emailDomainTf, phoneFrontComboBox,
                                        phoneMiddleTf, phoneBackTf, addressTf, detailedAddressTf, yearComboBox,
                                        monthComboBox, dayComboBox, maleButton, tableModel);
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "삭제할 데이터를 선택하세요.");
                return;
            }

            // 삭제 확인
            int confirm = JOptionPane.showConfirmDialog(this, "정말로 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DefaultTableModel model = (DefaultTableModel) memberTable.getModel();
                model.removeRow(selectedRow); // 선택된 행 삭제
                System.out.println("데이터 삭제됨 - 선택된 행: " + selectedRow);
            }
        });
        
        memberButtonPanel.add(addButton);
        memberButtonPanel.add(openButton);
        memberButtonPanel.add(editButton);
        memberButtonPanel.add(deleteButton);

        memberListPanel.add(memberButtonPanel, BorderLayout.SOUTH);
        
        add(memberListPanel);

        // memberEditPanel 구성
        memberEditPanel = new JPanel(new BorderLayout());
        memberEditPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel 설정
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Profile Picture Section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 3;
        gbc.insets = new Insets(5, 5, 5, 20);
        imageLabel = new JLabel("프로필 사진") {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (profileImage != null) {
                    // Scale the image to fit within the fixed size
                    Image scaledImage = profileImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    g.drawImage(scaledImage, 0, 0, 150, 150, this);
                }
            }
        };
        imageLabel.setPreferredSize(new Dimension(150, 150));
        imageLabel.setMinimumSize(new Dimension(150, 150));
        imageLabel.setMaximumSize(new Dimension(150, 150));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        formPanel.add(imageLabel, gbc);

        gbc.gridy = 3;
        gbc.gridheight = 2;
        changeProfileButton = new JButton("이미지 선택");
        formPanel.add(changeProfileButton, gbc);

        // Reset grid constraints
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        // 이름 필드
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gbc.gridx = 2;
        gbc.gridy = 0;
        formPanel.add(new JLabel("이름"), gbc);
        gbc.gridx = 3;
        nameTf = new JTextField(10);
        namePanel.add(nameTf, gbc);
        formPanel.add(namePanel, gbc);

        // 아이디 필드
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("아이디"), gbc);
        idTf = new JTextField(10);
        idPanel.add(idTf);
        idCheckButton = new JButton("중복 확인");
        idPanel.add(idCheckButton);
        gbc.gridx = 3;
        formPanel.add(idPanel, gbc);
        
        // 닉네임 필드
        JPanel nicknamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gbc.gridx = 2;
        gbc.gridy = 2;
        formPanel.add(new JLabel("닉네임"), gbc);
        nicknameTf = new JTextField(10);
        nicknamePanel.add(nicknameTf);
        nicknameButton = new JButton("중복 확인");
        nicknamePanel.add(nicknameButton);
        gbc.gridx = 3;
        formPanel.add(nicknamePanel, gbc);
        
        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gbc.gridx = 2;
        gbc.gridy = 3;
        formPanel.add(new JLabel("비밀번호"), gbc);
        passTf = new JPasswordField(10);
        passPanel.add(passTf);
        JProgressBar strengthBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                String strengthText = getStrengthText(getValue());
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.BLACK); // 텍스트 색상
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(strengthText)) / 2;
                int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
                g2d.drawString(strengthText, x, y);
            }
        };
        gbc.gridx = 3;
        strengthBar.setPreferredSize(new Dimension(150, 20));
        passPanel.add(strengthBar);
        formPanel.add(passPanel, gbc);
        
        JPanel passRePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gbc.gridx = 2;
        gbc.gridy = 4;
        formPanel.add(new JLabel("비밀번호 확인"), gbc);
        gbc.gridx = 3;
        passReTf = new JPasswordField(10);
        passRePanel.add(passReTf);
        formPanel.add(passRePanel, gbc);

        // 생년월일 필드
        gbc.gridx = 2;
        gbc.gridy = 5;
        formPanel.add(new JLabel("생년월일"), gbc);
        JPanel birthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        yearComboBox = new JComboBox<>(new String[]{"1950", "1951", "1952", "1953", "1954", "1955", "1956", "1957", "1958", "1959",
                "1960", "1961", "1962", "1963", "1964", "1965", "1966", "1967", "1968", "1969",
                "1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977", "1978", "1979",
                "1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989",
                "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999",
                "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009",
                "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019",
                "2020", "2021", "2022", "2023", "2024"});
        birthPanel.add(yearComboBox);
        monthComboBox = new JComboBox<>(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});
        birthPanel.add(monthComboBox);
        dayComboBox = new JComboBox<>(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
        		"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
        		"21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"});
        birthPanel.add(dayComboBox);
        gbc.gridx = 3;
        formPanel.add(birthPanel, gbc);

        // 전화번호 필드
        gbc.gridx = 2;
        gbc.gridy = 6;
        formPanel.add(new JLabel("전화번호"), gbc);
        JPanel phoneNumPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        phoneFrontComboBox = new JComboBox<>(new String[]{"010", "020", "030", "040", "050", "060", "070", "080", "090"});
        phoneNumPanel.add(phoneFrontComboBox);
        phoneMiddleTf = new JTextField(4);
        phoneNumPanel.add(phoneMiddleTf);
        phoneBackTf = new JTextField(4);
        phoneNumPanel.add(phoneBackTf);
        gbc.gridx = 3;
        formPanel.add(phoneNumPanel, gbc);

        // 성별 선택 필드
        gbc.gridx = 2;
        gbc.gridy = 7;
        formPanel.add(new JLabel("성별"), gbc);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        maleButton = new JRadioButton("남");
        femaleButton = new JRadioButton("여");
        genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        gbc.gridx = 3;
        formPanel.add(genderPanel, gbc);

        // 이메일 필드
        gbc.gridx = 2;
        gbc.gridy = 8;
        formPanel.add(new JLabel("이메일"), gbc);
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailLocalTf = new JTextField(10);
        emailPanel.add(emailLocalTf);
        emailPanel.add(new JLabel("@"));
        emailDomainTf = new JTextField(10);
        emailPanel.add(emailDomainTf);
        emailPanel.add(new JComboBox<>(new String[]{"직접 입력", "gmail.com", "naver.com", "daum.net", "hotmail.com"}));
        gbc.gridx = 3;
        formPanel.add(emailPanel, gbc);

        // 우편번호와 주소 필드
        JPanel postalCodePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gbc.gridx = 2;
        gbc.gridy = 9;
        formPanel.add(new JLabel("우편번호"), gbc);
        postalCodeTf = new JTextField(10);
        postalCodePanel.add(postalCodeTf);
        postalCodeBtn = new JButton("검색");
        postalCodePanel.add(postalCodeBtn);
        gbc.gridx = 3;
        formPanel.add(postalCodePanel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 10;
        formPanel.add(new JLabel("주소"), gbc);
        gbc.gridx = 3;
        addressTf = new JTextField(20);
        formPanel.add(addressTf, gbc);

        gbc.gridx = 2;
        gbc.gridy = 11;
        formPanel.add(new JLabel("상세주소"), gbc);
        gbc.gridx = 3;
        detailedAddressTf = new JTextField(20);
        formPanel.add(detailedAddressTf, gbc);

        // 중앙에 formPanel 추가
        memberEditPanel.add(formPanel, BorderLayout.CENTER);

        // Save and Cancel buttons
        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("확인");
        cancelButton = new JButton("취소");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        memberEditPanel.add(buttonPanel, BorderLayout.SOUTH);

        // memberEditPanel을 메인 프레임에 추가
        add(memberEditPanel);

        setVisible(true);
        
		// 아이디 중복 체크 버튼 리스너
		idCheckButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkIdDuplication();
			}
		});
		// 닉네임 중복 체크 버튼 리스너
		nicknameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkNicknameDuplication();
			}
		});
		
        // 우편번호 검색 버튼 액션 리스너
		postalCodeBtn.addActionListener(e -> {
		    ApiExplorer postalCodeSearch = new ApiExplorer((postalCode, address) -> {
		        // 이곳에서 모든 필요한 처리를 수행
		        updateAddressFields(postalCode, address); // AdminScreenMain의 메서드 호출
		        System.out.println("우편번호: " + postalCode + ", 주소: " + address);
		    });
		    postalCodeSearch.setVisible(true);
		});
    }
    
    private String getStrengthText(int strength) {
        if (strength == 100) {
            return "Strong";
        } else if (strength >= 75) {
            return "Medium";
        } else if (strength >= 50) {
            return "Weak";
        } else {
            return "Very Weak";
        }
    }
    
    // 테이블 데이터 로드
    private void loadTableData() {
        // 기존 데이터 제거
        tableModel.setRowCount(0);

        try {
            // 데이터베이스에서 모든 회원 정보를 가져옴
            List<Map<String, Object>> members = MemberList.getAllMembers();
            for (Map<String, Object> member : members) {
                tableModel.addRow(new Object[]{
                        member.get("id"),
                        member.get("name"),
                        member.get("nickname"),
                        member.get("email"),
                        member.get("gender")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "테이블 데이터를 로드하는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
	// 아이디 중복 체크 메소드
    private void checkIdDuplication() {
        String inputId = idTf.getText();
        if (inputId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 입력하세요.", "아이디 입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = dbConnection.getConnection();
            String query = "SELECT id FROM user_info WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, inputId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.", "중복된 아이디", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "사용 가능한 아이디입니다.", "아이디 사용 가능", JOptionPane.INFORMATION_MESSAGE);
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "아이디 확인 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
	// 닉네임 중복 체크 메소드
    private void checkNicknameDuplication() {
        String inputNickname = nicknameTf.getText(); // nicknameTf는 닉네임을 입력받는 텍스트 필드
        if (inputNickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "닉네임을 입력하세요.", "닉네임 입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = dbConnection.getConnection();
            String query = "SELECT nickname FROM user_info WHERE nickname = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, inputNickname);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "이미 사용 중인 닉네임입니다.", "중복된 닉네임", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "사용 가능한 닉네임입니다.", "닉네임 사용 가능", JOptionPane.INFORMATION_MESSAGE);
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "닉네임 확인 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void updateAddressFields(String postalCode, String address) {
    	System.out.println("updateAddressFields 호출됨: " + postalCode + ", " + address);
        postalCodeTf.setText(postalCode);  // 우편번호 필드 업데이트
        addressTf.setText(address);       // 주소 필드 업데이트
    }

    public static void main(String[] args) {
        new AdminScreenMain();
    }
}