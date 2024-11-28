package omok.member;

public class UserProfile {
    private String userId;
    private String nickname;
    private String email;
    private String phoneNumber;
    private byte[] profileImage;
    private String intro;
    private int score;
    private int rank;

    public UserProfile(String userId, String nickname, String email, String phoneNumber, byte[] profileImage) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
    }

    public UserProfile(String userId, String nickname, String email, String phoneNumber, byte[] profileImage, String intro, int score, int rank) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
        this.intro = intro;
        this.score = score;
        this.rank = rank;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public String getIntro() {
        return intro;
    }

    public int getScore() {
        return score;
    }

    public int getRank() {
        return rank;
    }
}