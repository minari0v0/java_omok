package client;

import javax.swing.*;
import GUI.CustomPanel;
import GUI.FontLoader;
import java.awt.*;
import java.util.List;

public class RankingPopup extends JFrame {

    private static final long serialVersionUID = 1L;
    FontLoader fontLoader = new FontLoader("src/main/java/img/Cookie.ttf", 18f);
    Font customFont = fontLoader.getCustomFont();

    public RankingPopup(List<ClientCache.RankingData> rankingDataList) {
        setTitle("랭킹");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350,500);
        setLocationRelativeTo(null);  // 화면 중앙에 팝업 띄우기
        setLayout(new BorderLayout());

        // 랭킹 제목 추가 (중앙 정렬)
        JPanel titlePanel= new JPanel();
        JLabel titleLabel = new JLabel("랭킹");
        titleLabel.setFont(customFont);
        titlePanel.setBackground(new Color(252, 250, 238));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        // 메인 패널을 생성해서 순위 패널을 담을 공간을 만듦
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(252, 250, 238));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);

        // 랭킹 데이터가 없다면 안내 메시지를 추가
        if (rankingDataList.isEmpty()) {
            JLabel noRankLabel = new JLabel("랭킹이 없습니다.");
            noRankLabel.setFont(customFont);
            mainPanel.add(noRankLabel);
        } else {
            int displayedRankCount = 0;  // 표시된 랭킹 개수
            for (ClientCache.RankingData data : rankingDataList) {
                if (displayedRankCount >= 5) break;  // 5위까지만 표시
                mainPanel.add(createRankingPanel(data));
                displayedRankCount++;
            }

            // 순위가 적을 경우 공백 추가 (3위, 4위, 5위가 없다면 빈 패널 추가)
            while (displayedRankCount < 5) {
                JPanel emptyPanel = new JPanel();
                emptyPanel.setPreferredSize(new Dimension(270, 80));  // 순위 패널 크기와 동일하게 설정
                emptyPanel.setBackground(new Color(252, 250, 238));
                mainPanel.add(emptyPanel);
                displayedRankCount++;
            }
        }

        setVisible(true);
    }

    private JPanel createRankingPanel(ClientCache.RankingData data) {
    	Image backgroundImage = null;

        // 순위별 배경 이미지 설정
        if (data.getRankPosition() == 1) {
            backgroundImage = new ImageIcon("src/main/java/img/금메달배경.jpg").getImage();
        } else if (data.getRankPosition() == 2) {
            backgroundImage = new ImageIcon("src/main/java/img/은메달배경.jpg").getImage();
        }

        CustomPanel panel = new CustomPanel(backgroundImage);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setPreferredSize(new Dimension(270, 80));

        // 3등일 경우 배경 색상 설정
        if (data.getRankPosition() == 3) {
            panel.setBackground(new Color(205, 127, 50)); // 3등 배경 색상
            panel.setOpaque(true); // 불투명하게 설정
        } else if (data.getRankPosition() >= 4) {
            panel.setBackground(new Color(240, 240, 240)); // 4등 및 5등 배경 색상
            panel.setOpaque(true); // 불투명하게 설정
        } else {
            panel.setOpaque(false); // 배경 이미지가 있을 경우 투명하게 설정
        }

        // 둥근 테두리 설정
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));

        // 메달 이미지 (30x30)
        JLabel medalLabel = new JLabel();
        if (data.getRankPosition() == 1) {
            medalLabel.setIcon(new ImageIcon(new ImageIcon("src/main/java/img/금.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        } else if (data.getRankPosition() == 2) {
            medalLabel.setIcon(new ImageIcon(new ImageIcon("src/main/java/img/은.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        } else if (data.getRankPosition() == 3) {
            medalLabel.setIcon(new ImageIcon(new ImageIcon("src/main/java/img/동.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        }
        panel.add(medalLabel);

        // pick에 따른 유저 이미지 (20x20)
        JLabel userImageLabel = new JLabel();
        String pickImagePath = getPickImagePath(data.getPick());
        if (pickImagePath != null) {
            userImageLabel.setIcon(new ImageIcon(new ImageIcon(pickImagePath).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH)));
        }
        panel.add(userImageLabel);

        // 닉네임과 승률을 HTML 형식으로 표시
        String labelText = String.format("<html>%s<br>승률: %.2f%%</html>", data.getNickname(), data.getWinRate());
        JLabel rankInfoLabel = new JLabel(labelText);
        rankInfoLabel.setFont(customFont);
        panel.add(rankInfoLabel);

        return panel;
    }

    private String getPickImagePath(int pick) {
        switch (pick) {
            case 1: return "src/main/java/img/치이카와.png";
            case 2: return "src/main/java/img/하치와레.png";
            case 3: return "src/main/java/img/우사기.png";
            case 4: return "src/main/java/img/용사.png";
            case 5: return "src/main/java/img/모몽가.png";
            case 6: return "src/main/java/img/밤토리.png";
            default: return null;  // 잘못된 pick 값일 경우 null 반환
        }
    }
}
