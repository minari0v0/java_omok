package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static List<ClientHandler> clients = new ArrayList<>();
    private static Map<String, String> activePrivateChats = new HashMap<>();
    private static Map<Integer, Room> rooms = new HashMap<>();
    private static Random random = new Random();
    private ServerSocket serverSocket;  // 서버 소켓을 필드로 저장
    private boolean isRunning = false;  // 서버 실행 상태를 나타내는 플래그
    static UserInfoDb userInfoDb = new UserInfoDb();

    public static void main(String[] args) {
        new ChatServer().startServer();
    }

    public void startServer() {
        isRunning = true;  // 서버 실행 상태를 true로 설정
        try {
            serverSocket = new ServerSocket(12345);
            System.out.println("<관리자> 채팅 서버가 시작되었어요..");

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("<관리자> 새로운 클라이언트가 연결됐어요");
                    ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                } catch (SocketException e) {
                    if (!isRunning) {
                    	System.out.println("<관리자> 서버가 종료되었어요.");
                        break;
                    }
                    throw e;
                }
            }
        } catch (IOException e) {
            System.err.println("서버 오류: " + e.getMessage());
        }
    }
    
    // 현재 접속 중인 사용자와 소켓을 매핑하여 반환하는 메소드
    public static Map<String, Socket> getUserSockets() {
        Map<String, Socket> userSockets = new HashMap<>();
        for (ClientHandler client : clients) {
            userSockets.put(client.getNickname(), client.getSocket()); // 닉네임과 소켓을 매핑
        }
        return userSockets;
    }
    
    // 서버 종료 메소드
    public void stopServer() {
        isRunning = false;  // 서버 실행 상태를 false로 설정
        try {
            // 모든 클라이언트에게 서버 종료 메시지 전송
            broadcast("Server >> 서버가 종료됩니다.", null);

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();  // 서버 소켓 닫기
            }

            // 모든 클라이언트 연결 닫기
            for (ClientHandler client : clients) {
                client.closeConnection();
            }
            clients.clear();  // 클라이언트 리스트 초기화
            
        } catch (IOException e) {
            System.err.println("서버 종료 중 오류 발생: " + e.getMessage());
        }
    }

    // 브로드캐스트 메소드
    public static void broadcast(String message, ClientHandler sender) {
    	String displayMessage = message;
    	if (sender == null && message.startsWith("Server")) {
            displayMessage = message;
        } else if (sender == null) {
            displayMessage = "[관리자] : " + message;
        }

        for (ClientHandler client : clients) {
            client.sendMessage(displayMessage);
        }
    }
    
    // 1:1 채팅을 위한 메소드
    public static void sendPrivateMessage(String senderNickname, String recipientNickname, String message) {
        Socket recipientSocket = getUserSockets().get(recipientNickname);
        if (recipientSocket != null) {
            ClientHandler recipientHandler = getClientHandlerBySocket(recipientSocket);
            if (recipientHandler != null && activePrivateChats.containsKey(recipientNickname) && activePrivateChats.get(recipientNickname).equals(senderNickname)) {
                try {
                    PrintWriter recipientOut = new PrintWriter(recipientSocket.getOutputStream(), true);
                    String privateMessage = "PRIVCHAT " + senderNickname + ": " + message;
                    recipientOut.println(privateMessage);
                    
                    // 1대1 채팅 메시지를 서버 로그에 출력
                    System.out.println("[1:1 채팅] " + senderNickname +" -> "+recipientNickname+ ": "+ message);  // 서버 로그에 출력
                } catch (IOException e) {
                    System.err.println("1:1 메시지 전송 실패: " + e.getMessage());
                }
            } else {
                System.out.println("상대방이 채팅창을 열고 있지 않습니다.");
            }
        } else {
            System.out.println("받는 사람을 찾을 수 없습니다.");
        }
    }
    
    public static void sendResultMessage(String senderNickname, String message) {
        // senderNickname을 통해 해당 유저의 소켓을 가져옴
        Socket senderSocket = getUserSockets().get(senderNickname);
        if (senderSocket != null) {
            ClientHandler senderHandler = getClientHandlerBySocket(senderSocket);
            if (senderHandler != null) {
                try {
                    // senderNickname에게 메시지 전송
                    PrintWriter senderOut = new PrintWriter(senderSocket.getOutputStream(), true);
                    senderOut.println(message);  // 메시지를 그대로 전송

                } catch (IOException e) {
                    System.err.println("검색 결과 전송 실패: " + e.getMessage());
                }
            } else {
                System.out.println("클라이언트 핸들러를 찾을 수 없습니다. 닉네임: " + senderNickname);
            }
        } else {
            System.out.println("발신자를 찾을 수 없습니다. 닉네임: " + senderNickname);
        }
    }
    
    // 소켓을 이용해 ClientHandler를 찾는 메소드
    public static ClientHandler getClientHandlerBySocket(Socket socket) {
        for (ClientHandler client : clients) {
            if (client.getSocket().equals(socket)) {
                return client;
            }
        }
        return null;
    }
    
    // 서버에 접속중인 유저 리스트를 보내는 거
    public StringBuilder serverUserList() {
        StringBuilder userList = new StringBuilder();
        for (ClientHandler client : clients) {
            userList.append(client.getNickname()).append(",");
        }
        if (userList.length() > 0) {
            userList.setLength(userList.length() - 1); // 마지막 쉼표 제거
        }
        return userList;
    }

    // 현재 접속중인 유저 리스트를 새로고치는 브로드캐스트
    public static void broadcastUserList() {
        StringBuilder userList = new StringBuilder("USERLIST ");
        for (ClientHandler client : clients) {
            userList.append(client.getNickname()).append(",");
        }
        String userListMessage = userList.toString();
        for (ClientHandler client : clients) {
            client.sendMessage(userListMessage);  // 유저 리스트 전송
        }
    }
    
    //생성된 방들 브로드캐스트
    public static void broadcastRoomList() {
        StringBuilder roomListMessage = new StringBuilder("UPTROOM ");

        // 방 목록 추가
        for (Room room : rooms.values()) {
            roomListMessage.append(room.getTitle())
                           .append(" - ")
                           .append(room.getRoomId())
                           .append(", ");
        }

        // 방 목록이 없는 경우 메시지 초기화
        if (rooms.isEmpty()) {
            roomListMessage.append("EMPTY");
        } else {
            // 마지막 쉼표 및 공백 제거
            roomListMessage.setLength(roomListMessage.length() - 2);
        }

        // 모든 클라이언트에게 방 목록 메시지 전송
        for (ClientHandler client : clients) {
            client.sendMessage(roomListMessage.toString());
        }
    }


    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String nickname;
        private ChatServer chatServer;
        private int uid;
        UserInfoS userInfo;
        
        public ClientHandler(ChatServer chatServer, Socket socket) {
            this.chatServer = chatServer;
            this.socket = socket;
        }

        public ClientHandler(ChatServer chatServer) {
            this.chatServer = chatServer;
        }
        
        public int getUid() {
            return this.uid;
        }
        
        public Socket getSocket() {
            return socket; // 소켓에 대한 getter 메소드 추가
        }

        public String getNickname() {
            return nickname;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // 닉네임 수신 후, 입장 메시지를 브로드캐스트하고 유저 리스트 갱신
                this.nickname = in.readLine();
                this.uid=userInfoDb.getUidByNick(this.nickname);
                System.out.println("[서버] 서버에 " + nickname + "님이 들어왔어요");
                broadcast("Server >> " + nickname + "님이 들어왔어요!", this);
                broadcastUserList();  // 유저 리스트 갱신
                broadcastRoomList(); //방 목록도 갱신

                String message;
                MessageDbManager messageDbManager = new MessageDbManager(); // 메시지 DB 매니저 인스턴스 생성

                while ((message = in.readLine()) != null) {
                    if (message.startsWith("GET_USER_INFO")) {
                        String requestedNickname = message.split(" ")[1];

                        // DB에서 유저 정보 조회
                        userInfo = userInfoDb.getUserInfo(userInfoDb.getUidByNick(requestedNickname));

                        if (userInfo != null) {
                            // base64로 이미지 변환
                            String encodedImage = userInfo.getProfileImg() != null ? Base64.getEncoder().encodeToString(userInfo.getProfileImg()) : "NULL"; // 이미지가 없으면 "NULL"로 처리
                            // 클라이언트로 정보 전송
                            out.println("USER_INFO " + userInfo.getNickname() + " " + userInfo.getWin() + " " + userInfo.getLose() + " " + encodedImage);
                        } else {
                            out.println("[서버] ERROR: 유저 정보를 찾을 수 없습니다.");
                        }
                    } else if (message.startsWith("PRIVCHAT")) { // PRIVCHAT 명령어 처리
                        String[] parts = message.split(" ", 3);  // "PRIVCHAT" + 수신자 + 메시지
                        String recipientNickname = parts[1];
                        String privateMessage = parts[2];

                        // 채팅 상태를 맵에 저장 (양방향 채팅 상태)
                        activePrivateChats.put(nickname, recipientNickname);
                        activePrivateChats.put(recipientNickname, nickname);

                        // 상대방에게 메시지 보내기
                        sendPrivateMessage(nickname, recipientNickname, privateMessage);
                        
                        String type="PRIV";
                        messageDbManager.saveMessage(type, nickname, recipientNickname, privateMessage);
                    } else if (message.startsWith("SETMYINFO")) {
                        String[] parts = message.split(" ");
                        if (parts.length > 1) {
                            try {
                                int uid = Integer.parseInt(parts[1]);
                                Integer pick = userInfoDb.getUserPick(uid);
                                
                                if (pick != null) {
                                    out.println("SETMYINFO " + pick);
                                } else {
                                    out.println("SETMYINFO NULL");
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                out.println("SETMYINFO NULL");
                            }
                        } else {
                            out.println("SETMYINFO NULL");
                        }
                    } else if (message.startsWith("UPDATEMYINFO")) {
                        String[] parts = message.split(" ");
                        if (parts.length > 2) {
                            try {
                                int uid = Integer.parseInt(parts[1]);
                                int pick = Integer.parseInt(parts[2]);

                                boolean success = userInfoDb.updateUserPick(uid, pick); // pick 업데이트
                                out.println("UPDATEMYINFO " + (success ? "true" : "false")); // 성공 여부 전송
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                out.println("UPDATEMYINFO false");
                            }
                        } else {
                            out.println("UPDATEMYINFO false");
                        }
                    } else if (message.startsWith("SEARCH_MESSAGES")) { // SEARCH_MESSAGES 명령어 처리
                        String[] parts = message.split(" ", 4); // "SEARCH_MESSAGES" + 송신자 + 수신자 + 검색어
                        String senderNickname = parts[1];
                        String recipientNickname = parts[2];
                        String searchTerm = parts[3];

                        // 메시지 검색
                        List<String> searchResults = messageDbManager.fetchMessages(senderNickname, recipientNickname, searchTerm);

                        // searchResults가 null이거나 비어 있는 경우 처리
                        if (searchResults == null || searchResults.isEmpty()) {
                            // 검색 결과가 없을 경우
                            sendResultMessage(senderNickname, "NO_SEARCH_RESULT");
                        } else {
                            // 검색된 메시지를 하나의 문자열로 합침
                            StringBuilder resultBuilder = new StringBuilder();
                            for (String result : searchResults) {
                                resultBuilder.append(result).append(","); // 각 메시지를 줄바꿈으로 구분
                            }

                            // 한 번에 전송
                            String resultM = "SEARCH_RESULT " + resultBuilder.toString().trim();
                            sendResultMessage(senderNickname, resultM);
                        }
                    }else if (message.startsWith("CRTROOM")) { 
                        // 메시지 파싱: "CRTROOM title 비밀번호설정여부 관전허용여부 비밀번호"
                        String[] parts = message.split("/");

                        if (parts.length < 4) {
                            out.println("Error: Invalid message format");
                            return;
                        }

                        String title = parts[1]; // 방 제목
                        boolean isSecret = Boolean.parseBoolean(parts[2]); // 비밀번호 설정 여부
                        boolean allowSpectators = Boolean.parseBoolean(parts[3]); // 관전 허용 여부

                        // 비밀번호가 설정되어 있으면 비밀번호 받기
                        String password = null;
                        if (isSecret) {
                            if (parts.length < 5) {
                                out.println("Error: Password required for secret room");
                                return;
                            }
                            password = parts[4]; // 비밀번호
                        }

                        // 방 번호 생성 (중복 방지)
                        int roomId;
                        synchronized (rooms) {
                            do {
                                roomId = random.nextInt(999) + 1; // 1 ~ 999 사이의 랜덤 방 번호
                            } while (rooms.containsKey(roomId)); // 중복 확인

                            // 방 생성 후 저장
                            Room newRoom = new Room(roomId, title, isSecret, password, allowSpectators, this.chatServer);
                            rooms.put(roomId, newRoom);
                        }

                        // 생성된 방 번호 클라이언트로 전달
                        out.println("SCRTROOM " + roomId);
                        broadcastRoomList();
                    } else if (message.startsWith("ENTROOM")) { // 방 입장 요청 처리
                        String[] parts = message.split(" ");
                        int roomId = Integer.parseInt(parts[1]);

                        Room room = rooms.get(roomId);
                        if (room != null) {
                            if (room.hasPassword()) {
                                // 비밀번호가 있는 경우
                                out.println("SCPW " + roomId);
                            } else {
                                // 비밀번호 없이 입장 가능
                                out.println("SCRTROOM " + roomId);
                            }
                        } else {
                            out.println("[서버] ERROR: 해당 방이 존재하지 않습니다.");
                        }
                        broadcastRoomList();
                    } else if (message.startsWith("SCPWROOM")) { // 비밀번호 확인 요청 처리
                        String[] parts = message.split(" ", 3); // SCPWROOM 방번호 비밀번호
                        int roomId = Integer.parseInt(parts[1]);
                        String password = parts[2];

                        Room room = rooms.get(roomId);
                        if (room != null) {
                            if (room.getPassword().equals(password)) {
                                // 비밀번호 일치
                                out.println("SCRTROOM " + roomId);
                            } else {
                                // 비밀번호 불일치
                                out.println("FSCPW");
                            }
                        } else {
                            out.println("[서버] ERROR: 해당 방이 존재하지 않습니다.");
                        }
                    } else if (message.startsWith("ENTERROOM") || message.startsWith("EXITROOM")) {
                        String[] parts=message.split(":");
                        String[] roomParts = parts[1].split(",");
                        int roomId = Integer.parseInt(roomParts[0]);
                        int uid = Integer.parseInt(roomParts[1]);

                        Room room = ChatServer.getRoomById(roomId);

                        if ("ENTERROOM".equals(parts[0])) {
                            handleEnter(room, uid);
                        } else if ("EXITROOM".equals(parts[0])) {
                            handleExit(room, uid);
                        }
                    } else if (message.startsWith("CHAT")) {
                        String[] parts=message.split(":");
                        String[] chatParts = parts[1].split(",");

                        int roomId = Integer.parseInt(chatParts[0]);
                        int uid = Integer.parseInt(chatParts[1]);
                        String chatMessage = chatParts[2];

                        // UID로 닉네임 가져오기
                        Map<String, Object> userInfo = userInfoDb.getNickAndPickByUid(uid);

                        String nickname = (String) userInfo.get("nickname");

                        // 해당 roomId에 맞는 Room 객체 가져오기
                        Room room = ChatServer.getRoomById(roomId);
                        if (room == null) {
                            System.out.println("ERROR: 방을 찾을 수 없습니다.");
                            return;
                        }

                        // 브로드캐스트: "닉네임: 메시지내용"
                        String broadcastMessage = "CHAT:"+nickname + ": " + chatMessage;
                        handleChat(room, broadcastMessage);
                        String type="GAME";
                        messageDbManager.saveMessage(type, nickname, chatMessage);

                    }else if (message.startsWith("RBUTN")) {
                        // 메시지를 ":"로 구분
                        String[] parts = message.split(":");
                        if (parts.length < 2) return; // 메시지 형식 검증

                        // ":"로 구분된 [1] 번째 메시지를 ","로 다시 구분
                        String[] readyParts = parts[1].split(",");
                        if (readyParts.length < 3) return; // 메시지 형식 검증

                        String player = readyParts[0]; // PLY1R 또는 PLY2R
                        boolean isReady = "TRUE".equalsIgnoreCase(readyParts[1]); // TRUE 또는 FALSE
                        int roomId = Integer.parseInt(readyParts[2]); // 방번호

                        // roomId로 room 객체를 가져옴
                        Room room = ChatServer.getRoomById(roomId);
                        if (room == null) {
                            System.err.println("Room not found for roomId: " + roomId);
                            return; // 해당 방이 없으면 종료
                        }

                        // 플레이어 상태를 업데이트
                        switch (player) {
                            case "PLY1R":
                                room.setPlayer1Ready(isReady);
                                break;
                            case "PLY2R":
                                room.setPlayer2Ready(isReady);
                                break;
                            default:
                                break;
                        }
                    } else if (message.startsWith("GAME:")) {//-----------------------------game 관련 처리
                        String[] parts = message.split(",");
                        String command = parts[0].split(":")[1];
                        
                        if (command.equals("MOVE")) {
                            int roomNumber = Integer.parseInt(parts[1]);
                            int uid = Integer.parseInt(parts[2]);
                            int x = Integer.parseInt(parts[3]);
                            int y = Integer.parseInt(parts[4]);

                            ClientHandler clientHandler = ChatServer.getClientHandlerByUid(uid);
                            if (clientHandler == null) {
                                return;
                            }

                            Room room = getRoomById(roomNumber);
                            if (room != null) {
                                room.move(clientHandler, x, y);
                            }
                        } else if (command.equals("GVUP")) { // 기권 처리
                            int roomNumber = Integer.parseInt(parts[1]);
                            int uid = Integer.parseInt(parts[2]);

                            // 기권한 유저 처리
                            Room room = getRoomById(roomNumber);
                            if (room != null) {
                                ClientHandler clientHandler = ChatServer.getClientHandlerByUid(uid);
                                if (clientHandler != null) {
                                    room.handleGiveUp(clientHandler);  // 기권한 유저 처리 메소드 호출
                                }
                            }
                        }
                    } else {
                        // 일반 채팅 메시지 처리
                        System.out.println("[유저] " + nickname + " - " + message);
                        // 메시지를 DB에 저장
                        String type="LOBBY";
                        messageDbManager.saveMessage(type, nickname, message);

                        broadcast("[" + nickname + "]: " + message, this);
                    }
                }
            } catch (IOException e) {
                System.out.println("[서버] " + nickname + "님이 종료하셨어요.");
                broadcast("Server >> " + nickname + "님이 종료하셨어요.", this);
            } finally {
                clients.remove(this);
                activePrivateChats.remove(nickname);
                broadcastUserList();  // 유저 나가면 유저 리스트 갱신
            }
        }
        
        private void handleChat(Room room, String chatMessage) {
        	room.broadcast(chatMessage);
        }
        
        
        private void handleEnter(Room room, int uid) throws IOException {
            // DB에서 유저 정보 가져오기
            Map<String, Object> userInfo = userInfoDb.getNickAndPickByUid(uid);
            if (userInfo == null) {
                System.out.println("유효하지 않은 UID입니다: " + uid);
                return;
            }

            String nickname = (String) userInfo.get("nickname");
            Integer pick = (Integer) userInfo.get("pick");

            if (nickname == null || pick == null) {
                System.out.println("유효하지 않은 유저 정보입니다: UID " + uid);
                return;
            }

            // Room 객체에 클라이언트 추가
            ClientHandler clientHandler = ChatServer.getClientHandlerByUid(uid);
        	// 입장한 유저에게 닉네임과 PICK 전송
            room.sendMessageToClient("SENTER:" + nickname + "," + pick, clientHandler);
            if (clientHandler == null) {
                System.out.println("클라이언트를 찾을 수 없습니다: UID " + uid);
                return;
            }

            String role = room.addClient(clientHandler, nickname, pick);
            if ("FULL".equals(role)) {
                clientHandler.sendMessage("ERROR: 방이 가득 찼습니다.");
                return;
            }

            
        }
        
        private void handleExit(Room room, int uid) {
            if (room == null) {
                return;
            }
            ClientHandler client = ChatServer.getClientHandlerByUid(uid);
            if (client != null) {
                room.removeClient(client);
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }


        public void closeConnection() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("클라이언트 연결 종료 중 오류: " + e.getMessage());
            }
        }
    }
    
    public synchronized void removeRoom(Room room) {
        int roomId = room.getRoomId();
        if (rooms.containsKey(roomId)) {
            rooms.remove(roomId);
            broadcastRoomList(); // 방 목록 업데이트
        } else {
        }
    }
    
    public static Room getRoomById(int roomId) {
        return rooms.get(roomId); // Map을 사용해 roomId로 Room 객체를 가져옴
    }
    
    //uid를 통해 클라이언트 소켓을 UDP에게 보내주는 함수임
    public static ClientHandler getClientHandlerByUid(int uid) {
        UserInfoDb userInfoDb = new UserInfoDb();
        Map<String, Object> userInfo = userInfoDb.getNickAndPickByUid(uid);

        if (userInfo != null) {
            String nickname = (String) userInfo.get("nickname");
            for (ClientHandler client : clients) {
                if (client.getNickname().equals(nickname)) {
                    return client; // 해당 닉네임의 클라이언트 핸들러 반환
                }
            }
        }
        return null; // 클라이언트 핸들러를 찾지 못한 경우
    }
    
}