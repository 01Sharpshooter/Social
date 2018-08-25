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
import hu.mik.beans.SocialUserWrapper;
import hu.mik.beans.User;
import hu.mik.components.UserListLayout;
import hu.mik.services.FriendRequestService;
import hu.mik.services.FriendshipService;
import hu.mik.services.LdapService;
import hu.mik.services.UserService;
import hu.mik.utils.Converters;
import hu.mik.utils.UserUtils;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name = ContactListView.NAME)
public class ContactListView extends Panel implements View {
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
	@Autowired
	UserUtils userUtils;

	private CssLayout contactsLayout;
	private List<User> friendList = new ArrayList<>();
	private VerticalLayout base;
	private SocialUserWrapper socialUser;
	private List<FriendRequest> requests;
	private List<LdapGroup> groups;
	private ComboBox<LdapGroup> groupSelect;

	@Override
	public void enter(ViewChangeEvent event) {
		this.socialUser = this.userUtils.initSocialUser(event.getParameters());
		this.base = new VerticalLayout();

		this.setSizeFull();
		this.setCaption(this.socialUser.getDbUser().getUsername() + "'s " + "Friendlist:");

		this.requests = this.friendRequestService.findAllByRequestedId(this.socialUser.getDbUser().getId());
		this.groups = this.ldapService.findGroupsByUserId(this.socialUser.getLdapUser().getId());

		this.friendshipService.findAllByUserId(this.socialUser.getDbUser().getId())
				.forEach(friendShip -> this.friendList.add(this.userService.findUserById(friendShip.getFriendId())));

		this.createContent();

		this.setContent(this.base);
	}

	private void createContent() {
		this.contactsLayout = new CssLayout();
		this.createGroupComboBox();
		this.createRadioBtnGroup();

		this.base.addComponent(this.contactsLayout);
		this.base.setExpandRatio(this.contactsLayout, 80);
	}

	private void createGroupComboBox() {
		this.groupSelect = new ComboBox<>("Select a team:", this.groups);
		this.groupSelect.setEmptySelectionAllowed(false);
		this.groupSelect.addSelectionListener(this::groupSelectionListener);
		if (this.groups.size() > 0) {
			this.groupSelect.setSelectedItem(this.groups.get(0));
		}
		this.groupSelect.setVisible(false);

		this.base.addComponent(this.groupSelect);
		this.base.setExpandRatio(this.groupSelect, 10);
	}

	private void createRadioBtnGroup() {
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

		this.base.addComponent(radioButtonGroup);
		this.base.setExpandRatio(radioButtonGroup, 10);
	}

	private void radioBtnListener(SingleSelectionEvent<String> event) {
		String value = event.getSource().getValue();
		this.groupSelect.setVisible(false);
		this.contactsLayout.removeAllComponents();
		if (value.matches("Requests.*")) {
			List<User> requestors = new ArrayList<>();
			this.requests.forEach(request -> requestors.add(this.userService.findUserById(request.getRequestorId())));
			this.contactsLayout = this.userListLayout.createUserListLayoutFromDb(requestors);
		} else if (value.matches("Friends.*")) {
			this.contactsLayout = this.userListLayout.createUserListLayoutFromDb(this.friendList);
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
			if (!name.equals(this.socialUser.getLdapUser().getId())) {
				teamMembers.add(this.ldapService.findUserByUsername(Converters.convertLdapNameToUsername(name)));
			}
		});
		this.contactsLayout.removeAllComponents();
		this.contactsLayout = this.userListLayout.createUserListLayoutFromLdap(teamMembers);
	}
}
