package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;

public class BoardLabel extends JLabel {
	private static final long serialVersionUID = 1L;
	private final int startX = 21; // 격자 시작 X (기존보다 3px 우측)
    private final int startY = 19; // 격자 시작 Y (기존보다 3px 아래)
    private final int cellSize = 36; // 격자 간격 (작게 조정, 기존 40 -> 36)
    private final int gridSize = 19; // 오목판 격자 크기 (19x19)

    private int hoverX = -1; // 마우스가 있는 격자 X 좌표
    private int hoverY = -1; // 마우스가 있는 격자 Y 좌표

    private int[][] board = new int[gridSize][gridSize]; // 오목판 상태, 0: 빈 칸, 1: 흑돌, 2: 백돌
    private ImageIcon p1Icon;  // 흑돌 이미지
    private ImageIcon p2Icon;  // 백돌 이미지
    private int stoneSize;

    private BoardClickListener clickListener; // 클릭 리스너 추가

    public interface BoardClickListener {
        void onCellClick(int x, int y); // 클릭 이벤트를 GameRoom으로 전달
    }

    public void setBoardClickListener(BoardClickListener listener) {
        this.clickListener = listener;
    }

    // 아이콘 설정 메서드
    public void setIcons(ImageIcon p1, ImageIcon p2) {
        this.p1Icon = p1;
        this.p2Icon = p2;
        this.stoneSize = 45;
        repaint(); // 아이콘이 설정된 후 다시 그리기
    }

    public BoardLabel(ImageIcon imageIcon) {
        super(imageIcon); // 배경 이미지 설정
        setBounds(0, 0, 690, 690); // 이미지 크기

        // 마우스 이동 이벤트 처리
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                hoverX = (mouseX - startX + cellSize / 2) / cellSize;
                hoverY = (mouseY - startY + cellSize / 2) / cellSize;

                if (hoverX < 0 || hoverX >= gridSize || hoverY < 0 || hoverY >= gridSize) {
                    hoverX = -1;
                    hoverY = -1;
                }

                repaint(); // 화면 갱신
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // 드래그 동작은 필요하지 않음
            }
        });

        // 마우스 클릭 이벤트 처리
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                int clickX = (mouseX - startX + cellSize / 2) / cellSize;
                int clickY = (mouseY - startY + cellSize / 2) / cellSize;

                if (clickX >= 0 && clickX < gridSize && clickY >= 0 && clickY < gridSize) {
                    // 리스너에 클릭된 좌표 전달
                    if (clickListener != null) {
                        clickListener.onCellClick(clickX, clickY);
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 오목판 이미지 그리기
        ImageIcon icon = (ImageIcon) getIcon();
        if (icon != null) {
            g.drawImage(icon.getImage(), 0, 0, null);
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);

        // 격자 그리기
        for (int i = 0; i < gridSize; i++) {
            int x = startX + i * cellSize;
            int y = startY + i * cellSize;

            g2d.drawLine(startX, y, startX + (gridSize - 1) * cellSize, y);
            g2d.drawLine(x, startY, x, startY + (gridSize - 1) * cellSize);
        }

        // 돌 그리기
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (board[i][j] == 1 && p1Icon != null) { // 흑돌
                    g.drawImage(p1Icon.getImage(), startX + i * cellSize - stoneSize / 2, startY + j * cellSize - stoneSize / 2, stoneSize, stoneSize, null);
                } else if (board[i][j] == 2 && p2Icon != null) { // 백돌
                    g.drawImage(p2Icon.getImage(), startX + i * cellSize - stoneSize / 2, startY + j * cellSize - stoneSize / 2, stoneSize, stoneSize, null);
                }
            }
        }

        // 마우스 위치에 빨간 사각형 그리기
        if (hoverX != -1 && hoverY != -1) {
            int rectX = startX + hoverX * cellSize - cellSize / 2;
            int rectY = startY + hoverY * cellSize - cellSize / 2;

            g2d.setColor(Color.RED);
            g2d.drawRect(rectX, rectY, cellSize, cellSize);
        }
    }
    
    // BoardLabel 클래스에 추가
    public void setStone(int x, int y, int stone) {
        if (x >= 0 && x < gridSize && y >= 0 && y < gridSize) {
            // 돌을 board 배열에 추가
            board[x][y] = stone;
        }
    }
    //오목판 지우기
    public void clearBoard() {
        // board 배열 초기화 (0으로 설정)
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                board[i][j] = 0; // 모든 칸을 0으로 초기화
            }
        }
        // 화면을 다시 그려서 돌 이미지들을 지운다.
        repaint();  // repaint() 메소드를 호출하여 다시 그리기
    }
}
