package hu.mik.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.vaadin.liveimageeditor.LiveImageEditor.ImageReceiver;

import com.vaadin.navigator.View;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import hu.mik.beans.User;
import hu.mik.constants.UserConstants;
import hu.mik.services.UserService;
import hu.mik.views.PictureUploadView;

@Component
public class UploadProfilePicEdit implements Receiver, SucceededListener, ImageReceiver{
	@Autowired
	UserService userService;
	
	private File newImage;
	
	private View view;
	
	private String mimeType;
	
	private boolean upload=true;
	
	private ByteArrayOutputStream ops;
	

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
			((PictureUploadView) view).editImage(ops);
		}
		else{			
			((PictureUploadView) view).addComponent(new Label("Wrong file type!"));
		}
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {		
		this.mimeType=mimeType;
		return ops = new ByteArrayOutputStream();	
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
