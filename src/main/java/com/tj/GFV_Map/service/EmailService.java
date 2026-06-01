package com.tj.GFV_Map.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(fromEmail, "GFV-Map");
            helper.setTo(toEmail);
            helper.setSubject("[GFV-Map] 이메일 인증 코드");
            helper.setText(buildHtmlContent(code), true);  // true = HTML 모드

            mailSender.send(message);
            log.info("인증 메일 발송 완료: {}", toEmail);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("인증 메일 발송 실패: {}", toEmail, e);
            throw new RuntimeException("메일 발송에 실패했습니다.");
        }
    }

    private String buildHtmlContent(String code) {
        return """
                <div style="max-width: 500px; margin: 0 auto; padding: 40px 20px; font-family: 'Malgun Gothic', sans-serif;">
                    <h2 style="color: #2E7D32; text-align: center;">🌱 GFV-Map 이메일 인증</h2>
                    <p style="font-size: 16px; color: #333; line-height: 1.6;">
                        안녕하세요! 아래 인증 코드를 입력해 주세요.
                    </p>
                    <div style="background: #E8F5E9; border-radius: 8px; padding: 24px; text-align: center; margin: 24px 0;">
                        <div style="font-size: 36px; font-weight: bold; color: #2E7D32; letter-spacing: 8px;">
                            %s
                        </div>
                    </div>
                    <p style="font-size: 14px; color: #666;">
                        이 코드는 <strong>5분</strong> 동안만 유효합니다.
                    </p>
                    <p style="font-size: 13px; color: #999; margin-top: 32px; text-align: center;">
                        본인이 요청한 것이 아니라면 이 메일을 무시해 주세요.
                    </p>
                </div>
                """.formatted(code);
    }
}