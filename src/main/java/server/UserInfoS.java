package server;

public class UserInfoS {
	private String id;
    private String pw;
    private String nickname;
    private String email;
    private String phone;
    private String address;
    private String birth;
    private int pick;
    private int win;
    private int lose;
    private int play;
    private int gender;
    private byte[] profileImg;

    public UserInfoS(String nickname, int win, int lose, byte[] profileImg) {
        this.nickname = nickname;
        this.win = win;
        this.lose = lose;
        this.profileImg = profileImg;
    }
    
    public UserInfoS(String id, String pw, String nickname, String email, String phone, String address, String birth,
            int pick, int win, int lose, int play, int gender) {
    	this.id = id;
    	this.pw = pw;
    	this.nickname = nickname;
    	this.email = email;
    	this.phone = phone;
    	this.address = address;
    	this.birth = birth;
    	this.pick = pick;
    	this.win = win;
    	this.lose = lose;
    	this.play = play;
    	this.gender = gender;
    	this.profileImg = null; // 프로필 이미지는 null로 설정
}
    
    public UserInfoS(String id, String pw, String nickname, String email, String phone, String address, String birth,
            int pick, int win, int lose, int play, int gender, byte[] profileImg) {
    	this.id = id;
    	this.pw = pw;
    	this.nickname = nickname;
    	this.email = email;
    	this.phone = phone;
    	this.address = address;
    	this.birth = birth;
    	this.pick = pick;
    	this.win = win;
    	this.lose = lose;
    	this.play = play;
    	this.gender = gender;
    	this.profileImg = profileImg;
}

    // Getter 메소드들
    public String getId() {
        return id;
    }

    public String getPw() {
        return pw;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getBirth() {
        return birth;
    }

    public int getPick() {
        return pick;
    }

    public int getWin() {
        return win;
    }

    public int getLose() {
        return lose;
    }

    public int getPlay() {
        return play;
    }

    public Integer getGender() {
        return gender;
    }

    public byte[] getProfileImg() {
        return profileImg;
    }
}

