package hu.mik.views;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.FriendRequest;
import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.FriendRequestService;
import hu.mik.services.UserService;
import hu.mik.ui.MainUI;

@ViewScope
@SpringView(name=RequestsView.NAME)
public class RequestsView extends VerticalLayout implements View{
	public static final String NAME="RequestsView";
	
	@Autowired
	FriendRequestService friendRequestService;
	@Autowired
	UserService userService;
	
	private User user;
	private Panel panel=new Panel();
	private HorizontalLayout row=new HorizontalLayout();
	private VerticalLayout rows=new VerticalLayout();
	private HorizontalLayout userDiv;
	private int divsPerRow=3;

	@Override
	public void enter(ViewChangeEvent event) {
		fill();
		
	}

	private void fill() {
		this.addComponent(panel);
		
		panel.setWidth("100%");
		panel.setContent(rows);
		rows.setWidth("100%");
		
		user=(User) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("User");
		List<FriendRequest> requests=friendRequestService.findAllByRequestedId(user.getId());
		rows.addComponent(row);
		int i=0;		
		for(FriendRequest request : requests){
			i++;
			User requestUser=userService.findUserById(request.getRequestorId());
			Image image=new Image(null, new FileResource(
					new File(UserConstants.PROFILE_PICTURE_LOCATION+requestUser.getImageName())));
			image.setHeight("100%");
			Button nameButton=new Button(requestUser.getUsername(), this::userNameListener);
			nameButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			nameButton.addStyleName(ValoTheme.LABEL_H1);
			userDiv=new HorizontalLayout();
			userDiv.addComponent(image);
			userDiv.addComponent(nameButton);
			userDiv.setWidth(panel.getWidth()/divsPerRow, panel.getWidthUnits());
			userDiv.addStyleName(ThemeConstants.BORDERED);
			row.addComponent(userDiv);
			if(i==divsPerRow){
				row=new HorizontalLayout();
				rows.addComponent(row);
				i=0;
			}
			
		}		
		
	}
	
	private void userNameListener(Button.ClickEvent event){
		((MainUI)getUI()).changeToRequestor(userService.findUserByUsername(event.getButton().getCaption()));
	}

}
