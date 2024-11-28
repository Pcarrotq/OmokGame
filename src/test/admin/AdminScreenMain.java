package test.admin;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;

import test.admin.management.*;
import test.api.ApiExplorer;
import test.member.db.DBConnection;
import test.member.dbLoad.MemberList;
import test.member.retouch.ImageCropper;

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
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }

            private void filterTable() {
                String searchText = searchField.getText();
                if (searchText.trim().isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                }
            }
        });

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel titleAndSearchPanel = new JPanel(new BorderLayout());
        titleAndSearchPanel.add(tableTitle, BorderLayout.NORTH);
        titleAndSearchPanel.add(searchPanel, BorderLayout.SOUTH);

        memberListPanel.add(titleAndSearchPanel, BorderLayout.NORTH);

        String[] columnNames = {"아이디", "이름", "닉네임", "이메일", "성별"};
        tableModel = new DefaultTableModel(columnNames, 0);
        memberTable = new JTable(tableModel);
        
        // 테이블 선택 이벤트 리스너
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

        JButton openButton = new JButton("열람"); // 회원 정보 열람 버튼
        JButton editButton = new JButton("수정"); // 회원 정보 수정 버튼
        JButton blockButton = new JButton("차단");
        JButton deleteButton = new JButton("삭제"); // 회원 차단 버튼
        
        openButton.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "열람할 데이터를 선택하세요.");
                return;
            }

            Object id = memberTable.getValueAt(selectedRow, 0);
            if (id == null) {
                JOptionPane.showMessageDialog(this, "선택된 데이터의 ID를 가져올 수 없습니다.");
                return;
            }

            Map<String, Object> memberInfo = MemberView.getMemberDetails(id.toString());
            if (memberInfo == null || memberInfo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "회원 정보를 가져올 수 없습니다.");
                return;
            }

            idTf.setText(memberInfo.getOrDefault("id", "") != null ? memberInfo.get("id").toString() : "");
            nameTf.setText(memberInfo.getOrDefault("name", "") != null ? memberInfo.get("name").toString() : "");
            nicknameTf.setText(memberInfo.getOrDefault("nickname", "") != null ? memberInfo.get("nickname").toString() : "");
            passTf.setText(memberInfo.getOrDefault("password", "") != null ? memberInfo.get("password").toString() : "");
            passReTf.setText(memberInfo.getOrDefault("password", "") != null ? memberInfo.get("password").toString() : "");
            
            // 생년월일 처리
            yearComboBox.setSelectedItem(memberInfo.getOrDefault("birth_year", "").toString());
            monthComboBox.setSelectedItem(memberInfo.getOrDefault("birth_month", "").toString());
            dayComboBox.setSelectedItem(memberInfo.getOrDefault("birth_day", "").toString());

            // 성별 처리
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

            // 전화번호 처리
            String phoneNumber = memberInfo.getOrDefault("phone_number", "").toString();
            if (phoneNumber.length() == 11) {
                phoneFrontComboBox.setSelectedItem(phoneNumber.substring(0, 3));
                phoneMiddleTf.setText(phoneNumber.substring(3, 7));
                phoneBackTf.setText(phoneNumber.substring(7));
            }

            // 주소 처리
            postalCodeTf.setText(memberInfo.getOrDefault("postal_code", "") != null ? memberInfo.get("postal_code").toString() : "");
            addressTf.setText(memberInfo.getOrDefault("address", "") != null ? memberInfo.get("address").toString() : "");
            detailedAddressTf.setText(memberInfo.getOrDefault("detailed_address", "") != null ? memberInfo.get("detailed_address").toString() : "");

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
            clearRightPanel();
            JOptionPane.showMessageDialog(this, "수정이 완료되었습니다.");
        });
        
        blockButton.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "차단할 유저를 선택하세요.");
                return;
            }

            String memberId = memberTable.getValueAt(selectedRow, 0).toString();
            String reason = JOptionPane.showInputDialog(this, "차단 사유를 입력하세요:", "차단 사유", JOptionPane.PLAIN_MESSAGE);

            if (reason != null && !reason.trim().isEmpty()) {
                boolean success = MemberBlock.blockMember(memberId, reason);
                if (success) {
                    JOptionPane.showMessageDialog(this, "유저가 성공적으로 차단되었습니다.");
                    tableModel.removeRow(selectedRow); // 테이블에서 제거
                } else {
                    JOptionPane.showMessageDialog(this, "차단 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "차단 사유를 입력해야 합니다.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "삭제할 유저를 선택하세요.");
                return;
            }

            String memberId = memberTable.getValueAt(selectedRow, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this, "해당 유저를 삭제하시겠습니까?", "유저 삭제", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = MemberDelete.softDeleteMember(memberId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "유저가 성공적으로 삭제되었습니다.");
                    tableModel.removeRow(selectedRow); // 테이블에서 제거
                } else {
                    JOptionPane.showMessageDialog(this, "삭제 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        memberButtonPanel.add(openButton);
        memberButtonPanel.add(editButton);
        memberButtonPanel.add(blockButton);
        memberButtonPanel.add(deleteButton);
        
        JButton blockedUsersButton = new JButton("차단 유저");
        JButton deletedUsersButton = new JButton("삭제 유저");
        
        blockedUsersButton.addActionListener(e -> {
            JFrame blockedFrame = new JFrame("차단 유저 목록");
            blockedFrame.setSize(800, 600);
            blockedFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            DefaultTableModel blockedTableModel = new DefaultTableModel(
                new String[]{"아이디", "이름", "닉네임", "차단 사유", "차단 날짜"}, 0);
            JTable blockedTable = new JTable(blockedTableModel);

            // 차단 유저 데이터 로드
            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT id, name, nickname, reason, blocked_date FROM user_info WHERE status = 'BLOCKED'";
                try (PreparedStatement pstmt = conn.prepareStatement(query);
                     ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        blockedTableModel.addRow(new Object[]{
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("nickname"),
                            rs.getString("reason"),
                            rs.getTimestamp("blocked_date")
                        });
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "차단 유저 목록을 불러오는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }

            JPanel buttonPanel = new JPanel();

            JButton unblockButton = new JButton("차단 해제");
            unblockButton.addActionListener(ev -> {
                int selectedRow = blockedTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(blockedFrame, "차단 해제할 유저를 선택하세요.");
                    return;
                }

                String memberId = blockedTable.getValueAt(selectedRow, 0).toString();

                boolean success = MemberBlock.unblockMember(memberId);
                if (success) {
                    JOptionPane.showMessageDialog(blockedFrame, "차단이 해제되었습니다.");
                    ((DefaultTableModel) blockedTable.getModel()).removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(blockedFrame, "차단 해제 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(unblockButton);

            // 닫기 버튼 추가
            JButton closeButton = new JButton("닫기");
            closeButton.addActionListener(ev -> blockedFrame.dispose());
            buttonPanel.add(closeButton);

            blockedFrame.add(new JScrollPane(blockedTable), BorderLayout.CENTER);
            blockedFrame.add(buttonPanel, BorderLayout.SOUTH);
            blockedFrame.setVisible(true);
        });

        deletedUsersButton.addActionListener(e -> {
            JFrame deletedFrame = new JFrame("삭제 유저 목록");
            deletedFrame.setSize(800, 600);
            deletedFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            DefaultTableModel deletedTableModel = new DefaultTableModel(
                new String[]{"아이디", "이름", "닉네임", "삭제 날짜"}, 0);
            JTable deletedTable = new JTable(deletedTableModel);

            // 삭제 유저 데이터 로드
            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT id, name, nickname, deleted_date FROM user_info WHERE status = 'DELETED'";
                try (PreparedStatement pstmt = conn.prepareStatement(query);
                     ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        deletedTableModel.addRow(new Object[]{
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("nickname"),
                            rs.getTimestamp("deleted_date")
                        });
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "삭제 유저 목록을 불러오는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }

            JPanel buttonPanel = new JPanel();

            JButton restoreButton = new JButton("복구");
            restoreButton.addActionListener(ev -> {
                int selectedRow = deletedTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(deletedFrame, "복구할 유저를 선택하세요.");
                    return;
                }

                String memberId = deletedTable.getValueAt(selectedRow, 0).toString();

                boolean success = MemberDelete.restoreMember(memberId);
                if (success) {
                    JOptionPane.showMessageDialog(deletedFrame, "유저가 복구되었습니다.");
                    ((DefaultTableModel) deletedTable.getModel()).removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(deletedFrame, "복구 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(restoreButton);

            // 닫기 버튼 추가
            JButton closeButton = new JButton("닫기");
            closeButton.addActionListener(ev -> deletedFrame.dispose());
            buttonPanel.add(closeButton);

            deletedFrame.add(new JScrollPane(deletedTable), BorderLayout.CENTER);
            deletedFrame.add(buttonPanel, BorderLayout.SOUTH);
            deletedFrame.setVisible(true);
        });

        // 버튼 패널에 추가
        memberButtonPanel.add(blockedUsersButton);
        memberButtonPanel.add(deletedUsersButton);

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
                    // 이미지를 150x150 크기로 스케일링
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
        changeProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
		        JFileChooser fileChooser = new JFileChooser();
		        fileChooser.setFileFilter(new FileNameExtensionFilter("이미지 파일", "jpg", "jpeg", "png"));
		        int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		            File selectedFile = fileChooser.getSelectedFile();
		            try {
		                BufferedImage selectedImage = ImageIO.read(selectedFile);

		                // 이미지 크롭을 위해 ImageCropper 창 열기
		                ImageCropper cropper = new ImageCropper(selectedImage);
		                int result = JOptionPane.showConfirmDialog(null, cropper, "이미지 크롭", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);

		                if (result == JOptionPane.OK_OPTION) {
		                    // 사용자가 OK 버튼을 누르면 크롭된 이미지를 가져와서 프로필에 반영
		                    BufferedImage croppedImage = cropper.getCroppedImage();  // 크롭된 이미지 가져오기
		                    if (croppedImage != null) {
		                        profileImage = croppedImage;  // SignUp의 profileImage에 저장
		                        
		                        // 크롭된 이미지를 이미지 레이블에 아이콘으로 설정
		                        ImageIcon croppedIcon = new ImageIcon(croppedImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
		                        imageLabel.setIcon(croppedIcon);
		                        imageLabel.setPreferredSize(new Dimension(150, 150));
		                    }
		                }
		            } catch (IOException ex) {
		                ex.printStackTrace();
		                JOptionPane.showMessageDialog(null, "이미지 로드 실패", "오류", JOptionPane.ERROR_MESSAGE);
		            }
		        }
            }
        });
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
        saveButton.addActionListener(e -> {
            // 텍스트 필드 초기화
            clearRightPanel();
            JOptionPane.showMessageDialog(this, "저장이 완료되었습니다.");
        });
        cancelButton = new JButton("취소");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 프로그램 종료
                // System.exit(0);
            	dispose();
            }
        });
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
    
    private void clearRightPanel() {
        // 모든 입력 필드 내용 초기화
        idTf.setText("");
        nameTf.setText("");
        nicknameTf.setText("");
        passTf.setText("");
        passReTf.setText("");
        emailLocalTf.setText("");
        emailDomainTf.setText("");
        phoneMiddleTf.setText("");
        phoneBackTf.setText("");
        postalCodeTf.setText("");
        addressTf.setText("");
        detailedAddressTf.setText("");
        
        // 콤보박스 초기화
        yearComboBox.setSelectedIndex(0);
        monthComboBox.setSelectedIndex(0);
        dayComboBox.setSelectedIndex(0);
        phoneFrontComboBox.setSelectedIndex(0);
        
        // 성별 선택 초기화
        genderGroup.clearSelection();

        // 프로필 이미지 초기화
        profileImage = null;
        imageLabel.setIcon(null);
    }

    private void loadUserData(DefaultTableModel tableModel, String tableName) {
        tableModel.setRowCount(0); // 기존 데이터 초기화

        String query = "SELECT id, name, nickname, email, "
                     + (tableName.equals("blocked_users") ? "blocked_date AS blocked_date" : "deleted_date AS deleted_date")
                     + " FROM user_info WHERE status = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // 상태에 따라 조건 설정
            String status = tableName.equals("blocked_users") ? "BLOCKED" : "DELETED";
            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String nickname = rs.getString("nickname");
                    String email = rs.getString("email");
                    String date = rs.getTimestamp(status.equals("BLOCKED") ? "blocked_date" : "deleted_date").toString();
                    
                    tableModel.addRow(new Object[]{id, name, nickname, email, date});
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터를 불러오는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public class MemberBlock {
        public static boolean blockMember(String memberId, String reason) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "UPDATE user_info SET status = 'BLOCKED', reason = ?, blocked_date = SYSDATE WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, reason);
                    pstmt.setString(2, memberId);
                    return pstmt.executeUpdate() > 0;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        public static boolean unblockMember(String memberId) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "UPDATE user_info SET status = 'ACTIVE', blocked_date = NULL, reason = NULL WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, memberId);
                    return pstmt.executeUpdate() > 0;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public class MemberDelete {
        public static boolean softDeleteMember(String memberId) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "UPDATE user_info SET status = 'DELETED', deleted_date = SYSDATE WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, memberId);
                    return pstmt.executeUpdate() > 0;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        public static boolean restoreMember(String memberId) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "UPDATE user_info SET status = 'ACTIVE', deleted_date = NULL WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, memberId);
                    return pstmt.executeUpdate() > 0;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public static void main(String[] args) {
        new AdminScreenMain();
    }
}