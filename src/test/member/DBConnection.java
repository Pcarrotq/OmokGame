package test.member;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
    public static Connection dbConn;
    
    public static Connection getConnection() {
        Connection conn = null;
        try {
            String user = "sys as sysdba";
            String pw = "chocolate5871";
            String url = "jdbc:oracle:thin:@localhost:1521:xe";
            
            Class.forName("oracle.jdbc.driver.OracleDriver");        
            conn = DriverManager.getConnection(url, user, pw);
            
            System.out.println("Database에 연결되었습니다.\n");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("DB 드라이버 로딩 실패 :"+cnfe.toString());
        } catch (SQLException sqle) {
            System.out.println("DB 접속실패 : "+sqle.toString());
        } catch (Exception e) {
            System.out.println("Unkonwn error");
            e.printStackTrace();
        }
        return conn;
    }
    
    public void addMember(String id, String password, String name, String nickname, String email,
    		int birthYear, int birthMonth, int birthDay, String gender,
    		String phoneNumber, String postalCode, String address, String detailedAddress, byte[] profileImage) {
    	String sql = "INSERT INTO user_info (id, password, name, nickname, email, birth_year, birth_month, birth_day, " +
    	"gender, phone_number, postal_code, address, detailed_address, profile_image) " +
    	"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    	try (Connection conn = DBConnection.getConnection();
    			PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		pstmt.setString(1, id);
    		pstmt.setString(2, password);
    		pstmt.setString(3, name);
    		pstmt.setString(4, nickname);
    		pstmt.setString(5, email);
    		pstmt.setInt(6, birthYear);
    		pstmt.setInt(7, birthMonth);
    		pstmt.setInt(8, birthDay);
    		pstmt.setString(9, gender);
    		pstmt.setString(10, phoneNumber);
    		pstmt.setString(11, postalCode);
    		pstmt.setString(12, address);
    		pstmt.setString(13, detailedAddress);
    		pstmt.setBytes(14, profileImage);
    		int rowsAffected = pstmt.executeUpdate();
    		System.out.println(rowsAffected + " 회원이 추가되었습니다.");
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    public void viewAllMembers() {
        String sql = "SELECT * FROM user_info";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String nickname = rs.getString("nickname");
                // 나머지 필드들도 가져와서 화면에 표시하거나 JTable에 추가할 수 있습니다.
                System.out.println("ID: " + id + ", Name: " + name + ", Nickname: " + nickname);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateMember(String id, String newNickname, String newPhoneNumber, String newAddress) {
        String sql = "UPDATE user_info SET nickname = ?, phone_number = ?, address = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newNickname);
            pstmt.setString(2, newPhoneNumber);
            pstmt.setString(3, newAddress);
            pstmt.setString(4, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("업데이트된 행 수: " + rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteMember(String id) {
        String sql = "DELETE FROM user_info WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("삭제된 회원 수: " + rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
