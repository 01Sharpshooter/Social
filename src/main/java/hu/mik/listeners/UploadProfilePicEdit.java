package hu.mik.listeners;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.vaadin.navigator.View;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import hu.mik.beans.User;
import hu.mik.constants.SystemConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.UserService;
import hu.mik.views.PictureUploadView;

@SuppressWarnings("serial")
@Component
public class UploadProfilePicEdit implements Receiver, SucceededListener {
	@Autowired
	UserService userService;

	private View view;

	private String mimeType;

	private boolean upload = true;

	private String fileName;

	private ByteArrayOutputStream ops;

	@EventListener
	@Override
	public void uploadSucceeded(SucceededEvent event) {
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
			((PictureUploadView) this.view).editImage(this.ops);
		} else {
			((PictureUploadView) this.view).addComponent(new Label("Wrong file type!"));
		}
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		this.fileName = filename;
		this.mimeType = mimeType;
		return this.ops = new ByteArrayOutputStream();
	}

	public View getView() {
		return this.view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public void receiveImage(InputStream ins) {
		WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
		String username = (String) session.getAttribute(SystemConstants.SESSION_ATTRIBUTE_LDAP_USER);
		User user = this.userService.findUserByUsername(username);

		String imageName = System.currentTimeMillis() + this.fileName;
		File imageSave = new File(UserConstants.PROFILE_PICTURE_LOCATION + imageName);

		try {
			BufferedImage bufferedImage = ImageIO.read(ins);
			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
			ImageWriteParam writerParam = writer.getDefaultWriteParam();
			writerParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			writerParam.setCompressionQuality(0.1f);

			FileOutputStream ops = new FileOutputStream(imageSave);
			ImageOutputStream imageOS = ImageIO.createImageOutputStream(ops);
			writer.setOutput(imageOS);
			writer.write(null, new IIOImage(bufferedImage, null, null), writerParam);

			writer.dispose();
			ins.close();
			ops.close();

			if (!user.getImageName().equals("user.png")) {
				File fileToDel = new File(UserConstants.PROFILE_PICTURE_LOCATION + user.getImageName());
				fileToDel.delete();
			}
			user.setImageName(imageName);
			this.userService.saveChanges(user);
			((PictureUploadView) this.view).imageChange();

		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
