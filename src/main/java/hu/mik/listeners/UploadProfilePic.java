package hu.mik.listeners;

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
import hu.mik.utils.ProfileImageHelper;
import hu.mik.views.PictureUploadView;

@SuppressWarnings("serial")
@Component
public class UploadProfilePic implements Receiver, SucceededListener, ImageReceiver {

	@Autowired
	UserService userService;

	private File newImage;

	private View view;

	private String mimeType;

	private boolean upload = true;

	@EventListener
	@Override
	public void uploadSucceeded(SucceededEvent event) {
		User user = (User) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("User");
		String[] allowedTypes = UserConstants.ALLOWED_PICTURE_TYPES;
		for (String type : allowedTypes) {
			if (this.mimeType.equals(type)) {
				this.upload = true;
				break;

			} else {
				this.upload = false;
			}
		}
		if (this.upload) {
			if (!user.getImageName().equals("user.png")) {
				File fileToDel = ProfileImageHelper.loadUserImage(user.getImageName());
				fileToDel.delete();
			}
			user.setImageName(this.newImage.getName());
			this.userService.save(user);
			((PictureUploadView) this.view).imageChange();
		} else {
			this.newImage.delete();
			((PictureUploadView) this.view).addComponent(new Label("Wrong file type!"));
		}
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		this.mimeType = mimeType;
		this.newImage = new File(
				UserConstants.PROFILE_PICTURE_LOCATION + Calendar.getInstance().getTimeInMillis() + filename);
		OutputStream ops = null;
		try {
			ops = new FileOutputStream(this.newImage);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
		return ops;
	}

	public View getView() {
		return this.view;
	}

	public void setView(View view) {
		this.view = view;
	}

	@Override
	public void receiveImage(InputStream inputStream) {

	}

}
