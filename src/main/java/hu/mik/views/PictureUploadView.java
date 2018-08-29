package hu.mik.views;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.liveimageeditor.LiveImageEditor;
import org.vaadin.liveimageeditor.LiveImageEditor.ImageReceiver;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.constants.ThemeConstants;
import hu.mik.listeners.UploadProfilePicEdit;
import hu.mik.ui.MainUI;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name = PictureUploadView.NAME)
public class PictureUploadView extends VerticalLayout implements View, ImageReceiver {
	public static final String NAME = "PictureUpload";

	@Autowired
	UploadProfilePicEdit uploadProfilePicture;

	private Image editImage = new Image();

	private LiveImageEditor editor = new LiveImageEditor(this::receiveImage);

	private Upload upload;

	private boolean edit = true;

	private CssLayout navigation;

	private VerticalLayout editorLayout;

	@Override
	public void enter(ViewChangeEvent event) {
		this.uploadProfilePicture.setView(this);
		this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		this.upload = new Upload("Upload a new profile picture! (JPEG/PNG)", this.uploadProfilePicture);
		this.upload.setImmediateMode(true);
		this.upload.addSucceededListener(this.uploadProfilePicture);
		this.addComponent(this.upload);

	}

	public void imageChange() {
		((MainUI) this.getUI()).refreshImage();
		this.editorLayout.setVisible(!this.edit);
		this.upload.setVisible(true);
	}

	public void editImage(ByteArrayOutputStream ops) {
		VerticalLayout editorLayout = this.getEditor(ops);
		this.upload.setVisible(false);
		this.addComponent(editorLayout);
		editorLayout.setVisible(this.edit);

	}

	public VerticalLayout getEditor(ByteArrayOutputStream ops) {
		this.editorLayout = new VerticalLayout();
		this.addComponent(this.editorLayout);
		this.editorLayout.setSizeFull();
		Label instructions = new Label("Scroll to zoom, move by dragging the mouse, rotate by shift+mouse dragging.");
		instructions.setStyleName(ThemeConstants.BLUE_TEXT_H3);
		this.editorLayout.addComponent(instructions);
		this.editorLayout.addComponent(this.editor);
		this.editorLayout.addComponent(this.editImage);
		this.navigation = new CssLayout();
		this.navigation.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		Button uploadButton = new Button("Upload", this::uploadListener);
		Button resetButton = new Button("Reset", this::resetListener);
		Button cancelButton = new Button("Cancel", this::cancelListener);
		this.navigation.addComponent(uploadButton);
		this.navigation.addComponent(resetButton);
		this.navigation.addComponent(cancelButton);
		this.editorLayout.addComponent(this.navigation);
		this.editor.setWidth(300, Unit.PIXELS);
		this.editor.setHeight(300, Unit.PIXELS);
		this.editor.setImage(ops.toByteArray());
		this.editor.resetTransformations();
		this.editImage.setVisible(this.edit);
		this.editor.setVisible(this.edit);

		return this.editorLayout;
	}

	private void uploadListener(Button.ClickEvent event) {
		this.editor.requestEditedImage();
	}

	private void resetListener(Button.ClickEvent event) {
		this.editor.resetTransformations();
	}

	private void cancelListener(Button.ClickEvent event) {
		this.editorLayout.setVisible(!this.edit);
		this.upload.setVisible(this.edit);
	}

	@Override
	public void receiveImage(InputStream inputStream) {
		this.uploadProfilePicture.receiveImage(inputStream);

	}

}
