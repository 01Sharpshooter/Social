package hu.mik.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.FileResource;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.LdapUser;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.beans.User;
import hu.mik.constants.LdapConstants;
import hu.mik.constants.SystemConstants;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.listeners.NewMessageListener;
import hu.mik.services.LdapService;
import hu.mik.services.MessageBroadcastService;
import hu.mik.services.UserService;
import hu.mik.utils.UserUtils;
import hu.mik.views.AdminView;
import hu.mik.views.ContactListView;
import hu.mik.views.MainView;
import hu.mik.views.MessagesView;
import hu.mik.views.PictureUploadView;
import hu.mik.views.ProfileView;
import hu.mik.views.UserListView;

@SuppressWarnings("serial")
@SpringUI(path = "/main")
@SpringViewDisplay
@Theme(ThemeConstants.UI_THEME)
@Push(transport = Transport.WEBSOCKET_XHR)
@PreserveOnRefresh
//@VaadinServletConfiguration(ui = MainUI.class, productionMode = false, widgetset = "hu.mik.gwt.SocialWidgetset.gwt.xml")
@Widgetset("hu.mik.gwt.SocialWidgetset")
@Viewport("width=device-width,initial-scale=1")
public class MainUI extends UI implements ViewDisplay, NewMessageListener {

	@Autowired
	private UserService userService;
	@Autowired
	private LdapService ldapService;

	private static List<User> onlineUsers = new CopyOnWriteArrayList<>();
	private Panel viewDisplay;
	private WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
	private User user;
	private LdapUser userLdap;
	private SocialUserWrapper socialUser;
	private Image naviBarImage;
	private MessagesView messageView;
	private VerticalLayout base;
	private TextField nameSearchTf;
	private CssLayout navigationBar;
	private CssLayout dropDownMenu;
	private boolean menuIconFlag = false;
	private LdapGroup adminGroup;

	@Autowired
	UserUtils userUtils;

	private SecurityContext securityContext = SecurityContextHolder.getContext();

	@Override
	protected void init(VaadinRequest request) {
		this.socialUser = this.userUtils.getLoggedInUser();

		this.getPage().setTitle("Serious");
		String userName = this.securityContext.getAuthentication().getName();

		this.user = this.userService.findUserByUsername(userName);
		this.userLdap = this.ldapService.findUserByUsername(userName);
		if (this.user == null) {
			this.user = this.userService.createDefaultUserWithUsername(userName);
		}
		this.session.setAttribute("SecurityContext", this.securityContext);
		this.session.setAttribute(SystemConstants.SESSION_ATTRIBUTE_LDAP_USER, this.userLdap.getUsername());
		onlineUsers.add(this.user);
		this.adminGroup = this.ldapService.findGroupByGroupName(LdapConstants.GROUP_ADMIN_NAME);
		final VerticalLayout workingSpace = new VerticalLayout();
		this.getNavigator().addViewChangeListener(this::viewChangeListener);
		workingSpace.setSizeFull();

		this.createContent();
	}

	private void createContent() {
		this.base = new VerticalLayout();
		this.base.setSizeFull();
		this.base.setMargin(false);

		Responsive.makeResponsive(this.base);

		this.createTitleComponent();
		this.createNaviBar();
		this.createDropDownMenu();
		this.createViewDisplay();
		this.base.setId("base");

		this.base.setStyleName(ThemeConstants.BORDERED_THICK);
		this.setContent(this.base);
	}

	private void createViewDisplay() {
		this.viewDisplay = new Panel();
		this.viewDisplay.setSizeFull();
		this.viewDisplay.setId("viewDisplay");
		this.base.addComponent(this.viewDisplay);
		this.base.setExpandRatio(this.viewDisplay, 85);
	}

	private void createTitleComponent() {
		Label title = new Label("Social");
		title.setSizeFull();
		title.addStyleName(ThemeConstants.RESPONSIVE_FONT);
		title.addStyleName("h1");
		this.base.addComponent(title);
		this.base.setExpandRatio(title, 5);
	}

