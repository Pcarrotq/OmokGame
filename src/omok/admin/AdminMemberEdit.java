package omok.admin;

import javax.swing.*;

import omok.member.DBConnection;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.*;

public class AdminMemberEdit extends JFrame {
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

    public AdminMemberEdit(String userId) {
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
                // Logic to change profile picture
                // Implement image selection and cropping if needed
            }
        });

        postalCodeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logic to open postal code finder dialog
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
    
    private void updateUserInfo() {
        String userId = idTf.getText().trim();
        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "회원 ID를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "UPDATE user_info SET name = ?, nickname = ?, email = ?, birth_year = ?, birth_month = ?, birth_day = ?, gender = ?, phone_number = ?, postal_code = ?, address = ?, detailed_address = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // 입력된 데이터를 쿼리 매개변수에 설정
            pstmt.setString(1, nameTf.getText());
            pstmt.setString(2, nicknameTf.getText());

            // 이메일 결합 후 설정
            String email = emailLocalTf.getText() + "@" + emailDomainTf.getText();
            pstmt.setString(3, email);

            pstmt.setInt(4, Integer.parseInt((String) yearComboBox.getSelectedItem()));
            pstmt.setInt(5, Integer.parseInt((String) monthComboBox.getSelectedItem()));
            pstmt.setInt(6, Integer.parseInt((String) dayComboBox.getSelectedItem()));

            // 성별 설정
            String gender = menButton.isSelected() ? "M" : "F";
            pstmt.setString(7, gender);

            // 전화번호 결합 후 설정
            String phoneNumber = phoneFrontComboBox.getSelectedItem() + phoneMiddleTf.getText() + phoneBackTf.getText();
            pstmt.setString(8, phoneNumber);

            pstmt.setString(9, postalCodeTf.getText());
            pstmt.setString(10, addressTf.getText());
            pstmt.setString(11, detailedAddressTf.getText());
            pstmt.setString(12, userId);

            // 업데이트 실행
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "회원 정보가 업데이트되었습니다.");
            } else {
                JOptionPane.showMessageDialog(this, "업데이트에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "업데이트 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}