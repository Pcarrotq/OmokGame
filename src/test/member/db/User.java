package test.member.db;

public class User {
	private String id, password, name, nickname, email, gender, phoneNumber;
	private int birthYear, birthMonth, birthDay;
	private String poscalCode, address, detailAddress;
	private byte[] profileImage;

	public User(String id, String password, String name, String nickname, String email,
			int birthYear, int birthMonth, int birthDay, String gender, String phoneNumber,
			String poscalCode, String address, String detailAddress, byte[] profileImage) {
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
	    this.poscalCode = poscalCode;
	    this.address = address;
	    this.detailAddress = detailAddress;
	    this.profileImage = profileImage;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPassword() {
	    return password;
	}

	public void setPassword(String password) {
	    this.password = password;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}
	
	public String getNickname() {
	    return nickname;
	}

	public void setNickname(String nickname) {
	    this.nickname = nickname;
	}

	public String getEmail() {
	    return email;
	}

	public void setEmail(String email) {
	    this.email = email;
	}
	
	public int getBirthYear() {
	    return birthYear;
	}

	public void setBirthYear(int birthYear) {
	    this.birthYear = birthYear;
	}
	
	public int getBirthMonth() {
	    return birthMonth;
	}

	public void setBirthMonth(int birthMonth) {
	    this.birthMonth = birthMonth;
	}
	
	public int getBirthDay() {
	    return birthDay;
	}

	public void setBirthDay(int birthDay) {
	    this.birthDay = birthDay;
	}
	
	public String getGender() {
	    return gender;
	}

	public void setGender(String gender) {
	    this.gender = gender;
	}
	
	public String getPhoneNumber() {
	    return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
	    this.phoneNumber = phoneNumber;
	}
	
	public String getPoscalCode() {
	    return poscalCode;
	}

	public void setPoscalCode(String poscalCode) {
	    this.poscalCode = poscalCode;
	}
	
	public String getAddress() {
	    return address;
	}

	public void setAddress(String address) {
	    this.address = address;
	}
	
	public String getDetailAddress() {
	    return detailAddress;
	}

	public void setDetailAddress(String detailAddress) {
	    this.detailAddress = detailAddress;
	}
	
	public byte[] getProfileImage() {
	    return profileImage;
	}

	public void setProfileImage(byte[] profileImage) {
	    this.profileImage = profileImage;
	}
}