	@Override
	public void showView(View view) {
		if (this.viewDisplay != null) {
			this.viewDisplay.setContent((Component) view);
		}

	}

	private void createDropDownMenu() {
		this.dropDownMenu = new CssLayout();
		this.dropDownMenu.setId("dropDownMenu");
		this.dropDownMenu.addStyleName(ThemeConstants.BORDERED_GREEN);
		Label lblProfile = new Label(VaadinIcons.USER.getHtml() + " Profile", ContentMode.HTML);
		lblProfile.addStyleName(ThemeConstants.ICON_WHITE);
		this.dropDownMenu.addComponent(lblProfile);
		for (Label label : this.createNaviBarLabelList()) {
			this.dropDownMenu.addComponent(label);
		}
		this.dropDownMenu.setVisible(false);
		this.dropDownMenu.addLayoutClickListener(this::naviBarClickListener);
		this.base.addComponent(this.dropDownMenu);
		this.base.setExpandRatio(this.dropDownMenu, 1);
	}

	public static List<User> getOnlineUsers() {
		return onlineUsers;
	}

	private boolean viewChangeListener(ViewChangeEvent event) {
		if (event.getViewName().equals(MessagesView.NAME)) {
			this.messageView = (MessagesView) event.getNewView();
		} else if (event.getViewName().equals(UserListView.NAME)) {

		} else {
			MessageBroadcastService.unregister(this, this.user.getUsername());
		}
		return true;
	}

	@Override
	public void detach() {
		if (this.user != null) {
			MessageBroadcastService.unregister(this, this.user.getUsername());
		}
		super.detach();
	}

	@Override
	public void receiveMessage(String message, int senderId) {
		this.access(() -> {
			if (this.messageView != null) {
				this.messageView.receiveMessage(message, senderId);
			}
		});

	}

