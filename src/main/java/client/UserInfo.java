package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;
import GUI.FontLoader;

public class UserInfo extends JFrame {
	public UserInfo() {
		
	}
    public UserInfo(String nickname, int wins, int losses, byte[] profileImgData) {
        setTitle(nickname + "님의 정보");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        FontLoader fontLoader30 = new FontLoader("src/main/java/img/Cookie.ttf", 30f);
        Font customFont30 = fontLoader30.getCustomFont(); // 커스텀 글꼴 가져오기
        
        FontLoader fontLoader40B = new FontLoader("src/main/java/img/CookieB.ttf", 40f);
        Font customFont40 = fontLoader40B.getCustomFont(); // 커스텀 글꼴 가져오기
        
        // 닉네임 라벨
        JLabel nicknameLabel = new JLabel(nickname+" 님의 정보");
        nicknameLabel.setHorizontalAlignment(JLabel.CENTER);
        nicknameLabel.setFont(customFont40);

        // 승패 라벨
        JLabel recordLabel = new JLabel(wins + "승 " + losses + "패");
        recordLabel.setHorizontalAlignment(JLabel.CENTER);
        recordLabel.setFont(customFont30);
        
        // 승률 계산
        int winRate = 0;
        if (wins + losses > 0) {
            winRate = (int) Math.floor((double) wins / (wins + losses) * 100);
        }
        
        
        JLabel winRateTextLabel = new JLabel("승률: ");
        winRateTextLabel.setFont(customFont30);
        JLabel winRateLabel = new JLabel(winRate + "%");
        winRateLabel.setFont(customFont40);
        JPanel winRatePanel = new JPanel();
        winRatePanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // 중앙 정렬
        winRatePanel.add(winRateTextLabel);
        winRatePanel.add(winRateLabel);
        JPanel recordPanel = new JPanel();
        recordPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // 중앙 정렬
        recordPanel.add(recordLabel);

        // 프로필 이미지 표시
        JLabel profileImgLabel = new JLabel();
        Image profileImg = null; // 프로필 이미지를 저장할 변수

        if (profileImgData != null) {
            try {
                profileImg = ImageIO.read(new ByteArrayInputStream(profileImgData));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 프로필 이미지가 null일 경우 기본 이미지로 대체
        if (profileImg == null) {
            try {
                profileImg = ImageIO.read(new File("src/main/java/img/default_proImg.png"));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("기본 프로필 이미지를 로드하는 데 실패했습니다.");
            }
        }

        // 이미지 크기 조정
        if (profileImg != null) {
            profileImg = profileImg.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            profileImgLabel.setIcon(new ImageIcon(profileImg));
        } else {
            System.err.println("프로필 이미지가 null입니다. 기본 이미지가 설정되지 않았습니다.");
        }
        profileImgLabel.setHorizontalAlignment(JLabel.CENTER);
        
        profileImgLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        nicknameLabel.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(recordPanel);
        centerPanel.add(winRatePanel);
        // 컴포넌트 추가
        add(nicknameLabel, BorderLayout.NORTH);
        add(profileImgLabel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
