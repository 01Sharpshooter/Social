package hu.mik.ui;



import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.ComponentResizeListener;
import com.ejt.vaadin.sizereporter.SizeReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;

import de.steinwedel.messagebox.MessageBox;
import hu.mik.beans.FriendRequest;
import hu.mik.beans.Friendship;
import hu.mik.beans.LdapGroup;
import hu.mik.beans.User;
import hu.mik.beans.LdapUser;
import hu.mik.constants.LdapConstants;
import hu.mik.constants.SystemConstants;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.listeners.NewMessageListener;
import hu.mik.services.FriendRequestService;
import hu.mik.services.FriendshipService;
import hu.mik.services.LdapService;
import hu.mik.services.MessageBroadcastService;
import hu.mik.services.UserService;
import hu.mik.views.AdminView;
import hu.mik.views.ContactListView;
import hu.mik.views.MainView;
import hu.mik.views.MessagesView;
import hu.mik.views.PictureUploadView;
import hu.mik.views.ProfileView;
import hu.mik.views.RequestsView;
import hu.mik.views.UserListView;


@SuppressWarnings("serial")
@SpringUI(path="/main")
@SpringViewDisplay
@Theme(ThemeConstants.UI_THEME)
@Push
@PreserveOnRefresh
@Viewport("width=device-width,initial-scale=1")
public class MainUI extends UI implements ViewDisplay, NewMessageListener{
	
	@Autowired
	private FriendshipService friendshipService;
	@Autowired
	private FriendRequestService friendRequestService;
	@Autowired
	private UserService userService;
	@Autowired
	private LdapService ldapService;
	
	private static List<User> onlineUsers=new CopyOnWriteArrayList<>();
	private Panel viewDisplay;
	private WrappedSession session=VaadinService.getCurrentRequest().getWrappedSession();
	private User user;
	private LdapUser userLdap;
	private User sideUser;
	private Image naviBarImage;
	private MessagesView messageView;
	private VerticalLayout base=new VerticalLayout();
	private TextField nameSearchTf;
	private CssLayout navigationBar;
	private CssLayout dropDownMenu;
	private boolean menuIconFlag=false;
	private LdapGroup adminGroup;
	
	private SecurityContext securityContext=SecurityContextHolder.getContext();
	
	@Override
	protected void init(VaadinRequest request){	
			getPage().setTitle("Serious");
			System.out.println(securityContext.getAuthentication());
			String userName=securityContext.getAuthentication().getName();
			user=userService.findUserByUsername(userName);
			userLdap=ldapService.findUserByUsername(userName);
			System.out.println(ldapService.findGroupByGroupName("users").getListOfMembers());
			if(user==null) {
				user=userService.registerUser(userName);
			}
			session.setAttribute("SecurityContext", securityContext);
			session.setAttribute(SystemConstants.SESSION_ATTRIBUTE_LDAP_USER, userLdap.getUsername());
			onlineUsers.add(user);
			adminGroup=ldapService.findGroupByGroupName(LdapConstants.GROUP_ADMIN_NAME);
			final VerticalLayout workingSpace=new VerticalLayout();	
			this.getNavigator().addViewChangeListener(this::viewChangeListener);
			workingSpace.setSizeFull();
			base.setSizeFull();
			base.setMargin(false);		
			Label title=new Label("Social");
			title.setSizeFull();
			Responsive.makeResponsive(title);
			title.addStyleName(ThemeConstants.RESPONSIVE_FONT);			
			title.addStyleName("h1");
			dropDownMenu=createDropDownMenu();
			navigationBar=createNaviBar();
			viewDisplay=new Panel();
			viewDisplay.setSizeFull();
			viewDisplay.setId("viewDisplay");
			base.setId("base");
			navigationBar.setId("navigationBar");
			base.addComponent(title);
			base.addComponent(navigationBar);
			base.addComponent(dropDownMenu);
			base.addComponent(viewDisplay);
			base.setExpandRatio(title, 5);
			base.setExpandRatio(navigationBar, 10);
			base.setExpandRatio(dropDownMenu, 1);
			base.setExpandRatio(viewDisplay, 85);
			base.setStyleName(ThemeConstants.BORDERED_THICK);

			Responsive.makeResponsive(base);
			setContent(base);
		}

