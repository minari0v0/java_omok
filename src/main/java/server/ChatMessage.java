package server;

public class ChatMessage {
    private String nickname;
    private String message;

    public ChatMessage(String nickname, String message) {
        this.nickname = nickname;
        this.message = message;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return nickname + ": " + message;
    }
}
