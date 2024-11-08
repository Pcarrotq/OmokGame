package omok.member;

import java.io.*;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.awt.image.BufferedImage;

public class ProfilePictureSelector {
    private Connection connection;

    public ProfilePictureSelector() {
        // 데이터베이스 연결
        connectDatabase();
    }

    // Oracle 데이터베이스 연결
    private void connectDatabase() {
        try {
            // Oracle JDBC 드라이버 로드
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // Oracle 데이터베이스 연결 (호스트, 포트, SID, 계정 정보 필요)
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe", "sys as sysdba", "chocolate5871");
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    // 데이터베이스에서 이미지 불러오기
    public ImageIcon loadImageFromDatabase(int id) throws SQLException, IOException {
        String selectSQL = "SELECT image FROM profile_images WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(selectSQL);
        pstmt.setInt(1, id); // 조회할 이미지의 id 설정

        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            byte[] imageBytes = rs.getBytes("image");
            if (imageBytes != null) {
                // 바이트 배열을 BufferedImage로 변환
                ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                BufferedImage image = ImageIO.read(bais);
                return new ImageIcon(image); // BufferedImage를 ImageIcon으로 변환
            }
        }
        return null; // 이미지가 없을 경우 null 반환
    }

    public static void main(String[] args) {
        ProfilePictureSelector profileImageOracle = new ProfilePictureSelector();

        try {
            // ID 1로 이미지 불러오기
            ImageIcon profileImage = profileImageOracle.loadImageFromDatabase(1);
            if (profileImage != null) {
                System.out.println("Image loaded successfully for ID: 1");
                // 이미지 로드 성공 후 사용할 수 있음 (GUI에 표시 등)
            } else {
                System.out.println("No image found for ID: 1");
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }
}