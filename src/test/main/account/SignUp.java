package test.main.account;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.sql.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.regex.*;

import test.api.ApiExplorer;
import test.member.db.DBConnection;
import test.member.dbLoad.ProfilePictureSelector;
import test.member.retouch.ImageCropper;

public class SignUp extends JFrame implements ActionListener {
    // 패널 컴포넌트
    JPanel mainPanel, formPanel, profilePanel, registerBtnPanel;
    JPanel idPanel, nicknamePanel, passPanel, passRePanel;
	JPanel namePanel, birthPanel, genderPanel, phoneNumPanel, emailPanel;
	JPanel postalCodePanel, addressPanel, detailAddressPanel;
    
	// 입력란 컴포넌트
    public JTextField emailLocalTf;
	JTextField emailDomainTf;
    JTextField nameTf;
	public JTextField idTf;
	JTextField nicknameTf;
	JTextField phoneMiddleTf;
	JTextField phoneBackTf;
	JTextField postalCodeTf;
	JTextField detailedAddressTf;
    JTextField addressTf;
    public JPasswordField passTf;
	JPasswordField passReTf;

    // 선택 박스 컴포넌트 - 클릭하면 값이 밑으로 펼쳐짐
    JComboBox<String> yearComboBox, monthComboBox, dayComboBox;
    JComboBox<String> phoneFrontComboBox;

    // 선택 컴포넌트 - 원하는 것 선택
    JRadioButton menButton, girlButton;

    // 버튼 컴포넌트
    public JButton registerButton;
	JButton idCheckButton;
	JButton nicknameButton;
	JButton uploadButton;
	JButton addressBtn;
	JButton postalCodeBtn;
	JButton defaultProfileButton;

    // 라벨 컴포넌트 - 글자 띄워줌
    JLabel titleLabel;
    JLabel imageLabel, idLabel, nicknameLabel, passLabel, passReLabel, passSecurityLabel;
    JLabel nameLabel, birthLabel, genderLabel, phoneNumLabel, emailLabel, emailAtLabel;
    JLabel postalCodeLabel, addressLabel, detailedAddressLabel;
    
    BufferedImage profileImage;
    ButtonGroup sexGroup;
    JProgressBar strengthBar;

    JComboBox<String> emailDomainComboBox; // 이메일 주소 선택 박스
    JTextArea addressSuggestionTa; // 주소 추천 표시할 텍스트 영역
    ProfilePictureSelector profilePictureSelector;
    Font font = new Font("회원가입", Font.BOLD, 40);
    
    String years = "", months = "", days = "", id = "", pass = "", passRe = "", name = "", sex = "", nickname = "";
    String phoneFront = "", phone = "", email = "", gender = "", postalCode = "", address = "", detailedAddress = "";
    
    DBConnection lp = new DBConnection();
    private static SignUp currentInstance;
    
    public SignUp() {
    	currentInstance = this;
    	
        setTitle("회원가입");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫기 버튼 활성화
        // setSize(1500, 1000); // 창 크기 설정
        getContentPane().setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 컴포넌트 사이의 간격 설정
        gbc.anchor = GridBagConstraints.NORTHWEST; // 컴포넌트를 왼쪽 상단에 배치
        
        
        // 입력 필드 패널
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        
        // 제목 레이블
        titleLabel = new JLabel("회원가입 화면");
        titleLabel.setFont(font);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // 전체 너비 차지
        gbc.anchor = GridBagConstraints.CENTER; // 중앙 배치
        gbc.weightx = 1.0; // 가로로 여유 공간을 채우기 위해 가중치 추가
        gbc.weighty = 0.1; // 세로로 약간의 공간을 차지하게 설정
        getContentPane().add(titleLabel, gbc);
        
        
        // 프로필 사진 패널
        profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.add(new JLabel("프로필 사진 설정:"));
        profilePanel.add(Box.createVerticalStrut(10)); // 간격 추가

        // 프로필 사진 보이는 부분 (사각형)
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(150, 150)); // 사각형 크기
        imageLabel.setMinimumSize(new Dimension(150, 150)); // 최소 크기 고정
        imageLabel.setMaximumSize(new Dimension(150, 150)); // 최대 크기 고정
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // 검정 테두리
        profilePanel.add(imageLabel);
        
