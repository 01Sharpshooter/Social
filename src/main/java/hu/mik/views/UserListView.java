package hu.mik.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.FriendshipService;
import hu.mik.services.UserService;
import hu.mik.ui.MainUI;

@SpringView(name=UserListView.NAME)
public class UserListView extends VerticalLayout implements View{
	public static final String NAME="userList";
	
	@Autowired
	UserService userService;
	
	private Panel panel=new Panel();
	private HorizontalLayout row=new HorizontalLayout();
	private VerticalLayout rows=new VerticalLayout();
	private HorizontalLayout userDiv;
	private int divsPerRow=5;

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

	public void fill(String username) {
		this.addComponent(panel);
		this.setMargin(false);
		this.setSizeFull();
		List<User> userList=new ArrayList<>();
		
		panel.setSizeFull();
		panel.setContent(rows);
		rows.setHeight("100%");
		rows.setWidth("100%");
		rows.addComponent(row);
		panel.setCaption("Users with similar names:");
		row.setHeight(panel.getHeight()/divsPerRow, panel.getHeightUnits());
		row.addStyleName(ThemeConstants.HOVER_GREEN_LAYOUTS);
		userList=userService.findAllLike(username);
		
		int i=0;		
		if(userList!=null){
			for(User user : userList){
				i++;
				Image image=new Image(null, new FileResource(
						new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImageName())));
				image.setHeight("100%");
				image.addStyleName(ThemeConstants.BORDERED_IMAGE);
				Label lblName=new Label(user.getUsername());	
//				lblName.setId(user.getId().toString());
//				userDiv.addComponent(lblName);
//				Button nameButton=new Button(user.getUsername(), this::userNameListener);
//				nameButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
//				nameButton.setSizeFull();
				userDiv=new HorizontalLayout();
				userDiv.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
				userDiv.setHeight("100%");
				userDiv.setWidth(panel.getWidth()/divsPerRow, panel.getWidthUnits());
				userDiv.addComponent(image);
				userDiv.addComponent(lblName);
				userDiv.setId(user.getId().toString());
				userDiv.addStyleName(ThemeConstants.BORDERED);
				userDiv.addLayoutClickListener(this::layoutClickListener);
				row.addComponent(userDiv);
				if(i==divsPerRow){
					row=new HorizontalLayout();
					row.setHeight(panel.getHeight()/divsPerRow, panel.getHeightUnits());
					rows.addComponent(row);
					i=0;
				}
				
			}		
		}
	}
	
	private void layoutClickListener(LayoutClickEvent event){
		((MainUI)getUI()).getNavigator().navigateTo(ProfileView.NAME+"/"+event.getComponent().getId());
	}
	
}
