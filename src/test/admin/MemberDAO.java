package test.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import test.member.DBConnection;

public class MemberDAO {

    // Add a new member to the database
    public boolean addMember(Member member) {
        String sql = "INSERT INTO user_info (id, password, name, nickname, email, birth_date, gender, phone_number, postal_code, address, detailed_address, profile_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.getNickname());
            pstmt.setString(5, member.getEmail());
            pstmt.setDate(6, member.getBirthDate());
            pstmt.setString(7, member.getGender());
            pstmt.setString(8, member.getPhoneNumber());
            pstmt.setString(9, member.getPostalCode());
            pstmt.setString(10, member.getAddress());
            pstmt.setString(11, member.getDetailedAddress());
            pstmt.setBytes(12, member.getProfileImage());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve a member by ID
    public Member getMemberById(String id) {
        String sql = "SELECT * FROM user_info WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Member(
                        rs.getString("id"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getDate("birth_date"),
                        rs.getString("gender"),
                        rs.getString("phone_number"),
                        rs.getString("postal_code"),
                        rs.getString("address"),
                        rs.getString("detailed_address"),
                        rs.getBytes("profile_image")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update a member's information
    public boolean updateMember(Member member) {
        String sql = "UPDATE user_info SET password = ?, name = ?, nickname = ?, email = ?, birth_date = ?, gender = ?, phone_number = ?, postal_code = ?, address = ?, detailed_address = ?, profile_image = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getPassword());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, member.getNickname());
            pstmt.setString(4, member.getEmail());
            pstmt.setDate(5, member.getBirthDate());
            pstmt.setString(6, member.getGender());
            pstmt.setString(7, member.getPhoneNumber());
            pstmt.setString(8, member.getPostalCode());
            pstmt.setString(9, member.getAddress());
            pstmt.setString(10, member.getDetailedAddress());
            pstmt.setBytes(11, member.getProfileImage());
            pstmt.setString(12, member.getId());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a member by ID
    public boolean deleteMember(String id) {
        String sql = "DELETE FROM user_info WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve a list of all members (for displaying in the AdminDashboard)
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM user_info";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Member member = new Member(
                        rs.getString("id"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getDate("birth_date"),
                        rs.getString("gender"),
                        rs.getString("phone_number"),
                        rs.getString("postal_code"),
                        rs.getString("address"),
                        rs.getString("detailed_address"),
                        rs.getBytes("profile_image")
                );
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
}