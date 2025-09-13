package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ChatBubblePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private String message;
    private ImageIcon emojiIcon; // 이모티콘 하나만 처리
    private Color backgroundColor;
    private boolean isMine; // 본인 메시지 여부
    FontLoader fontLoader1 = new FontLoader("src/main/java/img/돌기마요.ttf", 18f);
    Font customFont1 = fontLoader1.getCustomFont();

    public ChatBubblePanel(String message, ImageIcon emojiIcon, Color backgroundColor, boolean isMine) {
        this.message = message;
        this.emojiIcon = emojiIcon;
        this.backgroundColor = backgroundColor;
        this.isMine = isMine;
        setOpaque(false); // 패널을 투명하게 설정
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // 세로 방향 레이아웃
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2d.setColor(backgroundColor);
        g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15)); // 둥글게 처리

        g2d.setColor(Color.BLACK);
        g2d.setFont(customFont1); // 커스텀 폰트 설정
        FontMetrics metrics = g2d.getFontMetrics();
        int lineHeight = metrics.getHeight();
        int y = lineHeight;

        // 오른쪽 패딩
        int padding = 15;
        int availableWidth = 250 - padding; // 패딩을 빼고 너비 계산

        // 메시지를 문자 단위로 처리
        StringBuilder line = new StringBuilder();
        for (char c : message.toCharArray()) {
            String str = String.valueOf(c);
            // 현재 줄에 문자 추가해도 너비가 250을 초과하지 않는지 확인
            if (metrics.stringWidth(line + str) < availableWidth) {
                line.append(str); // 문자 추가
            } else {
                // 현재 줄 그리기
                g2d.drawString(line.toString(), 10, y); // 패딩 적용
                y += lineHeight; // 다음 줄로 이동
                line = new StringBuilder(str); // 새 줄 시작
            }
        }
        // 마지막 줄 그리기
        if (line.length() > 0) {
            g2d.drawString(line.toString(), 10, y); // 패딩 적용
        }

        // 이모티콘 높이 추가
        if (emojiIcon != null) {
            y += lineHeight; // 이모티콘 높이를 다음 줄로 이동
            g2d.drawImage(emojiIcon.getImage(), 10, y - lineHeight, this); // 이모티콘 그리기
        }
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics metrics = getFontMetrics(customFont1); // 커스텀 폰트로 높이 계산
        int width = Math.min(250, metrics.stringWidth(message) + 20); // 최대 너비 250
        int height = metrics.getHeight() * (countLines() + (emojiIcon != null ? 2 : 1)); // 높이 조정
        return new Dimension(width, height); // 패널의 크기
    }

    private int countLines() {
        FontMetrics metrics = getFontMetrics(customFont1); // 커스텀 폰트로 높이 계산
        StringBuilder line = new StringBuilder();
        int lines = 1; // 최소 1줄

        for (char c : message.toCharArray()) {
            line.append(c);
            if (metrics.stringWidth(line.toString()) >= 250) { // 250 픽셀 초과 시
                lines++;
                line = new StringBuilder(String.valueOf(c)); // 새 줄 시작
            }
        }

        return lines;
    }

    public boolean isMine() {
        return isMine;
    }
}
