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
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.FileResource;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import hu.mik.beans.Conversation;
import hu.mik.beans.LdapGroup;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.beans.User;
import hu.mik.components.NavigationBar;
import hu.mik.constants.LdapConstants;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.listeners.NewMessageListener;
import hu.mik.services.ChatService;
import hu.mik.services.LdapService;
import hu.mik.services.MessageBroadcastService;
import hu.mik.utils.UserUtils;
import hu.mik.views.MessagesView;

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
	private MessagesView messageView;
	private VerticalLayout base;
	private NavigationBar navigationBar;
	private LdapGroup adminGroup;
	private SecurityContext securityContext = SecurityContextHolder.getContext();

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
		switch (event.getViewName()) {
		case MessagesView.NAME:
			this.messageView = (MessagesView) event.getNewView();
			break;

		default:
			break;
		}

		this.navigationBar.removeStyleName(ThemeConstants.SHOW_DROPDOWN);
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
		this.navigationBar = new NavigationBar(this.socialUser,
				this.messageService.getNumberOfUnseenConversations(this.socialUser.getDbUser()));
		this.base.addComponent(this.navigationBar);
		this.base.setExpandRatio(this.navigationBar, 10);
	}

	public void refreshImage() {
		this.socialUser = this.userUtils.getLoggedInUser();
		this.navigationBar.refreshImage(new FileResource(
				new File(UserConstants.getImageLocation() + this.socialUser.getDbUser().getImageName())));
	}

	public void refreshUnseenConversationNumber() {
		Long unseenCount = this.messageService.getNumberOfUnseenConversations(this.socialUser.getDbUser());
		this.navigationBar.refreshUnseenConversationNumber(unseenCount);
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
