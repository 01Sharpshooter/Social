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
		this.addStyleName(ThemeConstants.SIDE_MENU);		
		this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		VerticalLayout header=new VerticalLayout();
		VerticalLayout menu=new VerticalLayout();
		header.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		menu.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		header.setSpacing(false);
		header.setMargin(false);
		this.addComponent(header);
		this.addComponent(menu);
		this.setExpandRatio(header, 3);
		this.setExpandRatio(menu, 7);
		this.setSpacing(false);
		Image image=new Image(null, new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImageName()))); 
		image.setHeight("100%");
		image.setWidth("100%");
		name.setValue(user.getUsername());
		name.addStyleName(ValoTheme.LABEL_H2);
		header.addComponent(name);
		header.addComponent(image);	
		header.addComponent(menuBar);
		MenuItem options=menuBar.addItem("Options", null);
		MenuItem changePicture=options.addItem("Change picture", new Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				getUI().getNavigator().navigateTo(PictureUploadView.NAME);
				
			}
		});
		return this;
	}
	
	
}
