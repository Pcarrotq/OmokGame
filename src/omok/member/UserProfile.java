package omok.member;

public class UserProfile {
    private String id;
    private String nickname;
    private String email;
    private String phoneNumber;
    private byte[] profileImage;

    public UserProfile(String id, String nickname, String email, String phoneNumber, byte[] profileImage) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
    }

    // 모든 사용자 정보가 필요하지 않을 경우 사용하는 생성자
    public UserProfile(String id, String nickname, byte[] profileImage) {
        this.id = id;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    // Getters
    public String getId() { return id; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public byte[] getProfileImage() { return profileImage; }
}
