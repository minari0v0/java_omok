package GUI;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class CustomScrollBarUI extends BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {
        // Thumb(스크롤 핸들) 색상
        thumbColor = new Color(104, 109, 118);
        // Track(스크롤 배경) 색상 - 반투명 설정
        trackColor = new Color(104, 109, 118, 128);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        // Thumb를 둥근 모서리를 가진 직사각형으로 그리기
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(thumbColor);
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10); // 둥근 테두리
        g2.dispose();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        // Track을 둥근 모서리를 가진 반투명 직사각형으로 그리기
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(trackColor);
        g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10); // 둥근 테두리
        g2.dispose();
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        // Thumb의 최소 크기 설정
        return new Dimension(5, 50); // 폭을 줄이기 (10)
    }

    @Override
    protected Dimension getMaximumThumbSize() {
        // Thumb의 최대 크기 설정
        return new Dimension(5, Integer.MAX_VALUE); // 폭을 줄이기 (10)
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        // Arrow 버튼 제거
        return createInvisibleButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        // Arrow 버튼 제거
        return createInvisibleButton();
    }

    private JButton createInvisibleButton() {
        // 보이지 않는 버튼 생성
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0)); // 크기를 0으로 설정
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }
}
