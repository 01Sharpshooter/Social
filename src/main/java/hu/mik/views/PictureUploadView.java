package hu.mik.views;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.liveimageeditor.LiveImageEditor;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.constants.ThemeConstants;
import hu.mik.upload.UploadProfilePic;
import hu.mik.upload.UploadProfilePicEdit;

@ViewScope
@SpringView(name=PictureUploadView.NAME)
public class PictureUploadView extends VerticalLayout implements View{
	public static final String NAME="PictureUpload";
	
	@Autowired
	UploadProfilePicEdit uploadProfilePicture;
	
	private UI ui;
	
	private Image editImage=new Image();
	
	private LiveImageEditor editor=new LiveImageEditor(uploadProfilePicture);
	
	private Upload upload;
	
	private boolean edit=false;
	
	private CssLayout navigation;
	
	@PostConstruct
	public void init(){	
		ui=(UI)VaadinService.getCurrentRequest().getWrappedSession().getAttribute("UI");
		uploadProfilePicture.setView(this);
		this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		upload=new Upload("Upload a new profile picture! (JPEG/PNG/GIF)", uploadProfilePicture);
		upload.setImmediateMode(false);
		upload.addSucceededListener(uploadProfilePicture);
		addComponent(upload);
		}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	public void imageChange(){
		getUI().getPage().reload();
	}

	public void editImage(ByteArrayOutputStream ops) {
		VerticalLayout editorLayout=this.getEditor(ops);
		upload.setVisible(false);
		addComponent(editorLayout);
		
	}
	
	public VerticalLayout getEditor(ByteArrayOutputStream ops){
		VerticalLayout editorLayout=new VerticalLayout();
		editorLayout.setSizeFull();
		Label instructions=new Label("Scroll to zoom, move by dragging the mouse, rotate by shift+mouse dragging.");
		instructions.setStyleName(ThemeConstants.BLUE_TEXT_H1);
		editorLayout.addComponent(instructions);
		editorLayout.addComponent(editor);
		editorLayout.addComponent(editImage);
		navigation=new CssLayout();
		navigation.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		Button uploadButton=new Button("Upload", this::uploadListener);
		Button resetButton=new Button("Reset", this::resetListener);
		Button cancelButton=new Button("Cancel", this:cancelListener);
		editorLayout.addComponent(navigation);
		editor.setWidth(80, Unit.PERCENTAGE);
		editor.setHeight(80, Unit.PERCENTAGE);
		editor.setImage(ops.toByteArray());
		editor.resetTransformations();
		editor.setVisible(visible);
		
		return editorLayout;
	}
	
	private void uploadListener(Button.ClickEvent event){
		editor.requestEditedImage();
	}
	
	private void resetListener(Button.ClickEvent event){
		editor.resetTransformations();
	}
	
	private void cancelListener(Button.ClickEvent event){
		editor.setVisible(!edit);
		editor.setVisible(!edit);
		
	}

}
