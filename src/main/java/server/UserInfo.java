package server;

public class UserInfo {
    private String nickname;
    private int pick;

    public UserInfo(String nickname, int pick) {
        this.nickname = nickname;
        this.pick = pick;
    }

    public String getNickname() {
        return nickname;
    }

    public int getPick() {
        return pick;
    }
}
