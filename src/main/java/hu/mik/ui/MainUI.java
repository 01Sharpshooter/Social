package hu.mik.ui;

import java.io.File;
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
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.Conversation;
import hu.mik.beans.LdapGroup;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.beans.User;
import hu.mik.constants.LdapConstants;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.listeners.NewMessageListener;
import hu.mik.services.ChatService;
import hu.mik.services.LdapService;
import hu.mik.services.MessageBroadcastService;
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
	private LdapService ldapService;
	@Autowired
	private ChatService messageService;
	@Autowired
	private UserUtils userUtils;

	private static List<User> onlineUsers = new CopyOnWriteArrayList<>();
	private Panel viewDisplay;
	private SocialUserWrapper socialUser;
	private Image naviBarImage;
	private MessagesView messageView;
	private VerticalLayout base;
	private TextField nameSearchTf;
	private CssLayout navigationBar;
	private boolean dropDownShown = false;
	private LdapGroup adminGroup;
	private Label lblMessages;

	private SecurityContext securityContext = SecurityContextHolder.getContext();
	private CssLayout naviPerson;
	private CssLayout naviItemList;

	@Override
	protected void init(VaadinRequest request) {
		this.getPage().setTitle("Serious");
		if (this.userUtils.getLoggedInUser() == null) {
			return;
		}
		this.socialUser = this.userUtils.getLoggedInUser();
		onlineUsers.add(this.socialUser.getDbUser());
		this.adminGroup = this.ldapService.findGroupByGroupName(LdapConstants.GROUP_ADMIN_NAME);
		final VerticalLayout workingSpace = new VerticalLayout();
		this.getNavigator().addViewChangeListener(this::viewChangeListener);
		workingSpace.setSizeFull();

		MessageBroadcastService.register(this, this.socialUser.getDbUser().getId());

//		this.setErrorHandler(new DefaultExceptionHandler(this));

		this.createContent();
	}

	private void createContent() {
		this.base = new VerticalLayout();
		this.base.setSizeFull();
		this.base.setMargin(false);

		Responsive.makeResponsive(this.base);

		// this.createTitleComponent();
		this.createNaviBar();
		this.createViewDisplay();
		this.base.setId("base");

		this.setContent(this.base);
	}

	private void createViewDisplay() {
		this.viewDisplay = new Panel();
		this.viewDisplay.setSizeFull();
		this.viewDisplay.setId("viewDisplay");
		this.base.addComponent(this.viewDisplay);
		this.base.setExpandRatio(this.viewDisplay, 85);
	}

	@Override
	public void showView(View view) {
		if (this.viewDisplay != null) {
			this.viewDisplay.setContent((Component) view);
		}

	}

	public static List<User> getOnlineUsers() {
		return onlineUsers;
	}

	private boolean viewChangeListener(ViewChangeEvent event) {
		if (event.getViewName().equals(MessagesView.NAME)) {
			this.messageView = (MessagesView) event.getNewView();
		}

		this.navigationBar.removeStyleName(ThemeConstants.SHOW_DROPDOWN);
		this.dropDownShown = false;

		return true;
	}

	@Override
	public void detach() {
		if (this.socialUser != null) {
			MessageBroadcastService.unregister(this, this.socialUser.getDbUser().getId());
		}
		super.detach();
	}

	@Override
	public void receiveMessage(Conversation conversation) {
		this.access(() -> {
			if (this.messageView != null && this.getNavigator().getCurrentView().equals(this.messageView)) {
				this.messageView.receiveMessage(conversation);
			} else {
				this.refreshUnseenConversationNumber();
				this.showNewMessageNotification(conversation);
			}
		});
	}

	private void showNewMessageNotification(Conversation conversation) {
		Notification notification = Notification.show(conversation.getLastMessage().getSender().getFullName(),
				conversation.getLastMessage().getMessage(), Notification.Type.TRAY_NOTIFICATION);
		notification.setIcon(VaadinIcons.COMMENT);
	}

	public void createNaviBar() {
		this.navigationBar = new CssLayout();
		this.navigationBar.addLayoutClickListener(this::naviBarClickListener);
		this.navigationBar.setId("navigationBar");
		this.createNaviBarPerson();
		this.createNaviBarLabelList(this.navigationBar);
		this.createNaviSearchField();
		this.createNaviMenuIcon();
		this.base.addComponent(this.navigationBar);
		this.base.setExpandRatio(this.navigationBar, 10);
	}

	private void createNaviMenuIcon() {
		Image naviMenuIcon = new Image(null, new FileResource(new File(ThemeConstants.SYSTEM_IMAGE_MENU_ICON)));
		naviMenuIcon.addStyleName(ThemeConstants.NAVIGATION_BAR_ICON);
		naviMenuIcon.setId("menuIcon");
		naviMenuIcon.addClickListener(e -> this.changeDropDownVisibility());
		this.navigationBar.addComponent(naviMenuIcon);
	}

	private void createNaviSearchField() {
		CssLayout naviSearchField = new CssLayout();
		naviSearchField.addStyleName(ThemeConstants.NAVI_SEARCH_FIELD);
		this.nameSearchTf = new TextField();
		this.nameSearchTf.setStyleName(ValoTheme.BUTTON_SMALL);
		this.nameSearchTf.setPlaceholder("Search for a user...");
		Button namesearchButton = new Button("Search", this::nameSearchClickListener);
		namesearchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		namesearchButton.addStyleName(ThemeConstants.BLUE_TEXT);
		naviSearchField.addComponents(this.nameSearchTf, namesearchButton);
		this.navigationBar.addComponent(naviSearchField);

	}

	private void createNaviBarPerson() {
		this.naviPerson = new CssLayout();
		this.naviPerson.addStyleName(ThemeConstants.NAVI_PERSON);
		this.naviBarImage = this.socialUser.getDbUser().getVaadinImage();
		this.naviBarImage.setId("profilePicture");
		this.naviBarImage.addStyleName(ThemeConstants.BORDERED_IMAGE);
		this.naviBarImage.addStyleName(ThemeConstants.NAVIGATION_BAR_ICON);
		this.naviBarImage.addClickListener(this::profileImageClickListener);
		this.navigationBar.addComponent(this.naviBarImage);
		Label name = new Label();
		name.setValue(this.socialUser.getLdapUser().getFullName());
		name.setId("username");
		this.naviPerson.addComponents(this.naviBarImage, name);
		this.navigationBar.addComponent(this.naviPerson);
	}

	private void naviBarClickListener(LayoutClickEvent event) {
		if (event.getClickedComponent() != null && event.getClickedComponent().getClass().equals(Label.class)) {
			Label lblClicked = (Label) event.getClickedComponent();
			this.naviItemList.forEach(c -> c.removeStyleName(ThemeConstants.NAVI_ITEM_ACTIVE));
			lblClicked.addStyleName(ThemeConstants.NAVI_ITEM_ACTIVE);
			this.changeDropDownVisibility();
			String label = lblClicked.getValue().replaceFirst("<span.*</span><", "<").replaceFirst("<.?span>", "")
					.replaceFirst("<span.*>", "");
			switch (label) {
			case "Main":
				this.getNavigator().navigateTo(MainView.NAME);
				break;
			case "Contacts":
				this.getNavigator().navigateTo(ContactListView.NAME + "/" + this.socialUser.getUsername());
				break;
			case "Logout":
				this.getPage().setLocation("/logout");
				MainUI.getOnlineUsers().remove(this.socialUser.getDbUser());
			case "Admin":
				this.getNavigator().navigateTo(AdminView.NAME);
				break;
			default:
				if (label.contains("Messages")) {
					this.getNavigator().navigateTo(MessagesView.NAME);
					break;
				} else {
					this.getNavigator().navigateTo(ProfileView.NAME + "/" + this.socialUser.getUsername());
					break;
				}
			}
		}

	}

	private void profileImageClickListener(com.vaadin.event.MouseEvents.ClickEvent event) {
		this.getNavigator().navigateTo(PictureUploadView.NAME);
	}

	private void changeDropDownVisibility() {
		if (!this.dropDownShown) {
			this.navigationBar.addStyleName(ThemeConstants.SHOW_DROPDOWN);
		} else {
			this.navigationBar.removeStyleName(ThemeConstants.SHOW_DROPDOWN);
		}
		this.dropDownShown = !this.dropDownShown;
	}

	public void refreshImage() {
		this.socialUser = this.userUtils.getLoggedInUser();
		((Image) this.naviPerson.getComponent(0)).setSource(new FileResource(
				new File(UserConstants.PROFILE_PICTURE_LOCATION + this.socialUser.getDbUser().getImageName())));
	}

	private void nameSearchClickListener(Button.ClickEvent event) {
		if (!this.nameSearchTf.isEmpty()) {
			this.getNavigator().navigateTo(UserListView.NAME + "/" + this.nameSearchTf.getValue());
//			((UserListView) this.getNavigator().getCurrentView()).fill(this.nameSearchTf.getValue());
			this.nameSearchTf.clear();
		}
	}

	private void createNaviBarLabelList(AbstractLayout layout) {
		this.naviItemList = new CssLayout();
		Label lblMain = new Label(VaadinIcons.HOME.getHtml() + "<span class=\"folding\">Main</span>", ContentMode.HTML);
		lblMain.addStyleName(ThemeConstants.NAVI_ITEM_ACTIVE);
		Long unseenCount = this.messageService.getNumberOfUnseenConversations(this.socialUser.getDbUser());
		this.lblMessages = new Label(
				VaadinIcons.CHAT.getHtml() + "<span class=\"folding\">Messages (" + unseenCount + ")</span>",
				ContentMode.HTML);

		Label lblContacts = new Label(VaadinIcons.USERS.getHtml() + "<span class=\"folding\">Contacts</span>",
				ContentMode.HTML);

		Label lblLogout = new Label(VaadinIcons.EXIT.getHtml() + "<span class=\"folding\">Logout</span>",
				ContentMode.HTML);

		this.naviItemList.addComponents(lblMain, this.lblMessages, lblContacts, lblLogout);
		this.naviItemList.addStyleName(ThemeConstants.NAVI_ITEM_LIST);
		layout.addComponent(this.naviItemList);
	}

	public void refreshUnseenConversationNumber() {
		Long unseenCount = this.messageService.getNumberOfUnseenConversations(this.socialUser.getDbUser());
		this.naviItemList.forEach(e -> {
			if (e instanceof Label && ((Label) e).getValue().equals(this.lblMessages.getValue())) {
				((Label) e).setValue(
						VaadinIcons.CHAT.getHtml() + "<span class=\"folding\">Messages (" + unseenCount + ")</span>");
			}
		});
	}

	@Override
	public void refreshConversation(Conversation conversation) {
		this.access(() -> {
			if (this.messageView != null) {
				this.messageView.refreshOrCreateConversation(conversation, false);
			}
		});

	}
}
