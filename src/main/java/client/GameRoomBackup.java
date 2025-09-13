package client;

public class GameRoomBackup {
	/* package client;

import javax.swing.*;

import GUI.BoardLabel;
import GUI.FontLoader;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;

public class GameRoom extends JFrame {
	private static final long serialVersionUID = 1L;
	private String roomNumber;
    private int uid;
    private PrintWriter out;
    private String nick;
    private int pick;
    private String p1Nick;
    private String p2Nick;
    private int p1Pick;
    private int p2Pick;
    FontLoader fontLoader = new FontLoader("src/main/java/img/Cookie.ttf", 18f);
    Font customFont = fontLoader.getCustomFont(); // 커스텀 글꼴 가져오기
    FontLoader fontLoader1 = new FontLoader("src/main/java/img/Cookie.ttf", 13f);
    Font customFont1 = fontLoader1.getCustomFont(); // 커스텀 글꼴 가져오기
    
    public String getRoomNumber() {
    	return roomNumber;
    }
    
    public void setMyInfo(String nick, int pick) {
    	this.nick=nick;
    	this.pick=pick;
    	toggleReadyButton(player1Name.getText(), player2Name.getText());
    }

    private File[] pickPath = {
        new File("src/main/java/img/치이카와.png"),
        new File("src/main/java/img/하치와레.png"),
        new File("src/main/java/img/우사기.png"),
        new File("src/main/java/img/용사.png"),
        new File("src/main/java/img/모몽가.png"),
        new File("src/main/java/img/밤토리.png")
    };

    private JLabel player1Icon;
    private JLabel player1Name, player1RCheck;
    private JLabel player2Icon;
    private JLabel player2Name, player2RCheck;
    private JLabel observer1Label, observer2Label;
    private JButton player1ReadyButton, player2ReadyButton;
    private JTextArea chatArea;
    private JTextField chatInputField;

    public GameRoom(String roomNumber, int uid, PrintWriter out) {
        this.roomNumber = roomNumber;
        this.uid = uid;
        this.out = out;

        // 기본 설정
        setTitle("오목의 달인 - 방 " + roomNumber);
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // 왼쪽 오목판
        JPanel boardPanel = new JPanel();
        boardPanel.setPreferredSize(new Dimension(704, 704));
        boardPanel.setLayout(null);
        BoardLabel boardLabel = new BoardLabel(new ImageIcon("src/main/java/img/오목판690.png")); // 오목판 이미지 설정
        boardPanel.add(boardLabel); // 패널에 이미지 추가
        add(boardPanel, BorderLayout.WEST); // 패널을 WEST 영역에 추가

        // 오른쪽 패널 (유저 정보 + 채팅창)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(400, 704));
        add(rightPanel);

        // 유저 정보 패널
        JPanel userListPanel = new JPanel(null); // LayoutManager를 null로 설정
        userListPanel.setPreferredSize(new Dimension(400, 250));

        // Player1 Icon
        player1Icon = new JLabel();
        player1Icon.setBounds(80, 20, 100, 100); // 위치와 크기 설정
        player1Icon.setIcon(new ImageIcon(new ImageIcon("src/main/java/img/default_proImg.png").getImage()
                .getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        userListPanel.add(player1Icon);

        // Player1 Name
        player1Name = new JLabel("Player1", SwingConstants.CENTER);
        player1Name.setBounds(80, 130, 100, 20); // 위치와 크기 설정
        player1Name.setFont(customFont);
        userListPanel.add(player1Name);

        // Player1 Ready Button
        player1ReadyButton = new JButton("준비");
        player1ReadyButton.setBounds(90, 160, 75, 30); // 위치와 크기 설정
        player1ReadyButton.setBackground(new Color(244, 246, 255));
        player1ReadyButton.setFont(customFont);
        player1ReadyButton.setVisible(false); // 초기에는 버튼 숨김
        player1ReadyButton.addActionListener(e -> handleReadyButton(1));
        userListPanel.add(player1ReadyButton);

        // 관전자1 레이블
        observer1Label = new JLabel("관전자1", SwingConstants.CENTER);
        observer1Label.setBounds(50, 200, 100, 30); // 준비 버튼 아래쪽에 위치
        observer1Label.setFont(customFont1);
        userListPanel.add(observer1Label);

        // Player2 Icon
        player2Icon = new JLabel();
        player2Icon.setBounds(250, 20, 100, 100); // 위치와 크기 설정
        player2Icon.setIcon(new ImageIcon(new ImageIcon("src/main/java/img/default_proImg.png").getImage()
                .getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        userListPanel.add(player2Icon);

        // Player2 Name
        player2Name = new JLabel("Player2", SwingConstants.CENTER);
        player2Name.setBounds(250, 130, 100, 20); // 위치와 크기 설정
        player2Name.setFont(customFont);
        userListPanel.add(player2Name);

        // Player2 Ready Button
        player2ReadyButton = new JButton("준비");
        player2ReadyButton.setBounds(270, 160, 75, 30); // 위치와 크기 설정
        player2ReadyButton.setBackground(new Color(244, 246, 255));
        player2ReadyButton.setFont(customFont);
        player2ReadyButton.setVisible(false); // 초기에는 버튼 숨김
        player2ReadyButton.addActionListener(e -> handleReadyButton(2));
        userListPanel.add(player2ReadyButton);
        
        player1RCheck = new JLabel();
        player1RCheck.setBounds(40, 160, 40, 30);
        player1RCheck.setIcon(new ImageIcon(new ImageIcon("src/main/java/img/readyCheck.png").getImage()
        		.getScaledInstance(40, 30, Image.SCALE_SMOOTH)));
        player1RCheck.setVisible(false); // 초기에는 숨김 처리
        userListPanel.add(player1RCheck);
        
        player2RCheck = new JLabel();
        player2RCheck.setBounds(360, 160, 40, 30);
        player2RCheck.setIcon(new ImageIcon(new ImageIcon("src/main/java/img/readyCheck.png").getImage()
                .getScaledInstance(40, 30, Image.SCALE_SMOOTH)));
        player2RCheck.setVisible(false); // 초기에는 숨김 처리
        userListPanel.add(player2RCheck);
        
        // 관전자2 레이블
        observer2Label = new JLabel("관전자2", SwingConstants.CENTER);
        observer2Label.setBounds(250, 200, 100, 30); // 준비 버튼 아래쪽에 위치
        observer2Label.setFont(customFont1);
        userListPanel.add(observer2Label);

        // userListPanel을 rightPanel에 추가
        rightPanel.add(userListPanel, BorderLayout.NORTH);

        // 채팅 패널
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(400, 400));
        rightPanel.add(chatPanel, BorderLayout.CENTER);

        // 채팅 영역
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        // 채팅 입력 필드 및 버튼
        JPanel chatInputPanel = new JPanel();
        chatInputPanel.setLayout(new BorderLayout());
        chatInputField = new JTextField();
        JButton sendButton = new JButton("전송");
        chatInputPanel.add(chatInputField, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        // 전송 버튼 클릭 이벤트
        sendButton.addActionListener(e -> sendChatMessage());
        chatInputField.addActionListener(e -> sendChatMessage()); // Enter 키로 전송

        // 방 입장 요청
        sendEnterRequest();

        // 윈도우 닫기 이벤트 처리
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                sendExitRequest();
                dispose();
            }
        });

    }

    // 채팅 메시지 전송
    private void sendChatMessage() {
        String message = chatInputField.getText().trim();
        if (!message.isEmpty()) {
            out.println("CHAT:" + roomNumber + "," + uid + "," + message);
            chatInputField.setText(""); // 입력 필드 초기화
        }
    }

    // 방 입장 요청
    private void sendEnterRequest() {
        out.println("ENTERROOM:" + roomNumber + "," + uid);
    }

    // 퇴장 요청
    public void sendExitRequest() {
        out.println("EXITROOM:" + roomNumber + "," + uid);
    }

    // 메시지 처리
    public void handleMessage(String message) {
    	if (message.startsWith("GAME_USER_INFO:")) {
    		handleUserInfo(message);
        }  else if (message.startsWith("CHAT")){
        	handleChatMessage(message);
        }
    }
    
    private void handleUserInfo(String message) {
    	String[] parts = message.split(":", 2);
        if (parts.length < 2) return;

        String[] userInfo = parts[1].split(",");

        // 최소한 roomId와 4명의 정보 (플레이어 2명, 관전자 2명)가 와야 함
        if (userInfo.length < 9) return;

        int roomId = Integer.parseInt(userInfo[0]);

        // 방 번호가 일치하는지 확인
        if (roomId == Integer.parseInt(roomNumber)) {
        	System.out.println(message);
            // 플레이어 1의 닉네임과 PICK 설정
        	if (!userInfo[1].equals("null") && !userInfo[2].equals("null")) {
                this.p1Nick = userInfo[1]; // p1의 닉네임
                int pick1 = Integer.parseInt(userInfo[2]); // PICK 값이 userInfo[2]에 있다고 가정
                this.p1Pick = pick1;
            }

            // 플레이어 2의 닉네임과 PICK 설정
            if (!userInfo[3].equals("null") && !userInfo[4].equals("null")) {
                this.p2Nick = userInfo[3]; // p2의 닉네임
                int pick2 = Integer.parseInt(userInfo[4]); // PICK 값이 userInfo[4]에 있다고 가정
                this.p2Pick = pick2;
            }
            // 플레이어 1
            updatePlayerInfo(player1Icon, player1Name, userInfo[1], userInfo[2]);

            // 플레이어 2
            updatePlayerInfo(player2Icon, player2Name, userInfo[3], userInfo[4]);

            // 관전자 1 (닉네임만 업데이트)
            updateObserverInfo(observer1Label, userInfo[5]);

            // 관전자 2 (닉네임만 업데이트)
            updateObserverInfo(observer2Label, userInfo[7]);
            toggleReadyButton1(userInfo[1],userInfo[3]);
        }
    }
    
    private void handleChatMessage(String message) {
    	String[] parts=message.split(":",2);
    	String broadChat = parts[1];
        chatArea.append(broadChat + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
        
    
    private void toggleReadyButton(String player1, String player2) {
        String trimmedNick = this.nick.trim();
        player1ReadyButton.setVisible(player1.trim().equals(trimmedNick));
        player2ReadyButton.setVisible(player2.trim().equals(trimmedNick));
    }
    
    private void toggleReadyButton1(String player1, String player2) {
        // p1Nick이 "null"이 아닌 경우에만 player1ReadyButton의 가시성을 설정
        if (p1Nick != null && !p1Nick.equals("null")) {
            String trimmedNick = this.p1Nick.trim();
            // player1이 p1Nick 또는 this.nick과 일치할 때만 player1ReadyButton을 보이게 설정
            player1ReadyButton.setVisible(player1.trim().equals(trimmedNick) && player1.trim().equals(this.nick.trim()));
        } else {
            // p1Nick이 null이거나 "null"인 경우 player1ReadyButton을 숨김
            player1ReadyButton.setVisible(false);
        }

        // p2Nick이 "null"이 아닌 경우에만 player2ReadyButton의 가시성을 설정
        if (p2Nick != null && !p2Nick.equals("null")) {
            String trimmedNick = this.p2Nick.trim();
            // player2가 p2Nick 또는 this.nick과 일치할 때만 player2ReadyButton을 보이게 설정
            player2ReadyButton.setVisible(player2.trim().equals(trimmedNick) && player2.trim().equals(this.nick.trim()));
        } else {
            // p2Nick이 null이거나 "null"인 경우 player2ReadyButton을 숨김
            player2ReadyButton.setVisible(false);
        }
    }
    
    //준비버튼 이벤트 처리 메소드
    private void handleReadyButton(int playerNumber) {
        JButton readyButton = (playerNumber == 1) ? player1ReadyButton : player2ReadyButton;
        String message;

        // 버튼 토글 상태에 따라 메시지 설정
        if ("준비".equals(readyButton.getText())) {
            readyButton.setText("취소");
            readyButton.setBackground(new Color(210, 211, 214));
            message = (playerNumber == 1) ? "RBUTN:PLY1R,TRUE" : "RBUTN:PLY2R,TRUE";
        } else {
            readyButton.setText("준비");
            readyButton.setBackground(new Color(244, 246, 255));
            message = (playerNumber == 1) ? "RBUTN:PLY1R,FALSE" : "RBUTN:PLY2R,FALSE";
        }

        // 서버로 메시지 전송
        String rMessage=message+","+roomNumber;
        out.println(rMessage);
    }
    
    public void handleReadyCheck(String message) {
        // 메시지를 ":"로 나누기
        String[] parts = message.split(":", 2);
        if (parts.length < 2) return; // 메시지 형식 검증

        // RBUTN 내용 파싱 (e.g., "ply1,true")
        String[] readyInfo = parts[1].split(",");
        if (readyInfo.length < 2) return; // 메시지 형식 검증

        String player = readyInfo[0];  // ply1 또는 ply2
        boolean isReady = Boolean.parseBoolean(readyInfo[1]); // true 또는 false

        // player1, player2 체크
        if ("ply1".equals(player)) {
            player1RCheck.setVisible(isReady); // player1 준비 체크 표시
        } else if ("ply2".equals(player)) {
            player2RCheck.setVisible(isReady); // player2 준비 체크 표시
        }
    }

    
    // 플레이어 정보 업데이트
    private void updatePlayerInfo(JLabel iconLabel, JLabel nameLabel, String nickname, String pickStr) {
        if ("null".equals(nickname)) {
            nameLabel.setText("입장 대기 중..");
            iconLabel.setIcon(new ImageIcon(new ImageIcon("src/main/java/img/default_proImg.png")
                    .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        } else {
            nameLabel.setText(nickname);

            // pick 처리
            try {
                int pick = Integer.parseInt(pickStr);
                if (pick > 0 && pick <= pickPath.length) {
                    ImageIcon originalIcon = new ImageIcon(pickPath[pick - 1].getAbsolutePath());
                    Image img = originalIcon.getImage();
                    Image resizedImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    iconLabel.setIcon(new ImageIcon(resizedImg));
                } else {
                    iconLabel.setIcon(new ImageIcon(new ImageIcon("src/main/java/img/default_proImg.png")
                            .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
                }
            } catch (NumberFormatException e) {
                iconLabel.setIcon(new ImageIcon(new ImageIcon("src/main/java/img/default_proImg.png")
                        .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
            }
        }

    }
    
    private void handleRoleUpgrade(String[] userInfo) {
        // player1 자리 비었을 경우
        if ("null".equals(userInfo[1])) { // player1 자리가 비었을 경우
            if (!"null".equals(userInfo[5])) {
                // observer1이 player1으로 승격
                updatePlayerInfo(player1Icon, player1Name, userInfo[5], userInfo[6]);

                // 준비 버튼과 체크 표시
                player1ReadyButton.setVisible(true);
                player1RCheck.setVisible(true);

            }
        }
        
        // player2 자리 비었을 경우
        if ("null".equals(userInfo[3])) {
            if (!"null".equals(userInfo[7])) {
                // observer2가 player2로 승격
                updatePlayerInfo(player2Icon, player2Name, userInfo[7], userInfo[6]);

                // 준비 버튼과 체크 표시
                player2ReadyButton.setVisible(true);
                player2RCheck.setVisible(true);

            }
        }
    }
    
    // 관전자 정보 업데이트
    private void updateObserverInfo(JLabel label, String nickname) {
        if ("null".equals(nickname)) {
            label.setText(""); // 관전자가 없으면 빈 문자열로 설정
        } else {
            label.setText(nickname); // 관전자 닉네임만 표시
        }
    }
    
    public void broadChat(String message) {
    	chatArea.append(message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

}


*/
}
