package hu.mik.navigation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.springframework.core.io.ClassPathResource;

import com.vaadin.server.ClassResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.User;
import hu.mik.constants.UserConstants;
import hu.mik.views.PictureUploadView;

public class SideMenu extends VerticalLayout{
	private MenuBar menuBar=new MenuBar();
	private UI ui;
	private User user;	
	private Label name=new Label();

	public SideMenu(User user, UI ui) {
		this.user = user;
		this.ui=ui;
	}	
	
	public SideMenu getSideMenu(){		
		name.setValue(user.getUsername());
		name.addStyleName(ValoTheme.LABEL_H2);
		this.addComponent(name);
		this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		this.setSpacing(true);
		Image image=new Image(null, new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImage()))); 
		image.setHeight("80%");
		image.setWidth("80%");
		addComponent(image);	
		addComponent(menuBar);
		MenuItem options=menuBar.addItem("Options", null);
		MenuItem changePicture=options.addItem("Change picture", new Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				ui.getNavigator().navigateTo(PictureUploadView.NAME);
				
			}
		});
		return this;
	}
	
	
}