        profilePanel.add(Box.createVerticalStrut(10)); // 간격 추가
        
        uploadButton = new JButton("프로필 선택 버튼");
        defaultProfileButton = new JButton("기본 프로필 선택");
        profilePanel.add(uploadButton);
        profilePanel.add(defaultProfileButton);
        
        // profilePanel 위치 설정
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // 왼쪽에 위치
        gbc.anchor = GridBagConstraints.NORTHEAST; // 오른쪽 정렬
        gbc.weightx = 0.5; // 좌우 공간을 동일하게 차지
        getContentPane().add(profilePanel, gbc);
        
        // profilePictureSelector 초기화, 기본 프로필 설정을 위한 변수
        profilePictureSelector = new ProfilePictureSelector();
        
        
        // 서브 패널 설정
        formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        
        nameTf = new JTextField(10);
        addField(formPanel, gbc, "이름:", nameTf, 0);
        

        idTf = new JTextField(10);
        idCheckButton = new JButton("중복 확인");
        idPanel = new JPanel();
        idPanel.add(idTf);
        idPanel.add(idCheckButton);
        addField(formPanel, gbc, "아이디:", idPanel, 1);
        

        nicknameTf = new JTextField(10);
        nicknameButton = new JButton("중복 확인");
        nicknamePanel = new JPanel();
        nicknamePanel.add(nicknameTf);
        nicknamePanel.add(nicknameButton);
        addField(formPanel, gbc, "닉네임:", nicknamePanel, 2);
        

