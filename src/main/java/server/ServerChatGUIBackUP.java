package server;

public class ServerChatGUIBackUP {
/*
 * package server;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import GUI.FontLoader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ChatServerGUI {
    private JFrame frame;
    private JTextArea serverLogArea;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private ChatServer chatServer;
    private ServerManage serverManage;

    FontLoader fontLoader = new FontLoader("src/main/java/img/NanumSquareNeoB.ttf", 13f);
    Font customFont = fontLoader.getCustomFont(); // 커스텀 글꼴 가져오기
    
    FontLoader fontLoader1 = new FontLoader("src/main/java/img/돌기마요.ttf", 13f);
    Font customFont1 = fontLoader1.getCustomFont(); // 커스텀 글꼴 가져오기
    
    public ChatServerGUI() {
        chatServer = new ChatServer();
        serverManage = new ServerManage(chatServer);

        frame = new JFrame("서버 관리 페이지");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 800);
        frame.setLocationRelativeTo(null);

        // 상단 패널 - 관리자 기능 버튼들
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new GridLayout(1, 5));

        JButton addUserButton = new JButton("사용자 추가");
        JButton refreshButton = new JButton("유저 새로고침");
        JButton viewUserInfoButton = new JButton("사용자 정보 조회");
        JButton deleteUserButton = new JButton("사용자 삭제");
        JButton restoreUserButton = new JButton("사용자 복원");
        JButton forceLogoutButton = new JButton("접속 강제 종료");
        
        addUserButton.setFont(customFont);
        refreshButton.setFont(customFont);
        viewUserInfoButton.setFont(customFont);
        deleteUserButton.setFont(customFont);
        forceLogoutButton.setFont(customFont);
        restoreUserButton.setFont(customFont);
        addUserButton.setBackground(new Color(153, 188, 133));
        addUserButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(153, 188, 133));
        refreshButton.setForeground(Color.WHITE);
        viewUserInfoButton.setBackground(new Color(153, 188, 133));
        viewUserInfoButton.setForeground(Color.WHITE);
        deleteUserButton.setBackground(new Color(153, 188, 133));
        deleteUserButton.setForeground(Color.WHITE);
        forceLogoutButton.setBackground(new Color(153, 188, 133));
        forceLogoutButton.setForeground(Color.WHITE);
        restoreUserButton.setBackground(new Color(153, 188, 133));
        restoreUserButton.setForeground(Color.WHITE);

        adminPanel.add(addUserButton);
        adminPanel.add(refreshButton);
        adminPanel.add(viewUserInfoButton);
        adminPanel.add(deleteUserButton);
        adminPanel.add(restoreUserButton);
        adminPanel.add(forceLogoutButton);

        // 하단 패널 - 서버 로그
        serverLogArea = new JTextArea();
        serverLogArea.setEditable(false);
        serverLogArea.setFont(customFont);
        serverLogArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // 사용자가 서버 로그 영역을 클릭하면 테이블의 선택 해제
                userTable.clearSelection();
            }
        });
        JScrollPane logScrollPane = new JScrollPane(serverLogArea);

        // 서버 로그에 콘솔 출력 리디렉션
        PrintStream printStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                // 바이트를 문자로 변환하여 JTextArea에 추가
                serverLogArea.append(String.valueOf((char) b));
                serverLogArea.setCaretPosition(serverLogArea.getDocument().getLength());
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                // 바이트 배열을 문자열로 변환하여 JTextArea에 추가
                String str = new String(b, off, len, "UTF-8");
                serverLogArea.append(str);
                serverLogArea.setCaretPosition(serverLogArea.getDocument().getLength());
            }
        });
        System.setOut(printStream);
        System.setErr(printStream);

        // 서버 제어 버튼들
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(225, 240, 218));
        JPanel broadcastPanel = new JPanel();
        JTextField broadcastField = new JTextField(20);
        JButton broadcastButton = new JButton("전송");
        broadcastButton.setBackground(new Color(225, 240, 218));
        
        broadcastButton.setFont(customFont);
        broadcastPanel.add(broadcastField);
        broadcastPanel.add(broadcastButton);
        controlPanel.setLayout(new BorderLayout());
        broadcastPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        controlPanel.add(broadcastPanel, BorderLayout.WEST);
        
        // 오른쪽에 서버 제어 버튼
        JPanel serverControlPanel = new JPanel();
        JButton startServerButton = new JButton("서버 시작");
        JButton stopServerButton = new JButton("서버 종료");
        
        startServerButton.setFont(customFont);
        startServerButton.setBackground(new Color(225, 240, 218));
        stopServerButton.setFont(customFont);
        stopServerButton.setBackground(new Color(225, 240, 218));
        serverControlPanel.add(startServerButton);
        serverControlPanel.add(stopServerButton);
        serverControlPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 150));
        controlPanel.add(serverControlPanel, BorderLayout.CENTER);
        
        broadcastPanel.setBackground(new Color(161, 194, 152));
        serverControlPanel.setBackground(new Color(161, 194, 152));
        
        JButton changeAdminPwButton = new JButton("관리자 비밀번호 변경");
        changeAdminPwButton.setFont(customFont);
        changeAdminPwButton.setBackground(new Color(225, 240, 218));
        JPanel chAdPw = new JPanel();
        chAdPw.add(changeAdminPwButton);
        chAdPw.setBackground(new Color(161, 194, 152));
        controlPanel.add(chAdPw, BorderLayout.EAST);
        changeAdminPwButton.addActionListener(e -> serverManage.changeAdminPw());
        
        broadcastButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = broadcastField.getText().trim();
                if (!message.isEmpty()) {
                    ChatServer.broadcast(message, null); // 모든 사용자에게 메시지 전송
                    broadcastField.setText(""); // 입력창 초기화
                    serverLogArea.append("[관리자] : "+message + "\n");
                    broadcastField.requestFocus();
                }
            }
        });

        // 버튼 리스너 설정
        startServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> chatServer.startServer()).start();
            }
        });
        
        stopServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatServer.stopServer();
            }
        });
        
        // #사용자 추가하기 버튼
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 사용자 추가 팝업을 띄우는 메소드 호출
                serverManage.showRegisterUserPopup();
            }
        });
        
        // #메인화면에 있는 모든 유저 목록 새로고침
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverManage.loadUserInfo(tableModel);
            }
        });
        
        // #특정 사용자 정보 조회 버튼
        viewUserInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow(); // 선택된 행의 인덱스 가져오기

                if (selectedRow == -1) {
                    // 행이 선택되지 않았다면 null을 전송
                    serverManage.showUserInfoPopup(null);
                } else {
                    // 선택된 행의 닉네임 가져오기 (2번 열이 닉네임이라고 가정)
                    String nickname = (String) userTable.getValueAt(selectedRow, 2);
                    serverManage.showUserInfoPopup(nickname); // 닉네임 전달
                }
            }
        });
        
        // #사용자 삭제 버튼
        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow(); // 선택된 행의 인덱스 가져오기

                if (selectedRow == -1) {
                    // 선택된 행이 없으면 null 전달
                    serverManage.deleteUser(null);
                } else {
                    // 선택된 행의 닉네임 가져오기 (2번 열이 닉네임이라고 가정)
                    String nickname = (String) userTable.getValueAt(selectedRow, 2);
                    serverManage.deleteUser(nickname); // 닉네임 전달
                }
            }
        });
        
        //사용자 복원 버튼
        restoreUserButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		serverManage.selectUserBin();
        	}
        });
        
        // #사용자 추방 버튼
        forceLogoutButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		// 사용자 제거 확인 절차 팝업 메소드
        		serverManage.adminUserList();
        	}
        });
        
        

     // 사용자 정보 테이블 설정
        String[] columnNames = {"ID", "PW", "닉네임", "Email", "핸드폰번호", "주소", "생년월일", "캐릭터", "승", "패", "판 수", "성별"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        
        
        TableColumn addressColumn = userTable.getColumn("주소");
        addressColumn.setPreferredWidth(200);
        
        TableColumn birthColumn = userTable.getColumn("생년월일");
        birthColumn.setPreferredWidth(75);
        birthColumn.setMaxWidth(75);
        
        TableColumn charColumn = userTable.getColumn("캐릭터");
        charColumn.setPreferredWidth(60);
        charColumn.setMaxWidth(60);
        
        TableColumn winColumn = userTable.getColumn("승");
        winColumn.setPreferredWidth(45);
        winColumn.setMaxWidth(45);

        TableColumn loseColumn = userTable.getColumn("패");
        loseColumn.setPreferredWidth(45);
        loseColumn.setMaxWidth(45);

        TableColumn playColumn = userTable.getColumn("판 수");
        playColumn.setPreferredWidth(45);
        playColumn.setMaxWidth(45);
        
        TableColumn genderColumn = userTable.getColumn("성별");
        genderColumn.setPreferredWidth(30);
        genderColumn.setMaxWidth(30);

        JScrollPane tableScrollPane = new JScrollPane(userTable);

        frame.setLayout(new BorderLayout());
        
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 클릭한 위치가 리스트 항목 안인지 확인
                int row = userTable.rowAtPoint(e.getPoint());
                
                if (row != -1) {  // 리스트 항목이 있는 위치에서만 실행
                    userTable.setRowSelectionInterval(row, row);  // 클릭한 항목을 선택

                    if (SwingUtilities.isRightMouseButton(e)) { // 우클릭 시 팝업 메뉴 표시
                        String selectedUser = (String) userTable.getValueAt(row, 2);
                        showPopupMenu(e.getX(), e.getY(), selectedUser);
                    }
                }
                else {
                	JOptionPane.showMessageDialog(frame, "사용자를 선택해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // 클릭한 위치가 리스트 항목 안인지 확인
                int row = userTable.rowAtPoint(e.getPoint());
                
                if (row != -1) {  // 리스트 항목이 있는 위치에서만 실행
                    userTable.setRowSelectionInterval(row, row);  // 클릭한 항목을 선택

                    if (SwingUtilities.isRightMouseButton(e)) { // 우클릭 시 팝업 메뉴 표시
                        String selectedUser = (String) userTable.getValueAt(row, 2);
                        showPopupMenu(e.getX(), e.getY(), selectedUser);
                    }
                } else {
                	JOptionPane.showMessageDialog(frame, "사용자를 선택해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // 중앙에 두 개의 행으로 나누는 패널 추가
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // 위쪽 패널(tableScrollPane)을 6에 해당하는 비율로 설정
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        centerPanel.add(tableScrollPane, gbc);

        // 아래쪽 패널(logScrollPane)을 4에 해당하는 비율로 설정
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 9;
        centerPanel.add(logScrollPane, gbc);

        // frame에 centerPanel 추가
        frame.add(centerPanel, BorderLayout.CENTER);

        Dimension logSize = new Dimension(centerPanel.getWidth(), centerPanel.getHeight() * 4 / 10);
        logScrollPane.setPreferredSize(logSize);
        logScrollPane.setMinimumSize(logSize);
        
        // 상단에 관리자 기능 버튼들
        frame.add(adminPanel, BorderLayout.NORTH);
        
        // 하단에 서버 제어 버튼들
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // GUI 실행 시 바로 모든 사용자 정보를 출력
        serverManage.loadUserInfo(tableModel);
    }
    
    private void showPopupMenu(int x, int y, String selectedUser) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem chatHistoryItem = new JMenuItem("채팅 내역");
        chatHistoryItem.setFont(customFont1);  // 글꼴을 customFont1로 설정
        chatHistoryItem.addActionListener(e -> {
            serverManage.chat_History(selectedUser);  // 서버에서 해당 사용자의 채팅 내역을 표시
        });

        popupMenu.add(chatHistoryItem);
        popupMenu.show(userTable, x, y);
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatServerGUI::new);
    }
}

 * */
}
