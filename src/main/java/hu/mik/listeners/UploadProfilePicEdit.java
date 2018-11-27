package hu.mik.listeners;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import hu.mik.beans.User;
import hu.mik.constants.UserConstants;
import hu.mik.services.UserService;
import hu.mik.utils.UserUtils;
import hu.mik.views.PictureUploadView;

@SuppressWarnings("serial")
@SpringComponent
public class UploadProfilePicEdit implements Receiver, SucceededListener {
	@Autowired
	private UserService userService;
	@Autowired
	private UserUtils userUtils;

	private PictureUploadView view;

	private String extension;
	private String mimeType;

	private ByteArrayOutputStream ops;

	@EventListener
	@Override
	public void uploadSucceeded(SucceededEvent event) {
		String[] allowedTypes = UserConstants.ALLOWED_PICTURE_TYPES;
		for (String type : allowedTypes) {
			if (this.mimeType.equals(type)) {
				this.view.editImage(this.ops);
				this.view.hideError();
				return;
			}
		}
		this.view.showError();
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		this.extension = filename.substring(filename.lastIndexOf('.'), filename.length()).toLowerCase();
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

		String imageName = user.getUsername() + this.extension;
		File imageFolder = new File(UserConstants.getImageLocation());
		if (!imageFolder.exists()) {
			imageFolder.mkdir();
		}
		File imageSave = new File(UserConstants.getImageLocation() + imageName);

		try {
			BufferedImage bufferedImage = ImageIO.read(ins);
			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
			ImageWriteParam writerParam = writer.getDefaultWriteParam();
			writerParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			writerParam.setCompressionQuality(0.5f);
			bufferedImage = this.resizeImage(bufferedImage);

			FileOutputStream ops = new FileOutputStream(imageSave);
			ImageOutputStream imageOS = ImageIO.createImageOutputStream(ops);
			writer.setOutput(imageOS);

			if (user.getImageName() != null) {
				File fileToDel = new File(UserConstants.getImageLocation() + user.getImageName());
				if (fileToDel.exists()) {
					fileToDel.delete();
				}
			}

			writer.write(null, new IIOImage(bufferedImage, null, null), writerParam);

			writer.dispose();
			ins.close();
			ops.close();
			user.setImageName(imageName);
			this.userUtils.getLoggedInUser().setDbUser(this.userService.save(user));
			this.view.imageChange();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private BufferedImage resizeImage(BufferedImage bufferedImage) {
		BufferedImage resizedImg = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(bufferedImage, 0, 0, 300, 300, null);
		g2.dispose();
		return resizedImg;
	}
}
