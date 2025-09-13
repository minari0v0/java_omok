package client;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;

public class ThumbnailCreate {
    public static void createThumbnail(BufferedImage originalImage, String outputFileName) {
        try {
            // 원본 이미지 크기 확인
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            BufferedImage resizedImage;
            
            // 이미지 비율을 유지하면서 최소 너비나 높이를 150으로 맞춤
            if (originalWidth > originalHeight) {
                // 가로가 더 크면 높이를 150으로 맞추고 비율 유지
                resizedImage = Thumbnails.of(originalImage)
                                         .height(150)
                                         .keepAspectRatio(true)
                                         .asBufferedImage();
            } else {
                // 세로가 더 크면 너비를 150으로 맞추고 비율 유지
                resizedImage = Thumbnails.of(originalImage)
                                         .width(150)
                                         .keepAspectRatio(true)
                                         .asBufferedImage();
            }

            // 리사이즈 후 크기 확인
            int resizedWidth = resizedImage.getWidth();
            int resizedHeight = resizedImage.getHeight();

            // 자를 좌표 계산 (가운데에서 150x150으로 자름)
            int cropX = (resizedWidth - 150) / 2;  // 가로가 크면 좌우에서 잘라냄
            int cropY = (resizedHeight - 150) / 2; // 세로가 크면 위아래에서 잘라냄

            BufferedImage croppedImage = resizedImage.getSubimage(cropX, cropY, 150, 150);

            // 잘라낸 이미지를 파일로 저장
            ImageIO.write(croppedImage, "png", new File("src/main/java/img/" + outputFileName));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (RasterFormatException e) {
            System.out.println("이미지를 자를 수 없습니다: " + e.getMessage());
        }
    }
}