	public void createNaviBar() {
		this.navigationBar = new CssLayout();
		this.navigationBar.setWidth("100%");
		this.navigationBar.addLayoutClickListener(this::naviBarClickListener);
		this.naviBarImage = new Image(null,
				new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION + this.user.getImageName())));
		this.naviBarImage.setId("profilePicture");
		this.naviBarImage.addStyleName(ThemeConstants.BORDERED_IMAGE);
		this.naviBarImage.addStyleName(ThemeConstants.NAVIGATION_BAR_ICON);
		this.naviBarImage.addClickListener(this::profileImageClickListener);
		this.navigationBar.addComponent(this.naviBarImage);
		Label name = new Label();
		name.setValue(this.userLdap.getFullName());
		name.setId("username");
		this.navigationBar.addComponent(name);
		Image naviBarIcon = new Image(null, new FileResource(new File(ThemeConstants.SYSTEM_IMAGE_MENU_ICON)));
		naviBarIcon.addStyleName(ThemeConstants.NAVIGATION_BAR_ICON);
		naviBarIcon.setId("menuIcon");
		naviBarIcon.addClickListener(this::menuIconClickListener);
		this.navigationBar.addComponent(naviBarIcon);

		for (Label label : this.createNaviBarLabelList()) {
			this.navigationBar.addComponent(label);
		}
		this.nameSearchTf = new TextField();
		this.nameSearchTf.setStyleName(ValoTheme.BUTTON_SMALL);
		this.nameSearchTf.setPlaceholder("Search for a user...");
		Button namesearchButton = new Button("Search", this::nameSearchClickListener);
		namesearchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		namesearchButton.addStyleName(ThemeConstants.BLUE_TEXT);
		this.navigationBar.addComponent(namesearchButton);
		this.navigationBar.addComponent(this.nameSearchTf);
		this.navigationBar.setId("navigationBar");
		this.base.addComponent(this.navigationBar);
		this.base.setExpandRatio(this.navigationBar, 10);
	}

	private void naviBarClickListener(LayoutClickEvent event) {
		if (event.getComponent().equals(this.dropDownMenu) && event.getClickedComponent() != null) {
			this.changeDropDownVisibility();
		}
		if (event.getClickedComponent() != null && event.getClickedComponent().getClass().equals(Label.class)) {
			Label lblClicked = (Label) event.getClickedComponent();
			String label = lblClicked.getValue().replaceFirst("<span.*</span><", "<").replaceFirst("<.?span>", "")
					.replaceFirst("<span.*>", "");
			switch (label) {
			case "Main":
				this.getNavigator().navigateTo(MainView.NAME);
				break;
			case "Messages":
				this.getNavigator().navigateTo(MessagesView.NAME);
				break;
			case "Contacts":
				this.getNavigator().navigateTo(ContactListView.NAME + "/" + this.userLdap.getUsername());
				break;
			case "Logout":
				this.getPage().setLocation("/logout");
				MainUI.getOnlineUsers().remove(this.session.getAttribute("User"));
				this.session = null;
			case "Admin":
				this.getNavigator().navigateTo(AdminView.NAME);
				break;
			default:
				this.getNavigator().navigateTo(ProfileView.NAME + "/" + this.userLdap.getUsername());
				break;
			}
		}

	}

	private void profileImageClickListener(com.vaadin.event.MouseEvents.ClickEvent event) {
		this.getNavigator().navigateTo(PictureUploadView.NAME);
	}

	private void menuIconClickListener(com.vaadin.event.MouseEvents.ClickEvent event) {
		this.changeDropDownVisibility();
	}

	private void changeDropDownVisibility() {
		this.menuIconFlag = !this.menuIconFlag;
		this.dropDownMenu.setVisible(this.menuIconFlag);
	}

	public void refreshImage() {
		User changedUser = this.userService.findUserById(this.user.getId());
		this.user.setImageName(changedUser.getImageName());
		((Image) this.navigationBar.getComponent(0)).setSource(
				new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION + this.user.getImageName())));
	}

	private void nameSearchClickListener(Button.ClickEvent event) {
		if (!this.nameSearchTf.isEmpty()) {
			this.getNavigator().navigateTo(UserListView.NAME + "/" + this.nameSearchTf.getValue());
//			((UserListView) this.getNavigator().getCurrentView()).fill(this.nameSearchTf.getValue());
			this.nameSearchTf.clear();
		}
	}

	private List<Label> createNaviBarLabelList() {
		List<Label> lblList = new ArrayList<>();

		Label lblAdmin = new Label(VaadinIcons.COG.getHtml() + "<span class=\"folding\">Admin</span>",
				ContentMode.HTML);
		lblAdmin.addStyleName(ThemeConstants.ICON_WHITE);
		Label lblMain = new Label(VaadinIcons.HOME.getHtml() + "<span class=\"folding\">Main</span>", ContentMode.HTML);
		lblMain.addStyleName(ThemeConstants.ICON_WHITE);
		Label lblMessages = new Label(VaadinIcons.CHAT.getHtml() + "<span class=\"folding\">Messages</span>",
				ContentMode.HTML);
		lblMessages.addStyleName(ThemeConstants.ICON_WHITE);
		Label lblContacts = new Label(VaadinIcons.USERS.getHtml() + "<span class=\"folding\">Contacts</span>",
				ContentMode.HTML);
		lblContacts.addStyleName(ThemeConstants.ICON_WHITE);
		Label lblLogout = new Label(VaadinIcons.EXIT.getHtml() + "<span class=\"folding\">Logout</span>",
				ContentMode.HTML);
		lblLogout.addStyleName(ThemeConstants.ICON_WHITE);

		if (this.adminGroup.getListOfMembers().contains(this.userLdap.getId())) {
			lblList.add(lblAdmin);
		}
		lblList.add(lblMain);
		lblList.add(lblMessages);
		lblList.add(lblContacts);
		lblList.add(lblLogout);

		return lblList;
	}

}
