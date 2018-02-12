package hu.mik.views;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.annotation.PostConstruct;

import org.hibernate.dialect.lock.UpdateLockingStrategy;
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
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.constants.ThemeConstants;
import hu.mik.listeners.UploadProfilePicEdit;
import hu.mik.ui.MainUI;

@ViewScope
@SpringView(name=PictureUploadView.NAME)
public class PictureUploadView extends VerticalLayout implements View, ImageReceiver{
	public static final String NAME="PictureUpload";
	
	@Autowired
	UploadProfilePicEdit uploadProfilePicture;
	
	private UI ui;
	
	private Image editImage=new Image();
	
	private LiveImageEditor editor=new LiveImageEditor(this::receiveImage);
	
	private Upload upload;
	
	private boolean edit=true;
	
	private CssLayout navigation;

	private VerticalLayout editorLayout;
	
	@PostConstruct
	public void init(){	
		uploadProfilePicture.setView(this);
		this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		upload=new Upload("Upload a new profile picture! (JPEG/PNG/GIF)", uploadProfilePicture);
		upload.setImmediateMode(true);
		upload.addSucceededListener(uploadProfilePicture);
		addComponent(upload);
		}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	public void imageChange(){
//		((MainUI)getUI()).refreshSideMenu();
//		this.editorLayout.setVisible(!edit);
//		upload.setVisible(true);
	}

	public void editImage(ByteArrayOutputStream ops) {
		VerticalLayout editorLayout=this.getEditor(ops);
		upload.setVisible(false);
		addComponent(editorLayout);
		editorLayout.setVisible(edit);
		
	}
	
	public VerticalLayout getEditor(ByteArrayOutputStream ops){
		editorLayout=new VerticalLayout();
		addComponent(editorLayout);
		editorLayout.setSizeFull();
		Label instructions=new Label("Scroll to zoom, move by dragging the mouse, rotate by shift+mouse dragging.");
		instructions.setStyleName(ThemeConstants.BLUE_TEXT_H3);
		editorLayout.addComponent(instructions);
		editorLayout.addComponent(editor);
		editorLayout.addComponent(editImage);
		navigation=new CssLayout();
		navigation.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		Button uploadButton=new Button("Upload", this::uploadListener);
		Button resetButton=new Button("Reset", this::resetListener);
		Button cancelButton=new Button("Cancel", this::cancelListener);
		navigation.addComponent(uploadButton);
		navigation.addComponent(resetButton);
		navigation.addComponent(cancelButton);
		editorLayout.addComponent(navigation);
		editor.setWidth(300, Unit.PIXELS);
		editor.setHeight(300, Unit.PIXELS);
		editor.setImage(ops.toByteArray());
		editor.resetTransformations();
		editImage.setVisible(edit);
		editor.setVisible(edit);
		
		return editorLayout;
	}
	
	private void uploadListener(Button.ClickEvent event){
		editor.requestEditedImage();
	}
	
	private void resetListener(Button.ClickEvent event){
		editor.resetTransformations();
	}
	
	private void cancelListener(Button.ClickEvent event){
		editorLayout.setVisible(!edit);
		upload.setVisible(edit);
	}

	@Override
	public void receiveImage(InputStream inputStream) {
        uploadProfilePicture.receiveImage(inputStream);
		
	}
	
	

}
