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

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import hu.mik.beans.User;
import hu.mik.constants.UserConstants;
import hu.mik.services.UserService;
import hu.mik.utils.ProfileImageHelper;
import hu.mik.utils.UserUtils;
import hu.mik.views.PictureUploadView;

@SuppressWarnings("serial")
@SpringComponent
public class UploadProfilePicEdit implements Receiver, SucceededListener {
	@Autowired
	UserService userService;
	@Autowired
	UserUtils userUtils;

	private PictureUploadView view;

	private String mimeType;

	private String fileName;

	private ByteArrayOutputStream ops;

	@EventListener
	@Override
	public void uploadSucceeded(SucceededEvent event) {
		String[] allowedTypes = UserConstants.ALLOWED_PICTURE_TYPES;
		for (String type : allowedTypes) {
			if (this.mimeType.equals(type)) {
				this.view.editImage(this.ops);
				return;
			}
		}
		this.view.addComponent(new Label("Wrong file type!"));
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

	public void setView(PictureUploadView view) {
		this.view = view;
	}

	public void receiveImage(InputStream ins) {
		User user = this.userUtils.getLoggedInUser().getDbUser();

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
				File fileToDel = ProfileImageHelper.loadUserImage(user.getImageName());
				fileToDel.delete();
			}
			user.setImageName(imageName);
			this.userUtils.getLoggedInUser().setDbUser(this.userService.save(user));
			this.view.imageChange();

		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
