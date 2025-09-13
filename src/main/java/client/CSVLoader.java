package client;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {
    public List<Town> loadTownData(String filePath) {
        List<Town> towns = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // 헤더 스킵
            br.readLine();

            // CSV 파일에서 한 줄씩 읽기
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 5) {
                    String regionParent = values[1].trim();
                    String regionChild = values[2].trim();
                    int nx = Integer.parseInt(values[3].trim());
                    int ny = Integer.parseInt(values[4].trim());

                    towns.add(new Town(regionParent, regionChild, nx, ny));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return towns;
    }
}