	@Override
	public void showView(View view) {
		if(viewDisplay!=null){
			viewDisplay.setContent((Component) view);
		}
		
	}
	private CssLayout createDropDownMenu() {
		CssLayout dropDownMenu=new CssLayout();
		dropDownMenu.setId("dropDownMenu");
		dropDownMenu.addStyleName(ThemeConstants.BORDERED_GREEN);
		if(adminGroup.getListOfMembers().contains(userLdap.getId())) {
			Label lblAdmin=new Label("Admin");
			dropDownMenu.addComponent(lblAdmin);
		}
		Label lblMain=new Label("Main");
		dropDownMenu.addComponent(lblMain);
		Label lblProfile=new Label("Profile");
		dropDownMenu.addComponent(lblProfile);
		Label lblMessages=new Label("Messages");
		dropDownMenu.addComponent(lblMessages);
		Label lblLogout=new Label("Logout");
		dropDownMenu.addComponent(lblLogout);
		dropDownMenu.setVisible(false);
		dropDownMenu.addLayoutClickListener(this::naviBarClickListener);
		return dropDownMenu;
	}

	public static List<User> getOnlineUsers() {
		return onlineUsers;
	}
	
	private boolean viewChangeListener(ViewChangeEvent event){
		if(event.getViewName().equals(MessagesView.NAME)){
			this.messageView=(MessagesView) event.getNewView();
//			if(!sideUser.equals(user))
//				changeSideMenu(user);
		}else if(event.getViewName().equals(UserListView.NAME)){
//			if(!sideUser.equals(user))
//				changeSideMenu(user);
		}else{
			MessageBroadcastService.unregister(this, user.getUsername());
		}
		return true;
	}
	
	
	

	@Override
	public void detach() {
		if(user!=null){
			MessageBroadcastService.unregister(this, user.getUsername());
		}
		super.detach();
	}

	@Override
	public void receiveMessage(String message, int senderId) {
		access(new Runnable() {
			
			@Override
			public void run() {
				if(messageView!=null){
					messageView.receiveMessage(message, senderId);
				}				
			}
		});
		
	}
	
