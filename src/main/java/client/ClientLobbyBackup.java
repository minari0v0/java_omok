package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ClientLobbyBackup {
/* package client;

import javax.swing.*;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import GUI.ChatBubblePanel;
import GUI.CustomScrollBarUI;
import GUI.FontLoader;

public class ClientLobby extends JFrame {
	private static final long serialVersionUID = 1L;
	private int uid;
    private String nickname;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JFrame resultFrame, chatFrame; // 검색 결과 프레임
    private JTextArea chatArea, searchResultsArea;
    private JTextField chatInput;
    private JPanel searchPanel = null, colorPanel=null; //첫번째는 배경, 두번쨰는 메시지색, 세번쨰는 전송버튼
    private Color[] selectColor = {new Color(161, 194, 152), new Color(249, 247, 207), new Color(254, 238, 145)};
    private JList<String> userList, roomList;  // 유저 리스트를 보여줄 컴포넌트
    private DefaultListModel<String> userListModel, roomListModel;  // 유저 리스트 모델
    private Map<String, JFrame> privateChatWindows = new HashMap<>();
    private JButton createRoomButton, enterRoomButton;
    private GameRoom gameRoom; //겜방 생성 ㅋㅋ
    
    FontLoader fontLoader = new FontLoader("src/main/java/img/NanumSquareNeoB.ttf", 18f);
    Font customFont = fontLoader.getCustomFont(); // 커스텀 글꼴 가져오기
    
    FontLoader fontLoader1 = new FontLoader("src/main/java/img/돌기마요.ttf", 18f);
    Font customFont1 = fontLoader1.getCustomFont(); // 커스텀 글꼴 가져오기
    
    FontLoader fontLoader2 = new FontLoader("src/main/java/img/돌기마요.ttf", 15f);
    Font customFont2 = fontLoader2.getCustomFont(); // 커스텀 글꼴 가져오기
    
    FontLoader fontLoader3 = new FontLoader("src/main/java/img/밑미.ttf", 36f);
    Font customFont3 = fontLoader3.getCustomFont();
    
    // 특정 사용자의 1:1 채팅 창이 열려 있는지 확인하고 반환하는 메서드
    private JFrame findOpenChatWindow(String recipientNickname) {
        return privateChatWindows.get(recipientNickname);
    }
    // 채팅 좌표 기본값 설정
    private int chbaseX = 940;
    private int chbaseY = 600;

    // 기본 포트 값 설정 (12345)
    private static final int DEFAULT_PORT = 12345;
    
    private final String[] emojiPaths = {
    		"src/main/java/img/smile.png",
            "src/main/java/img/sad.png",
            "src/main/java/img/embarrass.png",
            "src/main/java/img/soso.png",
            "src/main/java/img/good.png",
            "src/main/java/img/eww.png"
    	};
    	private final String[] emojiLabels = {"/미소", "/슬픔", "/당황", "/쏘쏘", "/행복", "/으액"};

    // 메시지 전송 메소드
    private void sendMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            out.println(message); // 닉네임 없이 메시지만 전송
            chatInput.setText("");
        }
    }
    
    // JTextPane에 이미지를 삽입하고 말풍선 스타일을 적용하는 메소드
    private void insertImageToChatPane(JTextPane chatPane, String message, String emojiPath, boolean isMine) {
        try {
            // 이모티콘 아이콘 설정
            ImageIcon emojiIcon = null;
            if (emojiPath != null && !emojiPath.isEmpty()) {
                emojiIcon = new ImageIcon(new ImageIcon(emojiPath).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            }

            // 배경색 설정
            Color backgroundColor = isMine ? selectColor[1] : Color.WHITE; // 본인 또는 상대방 메시지 배경색

            // ChatBubblePanel 생성tes
            ChatBubblePanel bubblePanel = new ChatBubblePanel(message, emojiIcon, backgroundColor, isMine);
            
            // 정렬을 위한 패널 생성
            JPanel alignPanel = new JPanel();
            alignPanel.setLayout(new FlowLayout(isMine ? FlowLayout.RIGHT : FlowLayout.LEFT)); // 수평 레이아웃
            alignPanel.setBackground(selectColor[0]);
            alignPanel.add(bubblePanel);

            // 패널을 JTextPane에 추가
            chatPane.insertComponent(alignPanel);

            // StyledDocument를 사용하여 줄 바꿈 추가
            StyledDocument doc = chatPane.getStyledDocument();
            doc.insertString(doc.getLength(), "\n", null); // 줄 바꿈 추가

            // 커서를 가장 끝으로 이동시켜 최신 메시지가 보이도록 설정
            chatPane.setCaretPosition(doc.getLength());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //방 생성 메소드
    private void showCreateRoomDialog() {
        JDialog createRoomDialog = new JDialog(this, "방 생성", true);
        createRoomDialog.setSize(400, 280);
        createRoomDialog.setLocationRelativeTo(this);

        // 컨텐츠 패널 생성
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(153, 188, 133)); // 배경색 설정
        contentPanel.setLayout(null); // null 레이아웃 사용

        // 컴포넌트 추가
        JLabel titleLabel = new JLabel("방 제목:");
        titleLabel.setFont(customFont1);
        titleLabel.setBounds(30, 30, 100, 30);
        contentPanel.add(titleLabel);

        JTextField titleField = new JTextField();
        titleField.setFont(customFont2);
        titleField.setBounds(140, 30, 200, 30);
        contentPanel.add(titleField);

        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setFont(customFont1);
        passwordLabel.setBounds(30, 80, 100, 30);
        contentPanel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(customFont2);
        passwordField.setBounds(140, 80, 200, 30);
        passwordField.setEnabled(false);
        contentPanel.add(passwordField);

        JCheckBox secretRoomCheckBox = new JCheckBox("비밀방");
        secretRoomCheckBox.setFont(customFont1);
        secretRoomCheckBox.setBounds(30, 130, 100, 30);
        secretRoomCheckBox.setBackground(new Color(153, 188, 133));
        secretRoomCheckBox.addActionListener(e -> passwordField.setEnabled(secretRoomCheckBox.isSelected()));
        contentPanel.add(secretRoomCheckBox);

        JCheckBox spectatorCheckBox = new JCheckBox("관전 허용");
        spectatorCheckBox.setFont(customFont1);
        spectatorCheckBox.setBackground(new Color(153, 188, 133));
        spectatorCheckBox.setBounds(140, 130, 120, 30);
        contentPanel.add(spectatorCheckBox);

        JButton createButton = new JButton("생성");
        createButton.setFont(customFont1);
        createButton.setBounds(60, 180, 100, 40);
        createButton.setBackground(new Color(245, 245, 220));
        createButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            boolean isSecret = secretRoomCheckBox.isSelected();
            boolean allowSpectators = spectatorCheckBox.isSelected();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(createRoomDialog, "방 제목을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 메시지 구성 시 각 부분을 '/'로 구분
            String message = "CRTROOM/" + title + "/" + isSecret + "/" + allowSpectators;

            // 비밀번호 설정 여부가 true인 경우 비밀번호 추가
            if (isSecret) {
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(createRoomDialog, "비밀번호를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                message += "/" + password;
            }

            // 메시지 전송
            out.println(message);
            createRoomDialog.dispose();
        });

        contentPanel.add(createButton);

        JButton cancelButton = new JButton("취소");
        cancelButton.setFont(customFont1);
        cancelButton.setBounds(200, 180, 100, 40);
        cancelButton.setBackground(new Color(245, 245, 220));
        cancelButton.addActionListener(e -> createRoomDialog.dispose());
        contentPanel.add(cancelButton);

        createRoomDialog.setContentPane(contentPanel); // 컨텐츠 패널 설정
        createRoomDialog.setVisible(true);
    }

    
    //방 입장 팝업창
    private void showEnterRoomDialog() {
        String roomNumber = JOptionPane.showInputDialog(this, "입장할 방 번호를 입력하세요:");
        
        if (roomNumber != null && !roomNumber.trim().isEmpty()) {
            try {
                int roomId = Integer.parseInt(roomNumber.trim()); // 입력된 문자열을 int로 변환
                out.println("ENTROOM " + roomId); // 변환된 roomId 사용
            } catch (NumberFormatException e) {
                // 숫자 변환에 실패하면 알림창 표시
                JOptionPane.showMessageDialog(this, "숫자만 적어주세요!", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void enterRoom(String roomNumber) {
        // ClientLobby 창 닫기


        // GameRoom 인스턴스 생성 및 창 표시
    	gameRoom = new GameRoom(roomNumber, uid, out);
        gameRoom.setVisible(true);
    }
    
    // 기본 포트로 호출하는 생성자
    public ClientLobby(int uid, String nickname, JPanel weatherInfoPanel, boolean weatherSuccess) {
        this(uid, nickname, DEFAULT_PORT, weatherInfoPanel, weatherSuccess);  // 기본 포트로 다른 생성자를 호출
    }

    // 사용자 지정 포트를 사용하는 생성자
    public ClientLobby(int uid, String nickname, int port, JPanel weatherInfoPanel, boolean weatherSuccess) {
        this.uid = uid;
        this.nickname = nickname;
        this.setLayout(null);
        
        if(weatherSuccess!=false) {
	        if (weatherInfoPanel != null) {
	            weatherInfoPanel.setBounds(10, 680, 120, 160); // 위치와 크기 설정
	            weatherInfoPanel.setVisible(true); // 패널 보이기
	            System.out.println("패널이 추가되었습니다."); // 디버깅용 출력문
	        } else {
	            System.out.println("weatherInfoPanel이 null입니다.");
	        }
        }


        setTitle("오목의 달인");
        setSize(1440, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 서버와 연결
        try {
            socket = new Socket("localhost", port);  // 포트 번호 설정
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            // 닉네임을 서버에 전송
            out.println(nickname); // 이 줄 추가
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showConfirmDialog(null,"서버가 연결되어 있지 않습니다.", "서버 오프라인", JOptionPane.DEFAULT_OPTION);
            System.exit(0);
        } 
        
        // 사용자 정의 글꼴 로드
        FontLoader fontLoader = new FontLoader("src/main/java/img/NanumSquareNeoB.ttf", 16f);
        Font customFont = fontLoader.getCustomFont(); // 커스텀 글꼴 가져오기
        
        // 배경 설정
        setContentPane(new BackgroundPanel("src/main/java/img/게임배경2.png"));
        getContentPane().setLayout(null);

        // 채팅 UI
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBounds(chbaseX, chbaseY-190, 400, 370);
        chatArea.setFont(customFont);  // 사용자 정의 글꼴 적용
        chatArea.setForeground(Color.BLACK);  // 글자 색상 설정
        chatArea.setBackground(Color.WHITE);  // 배경 색상 설정

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBounds(chbaseX, chbaseY-190, 400, 370);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        //커스텀 스크롤바
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new CustomScrollBarUI());
        verticalScrollBar.setPreferredSize(new Dimension(4, Integer.MAX_VALUE));

        chatInput = new JTextField();
        chatInput.setBounds(chbaseX, chbaseY+200, 310, 30);
        chatInput.setFont(customFont);  // 사용자 정의 글꼴 적용

        JButton sendButton = new JButton("전송");
        sendButton.setBackground(new Color(255, 248, 222));
        sendButton.setFont(customFont);
        sendButton.setBorderPainted(false);
        sendButton.setBounds(chbaseX+310, chbaseY+200, 95, 30);

        // 유저 리스트 UI
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setFont(customFont);  // 유저 리스트에도 글꼴 적용
        userList.setForeground(Color.BLACK);  // 유저 리스트 글자 색상 설정
        userList.setBackground(Color.WHITE);
        
        // 유저 리스트에 더블클릭 이벤트 추가
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = userList.locationToIndex(e.getPoint());
                userList.setSelectedIndex(index);  // 클릭한 항목을 선택
                
                if (index != -1) { // 리스트 항목이 있는 위치에서 클릭할 때만 실행
                    String selectedUser = userList.getModel().getElementAt(index);

                    if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) { // 더블클릭
                        out.println("GET_USER_INFO " + selectedUser); // 유저 정보 요청
                    } else if (SwingUtilities.isRightMouseButton(e)) { // 우클릭
                        showPopupMenu(e.getX(), e.getY(), selectedUser);
                    }
                }
            }
        });
        
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setBounds(chbaseX, chbaseY-490, 400, 170);
        userScrollPane.setOpaque(false);
        userScrollPane.getViewport().setOpaque(false);
        userScrollPane.setBorder(null);

        // 메시지 전송 버튼 클릭 이벤트
        sendButton.addActionListener(e -> sendMessage());
        // 엔터키 누르면 메시지 전송
        chatInput.addActionListener(e ->{
        	sendMessage();
        	chatInput.requestFocus();
        });
        
        // 랭킹 버튼 생성
        JButton rankingButton = new JButton(new ImageIcon("src/main/java/img/랭킹.png"));
        rankingButton.setBounds(1319, 64, rankingButton.getIcon().getIconWidth(), rankingButton.getIcon().getIconHeight());
        rankingButton.setBorderPainted(false);
        rankingButton.setContentAreaFilled(false);

        // 버튼 이미지 변경 (클릭 시 그림자 이미지로 변경)
        rankingButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                rankingButton.setIcon(new ImageIcon("src/main/java/img/랭킹_그림자.png"));  // 그림자 이미지로 변경
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                rankingButton.setIcon(new ImageIcon("src/main/java/img/랭킹.png"));  // 원래 이미지로 돌아감
                ClientCache clientCache = new ClientCache();
                java.util.List<ClientCache.RankingData> rankingDataList = clientCache.getRankingData();
                // 랭킹 팝업 띄우기
                new RankingPopup(rankingDataList);
            }
        });
        
        //--------------------방 UI 컴포넌트들----------------------------------
        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        roomList.setFont(customFont3);
        roomList.setBounds(162, 105, 673, 345);
        roomList.setBorder(null);
        JScrollPane roomScrollPane = new JScrollPane(roomList);
        roomScrollPane.setBounds(162, 105, 673, 345);
        roomScrollPane.setBorder(null);

        // 방 생성 버튼
        createRoomButton = new JButton(new ImageIcon("src/main/java/img/방생성.png"));
        createRoomButton.setFont(customFont3);
        createRoomButton.setBounds(162, 495, createRoomButton.getIcon().getIconWidth(), createRoomButton.getIcon().getIconHeight());
        createRoomButton.setBorderPainted(false);
        createRoomButton.setContentAreaFilled(false);
        createRoomButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	createRoomButton.setIcon(new ImageIcon("src/main/java/img/방생성_clicked.png"));  // 그림자 이미지로 변경
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	createRoomButton.setIcon(new ImageIcon("src/main/java/img/방생성.png"));  // 원래 이미지로 돌아감
            	showCreateRoomDialog();
            }
        });


        // 방 입장 버튼
        enterRoomButton = new JButton(new ImageIcon("src/main/java/img/방입장.png"));
        enterRoomButton.setFont(customFont3);
        enterRoomButton.setBounds(528, 495, enterRoomButton.getIcon().getIconWidth(), enterRoomButton.getIcon().getIconHeight());
        enterRoomButton.setBorderPainted(false);
        enterRoomButton.setContentAreaFilled(false);
        enterRoomButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	enterRoomButton.setIcon(new ImageIcon("src/main/java/img/방입장_clicked.png"));  // 그림자 이미지로 변경
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	enterRoomButton.setIcon(new ImageIcon("src/main/java/img/방입장.png"));  // 원래 이미지로 돌아감
            	showEnterRoomDialog();
            }
        });
        

        // 방 목록 더블클릭 이벤트
        roomList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = roomList.getSelectedValue();
                    String[] parts = selected.split(" - ");
                    int selectedRoom = Integer.parseInt(parts[1].trim());
                    out.println("ENTROOM "+selectedRoom);
                }
            }
        });

        
        // 소켓으로부터 메시지 수신
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("USER_INFO")) {
                        // 서버로부터 유저 정보 수신
                        String[] data = message.split(" ");
                        String infoNick = data[1];
                        int wins = Integer.parseInt(data[2]);
                        int losses = Integer.parseInt(data[3]);
                        byte[] profileImg = Base64.getDecoder().decode(data[4]); // 이미지 base64로 디코딩
                        
                        // UserInfo 팝업 생성
                        SwingUtilities.invokeLater(() -> new UserInfo(infoNick, wins, losses, profileImg));
                    } else if (message.startsWith("USERLIST")) {
                        // 기존 유저 리스트 갱신 처리
                        String[] users = message.substring(9).split(",");
                        userListModel.clear();
                        Arrays.stream(users).forEach(userListModel::addElement);
                    } else if (message.startsWith("PRIVCHAT")) {
                        int firstSpaceIndex = message.indexOf(" ") + 1;
                        int colonIndex = message.indexOf(":");

                        String senderNickname = message.substring(firstSpaceIndex, colonIndex).trim();
                        String privateMessage = message.substring(colonIndex + 1).trim();

                        SwingUtilities.invokeLater(() -> {
                            // 수신자의 채팅창
                            JFrame chatFrame = findOpenChatWindow(senderNickname);
                            if (chatFrame != null) {
                                JTextPane chatPane = (JTextPane) ((JScrollPane) chatFrame.getContentPane().getComponent(1)).getViewport().getView();

                                boolean isEmojiMessage = false;
                                for (int i = 0; i < emojiLabels.length; i++) {
                                    if (privateMessage.contains(emojiLabels[i])) {
                                        isEmojiMessage = true;
                                        // 텍스트와 이미지를 함께 출력
                                        String textPart = privateMessage.replace(emojiLabels[i], "");  // 텍스트 부분 추출
                                        insertImageToChatPane(chatPane, senderNickname + ": " + textPart, emojiPaths[i],false);
                                        break;
                                    }
                                }

                                if (!isEmojiMessage) {
                                    // 이모티콘이 없으면 일반 메시지로 출력
                                	insertImageToChatPane(chatPane, senderNickname + ": " + privateMessage, null,false);
                                }
                            }

                            // 발신자에게도 메시지 출력
                            JFrame senderChatFrame = findOpenChatWindow("나"); // '나'는 발신자의 닉네임
                            if (senderChatFrame != null) {
                                JTextPane senderChatPane = (JTextPane) ((JScrollPane) senderChatFrame.getContentPane().getComponent(1)).getViewport().getView();

                                boolean isEmojiMessageSender = false;
                                for (int i = 0; i < emojiLabels.length; i++) {
                                    if (privateMessage.contains(emojiLabels[i])) {
                                        isEmojiMessageSender = true;
                                        // 텍스트와 이미지를 함께 출력
                                        String textPartSender = privateMessage.replace(emojiLabels[i], "");  // 텍스트 부분 추출
                                        insertImageToChatPane(senderChatPane, senderNickname + ": " + textPartSender, emojiPaths[i], false);
                                        break;
                                    }
                                }

                                if (!isEmojiMessageSender) {
                                    // 이모티콘이 없으면 일반 메시지로 출력
                                    senderChatPane.setText(senderChatPane.getText() + senderNickname + ": " + privateMessage + "\n");
                                }
                            }
                        });
                    } else if (message.startsWith("SEARCH_RESULT")) {
                    	resultFrame = null;
                        // 검색 결과 처리
                        String resultText = message.substring("SEARCH_RESULT ".length()).trim();

                        if (resultFrame == null) {
                            // 검색 결과 창 초기화
                            resultFrame = new JFrame("검색 결과");
                            resultFrame.setSize(300, 400);
                            resultFrame.setLocationRelativeTo(null);
                            resultFrame.setLayout(new BorderLayout());

                            searchResultsArea = new JTextArea(); // JTextArea 초기화
                            searchResultsArea.setEditable(false);
                            searchResultsArea.setFont(customFont1);

                            resultFrame.add(new JScrollPane(searchResultsArea), BorderLayout.CENTER);
                            resultFrame.setVisible(true);
                        }

                        // ','로 구분된 검색 결과를 목록으로 분리하여 JTextArea에 추가
                        String[] results = resultText.split(","); // ','로 구분된 결과를 나눈다.
                        for (String result : results) {
                            searchResultsArea.append(result.trim() + "\n"); // 각 결과를 줄바꿈하여 추가
                        }

                    } else if (message.startsWith("NO_SEARCH_RESULT")) {
                        // 검색 결과가 없을 때 알림 팝업 표시
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, "검색 결과가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                        });

                        // 결과 창에 "검색 결과가 없습니다." 메시지를 표시
                        if (resultFrame != null) {
                            searchResultsArea.setText("검색 결과가 없습니다.");
                        }
                    } else if (message.equals("Server >> 서버로부터 추방되었습니다.")) {
                        // 서버로부터 추방되었을 때 처리
                        SwingUtilities.invokeLater(() -> {
                            // 바로 팝업 창 띄우기
                            JOptionPane.showMessageDialog(ClientLobby.this, "서버로부터 추방되었습니다!", "당신은 추방되었습니다! ㅋㅅㅋ", JOptionPane.WARNING_MESSAGE);
                            dispose(); // ClientLobby 창 닫기
                            new MainFrame(); // MainFrame 다시 실행
                        });
                    } else if (message.startsWith("SCRTROOM")) {
                        // 방 입장 처리
                        String roomNumber = message.substring(9).trim();
                        SwingUtilities.invokeLater(() -> {
                        	enterRoom(roomNumber);
                        });
                    } else if (message.startsWith("SCPW")) {
                        // 서버가 비밀번호 요청 메시지를 보낸 경우
                        String roomNumber = message.substring("SCPW".length()).trim(); // 방 번호 추출
                        
                        // 비밀번호 입력 팝업창 띄우기
                        SwingUtilities.invokeLater(() -> {
                            String password = JOptionPane.showInputDialog(ClientLobby.this, "비밀번호를 입력하세요:", "방 비밀번호 입력", JOptionPane.PLAIN_MESSAGE);
                            
                            if (password != null && !password.trim().isEmpty()) {
                                // 비밀번호가 입력되면 서버로 방 번호와 비밀번호 전송
                            	String message1="SCPWROOM " + roomNumber + " " + password;
                            	out.println(message1);
                            } else {
                                // 비밀번호가 입력되지 않으면 알림 표시
                                JOptionPane.showMessageDialog(ClientLobby.this, "비밀번호를 입력하지 않았습니다.", "알림", JOptionPane.WARNING_MESSAGE);
                            }
                        });
                    } else if (message.startsWith("FSCPW")) {
                        // 비밀번호가 틀린 경우 처리
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(ClientLobby.this, "비밀번호가 틀렸습니다. 다시 시도해주세요.", "입장 실패", JOptionPane.WARNING_MESSAGE);
                        });
                    }else if (message.startsWith("UPTROOM")) {
                        // 메시지에서 방 목록 추출
                        String roomListString = message.substring(8).trim(); // "UPTROOM " 제거 후 앞뒤 공백 제거

                        // 기존 목록 초기화
                        roomListModel.clear();

                        if (roomListString.equals("EMPTY")) {
                            // 방 목록이 비었음을 표시
                        	roomListModel.clear();
                        } else {
                            // 방 목록 문자열 파싱
                            String[] rooms = roomListString.split(", ");
                            for (String room : rooms) {
                                if (!room.isBlank()) {
                                    roomListModel.addElement(room); // 방 제목 - 방 번호 형식으로 추가
                                }
                            }
                        }
                    } else if (message.startsWith("GAME_USER_INFO:")) {
                        String[] parts = message.split(":", 2);
                        if (parts.length < 2) return;

                        String[] userInfo = parts[1].split(",");
                        if (userInfo.length < 7) return;

                        int roomId = Integer.parseInt(userInfo[0]);
                        String roomNumber = gameRoom.getRoomNumber();

                        // GameRoom과 roomId 비교 후 업데이트 실행
                        if (roomId == Integer.parseInt(roomNumber)) {
                            gameRoom.handleMessage(message);
                        }
                    } else if (message.equals("ERROR: 방이 가득 찼습니다.")) {
                        // gameRoom이 생성되어 있는지 확인
                        if (gameRoom != null) {
                            gameRoom.dispose(); // gameRoom 닫기
                            gameRoom = null; // 참조 제거
                        }
                        JOptionPane.showMessageDialog(null, "방이 가득 찼습니다. 다른 방을 선택해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    } else if (message.startsWith("CHAT:")){
                    	String[] parts=message.split(":",2);
                    	String broadChat = parts[1];
                    	gameRoom.broadChat(broadChat);
                    } else if (message.startsWith("SENTER:")) {
                    	String[] parts=message.split(":",2);
                    	String[] parts2=parts[1].split(",",2);
                    	String nick=parts2[0];
                    	int pick=Integer.parseInt(parts2[1]);
                    	gameRoom.setMyInfo(nick, pick);
                    } else if (message.startsWith("RBUTN:")) {
                        gameRoom.handleReadyCheck(message);
                    }else if (message.startsWith("GAME:")) {
                        String[] parts = message.split(":", 2);
                        if (parts.length < 2) return;

                        String[] gameInfo = parts[1].split(",");
                        if (gameInfo[0].equals("START")) {
                            gameRoom.gameStart(); // 게임 시작
                        } else if (gameInfo[0].equals("UPDATE")) {
                            if (gameInfo.length == 4) {
                                // x, y, stone 정보를 전달
                                int x = Integer.parseInt(gameInfo[1]);
                                int y = Integer.parseInt(gameInfo[2]);
                                int stone = Integer.parseInt(gameInfo[3]); // stone 값 추가
                                gameRoom.updateBoard(x, y, stone); // 게임 보드 업데이트
                            }
                        } else if (gameInfo[0].equals("WIN")) {
                            if (gameInfo.length == 2) {
                                int winnerPick = Integer.parseInt(gameInfo[1]);
                                gameRoom.displayWinner(winnerPick); // 1이면 p1, 2면 p2
                            }
                        }
                    } else {
                        // 로비 채팅 메시지 처리
                    	chatArea.append(message + "\n");
                        chatArea.setCaretPosition(chatArea.getDocument().getLength());
                    }
                    resultFrame = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
            	if (gameRoom != null) {
                    gameRoom.sendExitRequest();
                }
                dispose();
            }
        });


        // 컴포넌트 추가
        add(weatherInfoPanel);
        add(rankingButton);
        add(scrollPane);
        add(chatInput);
        add(sendButton);
        add(userScrollPane);  // 유저 리스트 추가
        add(roomScrollPane);
        add(createRoomButton);
        add(enterRoomButton);

        setVisible(true);
    }
    
    private void showPopupMenu(int x, int y, String selectedUser) {
        JPopupMenu popupMenu = new JPopupMenu();

        // "정보조회" 메뉴 아이템
        JMenuItem infoItem = new JMenuItem("정보조회");
        infoItem.addActionListener(e -> {
            // 정보 조회 실행
            out.println("GET_USER_INFO " + selectedUser);
        });
        popupMenu.add(infoItem);

        // 자신이 아닌 경우에만 "채팅하기" 메뉴 추가
        if (!selectedUser.equals(this.nickname)) {
            JMenuItem chatItem = new JMenuItem("채팅하기");
            chatItem.addActionListener(e -> {
                // 채팅하기 팝업창 띄우기
                openChatWindow(selectedUser);
            });
            popupMenu.add(chatItem);
        }

        // 팝업 메뉴 표시
        popupMenu.show(userList, x, y);
    }

    private void openChatWindow(String recipientNickname) {
        chatFrame = findOpenChatWindow(recipientNickname);

        if (chatFrame == null) {  // 이미 열려 있는지 확인
            chatFrame = new JFrame("대화 - " + recipientNickname);
            chatFrame.setSize(350, 500);
            chatFrame.setLayout(new BorderLayout());
            
            JPanel topPanel = new JPanel(new BorderLayout());
            JLabel titleLabel = new JLabel(recipientNickname, SwingConstants.CENTER);
            titleLabel.setFont(customFont);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            topPanel.add(titleLabel, BorderLayout.CENTER);
            topPanel.setBackground(new Color(238, 238, 238));
            topPanel.setBorder(null);
            
            // 검색 버튼 추가 (오른쪽 위)
            JButton searchButton = new JButton("🔍");
            searchButton.setBorderPainted(false); // 테두리 없애기
            searchButton.setContentAreaFilled(false); // 배경 제거
            searchButton.addActionListener(e -> addSearchField(topPanel, recipientNickname));
            topPanel.add(searchButton, BorderLayout.EAST);

            chatFrame.add(topPanel, BorderLayout.NORTH);	
            
            //색상 파레트 세트 버튼 추가
            JButton colorButton = new JButton("🎨");
            colorButton.setBackground(Color.WHITE);
            colorButton.setBorderPainted(false); // 테두리 없애기
            colorButton.setContentAreaFilled(false); // 버튼 배경 없애기
            colorButton.setFocusPainted(false); // 포커스 시 테두리 없애기
            colorButton.addActionListener(e -> selectColorPanel(topPanel, chatFrame));
            topPanel.add(colorButton, BorderLayout.WEST);
            
            
            JTextPane chatPane = new JTextPane();
            chatPane.setEditable(false);
            chatPane.setFont(customFont1);
            chatPane.setBorder(null);
            chatPane.setBackground(selectColor[0]);//
            JScrollPane scrollPane = new JScrollPane(chatPane);
            chatFrame.add(scrollPane, BorderLayout.CENTER);
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setUI(new CustomScrollBarUI());
            verticalScrollBar.setPreferredSize(new Dimension(3, Integer.MAX_VALUE));

            // 입력 필드와 전송 버튼을 포함할 패널 생성
            JPanel inputPanel = new JPanel(new BorderLayout());
            JTextField chatInputField = new JTextField();
            chatInputField.setBorder(BorderFactory.createEmptyBorder()); // 테두리 없애기
            chatInputField.setFont(customFont1);
            chatInputField.requestFocus();
            SwingUtilities.invokeLater(() -> {
                chatInputField.requestFocus();  // 채팅창 열 때 입력 필드에 포커스 부여
            });
            
         // 엔터키로 전송
            chatInputField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String message = chatInputField.getText().trim();
                    if (!message.isEmpty()) {
                        // 이모티콘 텍스트를 이미지로 변환하여 채팅창에 삽입
                        String[] emojiPaths = {
                            "src/main/java/img/smile.png",
                            "src/main/java/img/sad.png",
                            "src/main/java/img/embarrass.png",
                            "src/main/java/img/soso.png",
                            "src/main/java/img/good.png",
                            "src/main/java/img/eww.png"
                        };
                        String[] emojiLabels = {"/미소", "/슬픔", "/당황", "/쏘쏘", "/행복", "/으액"};

                        boolean isEmojiMessage = false;
                        for (int i = 0; i < emojiLabels.length; i++) {
                            if (message.contains(emojiLabels[i])) {
                                isEmojiMessage = true;
                                String textPart = message.replace(emojiLabels[i], ""); // 텍스트 추출
                                insertImageToChatPane(chatPane, "나: " + textPart, emojiPaths[i], true); // 이미지 삽입
                                break;
                            }
                        }

                        if (!isEmojiMessage) {
                        	insertImageToChatPane(chatPane, "나: " + message, null, true); // 기본 텍스트 처리
                        }

                        out.println("PRIVCHAT " + recipientNickname + " " + message); // 서버로 1:1 메시지 전송
                        chatInputField.setText("");
                        chatInputField.requestFocus();
                    }
                }
            });

            JButton sendButton = new JButton("전송");
            sendButton.setBackground(selectColor[2]);
            sendButton.setFont(customFont1);
            sendButton.setBorderPainted(false);

            // 전송 버튼 클릭 이벤트
            sendButton.addActionListener(e -> {
                String message = chatInputField.getText().trim();
                if (!message.isEmpty()) {
                    // 이모티콘 텍스트를 이미지로 변환하여 채팅창에 삽입
                    String[] emojiPaths = {
                        "src/main/java/img/smile.png",
                        "src/main/java/img/sad.png",
                        "src/main/java/img/embarrass.png",
                        "src/main/java/img/soso.png",
                        "src/main/java/img/good.png",
                        "src/main/java/img/eww.png"
                    };
                    String[] emojiLabels = {"/미소", "/슬픔", "/당황", "/쏘쏘", "/행복", "/으액"};

                    boolean isEmojiMessage = false;
                    for (int i = 0; i < emojiLabels.length; i++) {
                        if (message.contains(emojiLabels[i])) {
                            isEmojiMessage = true;
                            String textPart = message.replace(emojiLabels[i], ""); // 텍스트 추출
                            insertImageToChatPane(chatPane, "나: " + textPart, emojiPaths[i], true); // 이미지 삽입
                            break;
                        }
                    }

                    if (!isEmojiMessage) {
                    	insertImageToChatPane(chatPane, "나: " + message, null, true); // 기본 텍스트 처리
                    }

                    out.println("PRIVCHAT " + recipientNickname + " " + message); // 서버로 1:1 메시지 전송
                    chatInputField.setText("");
                    chatInputField.requestFocus();
                }
            });
            
            // 이모티콘 버튼 추가
            JButton emojiButton = new JButton("😄");
            emojiButton.setBackground(Color.WHITE);
            emojiButton.setBorderPainted(false); // 테두리 없애기
            emojiButton.setContentAreaFilled(false); // 버튼 배경 없애기
            emojiButton.setFocusPainted(false); // 포커스 시 테두리 없애기
            JPanel emojiPanel = new JPanel(new GridLayout(1, 6));
            // 이미지 경로 배열
            String[] emojiPaths = {
                "src/main/java/img/smile.png",
                "src/main/java/img/sad.png",
                "src/main/java/img/embarrass.png",
                "src/main/java/img/soso.png",
                "src/main/java/img/good.png",
                "src/main/java/img/eww.png"
            };
            String[] emojiLabels = {"/미소", "/슬픔", "/당황", "/쏘쏘", "/행복", "/으액"};

            // 이모티콘 버튼 클릭 시 가로로 나열된 패널 보이기/숨기기
            emojiButton.addActionListener(e -> {
                emojiPanel.setVisible(!emojiPanel.isVisible());
            });

            // 이모티콘 이미지 추가 및 클릭 시 입력 필드에 삽입
            for (int i = 0; i < emojiPaths.length; i++) {
                String path = emojiPaths[i];
                String emojiLabel = emojiLabels[i];
                try {
                    // 이미지 로드 및 아이콘 크기 조정
                    ImageIcon icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
                    JButton emojiItemButton = new JButton(icon);
                    emojiItemButton.setBorder(BorderFactory.createEmptyBorder());
                    emojiItemButton.setContentAreaFilled(false);
                    
                    // 이모티콘 버튼 클릭 시 해당 이미지가 삽입되는 동작 설정
                    emojiItemButton.addActionListener(event -> {
                        chatInputField.setText(chatInputField.getText() + emojiLabel + " ");
                        chatInputField.requestFocus();
                        emojiPanel.setVisible(false);  // 패널 숨기기
                    });
                    
                    emojiPanel.add(emojiItemButton);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            emojiPanel.setVisible(false);  // 처음에는 보이지 않음

            // 입력 필드와 버튼을 패널에 추가
            JPanel inputButtonPanel = new JPanel(new BorderLayout());
            inputButtonPanel.setBackground(Color.WHITE);
            inputButtonPanel.add(emojiButton, BorderLayout.WEST);  // 이모티콘 버튼을 왼쪽에 배치
            inputButtonPanel.add(sendButton, BorderLayout.EAST);   // 전송 버튼을 오른쪽에 배치
            inputPanel.add(chatInputField, BorderLayout.CENTER);
            inputPanel.add(inputButtonPanel, BorderLayout.EAST);
            inputPanel.add(emojiPanel, BorderLayout.NORTH);  // 이모티콘 패널을 입력 필드 위에 배치

            // 패널을 프레임에 추가
            chatFrame.add(inputPanel, BorderLayout.SOUTH);
            
            // 채팅창 위치 설정: 화면의 2번째 위치의 정가운데
            int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
            int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

            // 2번째 화면의 정가운데 위치 계산
            int x = screenWidth / 2 - chatFrame.getWidth() / 2; // 화면의 중앙
            int y = screenHeight / 4 + (screenHeight / 4) / 2 - chatFrame.getHeight() / 2; // 2번째 화면의 중앙
            chatFrame.setLocation(x, y);
            chatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chatFrame.setVisible(true);

            // 닫힐 때 맵에서 제거
            chatFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    privateChatWindows.remove(recipientNickname);
                }
            });

            // 채팅 창을 맵에 저장
            privateChatWindows.put(recipientNickname, chatFrame);
        } else {
            chatFrame.toFront();  // 이미 열려 있으면 최상단으로
        }
    }
    //검색버튼을 누르면 되는거
    private void addSearchField(JPanel parentPanel, String recipientNickname) {
    	if (searchPanel == null) {
            // 검색 패널이 없으면 생성
            searchPanel = new JPanel(new BorderLayout());
            searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 여백 추가
            searchPanel.setBackground(new Color(238, 238, 238));

            // 검색 필드와 버튼 생성
            JTextField searchField = new JTextField();
            JButton searchButton = new JButton("검색");
            searchField.setFont(customFont2);
            searchButton.setFont(customFont2);
            searchButton.setBackground(selectColor[1]);
            searchButton.setForeground(new Color(0, 11, 88));

            // 버튼 동작
            searchButton.addActionListener(e -> {
                String keyword = searchField.getText().trim();
                if (!keyword.isEmpty()) {
                    searchMessages(nickname, recipientNickname, keyword);
                }
            });

            // 패널에 검색 필드와 버튼 추가
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.EAST);

            // 부모 패널에 검색 패널 추가
            parentPanel.add(searchPanel, BorderLayout.SOUTH);
            parentPanel.revalidate();
            parentPanel.repaint();
        } else {
            // 검색 패널이 이미 있으면 제거
            parentPanel.remove(searchPanel);
            searchPanel = null;
            parentPanel.revalidate(); 
            parentPanel.repaint();
        }
    }
    
    // 메시지를 검색하는 메서드
    private void searchMessages(String sender, String recipient, String keyword) {
        if (keyword.isEmpty()) return;

        // 서버로 검색 요청 전송
        out.println("SEARCH_MESSAGES " + sender + " " + recipient + " " + keyword);
    }
    
    //색상 세트 보여주는 패널
    private void selectColorPanel(JPanel parentPanel, JFrame frame) {
        // colorPanel이 이미 존재하면 제거
        if (colorPanel != null) {
            parentPanel.remove(colorPanel); // 부모 패널에서 제거
            colorPanel = null;             // 참조 초기화
            parentPanel.revalidate();
            parentPanel.repaint();
            return;
        }

        // 색상 세트 배열
        Color[][] colorSets = {  //[0]은 배경쪽, [1]은 버튼쪽, [2]는 보조 버튼쪽임
            {new Color(161, 194, 152), new Color(249, 247, 207), new Color(254, 238, 145)}, // 기본 세트임
            {new Color(255, 230, 230), new Color(190, 174, 226), new Color(247, 219, 240)},       // 세트 2
            {new Color(233, 255, 151), new Color(255, 209, 142), new Color(255, 163, 143)}       // 세트 3
        };

        // 이미지 경로 배열
        String[] colorImagePaths = {
            "src/main/java/img/color1.png",
            "src/main/java/img/color2.png",
            "src/main/java/img/color3.png"
        };

        // colorPanel 생성
        colorPanel = new JPanel();
        colorPanel.setLayout(new GridLayout(1, 3)); // 1행 3열
        colorPanel.setPreferredSize(new Dimension(parentPanel.getWidth(), 30)); // 텍스트 한 줄 크기
        colorPanel.setMaximumSize(new Dimension(parentPanel.getPreferredSize().width, 30));
        colorPanel.setBackground(Color.WHITE);

        // 색상 버튼 추가
        for (int i = 0; i < colorSets.length; i++) {
            int index = i; // 람다에서 참조를 위해 필요
            JButton colorButton = new JButton();
            try {
                // 이미지 로드 및 아이콘 설정
                ImageIcon icon = new ImageIcon(new ImageIcon(colorImagePaths[i]).getImage().getScaledInstance(115, 30, Image.SCALE_SMOOTH));
                colorButton.setIcon(icon);
            } catch (Exception e) {
                e.printStackTrace();
                colorButton.setText("Set " + (i + 1)); // 이미지 로드 실패 시 텍스트 표시
            }
            colorButton.setBorder(BorderFactory.createEmptyBorder());
            colorButton.setBackground(Color.WHITE);
            
            colorButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                	colorButton.setBorderPainted(true);
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                	colorButton.setBorderPainted(false);
                	selectColor = colorSets[index]; // 색상 세트 선택
                    updateParentPanelColor(chatFrame); // 부모 패널 업데이트
                    chatFrame.revalidate();
                    chatFrame.repaint();
                    parentPanel.remove(colorPanel); // 부모 패널에서 제거
                    colorPanel = null;             // 참조 초기화
                    parentPanel.revalidate();
                    parentPanel.repaint();
                }
            });
            

            colorPanel.add(colorButton);
        }

        // parentPanel에 colorPanel 삽입
        parentPanel.add(colorPanel, BorderLayout.SOUTH);
        parentPanel.revalidate();
        parentPanel.repaint();
    }

    //부모 패널 UI 업데이트
    private void updateParentPanelColor(JFrame parentFrame) {
        // 1. parentFrame에서 필요한 특정 컴포넌트를 찾음
        Component[] components = parentFrame.getContentPane().getComponents();

        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;

                // 2. 특정 패널에 색상을 적용
                if ("alignPanel".equals(panel.getName())) {
                    panel.setBackground(selectColor[0]); // 배경 색상
                } else if ("bubblePanel".equals(panel.getName())) {
                    panel.setBackground(selectColor[1]); // 채팅 패널 색상
                }
            } else if (component instanceof JButton) {
                JButton button = (JButton) component;

                // 3. 전송 버튼에만 색상을 적용
                if ("sendButton".equals(button.getName())) {
                    button.setBackground(selectColor[2]); // 전송 버튼 색상
                }
            } else if (component instanceof JTextPane) {
            	JTextPane textPane=(JTextPane) component;
            	if("chatPane".equals(textPane.getName())) {
            		textPane.setBackground(selectColor[0]);
            	}
            }
        }

        // 부모 프레임을 새로 고침
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    
}




 * */
}
