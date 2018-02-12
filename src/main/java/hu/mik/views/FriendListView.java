package hu.mik.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.FriendshipService;
import hu.mik.services.UserService;
import hu.mik.ui.MainUI;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name=FriendListView.NAME)
public class FriendListView extends VerticalLayout implements View{
public static final String NAME="FriendListView";
	
	@Autowired
	FriendshipService friendshipService;
	@Autowired
	UserService userService;
	
	private Panel panel=new Panel();
	private HorizontalLayout row=new HorizontalLayout();
	private VerticalLayout rows=new VerticalLayout();
	private HorizontalLayout userDiv;
	private int divsPerRow=5;
	private List<User> friendList=new ArrayList<>();

	@Override
	public void enter(ViewChangeEvent event) {
		this.addComponent(panel);
		this.setMargin(false);
		this.setSizeFull();
		User sideUser=userService.findUserById(Integer.parseInt(event.getParameters()));
		((MainUI)getUI()).changeSideMenu(sideUser);
		
		panel.setSizeFull();
		panel.setContent(rows);
		rows.setHeight("100%");
		rows.setWidth("100%");
		rows.addComponent(row);
		panel.setCaption(sideUser.getUsername()+"'s "+"Friendlist:");
		row.setHeight(panel.getHeight()/divsPerRow, panel.getHeightUnits());
		
		friendshipService.findAllByUserId(sideUser.getId()).forEach(friendShip -> friendList.add(userService.findUserById(friendShip.getFriendId())));
		
		int i=0;		
		for(User user : friendList){
			i++;
			Image image=new Image(null, new FileResource(
					new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImageName())));
			image.setHeight("100%");
			image.addStyleName(ThemeConstants.BORDERED_IMAGE);
			Button nameButton=new Button(user.getUsername(), this::userNameListener);
			nameButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			nameButton.setSizeFull();
			userDiv=new HorizontalLayout();
			userDiv.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
			userDiv.setHeight("100%");
			userDiv.setWidth(panel.getWidth()/divsPerRow, panel.getWidthUnits());
			userDiv.addComponent(image);
			userDiv.addComponent(nameButton);
			userDiv.addStyleName(ThemeConstants.BORDERED);
			row.addComponent(userDiv);
			if(i==divsPerRow){
				row=new HorizontalLayout();
				row.setHeight(panel.getHeight()/divsPerRow, panel.getHeightUnits());
				rows.addComponent(row);
				i=0;
			}
			
		}		
	}
	
	
	private void userNameListener(Button.ClickEvent event){
//		((MainUI)getUI()).changeToUser(userService.findUserByUsername(event.getButton().getCaption()));
	}

	
	
	
	


}
