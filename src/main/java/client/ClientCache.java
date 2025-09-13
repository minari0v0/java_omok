package client;

import server.DBmanager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientCache {
    public static class RankingData {
        private int rankPosition;
        private String nickname;
        private double winRate;
        private int pick;

        public RankingData(int rankPosition, String nickname, double winRate, int pick) {
            this.rankPosition = rankPosition;
            this.nickname = nickname;
            this.winRate = winRate;
            this.pick = pick;
        }

        public int getRankPosition() {
            return rankPosition;
        }

        public String getNickname() {
            return nickname;
        }

        public double getWinRate() {
            return winRate;
        }

        public int getPick() {
            return pick;
        }
    }

    public List<RankingData> getRankingData() {
        List<RankingData> rankingList = new ArrayList<>();
        DBmanager dbManager = new DBmanager();
        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT r.rank_position, r.uid, u.nickname, r.win_rate, u.pick " +
                 "FROM ranking r " +
                 "JOIN user u ON r.uid = u.uid " +  // uid로 user 테이블의 nickname 가져오기
                 "ORDER BY r.rank_position ASC " +  // 순위 기준 오름차순
                 "LIMIT 5"  // 상위 5명
             );
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int rankPosition = rs.getInt("r.rank_position");  // r.rank_position
                String nickname = rs.getString("u.nickname");    // u.nickname
                double winRate = rs.getDouble("r.win_rate");      // r.win_rate
                int pick = rs.getInt("u.pick");                   // r.pick
                rankingList.add(new RankingData(rankPosition, nickname, winRate, pick));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rankingList;
    }

}
