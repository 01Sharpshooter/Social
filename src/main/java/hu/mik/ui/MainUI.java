package hu.mik.ui;



import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
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
import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.listeners.NewMessageListener;
import hu.mik.services.FriendRequestService;
import hu.mik.services.FriendshipService;
import hu.mik.services.MessageBroadcastService;
import hu.mik.services.UserService;
import hu.mik.views.FriendListView;
import hu.mik.views.MainView;
import hu.mik.views.MessagesView;
import hu.mik.views.PictureUploadView;
import hu.mik.views.RequestsView;
import hu.mik.views.UserListView;


@SuppressWarnings("serial")
@SpringUI(path="/main")
@SpringViewDisplay
@Theme(ThemeConstants.UI_THEME)
@Push
@PreserveOnRefresh
public class MainUI extends UI implements ViewDisplay, NewMessageListener{
	
	@Autowired
	private FriendshipService friendshipService;
	@Autowired
	private FriendRequestService friendRequestService;
	@Autowired
	private UserService userService;
	
	private static List<User> onlineUsers=new CopyOnWriteArrayList<>();
	private Panel viewDisplay;
	private WrappedSession session=VaadinService.getCurrentRequest().getWrappedSession();
	private User user;
	private User sideUser;
	private MessagesView messageView;
	private VerticalLayout sideMenu;
	private VerticalLayout oldSideMenu;
	private HorizontalLayout base=new HorizontalLayout();
	private TextField nameSearchTf;
	
	@Override
	protected void init(VaadinRequest request){	
			user=userService.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
			session.setAttribute("User", user);
			onlineUsers.add(user);
			sideMenu=createSideMenu(user);
			oldSideMenu=sideMenu;
			final VerticalLayout workingSpace=new VerticalLayout();
			final HorizontalLayout upperMenu=new HorizontalLayout();	
			this.getNavigator().addViewChangeListener(this::viewChangeListener);
			workingSpace.setSizeFull();
			base.setSizeFull();
			final CssLayout navigationBar=createNaviBar();
			upperMenu.addComponent(navigationBar);
			viewDisplay=new Panel();
			viewDisplay.setSizeFull();
			workingSpace.addComponent(upperMenu);
			workingSpace.addComponent(viewDisplay);
			workingSpace.setExpandRatio(upperMenu, 1);
			workingSpace.setExpandRatio(viewDisplay, 9);
			workingSpace.setStyleName(ThemeConstants.BORDERED_THICK);
			base.addComponent(sideMenu);
			base.addComponent(workingSpace);
			base.setExpandRatio(sideMenu, 15);
			base.setExpandRatio(workingSpace, 85);
			base.setMargin(false);
			setContent(base);
		}

	@Override
	public void showView(View view) {
		if(viewDisplay!=null){
			viewDisplay.setContent((Component) view);
		}
		
	}

	public static List<User> getOnlineUsers() {
		return onlineUsers;
	}
	
	private boolean viewChangeListener(ViewChangeEvent event){
		if(event.getViewName().equals(MessagesView.NAME)){
			this.messageView=(MessagesView) event.getNewView();
			changeSideMenu(user);
		}else if(event.getViewName().equals(UserListView.NAME)){
			changeSideMenu(user);
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
	
	public VerticalLayout createSideMenu(User sideUser){	
		this.sideUser=sideUser;
		VerticalLayout sideMenu=new VerticalLayout();
		sideMenu.addStyleName(ThemeConstants.SIDE_MENU);		
		VerticalLayout header=new VerticalLayout();
		VerticalLayout menu=new VerticalLayout();
		header.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		menu.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		menu.setWidth("100%");
		header.setSpacing(false);
		header.setMargin(false);
		header.addStyleName(ThemeConstants.SIDE_HEADER);
		sideMenu.addComponent(header);
		sideMenu.addComponent(menu);
		sideMenu.setExpandRatio(header, 3);
		sideMenu.setExpandRatio(menu, 7);
		sideMenu.setSpacing(true);
		sideMenu.setMargin(false);
		sideMenu.setSizeFull();
		Image image=new Image(null, new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+sideUser.getImageName()))); 
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);
		image.setWidth("60%");
		Label name=new Label();
		name.setValue(sideUser.getUsername());
		name.addStyleName(ValoTheme.LABEL_H2);
		name.addStyleName(ThemeConstants.BLUE_TEXT);
		header.addComponent(name);
		header.addComponent(image);	
		if(sideUser.getId()==user.getId()){
			MenuBar menuBar=new MenuBar();
			header.addComponent(menuBar);
			MenuItem options=menuBar.addItem("Options", null);
			menuBar.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
			menuBar.addStyleName(ThemeConstants.BLUE_TEXT);
			MenuItem changePicture=options.addItem("Change picture", new Command() {
				
				@Override
				public void menuSelected(MenuItem selectedItem) {
					getNavigator().navigateTo(PictureUploadView.NAME);
					
				}
			});
		}
		
