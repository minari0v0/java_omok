package server;

public class RoomBackup {
/*package server;

import java.util.LinkedHashMap;
import java.util.Map;

import server.ChatServer.ClientHandler;

public class Room {
    private int roomId;
    private String title;
    private boolean hasPassword;
    private String password;
    private boolean allowSpectators;
    private Map<ClientHandler, UserInfo> players; // 플레이어 정보
    private Map<ClientHandler, UserInfo> spectators; // 관전자 정보
    private ChatServer chatServer; // 상위 클래스 참조
    private boolean player1Ready, player2Ready;
    

    public Room(int roomId, String title, boolean hasPassword, String password, boolean allowSpectators, ChatServer chatServer) {
        this.roomId = roomId;
        this.title = title;
        this.hasPassword = hasPassword;
        this.password = password;
        this.allowSpectators = allowSpectators;
        this.players = new LinkedHashMap<>();
        this.spectators = new LinkedHashMap<>();
        this.chatServer=chatServer;
    }

    // 특정 유저에게 메시지 보내기
    public void sendMessageToClient(String message, ClientHandler client) {
        if (client != null) {
            client.sendMessage(message);
        }
    }
    
    public synchronized String addClient(ClientHandler client, String nickname, int pick) {
        if (players.size() < 2) {
            players.put(client, new UserInfo(nickname, pick));
            broadcast("CHAT:"+nickname + "님이 입장하셨습니다.");
            broadcastUserInfo();
            return "PLAYER";
        } else if (allowSpectators && spectators.size() < 2) {
            spectators.put(client, new UserInfo(nickname, pick));
            broadcast("CHAT:"+nickname + "님이 관전자로 입장하셨습니다.");
            broadcastUserInfo();
            return "SPECTATOR";
        }
        return "FULL";
    }

    private void broadcastUserInfo() {
        StringBuilder userInfoMessage = new StringBuilder("GAME_USER_INFO:" + roomId);

        // 플레이어 정보 추가
        int count = 0;
        for (UserInfo userInfo : players.values()) {
            userInfoMessage.append(",").append(userInfo.getNickname()).append(",").append(userInfo.getPick());
            count++;
        }
        // 플레이어가 부족하면 null로 채우기
        while (count < 2) {
            userInfoMessage.append(",null,null");
            count++;
        }

        // 관전자 정보 추가
        count = 0;
        for (UserInfo userInfo : spectators.values()) {
            userInfoMessage.append(",").append(userInfo.getNickname()).append(",").append(userInfo.getPick());
            count++;
        }
        // 관전자가 부족하면 null로 채우기
        while (count < 2) {
            userInfoMessage.append(",null,null");
            count++;
        }

        String finalMessage = userInfoMessage.toString();
        for (ClientHandler client : players.keySet()) {
            client.sendMessage(finalMessage);
        }
        for (ClientHandler client : spectators.keySet()) {
            client.sendMessage(finalMessage);
        }
    }

    public synchronized void removeClient(ClientHandler client) {
        if (players.containsKey(client)) {
            UserInfo removedPlayer = players.remove(client);
            broadcast("CHAT:"+removedPlayer.getNickname() + "님이 퇴장하셨습니다.");
            
            // 플레이어가 나가면 준비 상태를 false로 설정하고 메시지를 보냄
            Object[] playerKeys = players.keySet().toArray();
            if (playerKeys.length > 0) {
                if (removedPlayer.getNickname().equals(playerKeys[0])) { // player1이 나갔을 때
                    player1Ready = false;
                    broadcast("RBUTN:ply1,false");
                } else if (playerKeys.length > 1 && removedPlayer.getNickname().equals(playerKeys[1])) { // player2가 나갔을 때
                    player2Ready = false;
                    broadcast("RBUTN:ply2,false");
                }
            }

            // 관전자를 플레이어로 승격
            if (!spectators.isEmpty()) {
                Map.Entry<ClientHandler, UserInfo> nextSpectator = spectators.entrySet().iterator().next();
                players.put(nextSpectator.getKey(), nextSpectator.getValue());
                spectators.remove(nextSpectator.getKey());
                broadcast("CHAT:"+nextSpectator.getValue().getNickname() + "님이 플레이어로 승격되었습니다.");
            }
        } else if (spectators.containsKey(client)) {
            UserInfo removedSpectator = spectators.remove(client);
            broadcast("CHAT:"+removedSpectator.getNickname() + "님이 퇴장하셨습니다.");
        }
        broadcastUserInfo(); // 유저 정보 업데이트
        
        if (players.isEmpty() && spectators.isEmpty()) {
            chatServer.removeRoom(this); // ChatServer에 방 삭제 요청
        }
    }

    public synchronized void broadcast(String message) {
        for (ClientHandler client : players.keySet()) {
            client.sendMessage(message);
        }
        for (ClientHandler client : spectators.keySet()) {
            client.sendMessage(message);
        }
    }

    public int getRoomId() {
        return roomId;
    }

    public String getTitle() {
        return title;
    }

    public boolean hasPassword() {
        return hasPassword;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAllowSpectators() {
        return allowSpectators;
    }
    
    public void setPlayer1Ready(boolean ready) {
        this.player1Ready = ready;
        System.out.println("Player1 ready status updated: ");
        broadcast("RBUTN:ply1,"+ready);
    }

    public void setPlayer2Ready(boolean ready) {
        this.player2Ready = ready;
        System.out.println("Player2 ready status updated: ");
        broadcast("RBUTN:ply2,"+ready);
    }
}

*/
}
