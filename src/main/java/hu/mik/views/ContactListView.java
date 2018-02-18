package hu.mik.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.FriendRequest;
import hu.mik.beans.User;
import hu.mik.components.UserListLayout;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.FriendRequestService;
import hu.mik.services.FriendshipService;
import hu.mik.services.UserService;
import hu.mik.ui.MainUI;
import oracle.sql.LxMetaData;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name=ContactListView.NAME)
public class ContactListView extends VerticalLayout implements View{
public static final String NAME="FriendListView";
	
	@Autowired
	FriendshipService friendshipService;
	@Autowired
	FriendRequestService friendRequestService;
	@Autowired
	UserService userService;
	@Autowired
	UserListLayout userListLayout;
	
	private Panel panel=new Panel();
	private CssLayout layout=new CssLayout();
	private List<User> friendList=new ArrayList<>();
	private VerticalLayout base=new VerticalLayout();
	private User dbUser;
	private List<FriendRequest> requests;

	@Override
	public void enter(ViewChangeEvent event) {
		this.addComponent(panel);
		this.setMargin(false);
		this.setSizeFull();
//		base.setMargin(false);
//		base.setSpacing(false);
		dbUser=userService.findUserByUsername(event.getParameters());
		
		requests=new ArrayList<>();
		requests=friendRequestService.findAllByRequestedId(dbUser.getId());
		
		panel.setSizeFull();
		panel.setCaption(dbUser.getUsername()+"'s "+"Friendlist:");
		
		friendshipService.findAllByUserId(dbUser.getId()).forEach(friendShip -> friendList.add(userService.findUserById(friendShip.getFriendId())));
		
		RadioButtonGroup<String> radioButtonGroup=new RadioButtonGroup<>();
		radioButtonGroup.setItems("All", "Requests ("+requests.size()+")", "Team");
		radioButtonGroup.setSelectedItem("All");
		radioButtonGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		radioButtonGroup.addSelectionListener(this::radioBtnListener);
		
		layout=userListLayout.createUserListLayoutFromDb(friendList);
		
		base.addComponent(radioButtonGroup);
		base.addComponent(layout);
		base.setExpandRatio(radioButtonGroup, 10);
		base.setExpandRatio(layout, 90);
		
		panel.setContent(base);
	}
	
	
	private void userNameListener(Button.ClickEvent event){
		getUI().getNavigator().navigateTo(ProfileView.NAME+"/"+event.getButton().getId());
	}
	
	private void radioBtnListener(SingleSelectionEvent<String> event) {
		String value=event.getSource().getValue();
		layout.removeAllComponents();
		if(value.matches("Requests.*")) {
			List<User> requestors=new ArrayList<>();
			requests.forEach(request->requestors.add(userService.findUserById(request.getRequestorId())));
			layout=userListLayout.createUserListLayoutFromDb(requestors);
		}else if(value.equals("All")) {
			layout=userListLayout.createUserListLayoutFromDb(friendList);
		}
	}

	
	
	
	


}
