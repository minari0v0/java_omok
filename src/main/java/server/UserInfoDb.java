
package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

public class UserInfoDb {
    private DBmanager dbManager;

    public UserInfoDb() {
        dbManager = new DBmanager();
    }

    // 유저 정보를 uid로 조회하는 메소드
    public UserInfoS getUserInfo(int uid) {
        String query = "SELECT nickname, win, lose, profile_img FROM user WHERE uid = ?";
        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, uid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String nickname = rs.getString("nickname");
                int win = rs.getInt("win");
                int lose = rs.getInt("lose");
                byte[] profileImg = rs.getBytes("profile_img");  // 프로필 이미지 BLOB로 가져오기

                // UserInfo 객체 생성 후 반환
                return new UserInfoS(nickname, win, lose, profileImg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 정보가 없을 경우 null 반환
    }

    // 닉네임으로 uid를 조회하는 메소드
    public Integer getUidByNick(String nickname) {
        String query = "SELECT uid FROM user WHERE nickname = ?";
        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nickname);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("uid"); // uid 반환
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 정보가 없을 경우 null 반환
    }
    
    // 사용자 정보의 모든 정보를 uid로 조회하는 메소드 (프로필 이미지를 제외)
    public UserInfoS getUserInfoAll(int uid) {
        String query = "SELECT id, pw, nickname, email, phone, address, birth, pick, win, lose, play, gender, profile_img FROM user WHERE uid = ?";
        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, uid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String id = rs.getString("id");
                String pw = rs.getString("pw");
                String nickname = rs.getString("nickname");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String birth = rs.getString("birth");
                int pick = rs.getInt("pick");
                int win = rs.getInt("win");
                int lose = rs.getInt("lose");
                int play = rs.getInt("play");
                int gender = rs.getInt("gender");
                byte[] profileImg = rs.getBytes("profile_img");

                // UserInfo 객체 생성 후 반환 (프로필 이미지는 포함하지 않음)
                return new UserInfoS(id, pw, nickname, email, phone, address, birth, pick, win, lose, play, gender, profileImg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 정보가 없을 경우 null 반환
    }
    
    // 위 함수에서 모든 사용자의 정보를 조회하는 메소드(마찬가지로 프사제외)
    public List<UserInfoS> getUserInfoAll() {
        String query = "SELECT id, pw, nickname, email, phone, address, birth, pick, win, lose, play, gender FROM user";
        List<UserInfoS> userList = new ArrayList<>();
        
        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                String pw = rs.getString("pw");
                String nickname = rs.getString("nickname");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String birth = rs.getString("birth");
                int pick = rs.getInt("pick");
                int win = rs.getInt("win");
                int lose = rs.getInt("lose");
                int play = rs.getInt("play");
                int gender = rs.getInt("gender");
                
                // UserInfo 객체 생성 후 리스트에 추가
                userList.add(new UserInfoS(id, pw, nickname, email, phone, address, birth, pick, win, lose, play, gender));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return userList; // 모든 사용자 정보를 담은 리스트 반환
    }
    
    // 사용자의 정보를 수정하는 UPDATE 함수
    public void updateUserInfo(int uid, String id, String pw, String nickname, String email, String phone,
            String address, String birth, int pick, int win, int lose, int play, Integer gender) {
    	String query = "UPDATE user SET id = ?, pw = ?, nickname = ?, email = ?, phone = ?, address = ?, " +
   "birth = ?, pick = ?, win = ?, lose = ?, play = ?, gender = ? WHERE uid = ?";
    	try (Connection conn = dbManager.connect();
    			PreparedStatement pstmt = conn.prepareStatement(query)) {

	pstmt.setString(1, id);
	pstmt.setString(2, pw);
	pstmt.setString(3, nickname);
	pstmt.setString(4, email);
	pstmt.setString(5, phone);
	pstmt.setString(6, address);
	pstmt.setString(7, birth);
	pstmt.setInt(8, pick);
	pstmt.setInt(9, win);
	pstmt.setInt(10, lose);
	pstmt.setInt(11, play);
	if (gender == null) {
        pstmt.setObject(12, null); // gender가 null인 경우
    } else {
        pstmt.setInt(12, gender); // gender가 null이 아닐 경우
    }
	pstmt.setInt(13, uid);

	pstmt.executeUpdate(); // 업데이트 실행
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    // 사용자 정보를 삭제해버리는 delete 함수
    public void moveUserToBin(int uid) {
        String insertQuery = "INSERT INTO user_bin SELECT *, CURRENT_TIMESTAMP AS inserted_at " 
                           + "FROM user "
                           + "WHERE uid = ?";

        try (Connection conn = dbManager.connect();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, uid);
            int rowsInserted = insertStmt.executeUpdate(); // 삽입된 행 수 확인

            // 사용자 삭제
            String deleteQuery = "DELETE FROM user WHERE uid = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, uid);
                int rowsAffected = deleteStmt.executeUpdate(); // 삭제된 행 수

                // 삭제 성공 여부 확인
                if (rowsAffected > 0 && rowsInserted > 0) {
                    // 사용자 삭제 성공 시 팝업창으로 알림
                    JOptionPane.showMessageDialog(null, "사용자가 성공적으로 삭제되었습니다.", "삭제 완료", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // 삭제할 사용자가 없을 경우
                    JOptionPane.showMessageDialog(null, "삭제할 사용자가 없습니다.", "삭제 실패", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 오류 발생 시 팝업창으로 알림
            JOptionPane.showMessageDialog(null, "사용자 삭제 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    //삭제된 유저 조회
    public List<String> searchBinUser() {
        List<String> binUsers = new ArrayList<>();
        String query = "SELECT uid, id, nickname, inserted_at FROM user_bin";

        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int uid = rs.getInt("uid");
                String id = rs.getString("id");
                String nickname = rs.getString("nickname");
                String insertedAt = rs.getString("inserted_at");

                binUsers.add("UID: " + uid + " / 아이디: " + id + " / 닉네임: " + nickname + " / 삭제된 시간: " + insertedAt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return binUsers;
    }
    
    //삭제된 유저를 복원하는 함수
    public void restoreUser(int uid) {
        String insertQuery = "INSERT INTO user (uid, id, pw, nickname, email, phone, address, pick, win, lose, play, birth, gender, profile_img) " +
                             "SELECT uid, id, pw, nickname, email, phone, address, pick, win, lose, play, birth, gender, profile_img " +
                             "FROM user_bin WHERE uid = ?";
        String deleteQuery = "DELETE FROM user_bin WHERE uid = ?";

        try (Connection conn = dbManager.connect();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            // 복원
            insertStmt.setInt(1, uid);
            insertStmt.executeUpdate();

            // 삭제
            deleteStmt.setInt(1, uid);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //uid로 닉이랑 픽 얻
    public Map<String, Object> getNickAndPickByUid(int uid) {
        String query = "SELECT Nickname, Pick FROM user WHERE uid = ?";
        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, uid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("nickname", rs.getString("Nickname"));
                result.put("pick", rs.getInt("Pick"));
                return result; // 결과 반환
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 정보가 없을 경우 null 반환
    }
    
    //승리자 횟수 추가
    public void updateWinAndPlay(int uid) {
        // 승자의 UID에 해당하는 유저의 win과 play 값을 업데이트
        String query = "UPDATE user SET win = win + 1, play = play + 1 WHERE uid = ?";
        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, uid); // 해당 uid로 지정
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    //패배자 횟수 추가
    public void updateLoseAndPlay(int uid) {
        // 패자의 UID에 해당하는 유저의 lose와 play 값을 업데이트
        String query = "UPDATE user SET lose = lose + 1, play = play + 1 WHERE uid = ?";
        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, uid); // 해당 uid로 지정
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Integer getUserPick(int uid) {
        String query = "SELECT Pick FROM user WHERE uid = ?";
        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, uid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Pick"); // pick 값 반환
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 정보가 없을 경우 null 반환
    }
    
    public boolean updateUserPick(int uid, int pick) {
        String query = "UPDATE user SET Pick = ? WHERE uid = ?";
        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, pick);
            pstmt.setInt(2, uid);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

