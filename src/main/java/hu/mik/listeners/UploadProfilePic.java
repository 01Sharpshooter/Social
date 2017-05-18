package hu.mik.listeners;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.vaadin.liveimageeditor.LiveImageEditor.ImageReceiver;

import com.vaadin.navigator.View;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import hu.mik.beans.User;
import hu.mik.constants.UserConstants;
import hu.mik.services.UserService;
import hu.mik.services.UserServiceImpl;
import hu.mik.views.PictureUploadView;

@Component
public class UploadProfilePic implements Receiver, SucceededListener, ImageReceiver{
	
	@Autowired
	UserService userService;
	
	private File newImage;
	
	private View view;
	
	private String mimeType;
	
	private boolean upload=true;
	

	@EventListener
	@Override
	public void uploadSucceeded(SucceededEvent event) {		
		User user=(User)VaadinService.getCurrentRequest().getWrappedSession().getAttribute("User");
		String[] allowedTypes=UserConstants.ALLOWED_PICTURE_TYPES;
		for(String type : allowedTypes){
			if(mimeType.equals(type)){
				upload=true;
				break;
				
			}else{
				upload=false;				
			}
		}
		if(upload){		
			if(!user.getImageName().equals("user.png")){
				File fileToDel=new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImageName());
				fileToDel.delete();
			}
			user.setImageName(newImage.getName());
			userService.saveChanges(user);
			((PictureUploadView) view).imageChange();
		}
		else{
			newImage.delete();
			((PictureUploadView) view).addComponent(new Label("Wrong file type!"));
		}
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {		
		this.mimeType=mimeType;
		newImage=new File(UserConstants.PROFILE_PICTURE_LOCATION+Calendar.getInstance().getTimeInMillis()+filename);
		OutputStream ops=null;
		try {
			ops=new FileOutputStream(newImage);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}		
		return ops;		
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	@Override
	public void receiveImage(InputStream inputStream) {
		
		
	}
	
	
	
}
