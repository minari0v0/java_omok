package client;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream; 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import server.ChatServerGUI;
import server.DBmanager;
import GUI.CropImageWindow;
import GUI.FontLoader;
import GUI.NaverMailSender;


public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	// 라벨, 필드 변수들
	private JLabel idLabel, pwLabel,newIdLabel, newPwLabel, nicknameLabel, rePwLabel, passwordMessageLabel, zipCodeLabel, phoneLabel, emailLabel, atLabel, titleLabel, subtitleLabel, linkLabel, stoneLabel, birthLabel, genderLabel, profileImageLabel, imageDisplayLabel, dateLabel, weatherIconLabel, tempHumidityLabel, regionLabel, findIdLabel, findPwLabel, countLabel;
    private JTextField idField, newIdField, nicknameField, zipCodeField, addressField, detailAddressField, emailFieldLeft, emailFieldRight, phoneMiddleField, phoneLastField;
    private JPasswordField pwField, newPwField, rePwField;
    private JProgressBar passwordStrengthBar;
    private JButton loginButton, registerButton, backButton, confirmButton, checkIdButton, checkNicknameButton, addressSearchButton, selectImageButton, weatherButton, showPwButton, resizeButton;
    private JRadioButton stone1, stone2, stone3, stone4, stone5, stone6, maleButton, femaleButton;
    private JComboBox<String> emailDropdown, phoneDropdown, birthYearDropdown, monthDropdown, dayDropdown;
    private JPanel profileImagePanel, weatherInfoPanel;
    private BackgroundPanel backgroundPanel;
    private BufferedImage bufferedImage;
    private boolean isPasswordVisible = false;
    
    
    boolean weatherSuccess=false;
    boolean isPasswordMatching = false;
    private List<String> result;
    private List<String[]> regionList = new ArrayList<>();
    
    // 중복확인 메소드
    private boolean checkAvailability(String field, String value) {
        String query = "SELECT * FROM user WHERE " + field + " = ?";
        boolean isAvailable = false;

        try (Connection connection = DriverManager.getConnection(DBmanager.URL, DBmanager.USER, DBmanager.PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "이미 사용 중인 " + field + "입니다.", "중복 확인", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "사용 가능한 " + field + "입니다.", "중복 확인", JOptionPane.INFORMATION_MESSAGE);
                isAvailable = true; // 사용 가능하다는 플래그를 설정
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "데이터베이스 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }

        return isAvailable; // 사용 가능 여부 반환
    }
    
    // CSV 파일에서 데이터를 읽어와서 regionList에 저장하는 메서드
    private void loadRegionData(String filePath) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            br.readLine(); // 첫 번째 줄 (헤더) 건너뛰기
            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); // CSV 데이터를 쉼표로 구분
                regionList.add(data); // 데이터를 리스트에 추가
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //커스텀 글꼴 1
    FontLoader fontLoader = new FontLoader("src/main/java/img/NanumSquareNeoB.ttf", 11f);
    Font customFont = fontLoader.getCustomFont();
    //커스텀 글꼴 2
    FontLoader fontLoaderB = new FontLoader("src/main/java/img/NanumSquareNeoB.ttf", 18f);
    Font customFontB = fontLoaderB.getCustomFont();
    FontLoader fontLoader1 = new FontLoader("src/main/java/img/NanumSquareNeoB.ttf", 15f);
    Font customFont1 = fontLoader1.getCustomFont();
    FontLoader fontLoader2 = new FontLoader("src/main/java/img/신라문화체B.ttf", 90f);
    Font customFont2 = fontLoader2.getCustomFont();
    
    //아이디 찾기 팝업
    private void showFindIdPopup() {
        // 팝업 창 생성
        JFrame findIdFrame = new JFrame("아이디 찾기");
        findIdFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        findIdFrame.setSize(450, 300); // 크기 설정
        findIdFrame.setLocationRelativeTo(null); // 화면 가운데에 배치
        
        // 패널 설정
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // 세로 방향으로 컴포넌트 배치
        panel.setBackground(new Color(174, 195, 174)); // 배경색 설정
        
        // "아이디를 잃어버리셨나요?" 라벨
        JLabel infoLabel = new JLabel("아이디를 잃어버리셨나요?");
        infoLabel.setFont(customFontB);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        panel.add(Box.createVerticalStrut(20));  // 두 줄 정도 내려주기 위해 여백 추가
        panel.add(infoLabel);
        
        // 닉네임 라벨
        JLabel nicknameLabel = new JLabel("닉네임:");
        nicknameLabel.setFont(customFont1);
        nicknameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(30));  // 추가적인 간격을 줄 수 있음
        panel.add(nicknameLabel);
        
        // 닉네임 입력 필드 (높이 줄이기)
        JTextField idField = new JTextField(20);
        idField.setFont(customFont1);
        idField.setPreferredSize(new Dimension(idField.getPreferredSize().width, 30)); // 높이 30으로 조정
        idField.setMinimumSize(new Dimension(idField.getPreferredSize().width, 30)); // 최소 높이 설정
        idField.setMaximumSize(new Dimension(idField.getPreferredSize().width, 30)); // 최대 높이 설정
        panel.add(idField);
        
        // 이메일 라벨
        JLabel emailLabel = new JLabel("이메일:");
        emailLabel.setFont(customFont1);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(10));  // 추가적인 간격
        panel.add(emailLabel);
        
        // 이메일 입력 필드 (높이 줄이기)
        JTextField emailField = new JTextField(20);
        emailField.setFont(customFont1);
        emailField.setPreferredSize(new Dimension(emailField.getPreferredSize().width, 30)); // 높이 30으로 조정
        emailField.setMinimumSize(new Dimension(emailField.getPreferredSize().width, 30)); // 최소 높이 설정
        emailField.setMaximumSize(new Dimension(emailField.getPreferredSize().width, 30)); // 최대 높이 설정
        panel.add(emailField);
        
        // 확인 버튼
        JButton confirmButton = new JButton("확인");
        confirmButton.setBackground(new Color(244, 246, 255)); // 버튼 배경색 설정
        confirmButton.setFont(customFont1);
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.addActionListener(e -> handleFindIdAction(idField, emailField, findIdFrame));
        panel.add(Box.createVerticalStrut(25));  // 버튼 위에도 여백 추가
        panel.add(confirmButton);
        
        // 패널을 프레임에 추가
        findIdFrame.add(panel);
        
        // 팝업창 보여주기
        findIdFrame.setVisible(true);
    }
    
    //아이디 찾는 함수
    private void handleFindIdAction(JTextField idField, JTextField emailField, JFrame parentFrame) {
        String nickname = idField.getText(); // 입력된 닉네임
        String email = emailField.getText(); // 입력된 이메일
        
        // DB 연결
        DBmanager dbManager = new DBmanager();
        Connection conn = null;
		try {
			conn = dbManager.connect();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try {
            // 쿼리 준비
            String query = "SELECT id FROM user WHERE nickname = ? AND email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nickname);
            stmt.setString(2, email);
            
            // 실행
            ResultSet rs = stmt.executeQuery();
            
            // 결과 확인
            if (rs.next()) {
                String userId = rs.getString("id"); // 아이디 찾기 성공 시
                
                // findIdFrame 닫기
                parentFrame.getContentPane().removeAll();
                parentFrame.getContentPane().setBackground(new Color(174, 195, 174));
                // 아이디 표시 라벨
                JLabel idLabel = new JLabel("회원님의 ID는 " + userId + " 입니다.");
                idLabel.setFont(customFontB);
                idLabel.setHorizontalAlignment(SwingConstants.CENTER);
                idLabel.setVerticalAlignment(SwingConstants.CENTER);
                
                parentFrame.getContentPane().add(idLabel);
                // 컴포넌트 갱신
                parentFrame.revalidate();
                parentFrame.repaint();
            } else {
                // 아이디나 이메일이 일치하지 않으면 처리
                JOptionPane.showMessageDialog(parentFrame, "일치하는 아이디가 없습니다.");
            }
            
            // 연결 종료
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "데이터베이스 오류 발생");
        }
    }

    //비번 찾기 묻는 함수
    private void showFindPwPopup() {
        // 팝업 창 생성
        JFrame findPwFrame = new JFrame("비밀번호 찾기");
        findPwFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        findPwFrame.setSize(450, 300); // 크기 설정
        findPwFrame.setLocationRelativeTo(null); // 화면 가운데에 배치
        
        // 패널 설정
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // 세로 방향으로 컴포넌트 배치
        panel.setBackground(new Color(174, 195, 174)); // 배경색 설정
        
        // "비밀번호를 잃어버리셨나요?" 라벨
        JLabel infoLabel = new JLabel("비밀번호를 잃어버리셨나요?");
        infoLabel.setFont(customFontB);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        panel.add(Box.createVerticalStrut(20));  // 두 줄 정도 내려주기 위해 여백 추가
        panel.add(infoLabel);
        
        // 닉네임 라벨
        JLabel idLabel = new JLabel("아이디:");
        idLabel.setFont(customFont1);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(30));  // 추가적인 간격을 줄 수 있음
        panel.add(idLabel);
        
        // 닉네임 입력 필드 (높이 줄이기)
        JTextField idField = new JTextField(20);
        idField.setFont(customFont1);
        idField.setPreferredSize(new Dimension(idField.getPreferredSize().width, 30)); // 높이 30으로 조정
        idField.setMinimumSize(new Dimension(idField.getPreferredSize().width, 30)); // 최소 높이 설정
        idField.setMaximumSize(new Dimension(idField.getPreferredSize().width, 30)); // 최대 높이 설정
        panel.add(idField);
        
        // 이메일 라벨
        JLabel emailLabel = new JLabel("이메일:");
        emailLabel.setFont(customFont1);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(10));  // 추가적인 간격
        panel.add(emailLabel);
        
        // 이메일 입력 필드 (높이 줄이기)
        JTextField emailField = new JTextField(20);
        emailField.setFont(customFont1);
        emailField.setPreferredSize(new Dimension(emailField.getPreferredSize().width, 30)); // 높이 30으로 조정
        emailField.setMinimumSize(new Dimension(emailField.getPreferredSize().width, 30)); // 최소 높이 설정
        emailField.setMaximumSize(new Dimension(emailField.getPreferredSize().width, 30)); // 최대 높이 설정
        panel.add(emailField);
        
        // 확인 버튼
        JButton confirmButton = new JButton("확인");
        confirmButton.setBackground(new Color(244, 246, 255)); // 버튼 배경색 설정
        confirmButton.setFont(customFont1);
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.addActionListener(e -> {
			try {
				handleFindPwAction(idField, emailField, findPwFrame);
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
		});
        panel.add(Box.createVerticalStrut(25));  // 버튼 위에도 여백 추가
        panel.add(confirmButton);
        
        // 패널을 프레임에 추가
        findPwFrame.add(panel);
        
        // 팝업창 보여주기
        findPwFrame.setVisible(true);
    }
    //비번 찾는 함수
    private void handleFindPwAction(JTextField idField, JTextField emailField, JFrame parentFrame) throws MessagingException {
        String userId = idField.getText();
        String userEmail = emailField.getText();

        DBmanager dbManager = new DBmanager();
        Connection conn = null;
		try {
			conn = dbManager.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        try {
            // 사용자 정보 조회
            String query = "SELECT PW FROM user WHERE id = ? AND email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            stmt.setString(2, userEmail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String password = rs.getString("PW");
               sendEmailMethod(userEmail, "오목의 달인 비밀번호 조회 결과", "안녕하세요."
                		+ "\n회원님의 비밀번호는 다음과 같습니다.\n"+password+"\n감사합니다.");
                JOptionPane.showMessageDialog(parentFrame, "회원님의 이메일에 비밀번호를 전송하였습니다!");
                parentFrame.dispose();
            } else {
            	JOptionPane.showMessageDialog(parentFrame, "일치하는 회원 정보가 없습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "오류가 발생했습니다. 다시 시도해주세요.");
        }
    }
    
    private void sendEmailMethod(String to, String subject, String body) throws MessagingException {
    	NaverMailSender mailSender = new NaverMailSender("cc360653@naver.com", "gtxm48322#");
    	mailSender.sendMail(to, subject, body);
    }
    
    public MainFrame() {
        setTitle("오목의 달인");
        setSize(1440, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙에 위치
        
        // CSV 파일에서 데이터를 로드
        loadRegionData("src/main/java/regionList.csv");

        // 배경 패널 설정
        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null); // 자유 배치
        
        // ---- 제목 "오목의 달인" 추가 ----
        titleLabel = new JLabel("오목의 달인");
        titleLabel.setBounds(330, 150, 800, 100); // 위치 설정
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(customFont2);
        titleLabel.setForeground(Color.BLACK);
        backgroundPanel.add(titleLabel);

        // ---- 제목 아래 "omok" 추가 (작은 글씨) ----
        subtitleLabel = new JLabel("omok");
        subtitleLabel.setBounds(890, 220, 200, 50); // 위치 설정
        subtitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 20));
        subtitleLabel.setForeground(new Color(230, 230, 230));
        backgroundPanel.add(subtitleLabel);
        
        // ID 입력 필드
        idLabel = new JLabel("ID:");
        idLabel.setBounds(580, 600, 60, 25); // 위치 설정
        idLabel.setFont(customFontB);
        idLabel.setForeground(Color.BLACK);
        idLabel.setBackground(new Color(45, 45, 45, 128));
        backgroundPanel.add(idLabel);

        idField = new JTextField(20);
        idField.setBounds(650, 600, 210, 25); // 위치 설정
        backgroundPanel.add(idField);

        // PW 입력 필드
        pwLabel = new JLabel("Password:");
        pwLabel.setBounds(520, 640, 100, 25); // 위치 설정
        pwLabel.setFont(customFontB);
        pwLabel.setForeground(Color.BLACK);
        pwLabel.setBackground(new Color(45, 45, 45, 128));
        backgroundPanel.add(pwLabel);

        pwField = new JPasswordField(20);
        pwField.setBounds(650, 640, 210, 25); // 위치 설정
        backgroundPanel.add(pwField);

        // 로그인 버튼
        loginButton = new JButton("로그인");
        loginButton.setBounds(870, 600, 80, idField.getHeight() + pwField.getHeight() + 15); // 높이 합침
        loginButton.setFont(customFont);
        loginButton.setBackground(new Color(244, 246, 255));
        backgroundPanel.add(loginButton);

        // 회원가입 버튼 (초기화 위치 이동)
        registerButton = new JButton("회원가입");
        registerButton.setBounds(858, 680, 92, 30); // 우측에 회원가입 버튼 배치
        registerButton.setFont(customFont);
        registerButton.setBackground(new Color(244, 246, 255));
        backgroundPanel.add(registerButton);
        
        //아디 비번 찾기
        findIdLabel = new JLabel("<html><u>아이디 찾기</u></html>");
        findIdLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        findIdLabel.setBounds(670, 680, 60, 30);
        findIdLabel.setFont(new Font("맑은 고딕", Font.BOLD,11));
        findIdLabel.setForeground(new Color(230,230,230));
        
        countLabel=new JLabel(" | ");
        countLabel.setBounds(740, 680, 20, 30);
        countLabel.setFont(new Font("맑은 고딕", Font.BOLD,11));
        countLabel.setForeground(new Color(230,230,230));
        
        findPwLabel = new JLabel("<html><u>비밀번호 찾기</u></html>"); // HTML로 밑줄 추가
        findPwLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서 변경
        findPwLabel.setBounds(760, 680, 70, 30);
        findPwLabel.setFont(new Font("맑은 고딕", Font.BOLD,11));
        findPwLabel.setForeground(new Color(230,230,230));
        
        backgroundPanel.add(findIdLabel);
        backgroundPanel.add(findPwLabel);
        backgroundPanel.add(countLabel);

        findIdLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	showFindIdPopup();
            }
        });
        findPwLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	showFindPwPopup();
            }
        });

        // ---- 회원가입 버튼 클릭 시 UI 업데이트 ----
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegisterFields(backgroundPanel); // 회원가입 필드 추가 메서드 호출
            }
        });
     
        // 로그인 버튼 클릭 시 동작
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String id = idField.getText();
                String password = pwField.getText();
                // 로그인 메소드 호출
                loginUser(id, password);
            }
        });

        // 비밀번호 입력 필드에서 Enter 키 이벤트 처리
        pwField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String id = idField.getText();
                String password = pwField.getText();

                // 로그인 메소드 호출
                loginUser(id, password);
            }
        });
        
        // ---- 좌측 하단에 날씨 조회 버튼 추가 ----
        weatherButton = new JButton("날씨 조회하기");
        weatherButton.setBounds(10, 820, 120, 30); // 좌측 하단에 배치
        weatherButton.setBackground(new Color(244, 246, 255));
        weatherButton.setFont(customFont);
        backgroundPanel.add(weatherButton);

        // ---- 날씨 정보를 표시할 반투명 패널 추가 ----
        weatherInfoPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(255, 255, 255, 150)); // 투명도 적용된 배경색
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // 둥근 모서리 배경
            }
        };
        weatherInfoPanel.setLayout(null); // 자유 배치
        weatherInfoPanel.setBounds(10, 650, 120, 160); // 적절한 위치와 크기 지정
        weatherInfoPanel.setBackground(new Color(255, 255, 255, 150)); // 흰색 반투명 배경
        weatherInfoPanel.setOpaque(false);
        weatherInfoPanel.setVisible(false); //기본적으로 패널은 숨기는게 깔끔

        // ---- 날짜, 날씨 이미지, 기온/습도, 동네 이름을 표시할 라벨 추가 ----
        dateLabel = new JLabel(); // 날짜
        dateLabel.setBounds(30, 10, 180, 20); // 패널 내 위치 조정
        dateLabel.setFont(customFont);
        weatherInfoPanel.add(dateLabel);

        weatherIconLabel = new JLabel();
        weatherIconLabel.setBounds(30, 40, 60, 60); // 이미지 크기를 더 키움
        weatherInfoPanel.add(weatherIconLabel);

        tempHumidityLabel = new JLabel(); // 기온/습도
        tempHumidityLabel.setBounds(30, 105, 100, 20); // 기온/습도 텍스트 배치
        tempHumidityLabel.setFont(customFont);
        weatherInfoPanel.add(tempHumidityLabel);

        regionLabel = new JLabel(); // 동네 이름 (관악구 등)
        regionLabel.setBounds(30, 120, 180, 20); // 패널 내 위치 조정
        regionLabel.setFont(customFont);
        weatherInfoPanel.add(regionLabel);

        // ---- 패널을 메인 배경 패널에 추가 ----
        backgroundPanel.add(weatherInfoPanel);

        // ---- 날씨 조회 버튼 클릭 시 팝업창 띄우기 ----
        weatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openWeatherPopup();
            }
        });

        add(backgroundPanel);
        setVisible(true);
        }

        // ---- 날씨 데이터를 화면에 업데이트하는 함수 ----
        private void updateWeatherDisplay(String[] weatherData, String regionChild) {
            // 날짜 라벨에 데이터 추가 (날짜 형식 변환)
        	if (weatherData[0] != null && weatherData[0].length() >= 8) {
                // 날짜 라벨에 데이터 추가 (날짜 형식 변환)
                dateLabel.setText(weatherData[0].substring(0, 4) + "/" + weatherData[0].substring(4, 6) + "/" + weatherData[0].substring(6));
            } else {
                dateLabel.setText("날짜 정보 없음");
            }

            // 날씨 이미지 업데이트 (맑음, 흐림, 비 등)
            String weatherStatus = weatherData[2];
            ImageIcon weatherIcon = new ImageIcon(new ImageIcon("src/main/java/img/" + weatherStatus + ".png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)); // 날씨 상태 이미지 불러오기
            weatherIconLabel.setIcon(weatherIcon);

            // 기온과 습도 업데이트
            tempHumidityLabel.setText(weatherData[3] + "℃, " + weatherData[4] + "%");

            // 지역 이름 업데이트 (관악구 등)
            regionLabel.setText(regionChild);
            
            backgroundPanel.revalidate();
            backgroundPanel.repaint();
        }

        // ---- 날씨 조회 팝업창을 띄우는 메서드 ----
        private void openWeatherPopup() {
            // 팝업 프레임 생성 및 구성
            JFrame weatherPopup = new JFrame("동네 검색");
            weatherPopup.setSize(400, 300);
            weatherPopup.setLayout(new BorderLayout());

            // 검색 패널 생성
            JPanel searchPanel = new JPanel();
            searchPanel.setLayout(new FlowLayout());

            JTextField searchField = new JTextField(20);
            JButton searchButton = new JButton("조회");
            searchButton.setBackground(new Color(244, 246, 255));

            searchPanel.add(searchField);
            searchPanel.add(searchButton);

            // 중앙에 검색 결과 테이블 추가
            String[] columnNames = {"동네 이름(시/도/군/구)"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
            JTable resultTable = new JTable(tableModel) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // 셀 편집 비활성화
                }
            };
            JScrollPane tableScrollPane = new JScrollPane(resultTable);
            tableScrollPane.setPreferredSize(new Dimension(380, 150));

            // 검색 버튼 클릭 시 동네 검색
            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String searchText = searchField.getText().trim();
                    tableModel.setRowCount(0); // 기존 데이터 초기화

                    // 검색 결과를 CSV에서 필터링하여 테이블에 표시 (최대 5개)
                    int rowCount = 0;
                    for (String[] region : regionList) {
                        String regionParent = region[1];
                        String regionChild = region[2];

                        if (regionParent.contains(searchText) || regionChild.contains(searchText)) {
                            if (rowCount < 5) { // 5개까지만 출력
                                tableModel.addRow(new Object[]{regionParent + " " + regionChild});
                                rowCount++;
                            }
                        }
                    }
                }
            });

            // 테이블에서 동네 더블클릭 시 좌표 출력 및 날씨 업데이트
            resultTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int selectedRow = resultTable.getSelectedRow();
                        String selectedRegion = (String) resultTable.getValueAt(selectedRow, 0);

                        // 선택한 동네의 NX, NY 좌표를 찾음
                        for (String[] region : regionList) {
                            String regionParent = region[1];
                            String regionChild = region[2];

                            if (selectedRegion.equals(regionParent + " " + regionChild)) {
                                String nx = region[3].trim();
                                String ny = region[4].trim();

                                // 콘솔에 좌표 출력
                                System.out.println("선택한 동네의 좌표 - NX: " + nx + ", NY: " + ny);
                                weatherInfoPanel.setVisible(true);
                                // 날씨 API 호출
                                String[] v = new String[5];
                                String s = HnpWeather.get(Integer.parseInt(nx), Integer.parseInt(ny), v);

                                if (s == null) {
                                    // API 호출 결과를 화면에 업데이트
                                    updateWeatherDisplay(v, regionChild);
                                    weatherSuccess=true;
                                } else {
                                    // 에러 메시지 출력
                                    System.out.println("Error : " + s);
                                    weatherSuccess=false;
                                }

                                break;
                            }
                        }

                        weatherPopup.dispose(); // 팝업창 닫기
                    }
                }
            });

            // 팝업창에 컴포넌트 추가
            weatherPopup.add(searchPanel, BorderLayout.NORTH);
            weatherPopup.add(tableScrollPane, BorderLayout.CENTER);

            weatherPopup.setLocationRelativeTo(this); // 메인 프레임 기준으로 팝업창 위치
            weatherPopup.setVisible(true);
        }
    
    private void openHtmlFile(String fileName) {
        try {
            // 현재 패키지의 경로를 가져옵니다.
            String path = getClass().getClassLoader().getResource(fileName).toURI().toString();
            Desktop.getDesktop().browse(new URI(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 배경 이미지를 설정할 JPanel
    class BackgroundPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Image backgroundImage;

        public BackgroundPanel() {
            // 배경 이미지 로드
            backgroundImage = new ImageIcon(getClass().getResource("../img/무협배경.jpg")).getImage(); // 배경 이미지 경로
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
  
    // Placeholder 텍스트 추가 메서드
    private void addPlaceholderText(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.setFont(new Font("맑은 고딕", Font.ITALIC, 12));

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                    textField.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                    textField.setFont(new Font("맑은 고딕", Font.ITALIC, 12));
                }
            }
        });
    }
    
    //showCustomMessageDialog 메소드
    private void showCustomMessageDialog(String title, String mainMessage, String subMessage, boolean isSuccess) {
        // UIManager를 사용해 JOptionPane의 폰트 설정
        UIManager.put("OptionPane.messageFont", new Font("맑은 고딕", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("맑은 고딕", Font.PLAIN, 14));
        
        // 중앙에 위치한 메시지 만들기
        JLabel mainMessageLabel = new JLabel(mainMessage);
        mainMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);  // 중앙 정렬
        mainMessageLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));  // 메인 메시지는 굵은 글씨
        
        // 성공 메시지일 경우 녹색, 실패 메시지일 경우 빨간색
        if (isSuccess) {
            mainMessageLabel.setForeground(new Color(0, 128, 0));  // 녹색
        } else {
            mainMessageLabel.setForeground(Color.RED);  // 빨간색
        }

        // 아래에 표시될 서브 메시지
        JLabel subMessageLabel = new JLabel(subMessage);
        subMessageLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));  // 서브 메시지의 기본 폰트 설정
        subMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // 메시지를 두 개의 JLabel로 나누어 패널에 배치
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // 세로 정렬
        panel.add(mainMessageLabel);
        panel.add(Box.createVerticalStrut(10));  // 간격 추가
        panel.add(subMessageLabel);
        
        // JOptionPane에 커스텀 패널을 전달
        JOptionPane.showMessageDialog(null, panel, title, isSuccess ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private void resetToLoginScreen() {
        // ID와 비밀번호 필드 및 라벨 초기화
        newIdField.setText("");
        newPwField.setText("");
        nicknameField.setText("");
        passwordMessageLabel.setText("");

        // 기존 로그인 관련 버튼 및 필드 보이기
        loginButton.setVisible(true);
        registerButton.setVisible(true);
        idField.setVisible(true);
        pwField.setVisible(true);

        // 라벨 보이기
        idLabel.setVisible(true);
        pwLabel.setVisible(true);
        titleLabel.setVisible(true);
        subtitleLabel.setVisible(true);
        findIdLabel.setVisible(true);
        findPwLabel.setVisible(true);
        countLabel.setVisible(true);

        // 회원가입 필드 숨기기
        newIdLabel.setVisible(false);
        newIdField.setVisible(false);
        checkIdButton.setVisible(false);
        newPwLabel.setVisible(false);
        newPwField.setVisible(false);
        nicknameLabel.setVisible(false);
        nicknameField.setVisible(false);
        checkNicknameButton.setVisible(false);
        stoneLabel.setVisible(false);
        rePwLabel.setVisible(false); 
        rePwField.setVisible(false); 
        emailLabel.setVisible(false);
        atLabel.setVisible(false);
        emailFieldLeft.setVisible(false);
        emailFieldRight.setVisible(false);
        phoneLabel.setVisible(false);
        phoneDropdown.setVisible(false);
        phoneMiddleField.setVisible(false);
        phoneLastField.setVisible(false);
        zipCodeLabel.setVisible(false);
        zipCodeField.setVisible(false);
        addressField.setVisible(false);
        addressSearchButton.setVisible(false);
        detailAddressField.setVisible(false);
        emailDropdown.setVisible(false);
        linkLabel.setVisible(false);
        passwordStrengthBar.setVisible(false);
        birthLabel.setVisible(false);
        birthYearDropdown.setVisible(false);
        monthDropdown.setVisible(false);
        dayDropdown.setVisible(false);
        genderLabel.setVisible(false);
        maleButton.setVisible(false);
        femaleButton.setVisible(false);
        selectImageButton.setVisible(false);
        profileImageLabel.setVisible(false);
        imageDisplayLabel.setVisible(false);
        profileImagePanel.setVisible(false);
        showPwButton.setVisible(false);
        resizeButton.setVisible(false);
        
        
        // 라디오 버튼 숨기기
        stone1.setVisible(false);
        stone2.setVisible(false);
        stone3.setVisible(false);
        stone4.setVisible(false);
        stone5.setVisible(false);
        stone6.setVisible(false);
        confirmButton.setVisible(false);
        backButton.setVisible(false);
        // 패널 다시 그리기
        backgroundPanel.revalidate();
        backgroundPanel.repaint();
    }

    
    //회원가입 DB 메소드
    private void registerUser(String id, String password, String nickname, String email, String phone, String address, int pick, String birth, Integer genPick, File imageFile) {
        DBmanager dbManager = new DBmanager();

        String sql = "INSERT INTO user (id, pw, nickname, email, phone, address, pick, birth, gender, profile_img) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            pstmt.setString(3, nickname);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            pstmt.setString(6, address);
            pstmt.setInt(7, pick);
            pstmt.setString(8, birth);
            if (genPick == null) {
                pstmt.setObject(9, null); // gender가 null인 경우
            } else {
                pstmt.setInt(9, genPick); // gender가 null이 아닐 경우
            }
            
            if (imageFile != null && imageFile.exists() && imageFile.canRead()) {
                try {
                    // ImageIO를 사용하여 이미지를 읽음
                    BufferedImage bufferedImage = ImageIO.read(imageFile);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", baos); // 이미지를 PNG 형식으로 출력 스트림에 저장
                    pstmt.setBlob(10, new ByteArrayInputStream(baos.toByteArray())); // Blob에 입력
                    System.out.println("이미지 파일 성공적으로 불러옴");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("파일 읽기 실패");
                }
            } else {
                pstmt.setNull(10, java.sql.Types.BLOB);
                System.out.println("파일을 읽을 수 없음");
            }
            //
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                int result = JOptionPane.showConfirmDialog(null, "회원가입이 완료되었습니다!", "회원가입 성공", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    resetToLoginScreen(); // 팝업창 확인 버튼을 누르면 로그인 화면으로 돌아감
                }
            } else {
                showCustomMessageDialog("회원가입 실패", "회원가입 실패!", "데이터베이스 오류일까", false);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Error: " + e.getMessage());
            showCustomMessageDialog("회원가입 실패", "회원가입 실패!", "데이터베이스 오류", false);
        }
    }
    
    // 로그인한 유저의 uid와 닉네임을 넘겨서 ClientLobby 열기
    private void enterLobby(int uid, String userNickname, JPanel weatherInfoPanel, boolean weatherSuccess) {
        ClientLobby lobby = new ClientLobby(uid, userNickname, weatherInfoPanel, weatherSuccess);
        lobby.setVisible(true);  // 로비 창 띄우기
        this.dispose();  // 로그인 창 닫기
    }
    
    // 로그인 함수 구현
    private void loginUser(String id, String password) {
        DBmanager dbManager = new DBmanager();
        String loginSql = "SELECT 'user' AS role, uid, nickname FROM user WHERE id = ? AND pw = ? " +
                          "UNION ALL " +
                          "SELECT 'admin' AS role, NULL AS uid, NULL AS nickname FROM admin WHERE id = ? AND pw = ?";

        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(loginSql)) {
            
            stmt.setString(1, id);
            stmt.setString(2, password);
            stmt.setString(3, id);
            stmt.setString(4, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    if ("user".equals(role)) {
                        int uid = rs.getInt("uid");
                        String nickname = rs.getString("nickname");

                        // UI 업데이트는 메인 스레드에서 수행
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, "로그인 성공!", "로그인", JOptionPane.INFORMATION_MESSAGE);
                            enterLobby(uid, nickname, weatherInfoPanel, weatherSuccess);
                        });
                    } else if ("admin".equals(role)) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, "관리자 로그인 성공!", "로그인", JOptionPane.INFORMATION_MESSAGE);
                            SwingUtilities.getWindowAncestor(backgroundPanel).dispose();
                            new ChatServerGUI();
                        });
                    }
                    return;
                }
            }

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "ID 또는 비밀번호가 잘못되었습니다.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
            });

        } catch (SQLException ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "데이터베이스 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            });
        }
    }
    
    // 패스워드 강도 계산 메소드
    private int calculatePasswordStrength(String password) {
        int strength = 0;

        if (password.length() >= 6) strength += 20; // 길이가 6자 이상
        if (password.matches(".*[A-Z].*")) strength += 20; // 대문자 포함
        if (password.matches(".*[a-z].*")) strength += 20; // 소문자 포함
        if (password.matches(".*[0-9].*")) strength += 20; // 숫자 포함
        if (password.matches(".*[!@#$%^&*()\\-_=+{};:,<.>].*")) strength += 20; // 특수문자 포함

        return strength;
    }
    
    // 프로그레스 바에 색상과 텍스트 업데이트 메서드
    private void updatePasswordStrengthBar(int strength, JProgressBar passwordStrengthBar) {
        String strengthText = "";
        Color barColor = new Color(255, 100, 100); // 기본 색상 약함으로 설정

        if (strength <= 20) {
            strengthText = "약함";
            barColor = new Color(255, 100, 100);
        } else if (strength <= 40) {
            strengthText = "보통";
            barColor = new Color(255, 189, 103);
        } else if (strength <= 60) {
            strengthText = "양호";
            barColor = new Color(248, 254, 133);
        } else if (strength <= 80) {
            strengthText = "강함";
            barColor = new Color(156, 219, 166);
        } else if (strength <= 100) {
            strengthText = "매우 강함";
            barColor = new Color(80, 180, 152);
        }
        passwordStrengthBar.setFont(new Font("맑은 고딕", Font.BOLD | Font.ITALIC, 13));
        passwordStrengthBar.setString(strengthText); // 강도 텍스트 업데이트
        passwordStrengthBar.setForeground(Color.BLACK); // 텍스트 색상은 검은색으로 고정
        passwordStrengthBar.setBackground(Color.LIGHT_GRAY); // 기본 배경색 설정
        passwordStrengthBar.setForeground(barColor); // 프로그래스 바 색상 업데이트
    }
     
    // 회원가입 필드를 메인 프레임에 추가하는 메서드
    private void showRegisterFields(JPanel panel) {
        // ID와 비밀번호 필드 및 라벨 초기화
        idField.setText(""); // ID 필드 초기화
        pwField.setText(""); // 비밀번호 필드 초기화

        // 기존 로그인 관련 버튼 및 필드 숨기기
        loginButton.setVisible(false);
        registerButton.setVisible(false);
        idField.setVisible(false);
        pwField.setVisible(false);

        // 라벨 숨기기
        idLabel.setVisible(false); // ID 라벨 숨기기
        pwLabel.setVisible(false); // PW 라벨 숨기기
        titleLabel.setVisible(false);//title 라벨 숨기기
        subtitleLabel.setVisible(false);//부제목 라벨 숨기기
        findIdLabel.setVisible(false);
        findPwLabel.setVisible(false);
        countLabel.setVisible(false);
        
        // 중앙에 회원가입 필드 추가
        int baseX = 500;
        int baseY = 200; // 중앙 Y 위치 설정

        // 가입하기 버튼 (맨 위로 올려야함)
        confirmButton = new JButton("가입하기");
        confirmButton.setBounds(baseX + 100, baseY + 570, 100, 30);
        confirmButton.setFont(customFont);
        confirmButton.setBackground(new Color(244, 246, 255));
        confirmButton.setEnabled(false); // 기본적으로 비활성화
        panel.add(confirmButton);

        boolean[] availability = {false, false}; // 0: ID 사용 가능, 1: 닉네임 사용 가능

        // 돌아가기 버튼 (가입하기와 같이 올라옴)
        backButton = new JButton("돌아가기");
        backButton.setBounds(baseX + 210, baseY + 570, 100, 30);
        backButton.setFont(customFont);
        backButton.setBackground(new Color(244, 246, 255));
        panel.add(backButton);

        // ID 입력 필드 및 중복확인 버튼
        newIdLabel = new JLabel("ID:");
        newIdLabel.setBounds(baseX, baseY - 80, 40, 25);
        newIdLabel.setFont(customFont);
        newIdLabel.setOpaque(true);
        newIdLabel.setBackground(new Color(240, 240, 240, 128));
        newIdLabel.setForeground(Color.BLACK);
        panel.add(newIdLabel);

        newIdField = new JTextField(20);
        newIdField.setBounds(baseX + 100, baseY - 80, 200, 25);
        panel.add(newIdField);
        addPlaceholderText(newIdField, "아이디 입력"); // Placeholder 추가

        // DocumentListener 추가 (ID 필드 변경 감지)
        newIdField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { confirmButton.setEnabled(false); }
            @Override
            public void removeUpdate(DocumentEvent e) { confirmButton.setEnabled(false); }
            @Override
            public void changedUpdate(DocumentEvent e) { confirmButton.setEnabled(false); }
        });

        checkIdButton = new JButton("중복확인");
        checkIdButton.setBounds(baseX + 310, baseY - 80, 100, 25);
        checkIdButton.setFont(customFont);
        panel.add(checkIdButton);

        // Id 중복 체크 메소드
        checkIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = newIdField.getText().trim(); // 공백 제거

                if (id.equals("아이디 입력") || id.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "유효한 ID를 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // ID 사용 가능 여부 체크
                boolean isAvailable = checkAvailability("id", id);
                availability[0] = isAvailable; // ID 사용 가능 여부 저장

                // 가입하기 버튼 활성화 여부 체크
                confirmButton.setEnabled(availability[0] && availability[1] && isPasswordMatching);
            }
        });

        // Password 입력 필드
        newPwLabel = new JLabel("Password:");
        newPwLabel.setBounds(baseX, baseY - 40, 80, 25);
        newPwLabel.setFont(customFont);
        newPwLabel.setOpaque(true);
        newPwLabel.setBackground(new Color(240, 240, 240, 128));
        newPwLabel.setForeground(Color.BLACK);
        panel.add(newPwLabel);

        newPwField = new JPasswordField(20);
        newPwField.setBounds(baseX + 100, baseY - 40, 172, 25);
        showPwButton = new JButton();
        ImageIcon hiddenIcon = new ImageIcon("src/main/java/img/hiddenField.png");
        Image scaledHiddenIcon = hiddenIcon.getImage().getScaledInstance(33, 24, Image.SCALE_SMOOTH); // 이미지 크기 조정
        showPwButton.setIcon(new ImageIcon(scaledHiddenIcon)); // 기본 이미지
        showPwButton.setBounds(baseX+270, baseY-40, 33, 24); // 위치 및 크기 설정
        showPwButton.setBackground(Color.WHITE);
        showPwButton.setFocusPainted(false); // 포커스 시 테두리 없애기
        panel.add(showPwButton);
        panel.add(newPwField);
        
        showPwButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 비밀번호 표시 상태 토글
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    newPwField.setEchoChar((char) 0); // 비밀번호 표시
                    ImageIcon visibleIcon = new ImageIcon("src/main/java/img/showField.png");
                    Image scaledVisibleIcon = visibleIcon.getImage().getScaledInstance(33, 24, Image.SCALE_SMOOTH); // 이미지 크기 조정
                    showPwButton.setIcon(new ImageIcon(scaledVisibleIcon)); // 이미지 변경
                } else {
                    newPwField.setEchoChar('*'); // 비밀번호 숨김
                    showPwButton.setIcon(new ImageIcon(scaledHiddenIcon)); // 기본 이미지로 되돌리기
                }
            }
        });
        

        // 비밀번호 재입력 필드
        rePwLabel = new JLabel("confirm P/W:");
        rePwLabel.setBounds(baseX, baseY, 80, 25);
        rePwLabel.setFont(customFont);
        rePwLabel.setOpaque(true);
        rePwLabel.setBackground(new Color(240, 240, 240, 128));
        rePwLabel.setForeground(Color.BLACK);
        panel.add(rePwLabel);

        rePwField = new JPasswordField(20);
        rePwField.setBounds(baseX + 100, baseY, 200, 25);
        panel.add(rePwField);

        // 비밀번호 유효성 메시지 레이블 추가
        passwordMessageLabel = new JLabel();
        passwordMessageLabel.setBounds(baseX + 100, baseY - 20, 250, 20);
        passwordMessageLabel.setForeground(Color.RED); // 오류 메시지는 빨간색으로 설정
        panel.add(passwordMessageLabel);
        
        // 패스워드 강도 시각적 표시
        passwordStrengthBar = new JProgressBar(0, 100);
        passwordStrengthBar.setBounds(baseX + 310, baseY - 40, 100, 25); // 패스워드 필드 오른쪽에 배치
        passwordStrengthBar.setStringPainted(true); // 퍼센티지 표시
        passwordStrengthBar.setVisible(true);
        passwordStrengthBar.setString("");
        passwordStrengthBar.setValue(0);
        passwordStrengthBar.setToolTipText("<html>약함: 6자 이상, 대문자/소문자/숫자/특수문자 중 하나 포함<br>"
        		+ "보통: 그 중 2개 포함<br>양호: 그 중 3개 포함<br>강함: 그 중 4개 포함<br>매우 강함: 모든 조건 충족</html>");
        panel.add(passwordStrengthBar);
        

        
        // 비밀번호 필드에 DocumentListener 추가
        newPwField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkPassword();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkPassword();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkPassword();
            }

            private void checkPassword() {
                String password = new String(newPwField.getPassword());
                String rePassword = new String(rePwField.getPassword());

             // 비밀번호 강도 계산
                int strength = password.isEmpty() ? -1 : calculatePasswordStrength(password); // 빈 상태일 경우 -1로 설정
                passwordStrengthBar.setValue(strength >= 0 ? strength : 0); // 강도 바 업데이트, 빈 상태일 경우 0으로 설정

                // 빈 상태일 경우, 텍스트를 빈 문자열로 설정
                if (password.isEmpty()) {
                    passwordStrengthBar.setString("");
                } else {
                    updatePasswordStrengthBar(strength, passwordStrengthBar); // 강도 텍스트 및 색상 업데이트
                }

                if (password.length() < 6) {
                    passwordMessageLabel.setText("비밀번호는 최소 6자 이상이어야 합니다.");
                    passwordMessageLabel.setForeground(Color.RED);
                    isPasswordMatching = false;
                } else if (!password.equals(rePassword)) {
                    passwordMessageLabel.setText("비밀번호가 일치하지 않습니다.");
                    passwordMessageLabel.setForeground(Color.RED);
                    isPasswordMatching = false;
                } else {
                    passwordMessageLabel.setText("");
                    isPasswordMatching = true;
                }

                // 가입하기 버튼 활성화 여부 체크
                confirmButton.setEnabled(availability[0] && availability[1] && isPasswordMatching);
            }
        });

        rePwField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkPassword();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkPassword();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkPassword();
            }
            
            private void checkPassword() {
                String password = new String(newPwField.getPassword());
                String rePassword = new String(rePwField.getPassword());

                // 비밀번호 유효성 검사
                if (password.length() < 6) {
                    passwordMessageLabel.setText("비밀번호는 최소 6자 이상이어야 합니다.");
                    passwordMessageLabel.setForeground(Color.RED);
                    isPasswordMatching = false; // 비밀번호가 유효하지 않음
                } else if (!password.equals(rePassword)) {
                    passwordMessageLabel.setText("비밀번호가 일치하지 않습니다.");
                    passwordMessageLabel.setForeground(Color.RED);
                    isPasswordMatching = false; // 비밀번호 불일치
                } else {
                    passwordMessageLabel.setText(""); // 에러 메시지 초기화
                    isPasswordMatching = true; // 비밀번호 일치
                }
                // 가입하기 버튼 활성화 여부 체크
                confirmButton.setEnabled(availability[0] && availability[1] && isPasswordMatching);
            }
        });

        
        // Nickname 입력 필드 및 중복확인 버튼
        nicknameLabel = new JLabel("Nickname:");
        nicknameLabel.setBounds(baseX, baseY+40, 80, 25);
        nicknameLabel.setFont(customFont);
        nicknameLabel.setOpaque(true);
        nicknameLabel.setBackground(new Color(240, 240, 240, 128));
        nicknameLabel.setForeground(Color.BLACK);
        panel.add(nicknameLabel);
       

        nicknameField = new JTextField(20);
        nicknameField.setBounds(baseX + 100, baseY + 40, 200, 25);
        panel.add(nicknameField);
        addPlaceholderText(nicknameField, "닉네임 입력"); // Placeholder 추가
        
        // DocumentListener 추가 (닉네임 필드 변경 감지)
        nicknameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { confirmButton.setEnabled(false); }
            @Override
            public void removeUpdate(DocumentEvent e) { confirmButton.setEnabled(false); }
            @Override
            public void changedUpdate(DocumentEvent e) { confirmButton.setEnabled(false); }
        });

        checkNicknameButton = new JButton("중복확인");
        checkNicknameButton.setBounds(baseX + 310, baseY + 40, 100, 25);
        checkNicknameButton.setFont(customFont);
        panel.add(checkNicknameButton);
        
        // 닉네임 중복 확인 메소드
        checkNicknameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = nicknameField.getText().trim(); // 공백 제거

                if (nickname.equals("닉네임 입력") || nickname.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "유효한 닉네임을 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 닉네임 사용 가능 여부 체크
                boolean isAvailable = checkAvailability("nickname", nickname);
                availability[1] = isAvailable; // 닉네임 사용 가능 여부 저장

                // 가입하기 버튼 활성화 여부 체크
                confirmButton.setEnabled(availability[0] && availability[1] && isPasswordMatching);
            }
        });
        
        // 이메일과 전화번호 입력 필드 추가
        // 이메일 두 개 로 나눠놨음
        emailLabel = new JLabel("이메일:");
        emailLabel.setBounds(baseX, baseY + 100, 60, 25);
        emailLabel.setFont(customFont);
        emailLabel.setOpaque(true);
        emailLabel.setBackground(new Color(240, 240, 240, 128));
        emailLabel.setForeground(Color.BLACK);
        panel.add(emailLabel);

        emailFieldLeft = new JTextField();
        emailFieldLeft.setBounds(baseX + 100, baseY + 100, 100, 25); // 왼쪽 필드 크기
        panel.add(emailFieldLeft);

        // "@" 기호 라벨 추가
        atLabel = new JLabel("@");
        atLabel.setBounds(baseX + 205, baseY + 100, 15, 25); // "@" 기호 위치
        atLabel.setFont(customFont);
        atLabel.setForeground(Color.BLACK);
        panel.add(atLabel);

        // 이메일 필드의 오른쪽 부분 (도메인 입력)
        emailFieldRight = new JTextField();
        emailFieldRight.setBounds(baseX + 220, baseY + 100, 80, 25); // 오른쪽 필드 크기
        panel.add(emailFieldRight);

        // 드롭다운 메뉴 추가
        String[] emailDomains = {"직접 입력", "gmail.com", "naver.com", "nate.com", "daum.net"};
        emailDropdown = new JComboBox<>(emailDomains);
        emailDropdown.setBounds(baseX + 310, baseY + 100, 100, 25); // 드롭다운 버튼 위치
        panel.add(emailDropdown);

        // 드롭다운 선택 시 도메인 필드에 추가
        emailDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedDomain = (String) emailDropdown.getSelectedItem();
                
                if ("직접 입력".equals(selectedDomain)) {
                    // 직접 입력을 선택하면 도메인 삭제
                    emailFieldRight.setText(""); // 도메인 입력 필드를 비움
                } else {
                    // 도메인 선택 시 이메일 필드에 추가
                    emailFieldRight.setText(selectedDomain); // 선택된 도메인 추가
                }
            }
        });
        
        phoneLabel = new JLabel("전화번호:");
        phoneLabel.setBounds(baseX, baseY + 140, 60, 25);
        phoneLabel.setFont(customFont);
        phoneLabel.setOpaque(true);
        phoneLabel.setBackground(new Color(240, 240, 240, 128));
        phoneLabel.setForeground(Color.BLACK);
        panel.add(phoneLabel);

        String[] phonePrefixes = {"010", "011", "016", "017", "018", "019"};
        phoneDropdown = new JComboBox<>(phonePrefixes);
        phoneDropdown.setBounds(baseX + 100, baseY + 140, 60, 25);
        panel.add(phoneDropdown);
        
        phoneMiddleField = new JTextField();
        phoneMiddleField.setBounds(baseX + 165, baseY + 140, 65, 25);
        panel.add(phoneMiddleField);
        
        phoneLastField = new JTextField();
        phoneLastField.setBounds(baseX + 235, baseY + 140, 65, 25);
        panel.add(phoneLastField);

        // 전화번호 포맷팅
        phoneMiddleField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String input = phoneMiddleField.getText().replaceAll("[^\\d]", "");
                if (input.length() >= 3 && input.length() <= 4) {
                    phoneMiddleField.setText(input);
                } else {
                    phoneMiddleField.setText("");
                }
            }
        });
        
        phoneLastField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String input = phoneLastField.getText().replaceAll("[^\\d]", "");
                if (input.length() >= 3 && input.length() <= 4) {
                    phoneLastField.setText(input);
                } else {
                    phoneLastField.setText("");
                }
            }
        });
        
        //생년월일 라벨 필드
        birthLabel = new JLabel("생년월일:");
        birthLabel.setBounds(baseX, baseY + 180, 60, 25); // 이메일 필드 아래 50px에 위치
        birthLabel.setFont(customFont);
        birthLabel.setOpaque(true);
        birthLabel.setBackground(new Color(240, 240, 240, 128));
        birthLabel.setForeground(Color.BLACK);
        panel.add(birthLabel);

        // 연도 드롭다운 (연도 선택)
        String[] years = new String[100];
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 100; i++) {
            years[i] = String.valueOf(currentYear - i);
        }
        birthYearDropdown = new JComboBox<>(years);
        birthYearDropdown.setBounds(baseX + 100, baseY + 180, 60, 25);
        panel.add(birthYearDropdown);
        // 월 드롭다운
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = String.valueOf(i + 1); // 1부터 12까지
        }
        monthDropdown = new JComboBox<>(months);
        monthDropdown.setBounds(baseX + 165, baseY + 180, 60, 25);
        panel.add(monthDropdown);
        // 일 드롭다운
        String[] days = new String[31];
        for (int i = 0; i < 31; i++) {
            days[i] = String.valueOf(i + 1); // 1부터 31까지
        }
        dayDropdown = new JComboBox<>(days);
        dayDropdown.setBounds(baseX + 235, baseY + 180, 60, 25);
        panel.add(dayDropdown);
        
        // 성별 라벨 추가
        genderLabel = new JLabel("성별:");
        genderLabel.setBounds(baseX, baseY + 220, 60, 25); // 생년월일 필드 아래 50px에 위치
        genderLabel.setFont(customFont);
        genderLabel.setOpaque(true);
        genderLabel.setBackground(new Color(240, 240, 240, 128));
        genderLabel.setForeground(Color.BLACK);
        panel.add(genderLabel);

        // 성별 라디오 버튼 그룹
        maleButton = new JRadioButton("남");
        maleButton.setBounds(baseX + 100, baseY + 220, 60, 25);
        maleButton.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
        femaleButton = new JRadioButton("여");
        femaleButton.setBounds(baseX + 160, baseY + 220, 60, 25);
        femaleButton.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);

        panel.add(maleButton);
        panel.add(femaleButton);
        
        // 주소 관련 필드 추가
        zipCodeLabel = new JLabel("주소:");
        zipCodeLabel.setBounds(baseX, baseY + 280, 40, 25);
        zipCodeLabel.setFont(customFont);
        zipCodeLabel.setOpaque(true);
        zipCodeLabel.setBackground(new Color(240, 240, 240, 128));
        zipCodeLabel.setForeground(Color.BLACK);
        panel.add(zipCodeLabel);

        zipCodeField = new JTextField(20);
        zipCodeField.setBounds(baseX + 100, baseY + 280, 100, 25);
        panel.add(zipCodeField);

        // 주소 검색 버튼
        addressSearchButton = new JButton("주소 검색");
        addressSearchButton.setBounds(baseX + 210, baseY + 280, 90, 25);
        addressSearchButton.setFont(customFont);
        panel.add(addressSearchButton);

        // 주소 필드 추가
        addressField = new JTextField(20);
        addressField.setBounds(baseX + 100, baseY + 320, 200, 25);
        panel.add(addressField);
        
        // 상세 주소 필드
        detailAddressField = new JTextField(20);
        detailAddressField.setBounds(baseX + 100, baseY + 360, 200, 25);
        panel.add(detailAddressField);
        addPlaceholderText(detailAddressField, "상세 주소를 적어주세요. (동, 호)"); // Placeholder 추가

     // 주소 검색 버튼 ActionListener 추가
        addressSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 팝업창 생성
                JDialog searchDialog = new JDialog();
                searchDialog.setTitle("주소 검색");
                searchDialog.setSize(300, 400);
                searchDialog.setLayout(new BorderLayout());
                searchDialog.setLocationRelativeTo(null); // 화면 중앙에 위치

                // 검색 패널
                JPanel searchPanel = new JPanel();
                searchPanel.setLayout(new FlowLayout());
                JTextField searchField = new JTextField(15);
                JButton searchButton = new JButton("검색");
                searchPanel.add(searchField);
                searchPanel.add(searchButton);

                // 결과 패널
                DefaultListModel<String> listModel = new DefaultListModel<>();
                JList<String> resultList = new JList<>(listModel);
                JScrollPane scrollPane = new JScrollPane(resultList);
                scrollPane.setPreferredSize(new Dimension(280, 200));

                // 팝업창에 패널 추가
                searchDialog.add(searchPanel, BorderLayout.NORTH);
                searchDialog.add(scrollPane, BorderLayout.CENTER);

                // 검색 버튼 ActionListener 추가
                searchButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String searchText = searchField.getText();
                        if (!searchText.isEmpty()) {
                            // API 호출하여 주소 검색
                            result = new ArrayList<>();
                            int[] n = new int[2];
                            // find 함수 호출
                            MapApi.find(searchText, 1, 17, result, n); // 페이지 번호는 1, 페이지당 10개 표시

                            // 결과 리스트에 추가
                            listModel.clear();
                            for (int i = 0; i < n[0]; i++) {
                                String item = result.get(i * 3 + 1) + " (" + result.get(i * 3 + 0) + ")"; // 도로명주소 (우편번호)
                                listModel.addElement(item);
                            }
                        }
                    }
                });

                // 결과 리스트 클릭 시 동작
                resultList.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) { // 더블 클릭 시
                            String selectedValue = resultList.getSelectedValue();
                            if (selectedValue != null) {
                                // 선택한 주소에서 도로명주소와 우편번호 추출
                                String[] parts = selectedValue.split(" \\("); // "주소 (부가정보)" 형식

                                if (parts.length > 0) {
                                    String address = parts[0].trim(); // 주소 부분
                                    String zipCode = ""; // 초기화
                                    String detailAddress = ""; // 상세 주소 초기화

                                    // 부가정보와 우편번호 부분 분리
                                    if (parts.length > 1) {
                                        zipCode = result.get(0); // 첫 번째 요소가 우편번호

                                        // 부가정보에서 괄호 제거
                                        detailAddress = parts[1].replace(")", "").trim(); // 괄호 제거 후 부가정보
                                    }

                                    // 필드에 자동 입력
                                    zipCodeField.setText(zipCode);
                                    addressField.setText(address + " " + detailAddress); // 도로명주소 + 부가주소
                                    searchDialog.dispose(); // 팝업창 닫기
                                }
                            }
                        }
                    }
                });


                searchDialog.setVisible(true); // 팝업창 보이기
            }
        });

        // 오목돌 선택 라디오 버튼
        stoneLabel = new JLabel("사용할 캐릭터를 선택하세요:");
        stoneLabel.setBounds(baseX, baseY + 420, 150, 25);
        stoneLabel.setFont(customFont);
        stoneLabel.setOpaque(true);
        stoneLabel.setBackground(new Color(240, 240, 240, 128));
        stoneLabel.setForeground(Color.BLACK);
        panel.add(stoneLabel);

        // 오목돌 이미지 라디오 버튼 추가 (이미지 사용)
        stone1 = new CharacterRadioButton(new ImageIcon(getClass().getResource("../img/치이카와.png")));
        stone1.setBounds(baseX-120, baseY + 450, 100, 100);
        panel.add(stone1);

        stone2 = new CharacterRadioButton(new ImageIcon(getClass().getResource("../img/하치와레.png")));
        stone2.setBounds(baseX, baseY + 450, 100, 100);
        panel.add(stone2);

        stone3 = new CharacterRadioButton(new ImageIcon(getClass().getResource("../img/우사기.png")));
        stone3.setBounds(baseX + 120, baseY + 450, 100, 100);
        panel.add(stone3);

        stone4 = new CharacterRadioButton(new ImageIcon(getClass().getResource("../img/용사.png")));
        stone4.setBounds(baseX + 240, baseY + 450, 100, 100);
        panel.add(stone4);

        stone5 = new CharacterRadioButton(new ImageIcon(getClass().getResource("../img/모몽가.png")));
        stone5.setBounds(baseX + 360, baseY + 450, 100, 100);
        panel.add(stone5);

        stone6 = new CharacterRadioButton(new ImageIcon(getClass().getResource("../img/밤토리.png")));
        stone6.setBounds(baseX + 480, baseY + 450, 100, 100);
        panel.add(stone6);

        // 버튼 그룹화
        ButtonGroup stoneGroup = new ButtonGroup();
        stoneGroup.add(stone1);
        stoneGroup.add(stone2);
        stoneGroup.add(stone3);
        stoneGroup.add(stone4);
        stoneGroup.add(stone5);
        stoneGroup.add(stone6);

        
        //가입하기 버튼 클릭 시, db 전송
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = newIdField.getText();
                String password = newPwField.getText();
                String rePassword = rePwField.getText();
                String nickname = nicknameField.getText();
                String emailUserPart = emailFieldLeft.getText();
                String emailDomainPart = emailFieldRight.getText();
                String email = emailUserPart + "@" + emailDomainPart;
                String phone = phoneDropdown.getSelectedItem() + "-" + phoneMiddleField.getText() + "-" + phoneLastField.getText();
                String birth = birthYearDropdown.getSelectedItem() + "/" + monthDropdown.getSelectedItem() + "/" + dayDropdown.getSelectedItem();
                String zipCode = zipCodeField.getText().trim();
                String address = addressField.getText().trim();
                String detailAddress = detailAddressField.getText().trim();

                // 각 필드를 공백 한 칸씩 띄워서 하나의 주소로 합치기
                String fullAddress = zipCode + ", " + address + " " + detailAddress;
                final Integer[] genPick = new Integer[1]; // 포장객체 배열로 초기화
                int pick = 1; // 기본값, 선택된 라디오 버튼에 따라 설정할 것

                // 비밀번호가 일치하는지 확인
                if (!password.equals(rePassword)) {
                    passwordMessageLabel.setText("비밀번호가 일치하지 않습니다.");
                    passwordMessageLabel.setForeground(Color.RED);
                    return;
                }
                
                if(email.equals("@")) {
                	showCustomMessageDialog("알림", "이메일을 입력해주세요!", "", false);
                	return;
                }

                if (maleButton.isSelected()) genPick[0] = 1;
                else if (femaleButton.isSelected()) genPick[0] = 2;
                else genPick[0]= null;
                
                // 선택된 라디오 버튼에서 pick 값 설정
                if (stone1.isSelected()) pick = 1;
                else if (stone2.isSelected()) pick = 2;
                else if (stone3.isSelected()) pick = 3;
                else if (stone4.isSelected()) pick = 4;
                else if (stone5.isSelected()) pick = 5;
                else if (stone6.isSelected()) pick = 6;

                // 프로필 이미지가 선택된 경우 Blob으로 저장
                if (bufferedImage != null) {
                    // 프로필 이미지를 DB에 저장
                    try {
                        // 이미지 파일을 임시로 저장
                        File tempImageFile = new File("src/main/java/img/profile_image.png");
                        ImageIO.write(bufferedImage, "png", tempImageFile); // BufferedImage를 파일로 저장
                        
                        // 사용자 정보를 DB에 저장
                        registerUser(id, password, nickname, email, phone, fullAddress, pick, birth, genPick[0], tempImageFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showCustomMessageDialog("회원가입 실패", "회원가입 실패!", "이미지 저장 오류", false);
                    }
                } else {
                    // 사용자 정보를 DB에 저장 (이미지 없음)
                    registerUser(id, password, nickname, email, phone, fullAddress, pick, birth, genPick[0], null);
                }
            }
        });
        
        // 링크 라벨 생성
        linkLabel = new JLabel("<html><u>이곳을 클릭하여 도움말 보기</u></html>"); // HTML로 밑줄 추가
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서 변경
        linkLabel.setBounds(baseX+140, baseY + 600, 200, 30); // 위치 설정
        linkLabel.setFont(new Font("맑은 고딕", Font.BOLD,11));
        linkLabel.setForeground(new Color(230,230,230));

        // 링크 라벨 클릭 시 HTML 파일 열기
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openHtmlFile("help.html");
            }
        });

        // 패널에 라벨 추가
        panel.add(linkLabel);


        // 돌아가기 버튼 클릭 시 로그인 화면으로 돌아가기
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	resetToLoginScreen();
            }
        });
        
        
        
        // 프로필 이미지 라벨 및 빈 정사각형 설정
        profileImageLabel = new JLabel(" 프로필 이미지");
        profileImageLabel.setBounds(baseX - 165, baseY - 80, 80, 25); // X축으로 왼쪽으로 설정
        profileImageLabel.setFont(customFont);
        profileImageLabel.setOpaque(true);
        profileImageLabel.setBackground(new Color(240, 240, 240, 128));
        profileImageLabel.setForeground(Color.BLACK);
        panel.add(profileImageLabel);

        // 빈 정사각형을 위한 패널
        profileImagePanel = new JPanel();
        profileImagePanel.setLayout(null); // 자유 배치
        profileImagePanel.setBounds(baseX - 200, baseY - 50, 150, 150); // 위치 및 크기 설정
        profileImagePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 테두리 추가
        profileImagePanel.setBackground(Color.LIGHT_GRAY); // 배경색 설정
        panel.add(profileImagePanel);

        // 프로필 이미지를 표시할 레이블
        imageDisplayLabel = new JLabel();
        imageDisplayLabel.setPreferredSize(new Dimension(150, 150)); // 크기 설정
        imageDisplayLabel.setOpaque(false); // 배경을 투명하게 설정
        imageDisplayLabel.setBounds(0,0,150,150);
        profileImagePanel.add(imageDisplayLabel);


        // 선택하기 버튼
        selectImageButton = new JButton("이미지 선택");
        selectImageButton.setBounds(baseX - 175, baseY + 110, 100, 25);
        selectImageButton.setFont(customFont);
        panel.add(selectImageButton);
        
        resizeButton = new JButton("직접 리사이즈");
        resizeButton.setBounds(baseX - 175, baseY + 137, 100, 25); // Y축을 조정하여 버튼을 위치시킴
        resizeButton.setFont(customFont);
        resizeButton.setVisible(false); // 처음엔 숨김
        panel.add(resizeButton);

        // "이미지 선택"  버튼 클릭 시 파일 선택기 열기
        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // 파일 선택 필터 추가 (JPG, PNG, JPEG)
                fileChooser.setFileFilter(new FileNameExtensionFilter("이미지 파일 (jpg, jpeg, png)", "jpg", "png", "jpeg"));
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        // 선택한 이미지를 BufferedImage로 임시 저장
                        bufferedImage = ImageIO.read(selectedFile); // 멤버 변수에 저장
                        
                        // ThumbnailCreate 클래스에서 이미지를 처리하는 메소드 호출
                        ThumbnailCreate.createThumbnail(bufferedImage, "profile_image.png"); // 패키지 안에 저장
                        BufferedImage resizedImage = ImageIO.read(new File("src/main/java/img/profile_image.png"));//저장된 이미지 불러오기
                        Image scaledImage = resizedImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        imageDisplayLabel.setIcon(new ImageIcon(scaledImage));
                        resizeButton.setVisible(true);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        
     // "직접 리사이즈" 버튼 클릭 시
        resizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // "직접 리사이즈" 버튼을 클릭하면 새로운 창을 띄운다.
                CropImageWindow cropWindow = new CropImageWindow((JFrame) panel.getTopLevelAncestor(), bufferedImage);
                cropWindow.setVisible(true);

                // 잘라낸 이미지를 반환받아 새로운 변수에 저장
                BufferedImage croppedImage = cropWindow.getCroppedImage();

                // 잘라낸 이미지를 저장
                try {
                    ImageIO.write(croppedImage, "png", new File("src/main/java/img/profile_image.png"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                // 잘라낸 이미지를 레이블에 표시
                ImageIcon croppedIcon = new ImageIcon(croppedImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
                imageDisplayLabel.setIcon(croppedIcon);
            }
        });
        
        // 변경 사항을 반영하기 위해 다시 그리기
        panel.revalidate();
        panel.repaint();
    }

    // 서브 클래스 생성하여 라디오 버튼에 흰 박스 추가
    private class CharacterRadioButton extends JRadioButton {
		private static final long serialVersionUID = 1L;
		private ImageIcon icon;

        public CharacterRadioButton(ImageIcon icon) {
            super(); // 기본 아이콘 설정하지 않음
            this.icon = icon;
            setPreferredSize(new Dimension(100, 100));
            setOpaque(false); // 버튼 배경 투명화
            setFocusPainted(false); // 포커스 표시 제거
            setBorderPainted(false); // 경계 표시 제거
            setContentAreaFilled(false); // 내용 영역 채우기 비활성화
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (icon != null) {
                g.drawImage(icon.getImage(), 0, 0, 100, 100, null);
            }

            if (isSelected()) {
                g.setColor(new Color(0, 0, 0, 100)); 
                g.fillOval(0, 0, 100, 100);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame();
            }
        });
    }
}