		if(sideUser.getId()!=user.getId()){
			if(friendshipService.findOne(user.getId(), sideUser.getId())==null){
				if(friendRequestService.findOne(user.getId(), sideUser.getId())==null){
					if(friendRequestService.findOne(sideUser.getId(), user.getId())==null){
						Button friendRequestButton=new Button("Friend request");
						friendRequestButton.addClickListener(this::friendRequestClickListener);
						friendRequestButton.addStyleName(ValoTheme.BUTTON_SMALL);
						friendRequestButton.addStyleName(ThemeConstants.BLUE_TEXT);
						menu.addComponent(friendRequestButton);
						menu.setComponentAlignment(friendRequestButton, Alignment.MIDDLE_LEFT);
					}else{
						Label acceptLabel=new Label("Accept friend request?");
						Button acceptRequestButton=new Button("Accept");
						acceptRequestButton.addClickListener(this::acceptRequestClickListener);
						acceptRequestButton.addStyleName(ValoTheme.BUTTON_SMALL);
						acceptRequestButton.addStyleName(ThemeConstants.BLUE_TEXT);
						Button rejectRequestButton=new Button("Reject");
						rejectRequestButton.addClickListener(this::rejectRequestClickListener);
						rejectRequestButton.addStyleName(ValoTheme.BUTTON_SMALL);
						rejectRequestButton.addStyleName(ThemeConstants.BLUE_TEXT);
						menu.addComponent(acceptLabel);
						menu.addComponent(acceptRequestButton);
						menu.addComponent(rejectRequestButton);
					}
				}else{
					menu.addComponent(new Label("Request sent."));
				}
			}else{
				name.setContentMode(ContentMode.HTML);
				name.setValue("("+VaadinIcons.CHECK.getHtml()+") "+sideUser.getUsername());
				Button removeFriendButton=new Button("Remove friend");
				removeFriendButton.addClickListener(this::removeFriendClickListener);
				removeFriendButton.addStyleName(ValoTheme.BUTTON_SMALL);
				removeFriendButton.addStyleName(ThemeConstants.BLUE_TEXT);
				menu.addComponent(removeFriendButton);
				
			}
		}else{
			int count=friendRequestService.findAllByRequestedId(user.getId()).size();
			Button friendRequestsButton=new Button("Requests ("+count+")");
			friendRequestsButton.addClickListener(this::friendRequestsClickListener);
			friendRequestsButton.addStyleName(ValoTheme.BUTTON_SMALL);
			friendRequestsButton.addStyleName(ThemeConstants.BLUE_TEXT);
			menu.addComponent(friendRequestsButton);
			
		}
		
		Button friendListButton=new Button("Friendlist", this::friendListClickListener);
		menu.addComponent(friendListButton);
		friendListButton.addStyleName(ValoTheme.BUTTON_SMALL);
		friendListButton.addStyleName(ThemeConstants.BLUE_TEXT);
		
		return sideMenu;
	}
	
	public CssLayout createNaviBar(){
		CssLayout naviBar=new CssLayout();
		naviBar.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		Button mainButton=new Button("Main");
		mainButton.addClickListener(this::mainClickListener);
		mainButton.setStyleName(ValoTheme.BUTTON_SMALL);
		mainButton.addStyleName(ThemeConstants.BLUE_TEXT);
		naviBar.addComponent(mainButton);
		naviBar.addComponent(createNavigationButton("Messages", MessagesView.NAME));
		Button logoutButton=new Button("Logout");
		logoutButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				getPage().setLocation("/logout");
				MainUI.getOnlineUsers().remove((User)session.getAttribute("User"));
//				session.invalidate();
				session=null;
			}
		});		
		nameSearchTf=new TextField();
		nameSearchTf.setStyleName(ValoTheme.BUTTON_SMALL);
		nameSearchTf.setValue("Search for a user...");
		nameSearchTf.addFocusListener(this::nameSearchFocusListener);
		naviBar.addComponent(nameSearchTf);
		Button namesearchButton=new Button("Search", this::nameSearchClickListener);
		namesearchButton.setClickShortcut(KeyCode.ENTER);
		namesearchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		namesearchButton.addStyleName(ThemeConstants.BLUE_TEXT);
		naviBar.addComponent(namesearchButton);
		
		logoutButton.setStyleName(ValoTheme.BUTTON_SMALL);
		logoutButton.addStyleName(ThemeConstants.BLUE_TEXT);
		naviBar.addComponent(logoutButton);
		return naviBar;
	}
	
	private Button createNavigationButton(String caption, final String viewName){
		Button button=new Button(caption);
		button.addStyleName(ValoTheme.BUTTON_SMALL);
		button.addStyleName(ThemeConstants.BLUE_TEXT);
		button.addClickListener(event -> getNavigator().navigateTo(viewName));
		return button;
	}
	
	public void changeSideMenu(User user){
		VerticalLayout userSideMenu=createSideMenu(user);
		base.replaceComponent(oldSideMenu, userSideMenu);
		oldSideMenu=userSideMenu;
	}
	
	private void mainClickListener(Button.ClickEvent event){
		changeSideMenu(user);
		getNavigator().navigateTo(MainView.NAME);
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
		refreshSideMenu();
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
		refreshSideMenu();
	}
	
	private void rejectRequestClickListener(Button.ClickEvent event){
		friendRequestService.deleteFriendRequest(sideUser.getId(), user.getId());
		refreshSideMenu();
	}
	
	private void removeFriendClickListener(Button.ClickEvent event){
		friendshipService.deleteFriendship(user.getId(), sideUser.getId());
		friendshipService.deleteFriendship(sideUser.getId(), user.getId());
		MessageBox.createInfo()
		.withOkButton()
		.withCaption("User removed from friends")
		.withMessage(sideUser.getUsername()+" has been removed from your friends.")
		.open();
		refreshSideMenu();
	}
	
	private void friendListClickListener(Button.ClickEvent event){
		getNavigator().navigateTo(FriendListView.NAME);
		((FriendListView)getNavigator().getCurrentView()).fill(sideUser);
	}
	
	public void refreshSideMenu(){
		changeSideMenu(sideUser);
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
	
	public void changeToUser(User user){
		getNavigator().navigateTo(MainView.NAME+"/"+user.getId());
//		((MainView)getNavigator().getCurrentView()).changeToUser(user);
		changeSideMenu(user);	
	}
	
}
