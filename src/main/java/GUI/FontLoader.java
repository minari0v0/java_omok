package GUI;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontLoader {
    private Font customFont;

    public FontLoader(String fontPath, float size) {
        loadFont(fontPath, size);
    }

    private void loadFont(String fontPath, float size) {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(size);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);  // 글꼴을 시스템에 등록
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    public Font getCustomFont() {
        return customFont;
    }
}
