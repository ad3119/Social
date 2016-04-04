package com.aricent.control;

import com.aricent.model.MessageModel;
import com.aricent.constant.NotifConstants;

public class MailController implements MailServiceIntf {
	@Override
	public int sendmail(MessageModel ipMessageModel) {
		// TODO Auto-generated method stub

		MailModel MM = new MailModel();
		DummyMailList.CreateDummyList();
		MM.SetMessageModel(ipMessageModel);

		MM.sendMail();
		return NotifConstants.MAIL_SEND_SUCCESS;

	}
}
