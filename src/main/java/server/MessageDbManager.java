package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MessageDbManager {
    private DBmanager dbManager;

    public MessageDbManager() {
        dbManager = new DBmanager();  // 기존 DBmanager 인스턴스를 사용
    }

    // 메시지를 DB에 저장하는 메소드 (로비채팅인 브로드캐스팅 경우)
    public void saveMessage(String type, String sender, String message) {
        String sql = "INSERT INTO chat_messages (type, sender, recipient, message) VALUES (?, ?, NULL, ?)";

        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, type);
            pstmt.setString(2, sender);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 메시지를 DB에 저장하는 메소드 (1대1 채팅일 경우)
    public void saveMessage(String type, String sender, String recipient, String message) {
        String sql = "INSERT INTO chat_messages (type, sender, recipient, message) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, type);
            pstmt.setString(2, sender);
            pstmt.setString(3, recipient);
            pstmt.setString(4, message);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // 특정 키워드로 메시지를 검색하는 메소드
    public List<String> fetchMessages(String sender, String recipient, String keyword) {
        List<String> messages = new ArrayList<>();
        String sql = "SELECT message FROM chat_messages WHERE type = 'PRIV' AND sender = ? AND recipient = ? AND message LIKE ?";

        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sender);
            pstmt.setString(2, recipient);
            pstmt.setString(3, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(rs.getString("message"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages.isEmpty() ? null : messages;
    }
    
    //특정 사용자의 채팅 내역을 가져오는 메소드
    public List<String> searchMessage(String nickname) {
        List<String> messages = new ArrayList<>();
        String sql = "SELECT id, sender, recipient, message, timestamp FROM chat_messages WHERE sender = ?";

        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nickname);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 각 값이 null일 경우 처리하여 형식에 맞게 문자열로 변환
                	int id = rs.getInt("id");
                    String sender = rs.getString("sender");
                    String recipient = rs.getString("recipient");
                    String message = rs.getString("message");
                    Timestamp timestamp = rs.getTimestamp("timestamp");

                    // null 체크 후 안전하게 처리
                    String formattedMessage = String.format(
                            "ID: %d / 발신자: %s / 수신자: %s / 메시지: %s / 날짜: %s",
                            id, // ID 포함
                            (sender != null ? sender.trim() : ""),
                            (recipient != null ? recipient.trim() : ""),
                            (message != null ? message.trim() : ""),
                            (timestamp != null ? timestamp.toString() : "알 수 없음")
                    );

                    messages.add(formattedMessage);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages.isEmpty() ? null : messages; // 결과가 없으면 null 반환
    }
    
  //특정 사용자의 삭제된 메시지를 가져오는 메소드임 아 ㅋㅋ
    public List<String> searchBinMessages(String nickname) {
        List<String> messages = new ArrayList<>();
        String sql = "SELECT id, sender, recipient, message, timestamp FROM chat_messages_bin WHERE sender = ?";

        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nickname);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 각 값이 null일 경우 처리하여 형식에 맞게 문자열로 변환
                	int id = rs.getInt("id");
                    String sender = rs.getString("sender");
                    String recipient = rs.getString("recipient");
                    String message = rs.getString("message");
                    Timestamp timestamp = rs.getTimestamp("timestamp");

                    // null 체크 후 안전하게 처리
                    String formattedMessage = String.format(
                            "ID: %d / 발신자: %s / 수신자: %s / 메시지: %s / 날짜: %s",
                            id, // ID 포함
                            (sender != null ? sender.trim() : ""),
                            (recipient != null ? recipient.trim() : ""),
                            (message != null ? message.trim() : ""),
                            (timestamp != null ? timestamp.toString() : "알 수 없음")
                    );

                    messages.add(formattedMessage);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages.isEmpty() ? null : messages; // 결과가 없으면 null 반환
    }
    
    // 메시지를 메시지 휴지통에 옮기는 것임
    public void moveMessageToBin(String id) {
        String query = "INSERT INTO chat_messages_bin (id, type, sender, recipient, message, timestamp) "
                     + "SELECT id, type, sender, recipient, message, timestamp "
                     + "FROM chat_messages "
                     + "WHERE id = ?";

        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
            
            // 메시지 삭제
            String deleteQuery = "DELETE FROM chat_messages WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, id);
                deleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    //moveMessageToBin의 반대 함수
    public void restoreMessageFromBin(String id) {
        String query = "INSERT INTO chat_messages (id, type, sender, recipient, message, timestamp) "
                     + "SELECT id, type, sender, recipient, message, timestamp "
                     + "FROM chat_messages_bin "
                     + "WHERE id = ?";

        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
            
            // 메시지 삭제
            String deleteQuery = "DELETE FROM chat_messages_bin WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, id);
                deleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    //관리자 비번 변경 db
    public boolean changeAdPw(String newPassword) {
        String sql = "UPDATE admin SET pw = ?, modified_at = CURRENT_TIMESTAMP WHERE id = 'admin'";
        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // 업데이트 성공 여부 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 실패 시 false 반환
        }
    }
    
    //유저와 랭킹 테이블 한꺼번에 처리(게임 끝난 후) 수정해야함 **********
    public void updateUserStatsAndRanking(int uid, int win, int lose, int play) {
        try (Connection conn = dbManager.connect()) {
            conn.setAutoCommit(false);

            // Step 1: Update user stats
            String updateUserQuery = "UPDATE user SET win = ?, lose = ?, play = ? WHERE uid = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateUserQuery)) {
                ps.setInt(1, win);
                ps.setInt(2, lose);
                ps.setInt(3, play);
                ps.setInt(4, uid);
                ps.executeUpdate();
            }

            // Step 2: Update ranking if play >= 10
            String updateRankingQuery = play >= 10
                    ? "INSERT INTO ranking (uid, rank_position, win_rate, games_played, wins, loses) " +
                      "VALUES (?, " +
                      " (SELECT COUNT(*) + 1 FROM ranking WHERE win_rate > (? / ?) * 100 OR " +
                      " (win_rate = (? / ?) * 100 AND games_played > ?)), " +
                      " (? / ?) * 100, ?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE " +
                      "win_rate = VALUES(win_rate), games_played = VALUES(games_played), wins = VALUES(wins), loses = VALUES(loses)"
                    : "DELETE FROM ranking WHERE uid = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(updateRankingQuery)) {
                if (play >= 10) {
                    ps.setInt(1, uid);
                    ps.setDouble(2, win);
                    ps.setInt(3, play);
                    ps.setDouble(4, win);
                    ps.setInt(5, play);
                    ps.setInt(6, play);
                    ps.setDouble(7, win);
                    ps.setInt(8, play);
                    ps.setInt(9, play);
                    ps.setInt(10, win);
                    ps.setInt(11, lose);
                } else {
                    ps.setInt(1, uid);
                }
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}