        passTf = new JPasswordField(10);
        strengthBar = new JProgressBar(0, 100) {
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
        strengthBar.setPreferredSize(new Dimension(150, 20));
        passPanel = new JPanel();
        passPanel.add(passTf);
        passPanel.add(strengthBar);
        addField(formPanel, gbc, "패스워드:", passPanel, 3);

        
        passReTf = new JPasswordField(10);
        passRePanel = new JPanel();
        passRePanel.add(passReTf);
        addField(formPanel, gbc, "패스워드 확인:", passRePanel, 4);
        

        yearComboBox = new JComboBox<String>(
        		new String[] {"1950", "1951", "1952", "1953", "1954", "1955", "1956", "1957", "1958", "1959",
        				"1960", "1961", "1962", "1963", "1964", "1965", "1966", "1967", "1968", "1969",
        				"1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977", "1978", "1979",
        				"1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989",
        				"1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999",
        				"2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009",
        				"2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019",
        				"2020", "2021", "2022", "2023", "2024"});

        monthComboBox = new JComboBox<String>(
                new String[] {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});

        dayComboBox = new JComboBox<String>(new String[] {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
                "28", "29", "30", "31"});
        
        birthPanel = new JPanel();
        birthPanel.add(yearComboBox);
        birthPanel.add(monthComboBox);
        birthPanel.add(dayComboBox);
        addField(formPanel, gbc, "나이:", birthPanel, 5);
        

        phoneFrontComboBox = new JComboBox<String>(new String[] {"010", "020", "030", "040", "050", "060", "070", "080", "090"});
        phoneMiddleTf = new JTextField(10);
        phoneBackTf = new JTextField(10);
        phoneNumPanel = new JPanel();
        phoneNumPanel.add(phoneFrontComboBox);
        phoneNumPanel.add(phoneMiddleTf);
        phoneNumPanel.add(phoneBackTf);
        addField(formPanel, gbc, "전화번호:", phoneNumPanel, 6);
        

        menButton = new JRadioButton("남자");
        girlButton = new JRadioButton("여자");
        genderPanel = new JPanel();
        genderPanel.add(menButton);
        genderPanel.add(girlButton);
        addField(formPanel, gbc, "성별:", genderPanel, 7);
        

        emailLocalTf = new JTextField(10);
        emailAtLabel = new JLabel("@");
        emailDomainTf = new JTextField(10);
        emailDomainComboBox = new JComboBox<String>(new String[] {"직접 입력", "gmail.com", "naver.com", "daum.net", "hotmail.com"});
        emailPanel = new JPanel();
        emailPanel.add(emailLocalTf);
        emailPanel.add(emailAtLabel);
        emailPanel.add(emailDomainTf);
        emailPanel.add(emailDomainComboBox);
        addField(formPanel, gbc, "이메일:", emailPanel, 8);
        

        postalCodeTf = new JTextField(10);
        postalCodeTf.setEditable(false);
        postalCodeBtn = new JButton("우편번호 검색");
        postalCodePanel = new JPanel();
        postalCodePanel.add(postalCodeTf);
        postalCodePanel.add(postalCodeBtn);
        addField(formPanel, gbc, "우편번호:", postalCodePanel, 9);
        

        addressTf = new JTextField(30);
        addressTf.setEditable(false);
        addressPanel = new JPanel();
        addressPanel.add(addressTf);
        addField(formPanel, gbc, "주소:", addressPanel, 10);
        

        detailedAddressTf = new JTextField(30);
        detailAddressPanel = new JPanel();
        detailAddressPanel.add(detailedAddressTf);
        addField(formPanel, gbc, "상세 주소:", detailAddressPanel, 11);

        
        // formPanel 위치 설정
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // 오른쪽에 위치
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.WEST; // 왼쪽 정렬
        getContentPane().add(formPanel, gbc);
        

		registerButton = new JButton("회원가입");
		registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.gridwidth = 2; // 전체 너비 차지
	    gbc.weightx = 1.0;
	    gbc.weighty = 0.1; // 하단에 적절한 공간을 할당
	    gbc.anchor = GridBagConstraints.SOUTH; // 하단 중앙에 배치
	    getContentPane().add(registerButton, gbc);
        
        // JFrame에 메인 패널 추가
        add(mainPanel);

        pack();
        
        // 창을 화면에 표시
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
		
		passTf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String password = new String(passTf.getPassword());
                int strength = calculatePasswordStrength(password);
                strengthBar.setValue(strength);
            }
        });
		phoneFrontComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == phoneFrontComboBox) {
					JComboBox phoneBox = (JComboBox) e.getSource();
					phoneFront = (String) phoneBox.getSelectedItem();
					System.out.println(months);
				}
			}
		});

		// 날짜 선택 리스너
		monthComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == monthComboBox) {
					JComboBox monthBox = (JComboBox) e.getSource();
					months = (String) monthBox.getSelectedItem();
					System.out.println(months);
				}
			}
		});
		dayComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource() == dayComboBox) {
					JComboBox dayBox = (JComboBox) e.getSource();
					days = (String) dayBox.getSelectedItem();
					System.out.println(days);
				}
			}
		});
		
		// 성별 선택 리스너
		menButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sex = e.getActionCommand();
			}
		});
		girlButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sex = e.getActionCommand();
			}
		});
		
		// 회원가입 버튼 리스너
		registerButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        id = idTf.getText();
		        pass = new String(passTf.getPassword());
		        passRe = new String(passReTf.getPassword());
		        name = nameTf.getText();
		        nickname = nicknameTf.getText(); // 닉네임 추가
		        phone = phoneFrontComboBox.getSelectedItem() + phoneMiddleTf.getText() + phoneBackTf.getText();
		        email = emailLocalTf.getText() + "@" + emailDomainTf.getText();
		        postalCode = postalCodeTf.getText(); // 우편번호
		        address = addressTf.getText(); // 주소
		        detailedAddress = detailedAddressTf.getText(); // 상세 주소
		        gender = menButton.isSelected() ? "M" : "F";

		        // 비밀번호 유효성 검사
		        Pattern passPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$"); // 8자 영문+특수문자+숫자
		        Matcher passMatcher = passPattern.matcher(pass);

		        if (!passMatcher.find()) {
		            JOptionPane.showMessageDialog(null, "비밀번호는 영문+특수문자+숫자 8자로 구성되어야 합니다", "비밀번호 오류", JOptionPane.ERROR_MESSAGE);
		        } else if (!pass.equals(passRe)) {
		            JOptionPane.showMessageDialog(null, "비밀번호가 서로 맞지 않습니다", "비밀번호 오류", JOptionPane.ERROR_MESSAGE);
		        } else {
		            Connection conn = null;
		            PreparedStatement pstmt = null;

		            try {
		                conn = lp.getConnection(); // 데이터베이스 연결
		                String sql = "INSERT INTO user_info (id, password, name, nickname, email, birth_year, birth_month, birth_day, gender, phone_number, postal_code, address, detailed_address, profile_image) "
		                           + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // 쿼리 업데이트

		                pstmt = conn.prepareStatement(sql);
		                
		                // 생년월일 각 필드를 따로 저장
		                int birthYear = Integer.parseInt((String) yearComboBox.getSelectedItem());
		                int birthMonth = Integer.parseInt((String) monthComboBox.getSelectedItem());
		                int birthDay = Integer.parseInt((String) dayComboBox.getSelectedItem());

		                pstmt.setString(1, id);
		                pstmt.setString(2, pass);
		                pstmt.setString(3, name);
		                pstmt.setString(4, nickname); // 닉네임 추가
		                pstmt.setString(5, email);
		                pstmt.setInt(6, birthYear); // 생년 추가
		                pstmt.setInt(7, birthMonth); // 생월 추가
		                pstmt.setInt(8, birthDay); // 생일 추가
		                pstmt.setString(9, gender);
		                pstmt.setString(10, phone);
		                pstmt.setString(11, postalCode); // 우편번호 추가
		                pstmt.setString(12, address); // 주소 추가
		                pstmt.setString(13, detailedAddress); // 상세 주소 추가

		                // 프로필 이미지 저장 (이미지가 있으면 저장, 없으면 NULL)
		                if (profileImage != null) {
		                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		                    ImageIO.write(profileImage, "png", baos);
		                    pstmt.setBytes(14, baos.toByteArray());
		                } else {
		                    pstmt.setNull(14, java.sql.Types.BLOB);
		                }

		                // 쿼리 실행
		                int result = pstmt.executeUpdate();
		                if (result > 0) {
		                    JOptionPane.showMessageDialog(null, "회원 가입 완료!", "회원가입", JOptionPane.INFORMATION_MESSAGE);
		                }

		            } catch (SQLException | IOException ex) {
		                ex.printStackTrace();
		                JOptionPane.showMessageDialog(null, "회원 가입 중 오류가 발생했습니다. 다시 시도해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
		            } finally {
		                // 리소스 해제
		                if (pstmt != null) try { pstmt.close(); } catch (SQLException e1) { e1.printStackTrace(); }
		                if (conn != null) try { conn.close(); } catch (SQLException e1) { e1.printStackTrace(); }
		            }
		        }
		    }
		});
		
        // 이미지 선택 버튼 리스너
		uploadButton.addActionListener(new ActionListener() {
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
        
        // 우편번호 검색 버튼 액션 리스너
        postalCodeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApiExplorer postalCodeSearch = new ApiExplorer((postalCode, address) -> {
                    // SignUp 클래스의 메서드를 호출하여 우편번호와 주소를 업데이트합니다.
                    updateAddressFields(postalCode, address);
                });
                postalCodeSearch.setVisible(true);
            }
        });
        
        // 이메일 도메인 콤보박스의 ActionListener
        emailDomainComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 선택된 도메인을 이메일 도메인 텍스트 필드에 설정
                String selectedDomain = (String) emailDomainComboBox.getSelectedItem();
                emailDomainTf.setText(selectedDomain);
            }
        });
        
        // 기본 이미지 설정
        defaultProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	chooseDefaultProfile();
            }
        });
        
        setVisible(true);
    }
    
    // 현재 SignUp 인스턴스를 반환하는 메서드
    public static SignUp getCurrentInstance() {
        return currentInstance;
    }
    
    // 필드 추가 메서드
    private void addField(JPanel panel, GridBagConstraints gbc, String labelText, Component field, int row) {
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.gridx = 0;
        gbcLabel.gridy = row;
        gbcLabel.anchor = GridBagConstraints.WEST;
        gbcLabel.insets = new Insets(10, 10, 10, 10);
        panel.add(new JLabel(labelText), gbcLabel);

        GridBagConstraints gbcField = new GridBagConstraints();
        gbcField.gridx = 1;
        gbcField.gridy = row;
        gbcField.anchor = GridBagConstraints.WEST;
        gbcField.insets = new Insets(10, 10, 10, 10);
        panel.add(field, gbcField);
    }
    
	// 아이디 중복 체크 메소드
    private void checkIdDuplication() {
        String inputId = idTf.getText();
        if (inputId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 입력하세요.", "아이디 입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = lp.getConnection();
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
            Connection conn = lp.getConnection();
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

    
    private int calculatePasswordStrength(String password) {
        int strength = 0;

        // 비밀번호 길이 확인 (8자 이상)
        if (password.length() >= 8) {
            strength += 25;
        }

        // 대문자 포함 여부
        if (Pattern.compile("[A-Z]").matcher(password).find()) {
            strength += 25;
        }

        // 숫자 포함 여부
        if (Pattern.compile("[0-9]").matcher(password).find()) {
            strength += 25;
        }

        // 특수문자 포함 여부
        if (Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find()) {
            strength += 25;
        }

        return strength;
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
    
    // 우편번호, 주소 필드를 업데이트하는 메서드
    public void updateAddressFields(String postalCode, String address) {
    	postalCodeTf.setText(postalCode);
    	addressTf.setText(address);
    }
    
    // ActionListener의 추상 메서드 구현
    @Override
    public void actionPerformed(ActionEvent e) {
        // 버튼 클릭 시 동작할 코드
        if (e.getSource() == registerButton) {
            // 가입하기 버튼이 클릭된 경우 실행될 코드
            System.out.println("가입하기 버튼 클릭됨");
        } else if (e.getSource() == idCheckButton) {
            // 중복 체크 버튼이 클릭된 경우 실행될 코드
            System.out.println("아이디 중복 체크 버튼 클릭됨");
        }
    }
    
	// 기본 프로필 선택 기능
    private void chooseDefaultProfile() {
        String[] options = {"기본 프로필 1", "기본 프로필 2"}; // 기본 프로필 목록
        int choice = JOptionPane.showOptionDialog(
                this,
                "기본 프로필을 선택하세요",
                "기본 프로필 선택",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice != -1) {
            // '프로필 사진 미선택' 문구 제거
            imageLabel.setText(""); // 텍스트 제거

            // 선택한 기본 프로필 ID에 따라 이미지 로드
            int profileId = choice + 1; // ID는 1부터 시작한다고 가정
            loadProfileImage(profileId); // 해당 프로필 이미지를 로드하는 메서드 호출
        }
    }

    // 선택한 기본 프로필을 로드하여 imageLabel에 표시
    private void loadProfileImage(int profileId) {
        try {
            ImageIcon profileImage = profilePictureSelector.loadImageFromDatabase(profileId);
            if (profileImage != null) {
                imageLabel.setIcon(profileImage); // imageLabel에 이미지 표시
                System.out.println("기본 프로필 이미지 로드 완료: ID " + profileId);
            } else {
                JOptionPane.showMessageDialog(this, "이미지를 불러올 수 없습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(SignUp.this, "데이터베이스 오류가 발생했습니다.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(SignUp.this, "이미지를 불러오는 중 오류가 발생했습니다.");
        }
    }

    public static void main(String[] args) {
        new SignUp(); // Launch the SignUp GUI
    }
}