package com.brenda.clexis.notificationService.services;

import com.brenda.clexis.notificationService.enums.NotificationType;
import com.brenda.clexis.notificationService.models.EmailRequestDto;
import com.brenda.clexis.notificationService.models.dto.CustomEmailRequestDto;
import com.brenda.clexis.notificationService.models.entity.Notification;
import com.brenda.clexis.notificationService.models.entity.Student;
import com.brenda.clexis.notificationService.models.entity.User;
import com.brenda.clexis.notificationService.repository.NotificationMessageRepository;
import com.brenda.clexis.notificationService.repository.NotificationRepository;
import com.brenda.clexis.notificationService.repository.StudentRepository;
import com.brenda.clexis.notificationService.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private MessageService messageService;
    @Autowired
    private NotificationMessageRepository notificationMessageRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotificationEmail(String to, String subject, String messageBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariable("title", subject);
        context.setVariable("message", messageBody);

        // Process HTML template
        String htmlContent = templateEngine.process("emailTemplate", context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);


        mailSender.send(message);

    }

    public void sendCustomNotification(CustomEmailRequestDto customEmailRequestDto) {
        List<String> emails = new ArrayList<>();
        for(String userId : customEmailRequestDto.getRecipients()){
            User user = userRepository.findUserById(userId);
            if(user != null){
                emails.add(user.getEmail());
            }
        }
        int count = 0;
        for(String email : emails){
            try{
                sendNotificationEmail(email, customEmailRequestDto.getHeading(), customEmailRequestDto.getMessage());
                Notification notification = Notification.builder()
                        .title(customEmailRequestDto.getHeading())
                        .message(customEmailRequestDto.getMessage())
                        .type(NotificationType.CUSTOM)
                        .receiverEmail(email)
                        .userId(customEmailRequestDto.getRecipients().get(count))
                        .build();
                notificationRepository.save(notification);
                //form the object to store in the db
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            count++;

        }

    }

    public void sendDefinedEmail(EmailRequestDto emailRequestDto) {
        var notificationMessage = notificationMessageRepository.findNotificationMessageById(emailRequestDto.getMessageTag().name());
        for(String userId : emailRequestDto.getRecipients()){
            User user = userRepository.findUserById(userId);
            if(user != null){

                Student student = studentRepository.findStudentByUserId(userId);
                String language = student.getLanguage();
                String message = language.equals("english")? notificationMessage.getMessageEn() : notificationMessage.getMessageFr();
                String heading = language.equals("english")? notificationMessage.getHeadingEn(): notificationMessage.getHeadingFr();
                String messageToSend = getEmail(emailRequestDto.getMessageTag().name(),emailRequestDto.getParams(),message);
                try{
                    sendNotificationEmail(user.getEmail(),heading,messageToSend);
                    Notification notification = Notification.builder()
                            .title(heading)
                            .message(message)
                            .type(emailRequestDto.getMessageTag())
                            .receiverEmail(user.getEmail())
                            .userId(user.getId())
                            .build();
                    notificationRepository.save(notification);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            }
        }

    }

    public String getEmail(String messageTag, List<String> params,String theMessage) {
        var message = notificationMessageRepository.findNotificationMessageById(messageTag);
        log.info("Email Message \nargs: {}\nmessage: {}", params, message);
        if (message!=null){
            if (params!=null && !params.isEmpty()){
                for (int i = 0; i < params.size(); i++) {
                    theMessage = theMessage.replace("{"+i+"}", params.get(i));
                }
            }
            return theMessage;
        }
        return null;
    }
}
