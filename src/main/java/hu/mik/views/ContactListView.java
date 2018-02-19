package hu.mik.views;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.FriendRequest;
import hu.mik.beans.LdapGroup;
import hu.mik.beans.LdapUser;
import hu.mik.beans.User;
import hu.mik.components.UserListLayout;
import hu.mik.constants.ThemeConstants;
import hu.mik.services.FriendRequestService;
import hu.mik.services.FriendshipService;
import hu.mik.services.LdapService;
import hu.mik.services.UserService;

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
	@Autowired
	LdapService ldapService;
	
	private Panel panel=new Panel();
	private CssLayout layout=new CssLayout();
	private List<User> friendList=new ArrayList<>();
	private VerticalLayout base=new VerticalLayout();
	private User dbUser;
	private LdapUser ldapUser;
	private List<FriendRequest> requests;
	private List<LdapGroup> groups;
	private ComboBox<LdapGroup> groupSelect;

	@Override
	public void enter(ViewChangeEvent event) {
		this.addComponent(panel);
		this.setMargin(false);
		this.setSizeFull();
//		base.setMargin(false);
//		base.setSpacing(false);
		dbUser=userService.findUserByUsername(event.getParameters());
		ldapUser=ldapService.findUserByUsername(event.getParameters());
		
		requests=friendRequestService.findAllByRequestedId(dbUser.getId());		
		groups=ldapService.findGroupsByUserId(ldapUser.getId());
		
		panel.setSizeFull();
		panel.setCaption(dbUser.getUsername()+"'s "+"Friendlist:");
		
		friendshipService.findAllByUserId(dbUser.getId())
		.forEach(friendShip -> friendList.add(userService.findUserById(friendShip.getFriendId())));
		
		groupSelect=new ComboBox<>("Select a team:", groups);
		groupSelect.setEmptySelectionAllowed(false);
		groupSelect.addSelectionListener(this::groupSelectionListener);
		if(groups.size()>0)
			groupSelect.setSelectedItem(groups.get(0));
		groupSelect.setVisible(false);
		
		RadioButtonGroup<String> radioButtonGroup=new RadioButtonGroup<>();
		List<String> radioFixItems= new ArrayList<>();
		radioFixItems.add("Friends("+friendList.size()+")");
		radioFixItems.add("Requests("+requests.size()+")");
		if(!groups.isEmpty()) {
			radioFixItems.add("Teams("+groups.size()+")");
		}
		radioButtonGroup.setItems(radioFixItems);
		radioButtonGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		radioButtonGroup.setId("radioTeamSelect");
//		radioButtonGroup.addStyleName(ThemeConstants.RESPONSIVE_FONT);
		radioButtonGroup.addSelectionListener(this::radioBtnListener);
		radioButtonGroup.setSelectedItem("Friends("+(friendList.size())+")");
		
//		layout=userListLayout.createUserListLayoutFromDb(friendList);
		
		base.addComponent(radioButtonGroup);
		base.addComponent(groupSelect);
		base.addComponent(layout);
		base.setExpandRatio(radioButtonGroup, 10);
		base.setExpandRatio(groupSelect, 10);
		base.setExpandRatio(layout, 80);
		
		panel.setContent(base);
	}
	
	
	private void userNameListener(Button.ClickEvent event){
		getUI().getNavigator().navigateTo(ProfileView.NAME+"/"+event.getButton().getId());
	}
	
	private void radioBtnListener(SingleSelectionEvent<String> event) {
		String value=event.getSource().getValue();
		groupSelect.setVisible(false);
		layout.removeAllComponents();
		if(value.matches("Requests.*")) {
			List<User> requestors=new ArrayList<>();
			requests.forEach(request->requestors.add(userService.findUserById(request.getRequestorId())));
			layout=userListLayout.createUserListLayoutFromDb(requestors);
		}else if(value.matches("Friends.*")) {
			layout=userListLayout.createUserListLayoutFromDb(friendList);
		}else if(value.matches("Teams.*")) {
			groupSelect.setVisible(true);
			setLayoutByTeam(groupSelect.getValue());
		}			
	}
	
	
	private void groupSelectionListener(SingleSelectionEvent<LdapGroup> event) {
		LdapGroup selectedGroup=event.getValue();
		setLayoutByTeam(selectedGroup);
		
	}
	
	private void setLayoutByTeam(LdapGroup team) {
		List<LdapUser> teamMembers=new ArrayList<>();
		team.getListOfMembers()
		.forEach(name->{
		if(!name.equals(ldapUser.getId()))					
			teamMembers.add(ldapService.findUserByUsername(name.get(3).substring(4)));		
		});
		layout.removeAllComponents();
		layout=userListLayout.createUserListLayoutFromLdap(teamMembers);
	}
}
