package com.aricent.control;

import com.aricent.model.MessageModel;
import com.aricent.transaction.impl.UserTransactionImpl;
import com.aricent.constant.GlobalConstants;
import com.aricent.constant.NotifConstants;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

//TODO ...Add templated message 
public class MailModel {
	private JavaMailSender mailSender;
	private MimeMessage msg;
	private MessageModel mMessageModel;
	private static final Logger logger = Logger.getLogger(MailModel.class);

	public JavaMailSender GetMailSender() {
		return mailSender;

	}

	public MimeMessage GetMimeMsg() {
		return msg;
	}

	public void SetMimeMsg(MimeMessage ipmsg) {
		this.msg = ipmsg;
	}

	public void setMailSender() {
		ClassPathXmlApplicationContext lxmlctxt = new ClassPathXmlApplicationContext(
				"MailBeans.xml");
		ApplicationContext context = lxmlctxt;
		// new ClassPathXmlApplicationContext("MailBeans.xml");
		this.mailSender = (JavaMailSender) context.getBean("MSBean");
		lxmlctxt.close();

	}

	public void SetMessageModel(MessageModel ipMessageModel) {
		mMessageModel = ipMessageModel;
	}

	// Later will be enhanced to use a template version
	/*
	 * public int createMessage(List<String> iptoIds){ try{
	 * msg=mailSender.createMimeMessage(); MimeMessageHelper helper = new
	 * MimeMessageHelper(msg, true); String[] StringTo = iptoIds.toArray(new
	 * String[iptoIds.size()]); helper.setFrom("thakurp2007@gmail.com");
	 * helper.setTo(StringTo); helper.setSubject("Hello Hello !!!! ");
	 * helper.setText("Welcome To SocialNotification");
	 * logger.info("In MailModel::createMessage Success"); return
	 * NotifConstants.MAIL_SEND_SUCCESS; } catch (MessagingException e) {
	 * logger.info("In MailModel::createMessage Failed"); e.printStackTrace();
	 * return NotifConstants.MAIL_SEND_FAIL;}
	 * 
	 * 
	 * }
	 */
	public int createMessage() {
		try {
			msg = mailSender.createMimeMessage();

			String msgBody = "";
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			helper.setFrom("thakurp2007@gmail.com");
			helper.setTo(mMessageModel.getEmailId());
			helper.setSubject("Welcome To BLR Foody !!!! ");

			msgBody = "Hi, " + mMessageModel.getName() + "\n";
			msgBody += mMessageModel.getMsgStr() + "\n.";
			if (mMessageModel.getPasswd() != null)
				msgBody += "And Your Password is: " + mMessageModel.getPasswd();
			// msgBody += "You just logged into your account!!";

			helper.setText(msgBody);
			logger.info("In MailModel::createMessage Success");
			return NotifConstants.MAIL_SEND_SUCCESS;
		} catch (MessagingException e) {
			logger.info("In MailModel::createMessage Failed");
			logger.error(e);
			return NotifConstants.MAIL_SEND_FAIL;
		}
	}

	public int sendMail() {
		logger.info("In MailModel::sendMail");
		setMailSender();
		// check how to pass by reference in java
		if (createMessage() == NotifConstants.MAIL_SEND_SUCCESS) {
			mailSender.send(msg);
		} else {
			logger.info("In MailModel::sendMail Failed");
			return NotifConstants.MAIL_SEND_FAIL;
		}
		return NotifConstants.MAIL_SEND_SUCCESS;
	}
}
