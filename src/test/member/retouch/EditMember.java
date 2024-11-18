package test.member.retouch;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import test.api.ApiExplorer;
import test.member.db.DBConnection;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class EditMember extends JFrame {
    // UI Components
    private JPanel mainPanel, formPanel, profilePanel, buttonPanel;
    private JTextField nameTf, idTf, nicknameTf, emailLocalTf, emailDomainTf, phoneMiddleTf, phoneBackTf, postalCodeTf, detailedAddressTf, addressTf;
    private JComboBox<String> yearComboBox, monthComboBox, dayComboBox, phoneFrontComboBox, emailDomainComboBox;
    private JRadioButton menButton, girlButton;
    private JLabel imageLabel;
    private JButton postalCodeBtn, saveButton, cancelButton, changeProfileButton, idCheckButton, nicknameButton;
    private BufferedImage profileImage;
    private ButtonGroup sexGroup;

    private String userId;
    private DBConnection dbConnection = new DBConnection();

    public EditMember(String userId) {
        this.userId = userId;

        setTitle("Edit Profile");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Title label
        JLabel titleLabel = new JLabel("Edit Profile");
        titleLabel.setFont(new Font("Edit Profile", Font.BOLD, 40));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // Profile panel (image and edit button)
        profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.add(new JLabel("Profile Picture:"));
        profilePanel.add(Box.createVerticalStrut(10));

        imageLabel = new JLabel() {
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
        profilePanel.add(imageLabel);

        profilePanel.add(Box.createVerticalStrut(10));

        changeProfileButton = new JButton("Change Profile Picture");
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
        profilePanel.add(changeProfileButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(profilePanel, gbc);

        // Form panel
        formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Adding fields just like SignUp UI
        nameTf = new JTextField(10);
        addField(formPanel, gbc, "Name:", nameTf, 0);

        JPanel nicknamePanel = new JPanel();
        nicknameTf = new JTextField(10);
        nicknameButton = new JButton("중복 확인");
        nicknamePanel.add(nicknameTf);
        nicknamePanel.add(nicknameButton);
        addField(formPanel, gbc, "Nickname:", nicknamePanel, 1);

        idTf = new JTextField(10);
        idCheckButton = new JButton("중복 확인");
        JPanel idPanel = new JPanel();
        idPanel.add(idTf);
        idPanel.add(idCheckButton);
        addField(formPanel, gbc, "ID:", idPanel, 2);

        // Birth Year, Month, and Day ComboBox
        yearComboBox = new JComboBox<>(new String[]{"1950", "1951", "1952", "1953", "1954", "1955", "1956", "1957", "1958", "1959",
                "1960", "1961", "1962", "1963", "1964", "1965", "1966", "1967", "1968", "1969",
                "1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977", "1978", "1979",
                "1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989",
                "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999",
                "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009",
                "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019",
                "2020", "2021", "2022", "2023", "2024"});
        monthComboBox = new JComboBox<>(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});
        dayComboBox = new JComboBox<>(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"});

        JPanel birthPanel = new JPanel();
        birthPanel.add(yearComboBox);
        birthPanel.add(monthComboBox);
        birthPanel.add(dayComboBox);
        addField(formPanel, gbc, "Birth Date:", birthPanel, 3);

        // Phone Number
        phoneFrontComboBox = new JComboBox<>(new String[]{"010", "020", "030", "040", "050", "060", "070", "080", "090"});
        phoneMiddleTf = new JTextField(10);
        phoneBackTf = new JTextField(10);
        JPanel phoneNumPanel = new JPanel();
        phoneNumPanel.add(phoneFrontComboBox);
        phoneNumPanel.add(phoneMiddleTf);
        phoneNumPanel.add(phoneBackTf);
        addField(formPanel, gbc, "Phone Number:", phoneNumPanel, 4);

        // Gender Selection
        menButton = new JRadioButton("Male");
        girlButton = new JRadioButton("Female");
        sexGroup = new ButtonGroup();
        sexGroup.add(menButton);
        sexGroup.add(girlButton);
        JPanel genderPanel = new JPanel();
        genderPanel.add(menButton);
        genderPanel.add(girlButton);
        addField(formPanel, gbc, "Gender:", genderPanel, 5);

        // Email
        emailLocalTf = new JTextField(10);
        JLabel emailAtLabel = new JLabel("@");
        emailDomainTf = new JTextField(10);
        emailDomainComboBox = new JComboBox<>(new String[]{"직접 입력", "gmail.com", "naver.com", "daum.net", "hotmail.com"});
        JPanel emailPanel = new JPanel();
        emailPanel.add(emailLocalTf);
        emailPanel.add(emailAtLabel);
        emailPanel.add(emailDomainTf);
        emailPanel.add(emailDomainComboBox);
        addField(formPanel, gbc, "Email:", emailPanel, 6);

        // Postal Code and Address
        postalCodeTf = new JTextField(10);
        postalCodeTf.setEditable(false);
        postalCodeBtn = new JButton("Find Postal Code");
        JPanel postalCodePanel = new JPanel();
        postalCodePanel.add(postalCodeTf);
        postalCodePanel.add(postalCodeBtn);
        addField(formPanel, gbc, "Postal Code:", postalCodePanel, 7);

        addressTf = new JTextField(30);
        addressTf.setEditable(false);
        addField(formPanel, gbc, "Address:", addressTf, 8);

        detailedAddressTf = new JTextField(30);
        addField(formPanel, gbc, "Detailed Address:", detailedAddressTf, 9);

        // Add formPanel to main layout
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(formPanel, gbc);

        // Save and Cancel buttons
        buttonPanel = new JPanel();
        saveButton = new JButton("Save Changes");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Load user data from the database
        loadUserData();

        // Action Listeners
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

        postalCodeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApiExplorer postalCodeSearch = new ApiExplorer((postalCode, address) -> {
                    updateAddressFields(postalCode, address);
                });
                postalCodeSearch.setVisible(true);
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 사용자 입력 데이터 가져오기
                String newName = nameTf.getText();
                String newNickname = nicknameTf.getText();
                String newEmail = emailLocalTf.getText() + "@" + emailDomainTf.getText();
                String newPhone = phoneFrontComboBox.getSelectedItem() + phoneMiddleTf.getText() + phoneBackTf.getText();
                String newPostalCode = postalCodeTf.getText();
                String newAddress = addressTf.getText();
                String newDetailedAddress = detailedAddressTf.getText();
                String newGender = menButton.isSelected() ? "M" : "F";

                // 생년월일 처리
                int birthYear = Integer.parseInt((String) yearComboBox.getSelectedItem());
                int birthMonth = Integer.parseInt((String) monthComboBox.getSelectedItem());
                int birthDay = Integer.parseInt((String) dayComboBox.getSelectedItem());

                // 유효성 검사
                if (newName.isEmpty() || newNickname.isEmpty() || newEmail.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "필수 항목을 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Connection conn = null;
                PreparedStatement pstmt = null;

                try {
                    conn = dbConnection.getConnection(); // DB 연결
                    String sql = "UPDATE user_info SET name = ?, nickname = ?, email = ?, phone_number = ?, "
                               + "postal_code = ?, address = ?, detailed_address = ?, gender = ?, "
                               + "birth_year = ?, birth_month = ?, birth_day = ?, profile_image = ? "
                               + "WHERE id = ?";
                    pstmt = conn.prepareStatement(sql);

                    pstmt.setString(1, newName);
                    pstmt.setString(2, newNickname);
                    pstmt.setString(3, newEmail);
                    pstmt.setString(4, newPhone);
                    pstmt.setString(5, newPostalCode);
                    pstmt.setString(6, newAddress);
                    pstmt.setString(7, newDetailedAddress);
                    pstmt.setString(8, newGender);
                    pstmt.setInt(9, birthYear);
                    pstmt.setInt(10, birthMonth);
                    pstmt.setInt(11, birthDay);

                    // 프로필 이미지 처리
                    if (profileImage != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(profileImage, "png", baos);
                        pstmt.setBytes(12, baos.toByteArray());
                    } else {
                        pstmt.setNull(12, java.sql.Types.BLOB);
                    }

                    pstmt.setString(13, userId); // 조건: 현재 사용자 ID

                    // 쿼리 실행
                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(null, "프로필이 성공적으로 저장되었습니다.", "저장 완료", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "프로필 저장에 실패했습니다.", "저장 실패", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "저장 중 오류가 발생했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                } finally {
                    try {
                        if (pstmt != null) pstmt.close();
                        if (conn != null) conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the EditMember window
            }
        });

        setVisible(true);
    }

    private void loadUserData() {
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT id, name, nickname, email, birth_year, birth_month, birth_day, gender, phone_number, postal_code, address, detailed_address, profile_image FROM user_info WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                idTf.setText(rs.getString("id"));
                nameTf.setText(rs.getString("name"));
                nicknameTf.setText(rs.getString("nickname"));

                String email = rs.getString("email");
                if (email != null) {
                    String[] emailParts = email.split("@");
                    emailLocalTf.setText(emailParts[0]);
                    emailDomainTf.setText(emailParts[1]);
                }

                yearComboBox.setSelectedItem(String.valueOf(rs.getInt("birth_year")));
                monthComboBox.setSelectedItem(String.format("%02d", rs.getInt("birth_month")));
                dayComboBox.setSelectedItem(String.format("%02d", rs.getInt("birth_day")));

                String gender = rs.getString("gender");
                if ("M".equals(gender)) {
                    menButton.setSelected(true);
                } else if ("F".equals(gender)) {
                    girlButton.setSelected(true);
                }

                String phoneNumber = rs.getString("phone_number");
                if (phoneNumber != null && phoneNumber.length() > 3) {
                    phoneFrontComboBox.setSelectedItem(phoneNumber.substring(0, 3));
                    phoneMiddleTf.setText(phoneNumber.substring(3, 7));
                    phoneBackTf.setText(phoneNumber.substring(7));
                }

                postalCodeTf.setText(rs.getString("postal_code"));
                addressTf.setText(rs.getString("address"));
                detailedAddressTf.setText(rs.getString("detailed_address"));

                byte[] imageBytes = rs.getBytes("profile_image");
                if (imageBytes != null) {
                    ImageIcon profileImageIcon = new ImageIcon(imageBytes);
                    imageLabel.setIcon(profileImageIcon);
                } else {
                    imageLabel.setText("No Image");
                }
            } else {
                JOptionPane.showMessageDialog(null, "User information not found.");
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading user data: " + e.getMessage());
        }
    }

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
    
    public void updateAddressFields(String postalCode, String address) {
    	postalCodeTf.setText(postalCode);
    	addressTf.setText(address);
    }
}
