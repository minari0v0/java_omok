package server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import GUI.FontLoader;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerManage {
	private ChatServer chatServer;
	MessageDbManager dbManager = new MessageDbManager();

    public ServerManage(ChatServer chatServer) {
        this.chatServer = chatServer;
    }
	UserInfoDb userInfoDb = new UserInfoDb();
	private Map<String, Socket> userSockets = new HashMap<>();
	
	// 사용자 소켓 추가
    public void addUserSocket(String nickname, Socket socket) {
        userSockets.put(nickname, socket);
    }

    // 사용자 소켓 제거
    public void removeUserSocket(String nickname) {
        userSockets.remove(nickname);
    }

    // 현재 접속 중인 사용자와 소켓 매핑 반환
    public Map<String, Socket> getUserSockets() {
        return userSockets;
    }
    
    FontLoader fontLoader = new FontLoader("src/main/java/img/NanumSquareNeoB.ttf", 13f);
    FontLoader fontLoader1 = new FontLoader("src/main/java/img/NanumSquareNeoB.ttf", 11f);
    FontLoader fontLoader2 = new FontLoader("src/main/java/img/돌기마요.ttf", 13f);
    Font customFont = fontLoader.getCustomFont(); // 커스텀 글꼴 가져오기
    Font customFont1 = fontLoader1.getCustomFont();
    Font customFont2 = fontLoader2.getCustomFont(); // 커스텀 글꼴 가져오기
    
	public void showRegisterUserPopup() {
	    // 팝업 창 생성
	    JFrame registerFrame = new JFrame("사용자 추가");
	    registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    registerFrame.setSize(400, 300);
	    registerFrame.setLocationRelativeTo(null); // 화면 가운데에 배치

	    // 패널 설정
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());  // GridBagLayout 사용
	    panel.setBackground(new Color(174, 195, 174));
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(5, 5, 5, 5); // 패딩 설정

	    // ID 입력 필드
	    JLabel idLabel = new JLabel("ID:");
	    idLabel.setFont(customFont);
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.anchor = GridBagConstraints.WEST; // 왼쪽 정렬
	    panel.add(idLabel, gbc);

	    JTextField idField = new JTextField(14); // 필드 크기 조정
	    idField.setFont(customFont);
	    idField.setPreferredSize(new Dimension(200, 30)); // Y축 크기 조정
	    gbc.gridx = 1;
	    panel.add(idField, gbc);

	    // 비밀번호 입력 필드
	    JLabel passwordLabel = new JLabel("비밀번호:");
	    passwordLabel.setFont(customFont);
	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    panel.add(passwordLabel, gbc);

	    JTextField passwordField = new JPasswordField(15); // 필드 크기 조정
	    passwordField.setPreferredSize(new Dimension(200, 30)); // Y축 크기 조정
	    gbc.gridx = 1;
	    panel.add(passwordField, gbc);

	    // 닉네임 입력 필드
	    JLabel nicknameLabel = new JLabel("닉네임:");
	    nicknameLabel.setFont(customFont);
	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    panel.add(nicknameLabel, gbc);

	    JTextField nicknameField = new JTextField(15); // 필드 크기 조정
	    nicknameField.setPreferredSize(new Dimension(200, 30)); // Y축 크기 조정
	    gbc.gridx = 1;
	    panel.add(nicknameField, gbc);

	    // pick 드롭다운
	    JLabel pickLabel = new JLabel("Pick:");
	    pickLabel.setFont(customFont);
	    gbc.gridx = 0;
	    gbc.gridy = 3;
	    panel.add(pickLabel, gbc);

	    String[] pickOptions = {"치이카와", "하치와레", "우사기", "용사", "모몽가", "밤토리"};
	    JComboBox<String> pickComboBox = new JComboBox<>(pickOptions);
	    pickComboBox.setFont(customFont);
	    gbc.gridx = 1;
	    panel.add(pickComboBox, gbc);

	    // 버튼 패널 생성
	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new FlowLayout()); // 버튼을 가운데 정렬
	    buttonPanel.setBackground(new Color(174, 195, 174));
	    JButton addButton = new JButton("추가하기");
	    JButton cancelButton = new JButton("취소하기");
	    
	    // 버튼 크기 조정
	    addButton.setPreferredSize(new Dimension(100, 30));
	    addButton.setFont(customFont);
	    addButton.setBackground(new Color(244, 246, 255));
	    cancelButton.setPreferredSize(new Dimension(100, 30));
	    cancelButton.setFont(customFont);
	    cancelButton.setBackground(new Color(244, 246, 255));

	    buttonPanel.add(addButton);
	    buttonPanel.add(cancelButton);

	    gbc.gridx = 0;
	    gbc.gridy = 4;
	    gbc.gridwidth = 2; // 버튼 패널이 두 개의 열을 차지하도록 설정
	    gbc.anchor = GridBagConstraints.CENTER; // 가운데 정렬
	    panel.add(buttonPanel, gbc);

	    // 버튼 클릭 이벤트 처리
	    addButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            String id = idField.getText();
	            String password = new String(passwordField.getText());
	            String nickname = nicknameField.getText();
	            int pick = pickComboBox.getSelectedIndex() + 1;

	            String email = "";  // 이메일 비워두기
	            String phone = "";  // 전화번호 비워두기
	            String address = null; // 주소는 null로 설정
	            String birth = null; // 생일은 null로 설정
	            int gender = 0; //성별 기본 0으로 설정하여 무시, 1이면 남자 2면 여자
	            byte[] profileImg = null; // 프로필 이미지도 null로 설정

	            if (id.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
	                JOptionPane.showMessageDialog(registerFrame, "ID, 비밀번호, 닉네임은 필수 입력 항목입니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
	            } else {
	                registerUser(id, password, nickname, email, phone, address, pick, birth, gender, profileImg);
	                registerFrame.dispose(); // 팝업 창 닫기
	            }
	        }
	    });

	    cancelButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            registerFrame.dispose(); // 팝업 창 닫기
	        }
	    });

	    // 패널을 프레임에 추가하고 프레임 보이기
	    registerFrame.add(panel);
	    registerFrame.setVisible(true);
	}

	public void registerUser(String id, String pw, String nickname, String email, String phone, String address, int pick, String birth, int gender, byte[] profileImg) {
	    DBmanager dbManager0 = new DBmanager();
	    String checkDuplicateSql = "SELECT COUNT(*) FROM user WHERE id = ? OR nickname = ?";  // ID 또는 닉네임 중복 체크
	    String insertUserSql = "INSERT INTO user (id, pw, nickname, email, phone, address, pick, birth, gender, profile_img) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (Connection conn = dbManager0.connect();
	         PreparedStatement checkStmt = conn.prepareStatement(checkDuplicateSql)) {

	        checkStmt.setString(1, id);
	        checkStmt.setString(2, nickname);
	        
	        // 중복 검사
	        try (ResultSet rs = checkStmt.executeQuery()) {
	            if (rs.next() && rs.getInt(1) > 0) {
	                // 중복된 ID 또는 닉네임이 있을 경우
	                JOptionPane.showMessageDialog(null, "이미 사용 중인 ID 또는 닉네임입니다!", "중복 오류", JOptionPane.ERROR_MESSAGE);
	            } else {
	                // 중복이 없으면 사용자 등록 진행
	                try (PreparedStatement stmt = conn.prepareStatement(insertUserSql)) {
	                    stmt.setString(1, id);
	                    stmt.setString(2, pw);
	                    stmt.setString(3, nickname);
	                    stmt.setString(4, (email != null && !email.isEmpty()) ? email : ""); // 이메일이 null 또는 비어있으면 빈 문자열
	                    stmt.setString(5, (phone != null && !phone.isEmpty()) ? phone : ""); // 전화번호도 비어있으면 빈 문자열
	                    stmt.setString(6, (address != null && !address.isEmpty()) ? address : ""); // 주소도 비어있으면 빈 문자열
	                    stmt.setInt(7, pick);  // pick은 선택된 값이기 때문에 null 체크는 생략
	                    stmt.setString(8, (birth != null && !birth.isEmpty()) ? birth : ""); // 생일도 비어있으면 빈 문자열
	                    stmt.setInt(9, gender); // 성별도 비어있으면 빈 문자열
	                    stmt.setBytes(10, profileImg); // 프로필 이미지도 비어있으면 null 처리 가능

	                    int rowsAffected = stmt.executeUpdate();
	                    if (rowsAffected > 0) {
	                        JOptionPane.showMessageDialog(null, "사용자 추가 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
	                    } else {
	                        JOptionPane.showMessageDialog(null, "사용자 추가 실패!", "실패", JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	            }
	        }

	    } catch (SQLException e) {
	        JOptionPane.showMessageDialog(null, "오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	    }
	}
    
    // GUI에서 바로 실행해주는 모든 사용자 정보 조회
    public void loadUserInfo(DefaultTableModel tableModel) {
        List<UserInfoS> userInfoList = userInfoDb.getUserInfoAll(); // 모든 사용자 정보를 불러옴
        tableModel.setRowCount(0); // 기존 테이블 내용 초기화

        // 사용자 정보를 테이블에 추가
        for (UserInfoS userInfo : userInfoList) {
            String character = switch (userInfo.getPick()) {
                case 1 -> "치이카와";
                case 2 -> "하치와레";
                case 3 -> "우사기";
                case 4 -> "용사";
                case 5 -> "모몽가";
                case 6 -> "밤토리";
                default -> "알 수 없음";
            };

            String gender = (userInfo.getGender() == 1 ? "남" : (userInfo.getGender() == 2 ? "여" : ""));
            tableModel.addRow(new Object[]{
                userInfo.getId(),
                userInfo.getPw(),
                userInfo.getNickname(),
                userInfo.getEmail(),
                userInfo.getPhone(),
                userInfo.getAddress(),
                userInfo.getBirth(),
                character,
                userInfo.getWin(),
                userInfo.getLose(),
                userInfo.getPlay(),
                gender
            });
        }
    }

    
    // >> 사용자 정보 조회하기 전 닉네임 묻는 함수
    public void showUserInfoPopup(String nick) {
        if (nick == null) {
            // 닉네임을 묻는 팝업 띄우기
            JFrame nicknameFrame = new JFrame("사용자 검색");
            nicknameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            nicknameFrame.setSize(250, 150);
            nicknameFrame.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 0, 0, 0);

            JLabel tipLabel = new JLabel("조회할 사용자의 닉네임을 입력하세요:");
            tipLabel.setFont(customFont);
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(tipLabel, gbc);

            JTextField nicknameField = new JTextField(12);
            nicknameField.setFont(customFont);
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(nicknameField, gbc);

            JButton searchButton = new JButton("검색");
            searchButton.setFont(customFont);
            searchButton.setBackground(new Color(244, 246, 255));
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(searchButton, gbc);

            searchButton.addActionListener(e -> {
                String inputNickname = nicknameField.getText().trim();
                if (inputNickname.isEmpty()) {
                    JOptionPane.showMessageDialog(nicknameFrame, "닉네임을 입력해 주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 닉네임으로 uid 조회
                Integer uid = userInfoDb.getUidByNick(inputNickname); // UserInfoDb의 메소드 호출
                if (uid == null) {
                    JOptionPane.showMessageDialog(nicknameFrame, "해당 사용자를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // uid를 통해 사용자 정보 팝업 실행
                showUserInfoPopup(uid);
                nicknameFrame.dispose(); // 팝업 닫기
            });

            nicknameFrame.add(panel);
            nicknameFrame.setVisible(true);

        } else {
            // 닉네임으로 uid 조회
            Integer uid = userInfoDb.getUidByNick(nick); // UserInfoDb의 메소드 호출
            if (uid == null) {
                JOptionPane.showMessageDialog(null, "해당 사용자를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // uid로 사용자 정보 팝업 실행
            showUserInfoPopup(uid);
        }
    }
    
    // >> 사용자 정보 조회하기 및 수정 함수
    private void showUserInfoPopup(int uid) {
        // DB에서 사용자 정보 조회
        UserInfoDb userInfoDb = new UserInfoDb(); // UserInfoDb 인스턴스 생성
        UserInfoS userInfo = userInfoDb.getUserInfoAll(uid); // uid에 해당하는 사용자 정보 가져오기

        if (userInfo == null) {
            JOptionPane.showMessageDialog(null, "사용자 정보를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 팝업 창 생성
        JFrame userInfoFrame = new JFrame("사용자 정보 조회");
        userInfoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        userInfoFrame.setSize(400, 800);
        userInfoFrame.setLocationRelativeTo(null); // 화면 가운데에 배치
        Point location = userInfoFrame.getLocation();
        userInfoFrame.setLocation(location.x + 735, location.y);

        // 패널 설정
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(174, 195, 174));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 패딩 설정
        
     // 프로필 이미지 라벨
        JLabel profileImgLabel = new JLabel();

        // 프로필 이미지 데이터가 null이 아니면 해당 이미지를, 아니면 기본 이미지를 로드
        ImageIcon profileImgIcon = (userInfo.getProfileImg() != null) 
            ? new ImageIcon(userInfo.getProfileImg())
            : new ImageIcon("src/main/java/img/default_proImg.png");

        // 아이콘 크기 조정 (100x100으로 설정)
        profileImgIcon.setImage(profileImgIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));

        // 프로필 이미지 라벨에 아이콘 설정
        profileImgLabel.setIcon(profileImgIcon);
        profileImgLabel.setHorizontalAlignment(JLabel.CENTER);

        // 프로필 이미지를 그리드의 (0, 0)에 배치
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 0;  // 2개의 컬럼을 차지하도록 설정
        gbc.anchor = GridBagConstraints.CENTER;  // 중앙 정렬
        gbc.insets = new Insets(10, 10, 10, 10); // 여백 설정
        panel.add(profileImgLabel, gbc);
        gbc.gridwidth = 1;

        // ID 입력 필드
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(customFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(idLabel, gbc);

        JTextField idField = new JTextField(15);
        idField.setFont(customFont);
        idField.setText(userInfo.getId());
        idField.setEditable(false); // ID는 수정 불가
        gbc.gridx = 1;
        panel.add(idField, gbc);

        // PW 입력 필드
        JLabel pwLabel = new JLabel("PW:");
        pwLabel.setFont(customFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(pwLabel, gbc);

        JTextField pwField = new JTextField(15);
        pwField.setFont(customFont);
        pwField.setText(userInfo.getPw());
        pwField.setEditable(false); // PW는 수정 불가
        gbc.gridx = 1;
        panel.add(pwField, gbc);

        // Nickname 입력 필드
        JLabel nicknameLabel = new JLabel("닉네임:");
        nicknameLabel.setFont(customFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(nicknameLabel, gbc);

        JTextField nicknameField = new JTextField(15);
        nicknameField.setFont(customFont);
        nicknameField.setText(userInfo.getNickname());
        gbc.gridx = 1;
        panel.add(nicknameField, gbc);

        // Email 입력 필드
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(customFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(15);
        emailField.setFont(customFont);
        emailField.setText(userInfo.getEmail());
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Phone 입력 필드
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(customFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(phoneLabel, gbc);

        JTextField phoneField = new JTextField(15);
        phoneField.setFont(customFont);
        phoneField.setText(userInfo.getPhone());
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        // Address 입력 필드
        JLabel addressLabel = new JLabel("주소:");
        addressLabel.setFont(customFont);
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(addressLabel, gbc);

        JTextField addressField = new JTextField(15);
        addressField.setFont(customFont);
        addressField.setText(userInfo.getAddress());
        gbc.gridx = 1;
        panel.add(addressField, gbc);
        
        // 생일 입력 필드
        JLabel birthLabel = new JLabel("생일:");
        birthLabel.setFont(customFont);
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(birthLabel, gbc);

        JTextField birthField = new JTextField(15);
        birthField.setFont(customFont);
        birthField.setText(userInfo.getBirth());
        gbc.gridx = 1;
        panel.add(birthField, gbc);

        // Pick 드롭다운
        JLabel pickLabel = new JLabel("Pick:");
        pickLabel.setFont(customFont);
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(pickLabel, gbc);

        String[] pickOptions = {"치이카와", "하치와레", "우사기", "용사", "모몽가", "밤토리"};
	    JComboBox<String> pickComboBox = new JComboBox<>(pickOptions);
	    pickComboBox.setFont(customFont);
	    pickComboBox.setSelectedIndex(userInfo.getPick() - 1);
	    gbc.gridx = 1;
	    panel.add(pickComboBox, gbc);

        // Win, Lose, Play 입력 필드
        JLabel winLabel = new JLabel("승 수:");
        winLabel.setFont(customFont);
        gbc.gridx = 0;
        gbc.gridy = 9;
        panel.add(winLabel, gbc);

        JTextField winField = new JTextField(5);
        winField.setFont(customFont);
        winField.setText(String.valueOf(userInfo.getWin()));
        gbc.gridx = 1;
        panel.add(winField, gbc);

        JLabel loseLabel = new JLabel("패 수:");
        loseLabel.setFont(customFont);
        gbc.gridx = 0;
        gbc.gridy = 10;
        panel.add(loseLabel, gbc);

        JTextField loseField = new JTextField(5);
        loseField.setFont(customFont);
        loseField.setText(String.valueOf(userInfo.getLose()));
        gbc.gridx = 1;
        panel.add(loseField, gbc);

        JLabel playLabel = new JLabel("플레이 수:");
        playLabel.setFont(customFont);
        gbc.gridx = 0;
        gbc.gridy = 11;
        panel.add(playLabel, gbc);

        JTextField playField = new JTextField(5);
        playField.setFont(customFont);
        playField.setText(String.valueOf(userInfo.getPlay()));
        gbc.gridx = 1;
        panel.add(playField, gbc);

        // 성별 라디오 버튼
        JLabel genderLabel = new JLabel("성별:");
        genderLabel.setFont(customFont);
        gbc.gridx = 0;
        gbc.gridy = 12;
        panel.add(genderLabel, gbc);

        JRadioButton maleButton = new JRadioButton("남자");
        JRadioButton femaleButton = new JRadioButton("여자");
        ButtonGroup genderGroup = new ButtonGroup();
        maleButton.setBackground(new Color(174, 195, 174));
        femaleButton.setBackground(new Color(174, 195, 174));
        maleButton.setFont(customFont);
        femaleButton.setFont(customFont);
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        

        if (userInfo.getGender() == 1) {
            maleButton.setSelected(true);
        } else if (userInfo.getGender() == 2) {
            femaleButton.setSelected(true);
        }

        JPanel genderPanel = new JPanel();
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        genderPanel.setBackground(new Color(174, 195, 174));
        gbc.gridx = 1;
        panel.add(genderPanel, gbc);
        
        final Integer[] gender = new Integer[1]; // 포장객체 배열로 초기화
        // 수정하기 버튼
        JButton updateButton = new JButton("수정하기");
        updateButton.setBackground(new Color(244, 246, 255));
        updateButton.setFont(customFont);
        JButton backButton = new JButton("돌아가기");
        backButton.setBackground(new Color(244, 246, 255));
        backButton.setFont(customFont);

        // 수정하기 버튼 클릭 이벤트
        updateButton.addActionListener(e -> {
        	try {
	        	if (maleButton.isSelected()) {
	                gender[0] = 1; // 남자
	            } else if (femaleButton.isSelected()) {
	                gender[0] = 2; // 여자
	            } else {
	                gender[0] = null; // 선택되지 않음
	            }
	            // 사용자 정보 업데이트
	        	 String email = emailField.getText().trim();
	             String phone = phoneField.getText().trim();
	             String address = addressField.getText().trim();
	             String birth = birthField.getText().trim();
	             int pick = pickComboBox.getSelectedIndex() + 1;
	        	
	             userInfoDb.updateUserInfo(uid, idField.getText(), pwField.getText(), 
	                     nicknameField.getText(), 
	                     email.isEmpty() ? "" : email, // 빈 칸이면 빈 문자열
	                     phone.isEmpty() ? "" : phone, // 빈 칸이면 빈 문자열
	                     address.isEmpty() ? "" : address, // 빈 칸이면 빈 문자열
	                     birth.isEmpty() ? null : birth, // 빈 칸이면 null
	                     pick,
	                     Integer.parseInt(winField.getText()), 
	                     Integer.parseInt(loseField.getText()), 
	                     Integer.parseInt(playField.getText()), 
	                     gender[0]);
	            
	            JOptionPane.showMessageDialog(userInfoFrame, "사용자 정보가 수정되었습니다!", "완료", JOptionPane.INFORMATION_MESSAGE);
	            userInfoFrame.dispose(); // 팝업 닫기
        } catch (Exception ex) {
            // 예외 발생 시 오류 메시지 표시
        	ex.printStackTrace();
            JOptionPane.showMessageDialog(userInfoFrame, "정보 수정에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    });

        // 돌아가기 버튼 클릭 이벤트
        backButton.addActionListener(e -> userInfoFrame.dispose());
        
        // 버튼 패널 추가
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);
        buttonPanel.add(backButton);
        buttonPanel.setBackground(new Color(174, 195, 174));

        // 팝업 창에 패널 추가
        userInfoFrame.add(panel, BorderLayout.CENTER);
        userInfoFrame.add(buttonPanel, BorderLayout.SOUTH);
        userInfoFrame.setVisible(true);

        // 팝업 창에 패널 추가
        userInfoFrame.add(panel);
        userInfoFrame.setVisible(true);
    }
    
    // 사용자 제거 하기 전 닉네임을 물음
    public void deleteUser(String nick) {
        if (nick == null) {
            // 닉네임을 묻는 팝업 띄우기
            JFrame nicknameFrame = new JFrame("사용자 검색");
            nicknameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            nicknameFrame.setSize(250, 150);
            nicknameFrame.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 0, 0, 0);

            JLabel tipLabel = new JLabel("삭제할 사용자 닉네임을 입력하세요:");
            tipLabel.setFont(customFont);
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(tipLabel, gbc);

            JTextField nicknameField = new JTextField(12);
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(nicknameField, gbc);

            JButton searchButton = new JButton("삭제");
            searchButton.setFont(customFont);
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(searchButton, gbc);

            searchButton.addActionListener(e -> {
                String inputNickname = nicknameField.getText().trim();
                if (inputNickname.isEmpty()) {
                    JOptionPane.showMessageDialog(nicknameFrame, "닉네임을 입력해 주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 닉네임으로 uid 조회
                UserInfoDb userInfoDb = new UserInfoDb();
                Integer uid = userInfoDb.getUidByNick(inputNickname);

                if (uid == null) {
                    JOptionPane.showMessageDialog(nicknameFrame, "해당 사용자를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 삭제 실행
                deleteUser(uid, inputNickname);
                nicknameFrame.dispose(); // 팝업 닫기
            });

            nicknameFrame.add(panel);
            nicknameFrame.setVisible(true);

        } else {
            // 닉네임으로 uid 조회
            UserInfoDb userInfoDb = new UserInfoDb();
            Integer uid = userInfoDb.getUidByNick(nick);

            if (uid == null) {
                JOptionPane.showMessageDialog(null, "해당 사용자를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 삭제 실행
            deleteUser(uid, nick);
        }
    }
    
    //사용자를 제거하는 함수
    public void deleteUser(int uid, String nickname) {
        // 삭제 확인 팝업창 생성
        int confirm = JOptionPane.showConfirmDialog(null, "정말로 "+nickname + "님을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // 확인 버튼 클릭 시 사용자 삭제
            UserInfoDb userInfoDb = new UserInfoDb();
            userInfoDb.moveUserToBin(uid);
        }
        // 취소 버튼 클릭 시 아무 동작도 하지 않음 (팝업 자동 닫힘)
    }
    
    public void adminUserList() {
        // 팝업창 생성
        JFrame adminUserFrame = new JFrame("유저 강제 추방");
        adminUserFrame.setSize(400, 800);
        adminUserFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminUserFrame.setLocationRelativeTo(null);
        Point location = adminUserFrame.getLocation(); // userInfoFrame 위치 가져오기
        adminUserFrame.setLocation(location.x + 735, location.y);

        // 배경 색상 설정
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(174, 195, 174));

        // 유저 목록 표시를 위한 JTable 생성
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"접속 중인 유저"}, 0){
			private static final long serialVersionUID = 1L;
			@Override
            public boolean isCellEditable(int row, int column) {
                return false; // 모든 셀을 수정 불가능하게 설정
            }
        };
        JTable userTable = new JTable(tableModel);
        userTable.setBackground(new Color(244, 246, 255));
        userTable.getTableHeader().setFont(customFont1);
        userTable.setFont(customFont1);

        // 현재 접속 중인 유저 리스트 요청 및 업데이트
        updateUserList(tableModel);

        // 스크롤 추가
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBackground(new Color(174, 195, 174));
        userTable.setBackground(new Color(244, 246, 255));
        panel.add(scrollPane, BorderLayout.CENTER);
        // 공백 눌렀을 때 선택해제
        scrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = userTable.rowAtPoint(e.getPoint()); // 클릭한 위치의 행 확인
                if (row == -1) { // 공백 클릭 시
                    userTable.clearSelection(); // 선택 해제
                }
            }
        });

        // 버튼 패널 생성
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(174, 195, 174));

        // "추방하기" 버튼
        JButton kickButton = new JButton("추방하기");
        kickButton.setBackground(new Color(244, 246, 255));
        kickButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                String selectedNickname = (String) tableModel.getValueAt(selectedRow, 0);

                // "현재 접속 중인 유저가 없습니다" 메시지가 선택된 경우 추방을 시도하지 않도록 처리
                if (!selectedNickname.equals("현재 접속 중인 유저가 없습니다")) {
                    int confirm = JOptionPane.showConfirmDialog(
                        null,
                        selectedNickname + "님을 추방하시겠습니까?",
                        "추방 확인",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        // 선택된 닉네임으로 클라이언트 추방
                        kickUser(selectedNickname); // 추방 메서드 호출
                        tableModel.removeRow(selectedRow); // 테이블에서 해당 유저 제거
                    }
                }
            } else {
                // 아무것도 선택되지 않으면 기존 kickUser() 호출
                kickUser(adminUserFrame);
            }
        });

        // "돌아가기" 버튼
        JButton cancelButton = new JButton("돌아가기");
        cancelButton.setBackground(new Color(244, 246, 255));
        cancelButton.addActionListener(e -> adminUserFrame.dispose());

        // 새로고침 버튼
        JButton refreshButton = new JButton("🔃");
        refreshButton.setBackground(new Color(244, 246, 255)); // 추방하기 버튼과 동일한 색상
        refreshButton.addActionListener(e -> {
            // 새로고침 버튼 클릭 시 유저 목록을 다시 갱신
            tableModel.setRowCount(0); // 기존 테이블 내용 초기화
            updateUserList(tableModel);
        });

        // 버튼 패널에 버튼 추가
        buttonPanel.add(kickButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton); // 새로고침 버튼 추가

        // 버튼 패널을 화면 하단에 배치
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 팝업창 구성 및 표시
        adminUserFrame.add(panel);
        adminUserFrame.setVisible(true);

        // JTable 더블 클릭 이벤트 처리
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedNickname = (String) tableModel.getValueAt(selectedRow, 0);

                    // "현재 접속 중인 유저가 없습니다" 메시지일 때 더블클릭을 무시
                    if (!selectedNickname.equals("현재 접속 중인 유저가 없습니다")) {
                        if (e.getClickCount() == 2) { // 더블 클릭
                            int confirm = JOptionPane.showConfirmDialog(
                                null,
                                selectedNickname + "님을 추방하시겠습니까?",
                                "추방 확인",
                                JOptionPane.YES_NO_OPTION
                            );

                            if (confirm == JOptionPane.YES_OPTION) {
                                kickUser(selectedNickname); // 추방 메서드 호출
                                tableModel.removeRow(selectedRow); // 테이블에서 해당 유저 제거
                            }
                        }
                    }
                }
            }
        });
    }
    
    // 유저 목록 업데이트메소드 바로 위 adminUserList 꺼임
    private void updateUserList(DefaultTableModel tableModel) {
    	tableModel.setRowCount(0);
        StringBuilder userList = chatServer.serverUserList(); // serverUserList 사용
        String[] users = userList.toString().split(",");

        if (users.length == 1 && users[0].isEmpty()) {
            // 유저 목록이 비어있으면 "현재 접속 중인 유저가 없습니다" 메시지 추가
            tableModel.addRow(new Object[]{"현재 접속 중인 유저가 없습니다"});
        } else {
            for (String user : users) {
                tableModel.addRow(new Object[]{user});
            }
        }
    }


    
    // >> 사용자 강제 종료를 위한 메소드(아무 행도 클릭 안했을 때)
    private void kickUser(JFrame adminUserFrame) {
        String nickname = JOptionPane.showInputDialog(adminUserFrame, "추방할 유저의 닉네임을 입력하세요:");
        if (nickname == null || nickname.trim().isEmpty()) {
            JOptionPane.showMessageDialog(adminUserFrame, "닉네임을 입력하세요!", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        kickUser(nickname);
    }
    
    
    // 사용자를 강제로 추방하는 메소드
    public void kickUser(String nickname) {
    	Map<String, Socket> userSockets = ChatServer.getUserSockets(); // 최신 소켓 맵 가져오기
        Socket socketToKick = userSockets.get(nickname); // 해당 닉네임의 소켓 찾기

        if (socketToKick != null) {
            try {
                // 소켓을 통해 클라이언트에게 종료 메시지를 전송
                PrintWriter out = new PrintWriter(socketToKick.getOutputStream(), true);
                out.println("Server >> 서버로부터 추방되었습니다.");
                // 클라이언트 연결 종료
                socketToKick.close();
                System.out.println("<관리자> " + nickname + "님이 추방되었습니다.");
                removeUserSocket(nickname); // 소켓 제거
            } catch (IOException e) {
                System.err.println("강퇴 중 오류 발생: " + e.getMessage());
            }
        } else {
            System.out.println("<서버> " + nickname + "님은 현재 접속 중이지 않습니다.");
        }
    }
    
    // 채팅 내역 검색창 메소드
    public void chat_History(String nickname) {
        // 채팅 내역 팝업창 생성
        JFrame chatHistoryFrame = new JFrame(nickname + "님의 채팅 내역");
        chatHistoryFrame.setSize(400, 800);
        chatHistoryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chatHistoryFrame.setLocationRelativeTo(null);
        Point location = chatHistoryFrame.getLocation(); // userInfoFrame 위치 가져오기
        chatHistoryFrame.setLocation(location.x + 735, location.y);

        // 팝업창 배경색 설정
        chatHistoryFrame.getContentPane().setBackground(new Color(174, 195, 174));

        // 제목 레이블 생성
        JLabel titleLabel = new JLabel(nickname + "님의 채팅 내역");
        JPanel chatPanel = new JPanel();
        titleLabel.setFont(customFont); // 제목 글꼴 설정
        titleLabel.setBorder(new EmptyBorder(0, 75, 0, 0));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(174, 195, 174));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        JCheckBox selectAllCheckBox = new JCheckBox("전체 선택");
        selectAllCheckBox.setFont(customFont1);
        selectAllCheckBox.setBackground(new Color(174, 195, 174));
        selectAllCheckBox.addActionListener(e -> {
            // 전체 선택/해제 기능 구현
            Component[] components = chatPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    checkBox.setSelected(selectAllCheckBox.isSelected());
                }
            }
        });
        titlePanel.add(selectAllCheckBox, BorderLayout.WEST);
        chatHistoryFrame.add(titlePanel, BorderLayout.NORTH);

        // MessageDbManager를 이용해 채팅 내역 가져오기
        List<String> chatMessages = dbManager.searchMessage(nickname); // 해당 유저의 메시지 리스트 얻어오기

        // 채팅 내역 패널 레이아웃 설정하기
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(239, 234, 216));

        if (chatMessages == null || chatMessages.isEmpty()) {
            // 메시지가 없으면 "해당 유저는 메시지 내역이 없습니다!" 메시지 출력
            JLabel noMessagesLabel = new JLabel("해당 유저는 메시지 내역이 없습니다!");
            noMessagesLabel.setFont(customFont2);
            chatPanel.add(noMessagesLabel);
        } else {
            // 메시지가 있으면 각 메시지를 체크박스로 표시
            for (String message : chatMessages) {
                // 메시지에서 ID를 분리
                String[] parts = message.split(" / "); // "ID: %d / 발신자: %s / 수신자: %s / 메시지: %s / 날짜: %s" 형식에 맞춰 분리
                String formattedMessage = parts[1] + " / " + parts[2] + " / " + parts[3] + " / " + parts[4]; // ID를 제외한 메시지 부분

                JCheckBox messageCheckBox = new JCheckBox(formattedMessage); // 포맷된 메시지 사용
                messageCheckBox.setFont(customFont2);
                messageCheckBox.setBackground(new Color(239, 234, 216));
                
                // ID를 액션 커맨드로 설정
                messageCheckBox.setActionCommand(parts[0].split(": ")[1]); // "ID: %d"에서 ID만 추출하여 설정

                chatPanel.add(messageCheckBox);
            }
        }

        // 채팅 내역을 스크롤 가능하게 추가
        chatHistoryFrame.add(new JScrollPane(chatPanel), BorderLayout.CENTER);

        // 삭제 및 휴지통 버튼 추가
        JPanel buttonPanel = new JPanel(new BorderLayout()); // BorderLayout으로 설정
        buttonPanel.setBackground(new Color(174, 195, 174));

        // 왼쪽 패널에 삭제하기, 휴지통 버튼 추가
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(174, 195, 174));
        JButton deleteButton = new JButton("삭제하기");
        deleteButton.setBackground(new Color(244, 246, 255)); // 버튼 색상 설정
        deleteButton.setFont(customFont);
        JButton trashButton = new JButton("휴지통");
        trashButton.setBackground(new Color(244, 246, 255)); // 버튼 색상 설정
        trashButton.setFont(customFont);
        leftPanel.add(deleteButton);
        leftPanel.add(trashButton);

        // 삭제하기 버튼 리스너
        deleteButton.addActionListener(e -> {
            // 체크된 메시지 ID를 추출하여 List에 저장
            List<String> selectedMessageIds = new ArrayList<>();
            Component[] components = chatPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    if (checkBox.isSelected()) {
                        // 체크된 메시지의 ID 가져오기
                        String messageId = checkBox.getActionCommand(); // ID는 체크박스의 액션 커맨드로 설정됨
                        selectedMessageIds.add(messageId);
                    }
                }
            }
            
            if (selectedMessageIds.isEmpty()) {
                // 선택된 메시지가 없으면 팝업으로 알림
                JOptionPane.showMessageDialog(chatHistoryFrame, "삭제할 메시지를 선택해주세요.");
            } else {
                // 선택된 메시지들 삭제 처리
                for (String messageId : selectedMessageIds) {
                    // 선택된 메시지의 ID를 chat_messages_bin 테이블로 옮기기
                    dbManager.moveMessageToBin(messageId);
                }

                // 성공 메시지 팝업
                JOptionPane.showMessageDialog(chatHistoryFrame, "선택된 메시지가 삭제되었습니다!");
                
                // 삭제된 메시지 목록 새로고침
                chatHistoryFrame.dispose();  // 기존 채팅 내역 창을 닫고
                chat_History(nickname);  // 채팅 내역을 다시 로드하여 창을 새로 띄움
            }
        });

        // 휴지통 버튼 리스너 (binFrame 호출)
        trashButton.addActionListener(e -> {
            binFrame(chatHistoryFrame, nickname);  // 휴지통 버튼 클릭 시 binFrame 메소드 호출
        });

        // 왼쪽 패널을 buttonPanel의 WEST에 추가
        buttonPanel.add(leftPanel, BorderLayout.WEST);

        // 오른쪽 패널에 닫기 버튼 추가
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 오른쪽 패널 생성
        rightPanel.setBackground(new Color(174, 195, 174));
        JButton closeButton = new JButton("닫기");
        closeButton.setBackground(new Color(244, 246, 255)); // 닫기 버튼 색상 설정
        closeButton.setFont(customFont);
        closeButton.addActionListener(e -> chatHistoryFrame.dispose()); // 클릭 시 팝업창 닫기
        rightPanel.add(closeButton); // 오른쪽 패널에 닫기 버튼 추가

        // 오른쪽 패널을 buttonPanel의 EAST에 추가
        buttonPanel.add(rightPanel, BorderLayout.EAST);

        // 버튼 패널을 팝업창에 추가
        chatHistoryFrame.add(buttonPanel, BorderLayout.SOUTH);

        // 팝업창을 화면에 표시
        chatHistoryFrame.setVisible(true);
    }
    
    //휴지통 누르면 새로 화면 갱신되게하는 함수
    public void binFrame(JFrame chatHistoryFrame, String nickname) {
        // 새로운 패널로 binFrame을 표시할 수 있도록 설정
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(239, 234, 216));

        // MessageDbManager를 이용해 chat_messages_bin 테이블에서 메시지 가져오기
        List<String> chatMessages = dbManager.searchBinMessages(nickname); // 삭제된 메시지들 가져오기

        if (chatMessages == null || chatMessages.isEmpty()) {
            // 메시지가 없으면 "휴지통에 메시지가 없습니다!" 메시지 출력
            JLabel noMessagesLabel = new JLabel("휴지통에 메시지가 없습니다!");
            noMessagesLabel.setFont(customFont2);
            chatPanel.add(noMessagesLabel);
        } else {
            // 메시지가 있으면 각 메시지를 체크박스로 표시
            for (String message : chatMessages) {
                // 메시지에서 ID를 분리
                String[] parts = message.split(" / "); // "ID: %d / 발신자: %s / 수신자: %s / 메시지: %s / 날짜: %s" 형식에 맞춰 분리
                String formattedMessage = parts[1] + " / " + parts[2] + " / " + parts[3] + " / " + parts[4]; // ID를 제외한 메시지 부분

                JCheckBox messageCheckBox = new JCheckBox(formattedMessage); // 포맷된 메시지 사용
                messageCheckBox.setFont(customFont2);
                messageCheckBox.setBackground(new Color(239, 234, 216));
                // ID를 액션 커맨드로 설정
                messageCheckBox.setActionCommand(parts[0].split(": ")[1]); // "ID: %d"에서 ID만 추출하여 설정

                chatPanel.add(messageCheckBox);
            }
        }

        // 전체 선택 체크박스 추가
        JCheckBox selectAllCheckBox = new JCheckBox("전체 선택");
        selectAllCheckBox.setFont(customFont1);
        selectAllCheckBox.setBackground(new Color(174, 195, 174));
        selectAllCheckBox.addActionListener(e -> {
            // 전체 선택/해제 기능 구현
            Component[] components = chatPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    checkBox.setSelected(selectAllCheckBox.isSelected());
                }
            }
        });

        // 제목 레이블 생성
        JLabel titleLabel = new JLabel(nickname + "님의 휴지통");
        JPanel titlePanel = new JPanel(new BorderLayout());
        titleLabel.setFont(customFont); // 제목 글꼴 설정
        titleLabel.setBorder(new EmptyBorder(0, 75, 0, 0));
        titlePanel.setBackground(new Color(174, 195, 174));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(selectAllCheckBox, BorderLayout.WEST);

        // 기존 chatHistoryFrame의 컨텐츠를 갱신
        chatHistoryFrame.getContentPane().removeAll(); // 기존의 내용 삭제
        chatHistoryFrame.add(titlePanel, BorderLayout.NORTH);
        chatHistoryFrame.add(new JScrollPane(chatPanel), BorderLayout.CENTER); // 새로 업데이트된 패널 추가

        // '복원하기' 버튼 추가
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(174, 195, 174));

        // 왼쪽 패널에 복원하기 버튼 추가
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(174, 195, 174));
        JButton restoreButton = new JButton("복원하기");
        restoreButton.setBackground(new Color(244, 246, 255));
        restoreButton.setFont(customFont);
        leftPanel.add(restoreButton);

        // 복원하기 버튼 리스너
        restoreButton.addActionListener(e -> {
            // 체크된 메시지 ID를 추출하여 List에 저장
            List<String> selectedMessageIds = new ArrayList<>();
            Component[] components = chatPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    if (checkBox.isSelected()) {
                        // 체크된 메시지의 ID 가져오기
                        String messageId = checkBox.getActionCommand(); // ID는 체크박스의 액션 커맨드로 설정됨
                        selectedMessageIds.add(messageId);
                    }
                }
            }

            if (selectedMessageIds.isEmpty()) {
                JOptionPane.showMessageDialog(chatHistoryFrame, "복원할 메시지를 선택해주세요.");
            } else {
                for (String messageId : selectedMessageIds) {
                    // 선택된 메시지 복원 처리
                    dbManager.restoreMessageFromBin(messageId);
                }
                JOptionPane.showMessageDialog(chatHistoryFrame, "선택된 메시지가 복원 되었습니다!", "알림", JOptionPane.INFORMATION_MESSAGE);
                // 복원 후 메시지 목록 새로고침
                binFrame(chatHistoryFrame, nickname); // 휴지통 화면으로 돌아가기
            }
        });

        // 왼쪽 패널을 buttonPanel의 WEST에 추가
        buttonPanel.add(leftPanel, BorderLayout.WEST);

        // 오른쪽 패널에 돌아가기 버튼 추가
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(174, 195, 174));
        JButton backButton = new JButton("돌아가기");
        backButton.setBackground(new Color(244, 246, 255));
        backButton.setFont(customFont);
        backButton.addActionListener(e -> {
            // 돌아가기 버튼을 누르면 채팅 내역을 다시 로드
            chatHistoryFrame.dispose();
            chat_History(nickname); // 원래 채팅 내역 화면으로 돌아가기
        });
        rightPanel.add(backButton);

        // 오른쪽 패널을 buttonPanel의 EAST에 추가
        buttonPanel.add(rightPanel, BorderLayout.EAST);

        // 버튼 패널을 팝업창에 추가
        chatHistoryFrame.add(buttonPanel, BorderLayout.SOUTH);

        // 갱신된 내용으로 chatHistoryFrame을 화면에 표시
        chatHistoryFrame.revalidate();
        chatHistoryFrame.repaint();
    }
    
    //사용자 복원 페이지 띄우기
    public void selectUserBin() {
        // 프레임 생성
        JFrame binFrame = new JFrame("삭제된 사용자");
        binFrame.setSize(400, 800);
        binFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        binFrame.setLocationRelativeTo(null);
        Point location = binFrame.getLocation();
        binFrame.setLocation(location.x + 735, location.y);

        // 배경 색상 설정
        binFrame.getContentPane().setBackground(new Color(174, 195, 174));

     // 제목 레이블 생성
        JLabel titleLabel = new JLabel("삭제된 유저 목록");
        titleLabel.setFont(customFont);
        titleLabel.setBorder(new EmptyBorder(0, 80, 0, 0)); // 제목 위치 조정

        // "전체 선택" 체크박스 생성
        JCheckBox selectAllCheckBox = new JCheckBox("전체 선택");
        selectAllCheckBox.setFont(customFont1);
        selectAllCheckBox.setBackground(new Color(174, 195, 174));

        // 제목 패널 생성 및 구성
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(174, 195, 174));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(selectAllCheckBox, BorderLayout.WEST);

        // 타이틀 패널을 프레임에 추가
        binFrame.add(titlePanel, BorderLayout.NORTH);

        // 삭제된 사용자 목록 패널 생성
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(new Color(239, 234, 216));

        // UserInfoDb에서 삭제된 사용자 목록 가져오기
        List<String> deletedUsers = userInfoDb.searchBinUser(); // uid, id, nickname, inserted_at 정보

        if (deletedUsers == null || deletedUsers.isEmpty()) {
            // 삭제된 사용자가 없으면 안내 메시지 표시
            JLabel noUsersLabel = new JLabel("삭제된 유저가 없습니다!");
            noUsersLabel.setFont(customFont2);
            userPanel.add(noUsersLabel);
        } else {
            // 삭제된 사용자 정보를 체크박스로 표시
            for (String user : deletedUsers) {
                String[] parts = user.split(" / "); // "UID: %d / ID: %s / 닉네임: %s / 삭제된 시간: %s" 형식
                String formattedUser = parts[1] + " / " + parts[2] + " / " + parts[3]; // UID 제외한 정보 표시

                JCheckBox userCheckBox = new JCheckBox(formattedUser);
                userCheckBox.setFont(customFont2);
                userCheckBox.setBackground(new Color(239, 234, 216));

                // UID를 액션 커맨드로 설정
                userCheckBox.setActionCommand(parts[0].split(": ")[1]); // "UID: %d"에서 UID만 추출하여 설정
                userPanel.add(userCheckBox);
            }
        }

        // "전체 선택" 체크박스 동작 구현
        selectAllCheckBox.addActionListener(e -> {
            Component[] components = userPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    checkBox.setSelected(selectAllCheckBox.isSelected());
                }
            }
        });

        // 삭제된 사용자 목록 패널을 스크롤 가능하게 추가
        binFrame.add(new JScrollPane(userPanel), BorderLayout.CENTER);

        // 버튼 패널 생성
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(174, 195, 174));

        // "복원하기" 버튼 생성
        JButton restoreButton = new JButton("복원하기");
        restoreButton.setBackground(new Color(244, 246, 255));
        restoreButton.setFont(customFont);
        restoreButton.addActionListener(e -> {
            List<Integer> selectedUserIds = new ArrayList<>();
            Component[] components = userPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    if (checkBox.isSelected()) {
                        int uid = Integer.parseInt(checkBox.getActionCommand()); // UID 값 추출
                        selectedUserIds.add(uid);
                    }
                }
            }

            if (selectedUserIds.isEmpty()) {
                JOptionPane.showMessageDialog(binFrame, "복원할 유저를 선택해주세요.");
            } else {
                for (int uid : selectedUserIds) {
                    userInfoDb.restoreUser(uid); // UID를 이용해 복원 처리
                }
                JOptionPane.showMessageDialog(binFrame, "선택된 유저가 복원되었습니다!");
                binFrame.dispose(); // 창 닫기
                selectUserBin(); // 새로고침
            }
        });

        // "돌아가기" 버튼 생성
        JButton closeButton = new JButton("돌아가기");
        closeButton.setBackground(new Color(244, 246, 255));
        closeButton.setFont(customFont);
        closeButton.addActionListener(e -> binFrame.dispose()); // 창 닫기

        // 버튼 패널에 버튼 추가
        buttonPanel.add(restoreButton);
        buttonPanel.add(closeButton);

        // 버튼 패널을 프레임에 추가
        binFrame.add(buttonPanel, BorderLayout.SOUTH);

        // 프레임 표시
        binFrame.setVisible(true);
    }
    
    
    //관리자 비번 변경 ㅋㅋ
    public void changeAdminPw() {
        // 팝업창 생성
        JFrame popupFrame = new JFrame("관리자 비밀번호 변경");
        popupFrame.setSize(300, 150);
        popupFrame.setLayout(new BorderLayout());
        popupFrame.setBackground(new Color(174, 195, 174));

        // 필드와 버튼
        JTextField pwField = new JTextField();
        pwField.setPreferredSize(new Dimension(200, 30));
        pwField.setMaximumSize(new Dimension(200, 30));
        JButton changeButton = new JButton("변경");
        changeButton.setBackground(new Color(244, 246, 255));
        changeButton.setFont(customFont1);

        // 변경 버튼 액션
        changeButton.addActionListener(e -> {
            String newPassword = pwField.getText();
            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(popupFrame, "비밀번호를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MessageDbManager dbManager = new MessageDbManager();
            boolean success = dbManager.changeAdPw(newPassword);
            if (success) {
                JOptionPane.showMessageDialog(popupFrame, "비밀번호가 변경되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                popupFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(popupFrame, "비밀번호 변경에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

     // 레이아웃 설정
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // FlowLayout으로 설정
        inputPanel.setBackground(new Color(174, 195, 174));

        // 레이블과 필드 추가
        inputPanel.add(new JLabel("새 비밀번호:"));
        inputPanel.add(pwField);

        popupFrame.add(inputPanel, BorderLayout.CENTER);
        popupFrame.add(changeButton, BorderLayout.SOUTH);

        popupFrame.setLocationRelativeTo(null);
        popupFrame.setVisible(true);
    }
    
}
