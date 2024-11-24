package test.member.dbLoad;

public class UserProfile {
    private String id;
    private String password;
    private String name;
    private String nickname;
    private String email;
    private int birthYear;
    private int birthMonth;
    private int birthDay;
    private String gender;
    private String phoneNumber;
    private String postalCode;
    private String address;
    private String detailedAddress;
    private byte[] profileImage;
    
    // 모든 필드를 포함한 생성자
    public UserProfile(String id, String password, String name, String nickname, String email,
                       int birthYear, int birthMonth, int birthDay, String gender,
                       String phoneNumber, String postalCode, String address,
                       String detailedAddress, byte[] profileImage) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.postalCode = postalCode;
        this.address = address;
        this.detailedAddress = detailedAddress;
        this.profileImage = profileImage;
    }

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