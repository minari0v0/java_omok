package server;

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
    private boolean gameStart;
    private UserInfoDb userInfoDb;
    //--------게임 관련 변수들 ----------------
    private int[][] board; // 오목판 배열 (19x19)
    private boolean isBlackTurn; // true = 흑돌 차례, false = 백돌 차례
    private ClientHandler blackPlayer; // 흑돌 유저
    private ClientHandler whitePlayer; // 백돌 유저
    private static final int GRID_SIZE = 19; // 오목판 크기

    public Room(int roomId, String title, boolean hasPassword, String password, boolean allowSpectators, ChatServer chatServer) {
        this.roomId = roomId;
        this.title = title;
        this.hasPassword = hasPassword;
        this.password = password;
        this.allowSpectators = allowSpectators;
        this.players = new LinkedHashMap<>();
        this.spectators = new LinkedHashMap<>();
        this.chatServer=chatServer;
        this.gameStart=false;
        
        this.board = new int[GRID_SIZE][GRID_SIZE];
        this.isBlackTurn = true; // 초기 턴은 흑돌
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
            broadcast("CHAT:"+nickname + "님이 방에 입장했어요!");
            broadcastUserInfo();
            return "PLAYER";
        } else if (allowSpectators && spectators.size() < 2) {
            spectators.put(client, new UserInfo(nickname, pick));
            broadcast("CHAT:"+nickname + "님이 관전 중이에요..");
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
        	UserInfo removedPlayer = players.get(client); // 제거하기 전의 플레이어 정보
            String removedNickname = removedPlayer.getNickname(); // 나가는 사용자의 nickname
            
            // 나가기 전에 player1 또는 player2인지 확인
            boolean wasPlayer1 = players.size() > 0 && players.keySet().iterator().next().equals(client);
            boolean wasPlayer2 = players.size() > 1 && players.keySet().toArray()[1].equals(client);

            players.remove(client); // 실제로 제거
            broadcast("CHAT:" + removedNickname + "님이 퇴장하셨어요.");

            // 준비 상태 업데이트
            if (wasPlayer1) {
                player1Ready = false;
                broadcast("RBUTN:ply1,false");
            } else if (wasPlayer2) {
                player2Ready = false;
                broadcast("RBUTN:ply2,false");
            }

            // 관전자를 플레이어로 승격
            if (!spectators.isEmpty()) {
                Map.Entry<ClientHandler, UserInfo> nextSpectator = spectators.entrySet().iterator().next();
                players.put(nextSpectator.getKey(), nextSpectator.getValue());
                spectators.remove(nextSpectator.getKey());
                broadcast("CHAT:"+nextSpectator.getValue().getNickname() + "님이 플레이어로 참여했어요!");
            }
        } else if (spectators.containsKey(client)) {
            UserInfo removedSpectator = spectators.remove(client);
            broadcast("CHAT:"+removedSpectator.getNickname() + "님이 퇴장하셨어요.");
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
        broadcast("RBUTN:ply1,"+ready);
        checkGameStart();
    }

    public void setPlayer2Ready(boolean ready) {
        this.player2Ready = ready;
        broadcast("RBUTN:ply2,"+ready);
        checkGameStart();
    }
    
    public void checkGameStart() {//--------------------------------------게임 관련
        if (player1Ready && player2Ready) {
            // 게임 시작 메시지를 모든 유저에게 전송
        	assignPlayers();
            broadcast("GAME:START");
            gameStart=true;
            player1Ready=false;//시작했으니까 준비는 해제
            player2Ready=false;
            broadcast("RBUTN:ply1,false");
            broadcast("RBUTN:ply2,false");
        }
    }
    
    // 돌 놓기
    public synchronized void move(ClientHandler clientHandler, int x, int y) {
        if (players.size() < 2) {
            clientHandler.sendMessage("CHAT:[Notice] 게임을 시작하려면 두 명의 플레이어가 필요합니다.");
            return;
        }
        
        if (!gameStart) {
            clientHandler.sendMessage("CHAT:[Notice] 게임이 아직 시작되지 않았습니다.");
            return;
        }

        // 플레이어와 차례 확인
        if ((isBlackTurn && clientHandler != blackPlayer) || (!isBlackTurn && clientHandler != whitePlayer)) {
            clientHandler.sendMessage("CHAT:[Notice] 자신의 차례가 아닙니다!");
            return;
        }

        // 돌 놓기
        if (board[x][y] == 0) {
            int stone = isBlackTurn ? 1 : 2; // 흑돌은 1, 백돌은 2
            board[x][y] = stone;
            isBlackTurn = !isBlackTurn; // 턴 전환
            
            // 상태 업데이트 방송
            broadcast(String.format("GAME:UPDATE,%d,%d,%d", x, y, stone));
            
            // 승리 조건 확인
            if (checkWin(x, y, stone)) {
            	String winnerNick = (stone == 1) ? players.get(blackPlayer).getNickname() : players.get(whitePlayer).getNickname();
            	broadcast("CHAT:[Notice] "+winnerNick+"님께서 승리하셨어요!!");
                broadcast(String.format("GAME:WIN,%d", stone)); // 승리자 전송
                resetBoard(); // 보드 초기화
                broadcastUserInfo();
            }
        } else {
            clientHandler.sendMessage("CHAT: [Notice] 이미 돌이 놓여진 자리입니다!");
        }
    }


    // 승리 조건 체크
    private boolean checkWin(int x, int y, int stone) {
        return checkDirection(x, y, 1, 0, stone) || // 가로
               checkDirection(x, y, 0, 1, stone) || // 세로
               checkDirection(x, y, 1, 1, stone) || // 대각선 ↘
               checkDirection(x, y, 1, -1, stone);  // 대각선 ↗
    }

    // 특정 방향에서 연속된 돌 개수 확인
    private boolean checkDirection(int x, int y, int dx, int dy, int stone) {
        int count = 1;

        // 한 방향 체크
        for (int i = 1; i < 5; i++) {
            int nx = x + i * dx;
            int ny = y + i * dy;
            if (nx < 0 || nx >= GRID_SIZE || ny < 0 || ny >= GRID_SIZE || board[nx][ny] != stone) break;
            count++;
        }

        // 반대 방향 체크
        for (int i = 1; i < 5; i++) {
            int nx = x - i * dx;
            int ny = y - i * dy;
            if (nx < 0 || nx >= GRID_SIZE || ny < 0 || ny >= GRID_SIZE || board[nx][ny] != stone) break;
            count++;
        }

        return count >= 5; // 5개 이상 연결되면 승리
    }

	// 보드 초기화
    private void resetBoard() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                board[i][j] = 0;
            }
        }
        isBlackTurn = true; // 초기화 후 흑돌 차례로 시작
        gameStart=false;
    }
    
    // 흑돌, 백돌 플레이어 설정
    public void assignPlayers() {
        ClientHandler[] playerArray = players.keySet().toArray(new ClientHandler[0]);
        if (playerArray.length >= 2) {
            blackPlayer = playerArray[0]; // 첫 번째 플레이어
            whitePlayer = playerArray[1]; // 두 번째 플레이어
        }
    }
    
    public synchronized void handleGiveUp(ClientHandler clientHandler) {
        // 기권한 유저가 플레이어1인지, 플레이어2인지 확인
        if (blackPlayer == clientHandler) {
            // 흑돌 유저가 기권한 경우
            broadcast("CHAT:[Notice] " + players.get(blackPlayer).getNickname() + "님이 기권하셨습니다.");
            // 백돌 유저 승리 처리
            broadcast("CHAT:[Notice] " + players.get(whitePlayer).getNickname() + "님이 승리하셨습니다!");
            broadcast("GAME:WIN,2"); // 백돌 승리
            endGame(whitePlayer); // 게임 종료 및 백돌 승리 처리
        } else if (whitePlayer == clientHandler) {
            // 백돌 유저가 기권한 경우
            broadcast("CHAT:[Notice] " + players.get(whitePlayer).getNickname() + "님이 기권하셨습니다.");
            // 흑돌 유저 승리 처리
            broadcast("CHAT:[Notice] " + players.get(blackPlayer).getNickname() + "님이 승리하셨습니다!");
            broadcast("GAME:WIN,1"); // 흑돌 승리
            endGame(blackPlayer); // 게임 종료 및 흑돌 승리 처리
        } else {
        	broadcast("CHAT:[Notice] 관전자는 기권할 수 없습니다!");
        }

        // 게임 종료 처리
        resetBoard(); // 게임판 초기화
        broadcastUserInfo(); // 유저 정보 갱신
    }
    
    private void endGame(ClientHandler winner) {
        // 승리한 플레이어에게 승리 메시지 전송
        winner.sendMessage("CHAT:[Notice] 승리하셨어요!!");

        // 기권한 플레이어에게 패배 메시지 전송
        ClientHandler loser = (winner == blackPlayer) ? whitePlayer : blackPlayer;
        loser.sendMessage("CHAT:[Notice] 아쉽게도 패배하였어요..");
        updateUserStats(winner, loser);
    }
    
    private void updateUserStats(ClientHandler winner, ClientHandler loser) {
        int winnerUid = winner.getUid(); // 승리한 유저의 UID
        int loserUid = loser.getUid(); // 패배한 유저의 UID

        userInfoDb = new UserInfoDb();
       
        // 승리한 유저의 정보를 업데이트 (win, play 증가)
        userInfoDb.updateWinAndPlay(winnerUid);
        System.out.println("승리자 업데이트 완료");
        // 패배한 유저의 정보를 업데이트 (lose, play 증가)
        userInfoDb.updateLoseAndPlay(loserUid);
        System.out.println("패배자 업데이트 완료");
    }
    
    
}
