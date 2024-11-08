package omok.member;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import omok.member.DBConnection;

public class SignUp extends JFrame implements ActionListener {
    // 패널 컴포넌트
    JPanel mainPanel, subPanel;
    
    // 필드 컴포넌트 - 글자 작성
    JTextField emailLocalTf, emailDomainTf, addressTf;
    
	// 입력란 컴포넌트
    JTextField idTf, nameTf, phoneTf, postalCodeTf, detailedAddressTf;
    JPasswordField passTf, passReTf;

    // 선택 박스 컴포넌트 - 클릭하면 값이 밑으로 펼쳐짐
    JComboBox<String> yearComboBox, monthComboBox, dayComboBox;

    // 선택 컴포넌트 - 원하는 것 선택
    JRadioButton menButton, girlButton;

    // 버튼 컴포넌트
    JButton registerButton, idCheckButton, uploadButton, addressBtn, postalCodeBtn, defaultProfileButton;

    // 라벨 컴포넌트 - 글자 띄워줌
    JLabel imageLabel, idLabel, passLabel, passReLabel;
    JLabel nameLabel, birthLabel, sexLabel, phoneLabel, emailLabel, atLabel;
    JLabel postalCodeLabel, addressLabel, detailedAddressLabel;
    
    BufferedImage profileImage;
    ButtonGroup sexGroup;

    JComboBox<String> emailDomainCb; // 이메일 주소 선택 박스

    String[] emailDomains = {"직접 입력", "gmail.com", "naver.com", "daum.net", "hotmail.com"};
    JTextArea addressSuggestionTa; // 주소 추천 표시할 텍스트 영역
    
