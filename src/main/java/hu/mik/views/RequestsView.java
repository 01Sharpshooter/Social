package hu.mik.views;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.FriendRequest;
import hu.mik.beans.User;
import hu.mik.constants.SystemConstants;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.FriendRequestService;
import hu.mik.services.UserService;
import hu.mik.ui.MainUI;

@SuppressWarnings("serial")
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
	private int divsPerRow=5;

	@Override
	public void enter(ViewChangeEvent event) {
		fill();
		
	}

	private void fill() {
		this.addComponent(panel);
		this.setMargin(false);
		this.setSizeFull();
		
		panel.setSizeFull();
		panel.setContent(rows);
		rows.setHeight("100%");
		rows.setWidth("100%");
		rows.addComponent(row);
		panel.setCaption("Your friend requests:");
		row.setHeight(panel.getHeight()/divsPerRow, panel.getHeightUnits());
		
		WrappedSession session=VaadinService.getCurrentRequest().getWrappedSession();
		String username=(String) session.getAttribute(SystemConstants.SESSION_ATTRIBUTE_LDAP_USER);
		user=userService.findUserByUsername(username);
		
		List<FriendRequest> requests=friendRequestService.findAllByRequestedId(user.getId());
		
		int i=0;		
		for(FriendRequest request : requests){
			i++;
			User requestUser=userService.findUserById(request.getRequestorId());
			Image image=new Image(null, new FileResource(
					new File(UserConstants.PROFILE_PICTURE_LOCATION+requestUser.getImageName())));
			image.setHeight("100%");
			image.addStyleName(ThemeConstants.BORDERED_IMAGE);
			Button nameButton=new Button(requestUser.getUsername(), this::userNameListener);
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