	public CssLayout createNaviBar(){
		CssLayout naviBar=new CssLayout();
		naviBar.setWidth("100%");
		naviBar.addLayoutClickListener(this::naviBarClickListener);
		Responsive.makeResponsive(naviBar);
		naviBarImage=new Image(null, new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImageName()))); 
		naviBarImage.setId("profilePicture");
		naviBarImage.addStyleName(ThemeConstants.BORDERED_IMAGE);
		naviBarImage.addStyleName(ThemeConstants.NAVIGATION_BAR_ICON);
		naviBarImage.addClickListener(this::profileImageClickListener);
		naviBar.addComponent(naviBarImage);
		Label name=new Label();
		name.setValue(user.getUsername());
		name.setId("username");
		naviBar.addComponent(name);
		Image naviBarIcon=new Image(null, new FileResource(new File(ThemeConstants.SYSTEM_IMAGE_MENU_ICON))); 
		naviBarIcon.addStyleName(ThemeConstants.NAVIGATION_BAR_ICON);
		naviBarIcon.setId("menuIcon");
		naviBarIcon.addClickListener(this::menuIconClickListener);
		naviBar.addComponent(naviBarIcon);

		if(adminGroup.getListOfMembers().contains(userLdap.getId())) {
			Label lblAdmin=new Label("Admin");
			naviBar.addComponent(lblAdmin);
		}
		Label lblMain=new Label("Main");
		naviBar.addComponent(lblMain);
		Label lblMessages=new Label("Messages");
		naviBar.addComponent(lblMessages);
		Label lblContacts=new Label("Contacts");
		naviBar.addComponent(lblContacts);
		Label lblLogout=new Label("Logout");
		naviBar.addComponent(lblLogout);
		nameSearchTf=new TextField();
		nameSearchTf.setStyleName(ValoTheme.BUTTON_SMALL);
		nameSearchTf.setValue("Search for a user...");
		nameSearchTf.addFocusListener(this::nameSearchFocusListener);
		Button namesearchButton=new Button("Search", this::nameSearchClickListener);
		namesearchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		namesearchButton.addStyleName(ThemeConstants.BLUE_TEXT);
		naviBar.addComponent(namesearchButton);
		naviBar.addComponent(nameSearchTf);
		return naviBar;
	}
	
	private void naviBarClickListener(LayoutClickEvent event) {
		if(event.getComponent().equals(dropDownMenu) && event.getClickedComponent()!=null) {
			changeDropDownVisibility();
		}
		if(event.getClickedComponent()!=null && event.getClickedComponent().getClass().equals(Label.class)) {
			Label lblClicked=(Label) event.getClickedComponent();
//			for(Component lbl:navigationBar) {
//				lbl.removeStyleName(ThemeConstants.GREEN_HIGHLIGHT);
//			}
//			lblClicked.addStyleName(ThemeConstants.GREEN_HIGHLIGHT);
			switch (lblClicked.getValue()) {
			case "Main":
				getNavigator().navigateTo(MainView.NAME);
				break;
			case "Messages":
				getNavigator().navigateTo(MessagesView.NAME);
				break;
			case "Contacts":
				getNavigator().navigateTo(ContactListView.NAME+"/"+userLdap.getUsername());
				break;
			case "Logout":
				getPage().setLocation("/logout");
				MainUI.getOnlineUsers().remove((User)session.getAttribute("User"));
				session=null;
			case "Admin":
				getNavigator().navigateTo(AdminView.NAME);
				break;
			default:
				getNavigator().navigateTo(ProfileView.NAME+"/"+userLdap.getUsername());
				break;
			}
		}
		
	}
	
	
	private void profileImageClickListener(com.vaadin.event.MouseEvents.ClickEvent event) {
		getNavigator().navigateTo(PictureUploadView.NAME);
	}
	
	private void menuIconClickListener(com.vaadin.event.MouseEvents.ClickEvent event) {
		changeDropDownVisibility();
	}
	private void changeDropDownVisibility(){
		menuIconFlag=!menuIconFlag;
		dropDownMenu.setVisible(menuIconFlag);
	}
	
	private void friendRequestClickListener(Button.ClickEvent event){
		FriendRequest fr=new FriendRequest();
		fr.setRequestorId(user.getId());
		fr.setRequestedId(sideUser.getId());
		friendRequestService.saveFriendRequest(fr);
		MessageBox.createInfo()
			.withOkButton()
			.withCaption("Request sent")
			.withMessage("Request has been sent to "+sideUser.getUsername())
			.open();
		refreshImage();
	}
	
	private void acceptRequestClickListener(Button.ClickEvent event){
		friendRequestService.deleteFriendRequest(sideUser.getId(), user.getId());
		Friendship fs=new Friendship();
		fs.setUserId(user.getId());
		fs.setFriendId(sideUser.getId());
		friendshipService.saveFriendship(fs);
		fs=new Friendship();
		fs.setUserId(sideUser.getId());
		fs.setFriendId(user.getId());
		friendshipService.saveFriendship(fs);
		refreshImage();
	}
	
	private void rejectRequestClickListener(Button.ClickEvent event){
		friendRequestService.deleteFriendRequest(sideUser.getId(), user.getId());
		refreshImage();
	}
	
	private void removeFriendClickListener(Button.ClickEvent event){
		friendshipService.deleteFriendship(user.getId(), sideUser.getId());
		friendshipService.deleteFriendship(sideUser.getId(), user.getId());
		MessageBox.createInfo()
		.withOkButton()
		.withCaption("User removed from friends")
		.withMessage(sideUser.getUsername()+" has been removed from your friends.")
		.open();
		refreshImage();
	}
	
	private void friendListClickListener(Button.ClickEvent event){
		getNavigator().navigateTo(ContactListView.NAME+"/"+sideUser.getId());
//		((FriendListView)getNavigator().getCurrentView()).fill(sideUser);
	}
	
	public void refreshImage(){		
		User changedUser=userService.findUserById(this.user.getId());
		user.setImageName(changedUser.getImageName());
		((Image)navigationBar.getComponent(0)).setSource(new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImageName())));
	}
	
	private void friendRequestsClickListener(Button.ClickEvent event){
		getNavigator().navigateTo(RequestsView.NAME);
	}
	
	private void nameSearchClickListener(Button.ClickEvent event){
		if(!nameSearchTf.isEmpty()){
			getNavigator().navigateTo(UserListView.NAME);		
			((UserListView)getNavigator().getCurrentView()).fill(nameSearchTf.getValue());
			nameSearchTf.clear();
		}
	}
	
	private void nameSearchFocusListener(FocusEvent event){
		((TextField)event.getComponent()).clear();
	}
	
	
}
