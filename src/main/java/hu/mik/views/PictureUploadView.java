package hu.mik.views;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;

import hu.mik.upload.UploadProfilePic;

@ViewScope
@SpringView(name=PictureUploadView.NAME)
public class PictureUploadView extends HorizontalLayout implements View{
	public static final String NAME="PictureUpload";
	
	@Autowired
	UploadProfilePic uploadProfilePicture;
	
	private UI ui;
	
	@PostConstruct
	public void init(){	
		ui=(UI)VaadinService.getCurrentRequest().getWrappedSession().getAttribute("UI");
		uploadProfilePicture.setView(this);
		this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		Upload upload=new Upload("Upload a new profile picture! (JPEG/PNG/GIF)", uploadProfilePicture);
		upload.setImmediateMode(false);
		upload.addSucceededListener(uploadProfilePicture);
		addComponent(upload);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	public void imageChange(){
		ui.getPage().reload();
	}

}