    ProfilePictureSelector profilePictureSelector;

    
    Font font = new Font("회원가입", Font.BOLD, 40);

    
    String year = "", month = "", day = "";
    String id = "", pass = "", passRe = "", name = "", sex = "", phone = "";
    
    
    DBConnection lp = new DBConnection();

    
    public SignUp() {
        setTitle("회원가입");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫기 버튼 활성화
        setSize(1500, 1000); // 창 크기 설정
        
        // 메인 패널 설정
        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        // 제목 레이블
        JLabel signupLabel = new JLabel("회원가입 화면");
        signupLabel.setFont(font);
        signupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 서브 패널 설정
        subPanel = new JPanel();
        subPanel.setLayout(new GridBagLayout());
        subPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // 프로필 패널
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));

        imageLabel = new JLabel("프로필 사진 미선택");
        imageLabel.setBorder(BorderFactory.createEtchedBorder());
        imageLabel.setPreferredSize(new Dimension(150, 150)); // 이미지 표시 영역 크기
        profilePanel.add(imageLabel);

        uploadButton = new JButton("프로필 사진 선택");
        profilePanel.add(uploadButton);
        
        // 기본 프로필 선택 버튼
        defaultProfileButton = new JButton("기본 프로필 선택");
        profilePanel.add(defaultProfileButton);

        // ProfilePictureSelector 초기화
        profilePictureSelector = new ProfilePictureSelector();

        // 이메일 입력란 설정
        emailLocalTf = new JTextField(10);
        emailDomainCb = new JComboBox<>(emailDomains);

        // 우편번호 입력란 설정
        postalCodeTf = new JTextField(10);
        postalCodeBtn = new JButton("우편번호 검색");

        // 상세 주소 입력란 설정
        detailedAddressTf = new JTextField(15);

        // 정보 입력
        idLabel = new JLabel("아이디 : ");
        passLabel = new JLabel("비밀번호 : ");
        passReLabel = new JLabel("비밀번호 재확인 : ");
        nameLabel = new JLabel("이름 : ");
        birthLabel = new JLabel("생년월일 : ");
        sexLabel = new JLabel("성별 : ");
        phoneLabel = new JLabel("핸드폰번호 : ");
        atLabel = new JLabel("@");

        
        idTf = new JTextField(10);
        passTf = new JPasswordField(10);
        passReTf = new JPasswordField(10);
        nameTf = new JTextField(10);
        // yearTf = new JTextField(4);
        phoneTf = new JTextField(10);
        emailDomainTf = new JTextField(10);

        // 날짜 선택
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

        // 성별 선택
        menButton = new JRadioButton("남자");
        girlButton = new JRadioButton("여자");
        ButtonGroup sexGroup = new ButtonGroup();
        sexGroup.add(menButton);
        sexGroup.add(girlButton);

        // 아이디 중복 체크 버튼
        idCheckButton = new JButton("중복 체크");
        
        addressTf = new JTextField();

        // Add components to layout
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(15, 5, 0, 0);
        
        
        // 프로필 사진
        c.gridx = 1;
        c.gridy = 0;
        subPanel.add(imageLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        subPanel.add(uploadButton, c);
        c.gridx = 2;
        c.gridy = 1;
        subPanel.add(defaultProfileButton, c);

        // 아이디
		c.gridx = 0;
		c.gridy = 2;
		subPanel.add(idLabel, c);
		c.gridx = 1;
		c.gridy = 2;
		subPanel.add(idTf, c); // 아이디
		c.gridx = 2;
		c.gridy = 2;
		subPanel.add(idCheckButton, c); // 아디디 중복 체크

		// 비밀번호
		c.gridx = 0;
		c.gridy = 3;
		subPanel.add(passLabel, c);
		c.gridx = 1;
		c.gridy = 3;
		subPanel.add(passTf, c); // pass
		c.gridx = 2;
		c.gridy = 3; 
		subPanel.add(new JLabel("특수문자 + 8자"), c); // 보안설정

		// 비밀번호 재확인
		c.gridx = 0;
		c.gridy = 4;
		subPanel.add(passReLabel, c);
		c.gridx = 1;
		c.gridy = 4;
		subPanel.add(passReTf, c); // password 재확인

		// 이름
		c.gridx = 0;
		c.gridy = 5;
		subPanel.add(nameLabel, c);
		c.gridx = 1;
		c.gridy = 5;
		subPanel.add(nameTf, c); // 이름

		// 생일
		c.gridx = 0;
		c.gridy = 6;
		subPanel.add(birthLabel, c);
		c.gridx = 1;
		c.gridy = 6;
		c.weightx = 0.6;
		subPanel.add(yearComboBox, c); // 년
		c.gridx = 2;
		c.gridy = 6;
		c.weightx = 0.2;
		subPanel.add(monthComboBox, c); // 월
		c.gridx = 3;
		c.gridy = 6;
		c.weightx = 0.2;
		subPanel.add(dayComboBox, c); // 일

		// 성별
		c.gridx = 0;
		c.gridy = 7;
		subPanel.add(sexLabel, c);
		c.gridx = 1;
		c.gridy = 7;
		subPanel.add(menButton, c); // 남자
		c.gridx = 2;
		c.gridy = 7;
		subPanel.add(girlButton, c); // 여자

		// 전화번호
		c.gridx = 0;
		c.gridy = 8;
		subPanel.add(phoneLabel, c);
		c.gridx = 1;
		c.gridy = 8;
		subPanel.add(phoneTf, c);
		
		// 이메일
        c.gridx = 0;
        c.gridy = 9;
        subPanel.add(new JLabel("이메일:"), c);
        c.gridx = 1;
        c.gridy = 9;
        subPanel.add(emailLocalTf, c); // @ 앞 입력란
        c.gridx = 2;
        c.gridy = 9;
        subPanel.add(atLabel,c);
        c.gridx = 3;
        c.gridy = 9;
        subPanel.add(emailDomainTf, c); // @ 뒤 직접 입력란
        c.gridx = 4;
        c.gridy = 9;
        subPanel.add(emailDomainCb, c); // @ 뒤 도메인 선택 콤보박스

        // 우편번호
        c.gridx = 0;
        c.gridy = 10;
        subPanel.add(new JLabel("우편번호:"), c);
        c.gridx = 1;
        c.gridy = 10;
        postalCodeTf.setEditable(false); // 우편번호는 수정 불가
        subPanel.add(postalCodeTf, c);  // 우편번호 입력란
        c.gridx = 2;
        c.gridy = 10;
        subPanel.add(postalCodeBtn, c);  // 우편번호 검색 버튼

        // 주소
        c.gridx = 0;
        c.gridy = 11;
        subPanel.add(new JLabel("주소:"), c);
        c.gridx = 1;
        c.gridy = 11;
        addressTf.setEditable(false); // 검색된 주소로 자동 설정
        subPanel.add(addressTf, c);
        
        // 상세 주소
        c.gridx = 0;
        c.gridy = 12;
        subPanel.add(new JLabel("상세주소:"), c);
        c.gridx = 1;
        c.gridy = 12;
        subPanel.add(detailedAddressTf, c);

		registerButton = new JButton("회원가입");
		registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 메인 패널에 추가
        mainPanel.add(signupLabel);  // 제목 추가
        mainPanel.add(profilePanel); // 프로필 패널 추가
        mainPanel.add(subPanel);     // 서브 패널 추가
        mainPanel.add(registerButton); // 회원가입 버튼 추가
        
        // JFrame에 메인 패널 추가
        add(mainPanel);

        // 창을 화면에 표시
        setVisible(true);
        
		// 아이디 중복 체크 버튼 리스너
		idCheckButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkIdDuplication();
			}
		});

		// 날짜 선택 리스너
		monthComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == monthComboBox) {
					JComboBox monthBox = (JComboBox) e.getSource();
					month = (String) monthBox.getSelectedItem();
					System.out.println(month);
				}
			}
		});
		dayComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource() == dayComboBox) {
					JComboBox dayBox = (JComboBox) e.getSource();
					day = (String) dayBox.getSelectedItem();
					System.out.println(month);
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
	            phone = phoneTf.getText();
	            String email = emailLocalTf.getText() + "@" + emailDomainTf.getText(); // 이메일 통합
	            String address = addressTf.getText() + " " + detailedAddressTf.getText(); // 주소 통합
	            String gender = menButton.isSelected() ? "M" : "F"; // 성별 설정

	            String sql = "INSERT INTO user_info (id, password, name, email, birthdate, gender, phone_number, address, profile_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	            Pattern passPattern1 = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$"); //8자 영문+특문+숫자
	            Matcher passMatcher = passPattern1.matcher(pass);

	            if (!passMatcher.find()) {
	                JOptionPane.showMessageDialog(null, "비밀번호는 영문+특수문자+숫자 8자로 구성되어야 합니다", "비밀번호 오류", 1);
	            } else if (!pass.equals(passRe)) {
	                JOptionPane.showMessageDialog(null, "비밀번호가 서로 맞지 않습니다", "비밀번호 오류", 1);
	            } else {
	                try {
	                    Connection conn = lp.getConnection(); // lp 초기화 후 호출

	                    PreparedStatement pstmt = conn.prepareStatement(sql);

	                    String date = year + "-" + month + "-" + day;

	                    pstmt.setString(1, idTf.getText());
	                    pstmt.setString(2, pass);
	                    pstmt.setString(3, nameTf.getText());
	                    pstmt.setString(4, email);
	                    pstmt.setString(5, date);
	                    pstmt.setString(6, gender);
	                    pstmt.setString(7, phoneTf.getText());
	                    pstmt.setString(8, address);
	                    
	                    // 프로필 이미지를 BLOB으로 저장
	                    // 프로필 이미지가 선택되지 않았을 경우 처리
	                    if (profileImage != null) {
	                        try {
	                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                            boolean result = ImageIO.write(profileImage, "png", baos);
	                            byte[] profileImageByteArray = baos.toByteArray();
	                            if (!result) {
	                                throw new IOException("이미지 저장에 실패했습니다. 지원되지 않는 형식입니다.");
	                            }
	                            pstmt.setBytes(9, profileImageByteArray); // 프로필 이미지를 바이트 배열로 저장
	                        } catch (IOException ex) {
	                            ex.printStackTrace();
	                            JOptionPane.showMessageDialog(null, "이미지를 저장하는 도중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
	                        }
	                    } else {
	                        // 프로필 이미지가 선택되지 않은 경우 NULL 처리
	                        pstmt.setNull(9, java.sql.Types.BLOB); // 프로필 이미지가 없을 때는 NULL 설정
	                    }

	                    int r = pstmt.executeUpdate();
	                    System.out.println("변경된 row " + r);
	                    JOptionPane.showMessageDialog(null, "회원 가입 완료!", "회원가입", 1);
	                    // 로그인 화면으로 이동하는 코드는 필요에 따라 수정 가능
	                } catch (SQLException e1) {
	                    System.out.println("SQL error" + e1.getMessage());
	                    if (e1.getMessage().contains("PRIMARY")) {
	                        JOptionPane.showMessageDialog(null, "아이디 중복!", "아이디 중복 오류", 1);
	                    } else
	                        JOptionPane.showMessageDialog(null, "정보를 제대로 입력해주세요!", "오류", 1);
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
                        profileImage = ImageIO.read(selectedFile);
                        ImageIcon icon = new ImageIcon(profileImage.getScaledInstance(150, 150, BufferedImage.SCALE_SMOOTH));
                        imageLabel.setIcon(icon);
                        imageLabel.setText(""); // 텍스트 제거
                    } catch (Exception ex) {
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
                ApiExplorer postalCodeSearch = new ApiExplorer(SignUp.this);
                postalCodeSearch.setVisible(true);
            }
        });
        
        // 이메일 도메인 콤보박스의 ActionListener
        emailDomainCb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 선택된 도메인을 이메일 도메인 텍스트 필드에 설정
                String selectedDomain = (String) emailDomainCb.getSelectedItem();
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
    
    // 우편번호, 주소 필드를 업데이트하는 메서드
    public void updateAddressFields(String postalCode, String address) {
    	postalCodeTf.setText(postalCode);
    	addressTf.setText(address);
    }
    
    // Launching ApiExplorer from SignUp
    private void openApiExplorer() {
        ApiExplorer apiExplorer = new ApiExplorer(this); // Pass this instance
        apiExplorer.setVisible(true);
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
