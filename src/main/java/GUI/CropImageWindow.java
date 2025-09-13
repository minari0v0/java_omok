package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

public class CropImageWindow extends JDialog {
	private static final long serialVersionUID = 1L;
	private BufferedImage originalImage;
    private Rectangle cropArea;
    private Point startPoint;
    private Point endPoint;

    public CropImageWindow(JFrame parent, BufferedImage originalImage) {
        super(parent, "직접 리사이즈", true);
        this.originalImage = originalImage;

        // 창 크기와 위치 설정
        setSize(originalImage.getWidth(), originalImage.getHeight());
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // 창에 마우스 리스너 추가 (드래그를 통해 잘라내기 영역 설정)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();  // 드래그 시작점
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endPoint = e.getPoint(); // 드래그 종료점
                cropArea = new Rectangle(Math.min(startPoint.x, endPoint.x), Math.min(startPoint.y, endPoint.y),
                                         Math.abs(startPoint.x - endPoint.x), Math.abs(startPoint.y - endPoint.y));

                // 잘라낸 이미지를 처리하는 부분
                BufferedImage croppedImage = cropImage(originalImage, cropArea);
                saveCroppedImage(croppedImage);
                dispose();
            }
        });

        // 마우스 리스너로 드래그하는 동안 영역을 그려서 잘라낼 부분을 시각적으로 확인
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endPoint = e.getPoint();
                repaint(); // 드래그 중에 계속 갱신해서 잘라낼 부분 표시
            }
        });

        // 이미지를 표시하는 패널
        JPanel imagePanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(originalImage, 0, 0, null);

                if (startPoint != null && endPoint != null) {
                    // 잘라낼 영역을 보여주는 사각형
                    g.setColor(Color.RED);
                    g.drawRect(Math.min(startPoint.x, endPoint.x), Math.min(startPoint.y, endPoint.y),
                               Math.abs(startPoint.x - endPoint.x), Math.abs(startPoint.y - endPoint.y));
                }
            }
        };

        // 패널을 창에 추가
        add(imagePanel);
    }

    // 이미지 잘라내기
    private BufferedImage cropImage(BufferedImage image, Rectangle cropArea) {
        return image.getSubimage(cropArea.x, cropArea.y, cropArea.width, cropArea.height);
    }

    // 잘라낸 이미지 저장
    private void saveCroppedImage(BufferedImage croppedImage) {
        try {
            // "profile.png"로 저장
            ImageIO.write(croppedImage, "png", new File("src/main/java/img/profile.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 잘라낸 이미지를 반환
    public BufferedImage getCroppedImage() {
        return originalImage.getSubimage(cropArea.x, cropArea.y, cropArea.width, cropArea.height);
    }
}
