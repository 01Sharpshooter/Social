package hu.mik.upload;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Image;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import hu.mik.beans.User;
import hu.mik.constants.UserConstants;
import hu.mik.services.UserService;
import hu.mik.services.UserServiceImpl;

@Component
public class UploadProfilePic implements Receiver, SucceededListener{
	
	@Autowired
	UserService userService;
	
	private File file;

	@EventListener
	@Override
	public void uploadSucceeded(SucceededEvent event) {		
		User user=(User)VaadinService.getCurrentRequest().getWrappedSession().getAttribute("User");
		user.setImage(file.getName());
		userService.saveChanges(user);
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		file=new File(UserConstants.PROFILE_PICTURE_LOCATION+Calendar.getInstance().getTimeInMillis()+filename);
		
		OutputStream ops=null;
		try {
			ops=new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}		
		return ops;
	}
	
}
