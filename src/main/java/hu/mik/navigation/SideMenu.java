package hu.mik.navigation;

import java.io.File;
import com.vaadin.annotations.Theme;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.views.PictureUploadView;
@SuppressWarnings("serial")
@Theme(ThemeConstants.UI_THEME)
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
		addStyleName(ThemeConstants.SIDE_MENU);
		name.setValue(user.getUsername());
		name.addStyleName(ValoTheme.LABEL_H2);
		this.addComponent(name);
		this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		this.setSpacing(false);
		Image image=new Image(null, new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImage()))); 
		image.setHeight("80%");
		image.setWidth("80%");
		addComponent(image);	
		addComponent(menuBar);
		MenuItem options=menuBar.addItem("Options", null);
		options.setStyleName(ValoTheme.BUTTON_LINK);
		MenuItem changePicture=options.addItem("Change picture", new Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				getUI().getNavigator().navigateTo(PictureUploadView.NAME);
				
			}
		});
		return this;
	}
	
	
}
