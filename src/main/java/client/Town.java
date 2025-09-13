package client;
public class Town {
    private String regionParent;
    private String regionChild;
    private int nx;
    private int ny;

    // 생성자
    public Town(String regionParent, String regionChild, int nx, int ny) {
        this.regionParent = regionParent;
        this.regionChild = regionChild;
        this.nx = nx;
        this.ny = ny;
    }

    // Getter 메서드
    public String getRegionParent() { return regionParent; }
    public String getRegionChild() { return regionChild; }
    public int getNx() { return nx; }
    public int getNy() { return ny; }

    // 출력하기 쉽게 toString() 오버라이드 가능
    @Override
    public String toString() {
        return regionParent + " " + regionChild;
    }
}