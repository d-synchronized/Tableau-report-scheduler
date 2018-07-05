package com.subscription.service.impl;

import java.io.File;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.subscription.service.TableauMailService;

@Service
public class TableauMailServiceImpl implements TableauMailService {

  @Autowired
  private JavaMailSender javaMailSender;

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public void send(String recipitents, String subject, String bodyText, String filePath) {
    MimeMessage mail = javaMailSender.createMimeMessage();
    try {
      InternetAddress[] iAdressArray = InternetAddress.parse(recipitents);

      MimeMessageHelper helper = new MimeMessageHelper(mail, true);
      helper.setTo(iAdressArray);
      helper.setFrom("matis@maureva.com");
      helper.setSubject(subject);
      helper.setText(bodyText);

      File file = new File(filePath);

      helper.addAttachment(file.getName(), file);

    } catch (MessagingException e) {
      log.error("send: ", e);
    }
    javaMailSender.send(mail);
  }

}
