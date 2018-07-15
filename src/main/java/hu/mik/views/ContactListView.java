package hu.mik.views;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
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
import hu.mik.services.FriendRequestService;
import hu.mik.services.FriendshipService;
import hu.mik.services.LdapService;
import hu.mik.services.UserService;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name = ContactListView.NAME)
public class ContactListView extends VerticalLayout implements View {
	public static final String NAME = "FriendListView";
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

	private Panel panel = new Panel();
	private CssLayout layout = new CssLayout();
	private List<User> friendList = new ArrayList<>();
	private VerticalLayout base = new VerticalLayout();
	private User dbUser;
	private LdapUser ldapUser;
	private List<FriendRequest> requests;
	private List<LdapGroup> groups;
	private ComboBox<LdapGroup> groupSelect;

	@Override
	public void enter(ViewChangeEvent event) {
		this.addComponent(this.panel);
		this.setMargin(false);
		this.setSizeFull();
//		base.setMargin(false);
//		base.setSpacing(false);
		this.dbUser = this.userService.findUserByUsername(event.getParameters());
		this.ldapUser = this.ldapService.findUserByUsername(event.getParameters());

		this.requests = this.friendRequestService.findAllByRequestedId(this.dbUser.getId());
		this.groups = this.ldapService.findGroupsByUserId(this.ldapUser.getId());

		this.panel.setSizeFull();
		this.panel.setCaption(this.dbUser.getUsername() + "'s " + "Friendlist:");

		this.friendshipService.findAllByUserId(this.dbUser.getId())
				.forEach(friendShip -> this.friendList.add(this.userService.findUserById(friendShip.getFriendId())));

		this.groupSelect = new ComboBox<>("Select a team:", this.groups);
		this.groupSelect.setEmptySelectionAllowed(false);
		this.groupSelect.addSelectionListener(this::groupSelectionListener);
		if (this.groups.size() > 0) {
			this.groupSelect.setSelectedItem(this.groups.get(0));
		}
		this.groupSelect.setVisible(false);

		RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
		List<String> radioFixItems = new ArrayList<>();
		radioFixItems.add("Friends(" + this.friendList.size() + ")");
		radioFixItems.add("Requests(" + this.requests.size() + ")");
		if (!this.groups.isEmpty()) {
			radioFixItems.add("Teams(" + this.groups.size() + ")");
		}
		radioButtonGroup.setItems(radioFixItems);
		radioButtonGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		radioButtonGroup.setId("radioTeamSelect");
//		radioButtonGroup.addStyleName(ThemeConstants.RESPONSIVE_FONT);
		radioButtonGroup.addSelectionListener(this::radioBtnListener);
		radioButtonGroup.setSelectedItem("Friends(" + (this.friendList.size()) + ")");

//		layout=userListLayout.createUserListLayoutFromDb(friendList);

		this.base.addComponent(radioButtonGroup);
		this.base.addComponent(this.groupSelect);
		this.base.addComponent(this.layout);
		this.base.setExpandRatio(radioButtonGroup, 10);
		this.base.setExpandRatio(this.groupSelect, 10);
		this.base.setExpandRatio(this.layout, 80);

		this.panel.setContent(this.base);
	}

	private void radioBtnListener(SingleSelectionEvent<String> event) {
		String value = event.getSource().getValue();
		this.groupSelect.setVisible(false);
		this.layout.removeAllComponents();
		if (value.matches("Requests.*")) {
			List<User> requestors = new ArrayList<>();
			this.requests.forEach(request -> requestors.add(this.userService.findUserById(request.getRequestorId())));
			this.layout = this.userListLayout.createUserListLayoutFromDb(requestors);
		} else if (value.matches("Friends.*")) {
			this.layout = this.userListLayout.createUserListLayoutFromDb(this.friendList);
		} else if (value.matches("Teams.*")) {
			this.groupSelect.setVisible(true);
			this.setLayoutByTeam(this.groupSelect.getValue());
		}
	}

	private void groupSelectionListener(SingleSelectionEvent<LdapGroup> event) {
		LdapGroup selectedGroup = event.getValue();
		this.setLayoutByTeam(selectedGroup);

	}

	private void setLayoutByTeam(LdapGroup team) {
		List<LdapUser> teamMembers = new ArrayList<>();
		team.getListOfMembers().forEach(name -> {
			if (!name.equals(this.ldapUser.getId())) {
				teamMembers.add(this.ldapService.findUserByUsername(name.get(3).substring(4)));
			}
		});
		this.layout.removeAllComponents();
		this.layout = this.userListLayout.createUserListLayoutFromLdap(teamMembers);
	}
}
