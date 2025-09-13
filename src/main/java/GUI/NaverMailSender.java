package GUI;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class NaverMailSender {

    private static final String SMTP_SERVER = "smtp.naver.com";
    private static final int SMTP_PORT = 465;
    private final String username; // 네이버 이메일 주소
    private final String password; // 애플리케이션 비밀번호

    public NaverMailSender(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 메일 보내기 메소드
     *
     * @param to      수신자 이메일
     * @param subject 메일 제목
     * @param body    메일 본문
     * @throws MessagingException 예외 처리
     */
    public void sendMail(String to, String subject, String body) throws MessagingException {
        // SMTP 서버 설정
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_SERVER);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.trust", SMTP_SERVER);

        // 인증 정보 설정
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // 이메일 메시지 생성
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(body);

        // 이메일 전송
        Transport.send(message);
    }

    public static void main(String[] args) {
        // 네이버 이메일 계정 정보
        String username = "your_email@naver.com"; // 네이버 이메일 주소
        String password = "your_app_password";   // 네이버 애플리케이션 비밀번호

        // 수신자 정보 및 내용
        String to = "recipient@example.com";
        String subject = "네이버 SMTP 메일 테스트";
        String body = "이것은 Java를 사용한 네이버 SMTP 테스트 메일입니다.";

        // 메일 전송
        NaverMailSender mailSender = new NaverMailSender(username, password);
        try {
            mailSender.sendMail(to, subject, body);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("이메일 전송 실패");
        }
    }
}
