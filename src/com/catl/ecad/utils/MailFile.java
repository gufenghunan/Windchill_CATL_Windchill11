package com.catl.ecad.utils;

import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import wt.util.WTException;

public class MailFile {
    public InternetAddress from = null;

    public Vector to = new Vector();

    public String subject = "Test4";

    public String text = "OK!";

    public Vector attachFile = new Vector();

    public Vector attachFileMessage = new Vector();

    public String zipFilename = "";

    public String mailHost = "";

    public Properties props = new Properties();

    public void send() throws MessagingException, Exception {
        try {
            // 设置邮件服务器地址
            props.put("mail.smtp.host", mailHost);
            Session session = Session.getInstance(props, null);
            MimeMessage message = new MimeMessage(session);
            Transport bus = session.getTransport("smtp");
            bus.connect();

            message.setFrom(from);
            if (to.size() > 0) {

                String toList = "";
                for (int i = 0; i < to.size(); i++) {
                    InternetAddress iac = new InternetAddress((String) to.elementAt(i));
                    if (i + 1 > to.size()) {
                        toList += (String) to.elementAt(i);
                    } else {
                        toList = toList + "," + (String) to.elementAt(i);
                    }
                }
                InternetAddress[] addrArray = InternetAddress.parse(toList);
                message.addRecipients(Message.RecipientType.TO, addrArray);
            }

            // 设置邮件标题
            message.setSubject(subject);

            MimeBodyPart messageBodyPart = new MimeBodyPart();

            // fill message
            // 设置邮件文本的信息和格式

            DataHandler dh = new DataHandler(text, "text/html;charset=UTF-8");
            messageBodyPart.setDataHandler(dh);
            // messageBodyPart.setText(text);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            // Part two is attachment
            if (zipFilename != null && !zipFilename.equals("")) {
                String[] file = zipFilename.split(";");
                // 添加邮件附件
                for (int i = 0; i < file.length; i++) {
                    messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(file[i]);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    // 设置邮件附件的名称
                    String[] fileName = file[i].split("/");
                    String excelName = fileName[fileName.length - 1];
                    messageBodyPart.setFileName(excelName);
                    multipart.addBodyPart(messageBodyPart);
                }

            }
            // Put parts in message
            message.setContent(multipart);
            // Send the message
            bus.send(message, message.getAllRecipients());
            bus.close();
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new MessagingException(e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new WTException(e.getLocalizedMessage());
        }
    }

    /**
     * 初始化方法
     * 
     * @param path
     * @param mess
     */
    public void setAttachFile(String path, String mess) throws Exception {
        attachFile.addElement(path);
        if (mess == null) {
            mess = "";
        }
        attachFileMessage.addElement(mess);
    }

    /**
     * 初始化方法 邮件标题
     * 
     * @param _subject
     */
    public void setSubject(String _subject) throws Exception {
        subject = _subject;
    }

    /**
     * 初始化方法 附件路径
     * 
     * @param _zipFilename
     */
    public void setZipFilename(String _zipFilename) throws Exception {
        zipFilename = _zipFilename;
    }

    /**
     * 初始化方法 文本信息
     * 
     * @param _text
     */
    public void setText(String _text) throws Exception {
        text = _text;
    }

    /**
     * 初始化方法 寄件人
     * 
     * @param _from
     */
    public void setFrom(String _from) throws Exception {
        try {
            from = new InternetAddress(_from);
        } catch (AddressException e) {
            e.printStackTrace();
            throw new WTException(e.getLocalizedMessage());
        }
    }

    public void setTo(String _to) throws Exception {
        to.addElement(_to);
    }

    /**
     * 初始化方法 邮件服务器地址
     * 
     * @param _host
     */
    public void setMailHost(String _host) throws Exception {
        mailHost = _host;
        props.put("mail.smtp.host", mailHost);
        props.put("mail.smtp.auth", "false");
    }

}
