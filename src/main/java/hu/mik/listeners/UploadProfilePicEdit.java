package hu.mik.listeners;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.vaadin.liveimageeditor.LiveImageEditor.ImageReceiver;

import com.vaadin.navigator.View;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import hu.mik.beans.User;
import hu.mik.constants.SystemConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.UserService;
import hu.mik.views.PictureUploadView;

@Component
public class UploadProfilePicEdit implements Receiver, SucceededListener{
	@Autowired
	UserService userService;	
	
	private View view;
	
	private String mimeType;
	
	private boolean upload=true;
	
	private String fileName;
	
	private ByteArrayOutputStream ops;
	
	

	@EventListener
	@Override
	public void uploadSucceeded(SucceededEvent event) {		
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
			((PictureUploadView) view).editImage(ops);
		}
		else{			
			((PictureUploadView) view).addComponent(new Label("Wrong file type!"));
		}
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {	
		this.fileName=filename;
		this.mimeType=mimeType;
		return ops = new ByteArrayOutputStream();	
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}
	
	public void receiveImage(InputStream ins) {
		WrappedSession session=VaadinService.getCurrentRequest().getWrappedSession();
		String username=(String) session.getAttribute(SystemConstants.SESSION_ATTRIBUTE_LDAP_USER);
		User user=userService.findUserByUsername(username);
		
		String imageName=System.currentTimeMillis()+fileName;
		File imageSave=new File(UserConstants.PROFILE_PICTURE_LOCATION+imageName);
		FileOutputStream ops=null;
		int cursor;
		try {
			ops=new FileOutputStream(imageSave);
			while((cursor=ins.read())!=-1){				
				ops.write(cursor);
			}
			ins.close();
			ops.close();
			if(!user.getImageName().equals("user.png")){
				File fileToDel=new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImageName());
				fileToDel.delete();
			}
			user.setImageName(imageName);
			userService.saveChanges(user);
			((PictureUploadView) view).imageChange();			
			
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
