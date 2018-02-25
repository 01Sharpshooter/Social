package hu.mik.views;


import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.LdapUser;
import hu.mik.beans.Message;
import hu.mik.beans.User;
import hu.mik.components.UserListLayout;
import hu.mik.constants.SystemConstants;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.FriendshipService;
import hu.mik.services.LdapService;
import hu.mik.services.MessageBroadcastService;
import hu.mik.services.MessageService;
import hu.mik.services.UserService;
import hu.mik.ui.MainUI;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name=MessagesView.NAME)
public class MessagesView extends VerticalLayout implements View {
	public static final String NAME="messages";
	
	
	@Autowired
	MessageService messageService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	FriendshipService friendshipService;
	
	@Autowired
	UserListLayout userListLayout;
	@Autowired
	LdapService ldapService;
	
	private List<User> onlineUsers=MainUI.getOnlineUsers();
	private List<User> friendList=new ArrayList<>();
	private Panel messagesPanel=new Panel();
	private Panel userListPanel=new Panel();
	private VerticalLayout messagesLayout;
	private Message message;
	private TextField textField;
	private Button sendButton;
	private int scroll=100;
	private int scrollGrowth=50;
	private int senderId;
	private int receiverId;
	private User sender;
	private User receiver;
	private HorizontalLayout textWriter;
	private CssLayout chat;
	private List<Message> messagesList;
	private CssLayout userList;


	private int messageNumberAtOnce=20;
	

	@Override
	public void enter(ViewChangeEvent event) {
		WrappedSession session=VaadinService.getCurrentRequest().getWrappedSession();
		String username=(String) session.getAttribute(SystemConstants.SESSION_ATTRIBUTE_LDAP_USER);
		sender=userService.findUserByUsername(username);
		
		
		senderId=sender.getId();		
		friendshipService.findAllByUserId(senderId).forEach(friendShip -> friendList.add(userService.findUserById(friendShip.getFriendId())));
		
		this.addStyleName(ThemeConstants.BORDERED);
		this.setSizeFull();
		this.setMargin(false);
		this.setSpacing(false);
		
		CssLayout base=new CssLayout();
		base.setId("messageBase");
		base.setSizeFull();
		
		TextField tfSearch=new TextField("Search:");
		tfSearch.addValueChangeListener(this::searchValueChangeListener);
		base.addComponent(tfSearch);
		
		List<String> searchList=new ArrayList<>();
		ldapService.findAllUsers().forEach(user->searchList.add(user.getFullName()));
		ComboBox<String> cb=new ComboBox<>("test", searchList);
		cb.setWidth("100%");
//		base.addComponent(cb);
		
		userList=new CssLayout();	
		userList.addStyleName(ThemeConstants.HOVER_GREEN_LAYOUTS);
		userList.setId("latestMessagesLayout");
		
		textWriter=this.createTextWriter();
		
		messagesLayout=new VerticalLayout();
		messagesLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
		messagesPanel.setContent(messagesLayout);
		messagesPanel.addStyleName(ThemeConstants.BORDERED);
		messagesPanel.setSizeFull();
		
		chat=this.createChat();
		
		addComponent(base);
		base.addComponent(userList);
		base.addComponent(chat);			
		fillUserList();
		
		String parameters[]=event.getParameters().split("/");
		if(parameters.length>0) {
			receiver=userService.findUserByUsername(parameters[0]);
			if(receiver!=null) {
				receiverId=receiver.getId();
				boolean exists=false;
				for (Component userDiv : userList) {
					if(String.valueOf(receiverId).equals(userDiv.getId())) {
						exists=true;
						userList.removeComponent(userDiv);
						userList.addComponent(userDiv, 0);
						exists=true;
						break;
					}	
				}
				if(exists) {
					userListSelectionChange(userList.getComponent(0));
				}else {
					Message lastMessage=messageService.findLastByUserIDs(receiverId, senderId);
					String messageString;
					if(lastMessage!=null) {
						messageString=lastMessage.getMessage();
					}else {
						messageString="";
					}
					CssLayout newDiv=createUserDiv(receiver, messageString);
					userList.addComponent(newDiv, 0);
					userListSelectionChange(newDiv);
				}
				fillChat(userList.getComponent(0));
			}
		}
		
	}
	
	private List<CssLayout> fillUserList() {
		List<Message> latestMessages=messageService.findLastestMessagesOfUser(100, senderId);	
		List<Integer> alreadyUsedIds=new ArrayList<>();
		List<CssLayout> userDivs=new ArrayList<>();
		CssLayout userDiv;
		User user;
		
		
		for (Message message : latestMessages) {
			boolean amItheSender=message.getSenderId()==senderId;
			if(amItheSender) {
				if(!alreadyUsedIds.contains(message.getReceiverId())) {
					alreadyUsedIds.add(message.getReceiverId());
					user=userService.findUserById(message.getReceiverId());
					userDiv=createUserDiv(user, message.getMessage());
					userList.addComponent(userDiv);
					userDivs.add(userDiv);
				}
			}else {
				if(!alreadyUsedIds.contains(message.getSenderId())) {
					alreadyUsedIds.add(message.getSenderId());
					user=userService.findUserById(message.getSenderId());
					userDiv=createUserDiv(user, message.getMessage());
					userList.addComponent(userDiv);
					userDivs.add(userDiv);
				}
			}
		}
		return userDivs;
	}

	@PostConstruct
	public void init(){	
		
		
	}

	private CssLayout createUserDiv(User user, String lastMessage) {
		CssLayout userDiv=new CssLayout();
		LdapUser ldapUser=ldapService.findUserByUsername(user.getUsername());
		Image image=new Image(null, new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImageName())));
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);
		userDiv.addComponent(image);
		userDiv.setId(user.getId().toString());
		userDiv.addLayoutClickListener(this::userDivClickListener);
		Label lblName=new Label(ldapUser.getFullName()+"</br><span id=\"message\">"+lastMessage+"</span>", ContentMode.HTML);	
		userDiv.addComponent(lblName);

		return userDiv;
		
	}
	
	private CssLayout createUserDiv(LdapUser user, String lastMessage) {
		CssLayout userDiv=new CssLayout();
		User dbUser=userService.findUserByUsername(user.getUsername());
		Image image=new Image(null, new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+dbUser.getImageName())));
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);
		userDiv.addComponent(image);
		userDiv.setId(dbUser.getId().toString());
		userDiv.addLayoutClickListener(this::userDivClickListener);
		Label lblName=new Label(user.getFullName()+"</br><span id=\"message\">"+lastMessage+"</span>", ContentMode.HTML);	
		userDiv.addComponent(lblName);

		return userDiv;
		
	}
	
	private CssLayout changeUserDivMessage(CssLayout userDiv, String lastMessage){
		String name;
		String[] test=((Label)userDiv.getComponent(1)).getValue().split("<");
		name=test[0];	
		userDiv.removeComponent(userDiv.getComponent(1));
		Label lblName=new Label(name+"</br><span id=\"message\">"+lastMessage+"</span>", ContentMode.HTML);	
		userDiv.addComponent(lblName);
		return userDiv;
	}

	private CssLayout createChat() {
		CssLayout chat=new CssLayout();
//		chat.setCaption(receiver.getUsername());
//		chat.setWidth("75%");
//		chat.setHeight("100%");
//		chat.setMargin(false);
		chat.addComponent(messagesPanel);
		chat.addComponent(textWriter);
//		chat.setExpandRatio(messagesPanel, 9);
//		chat.setExpandRatio(textWriter, 1);		
		chat.setId("chatLayout");
		return chat;
	}

	private HorizontalLayout createTextWriter() {
		HorizontalLayout textWriter=new HorizontalLayout();
		textField=new TextField();
		textField.setWidth("100%");
		sendButton=new Button("Send", this::sendButtonClicked);
		sendButton.addStyleName(ThemeConstants.BLUE_TEXT);
		sendButton.setClickShortcut(KeyCode.ENTER);
		sendButton.setSizeUndefined();
		textWriter.addComponent(textField);
		textWriter.addComponent(sendButton);
		textWriter.setExpandRatio(textField, 7);
		textWriter.setExpandRatio(sendButton, 3);	
		textWriter.setSizeFull();
		textWriter.setEnabled(false);
		return textWriter;
	}	
	
	private void userDivClickListener(LayoutClickEvent event) {
//		messagesLayout.setCaption(event.getButton().getCaption());
		fillChat(event.getComponent());
	}
	
	private void fillChat(Component userDiv) {
		textWriter.setEnabled(true);
		messagesLayout.removeAllComponents();
		receiverId=Integer.parseInt(userDiv.getId());
		receiver=userService.findUserById(receiverId);
		messagesList=messageService.findAllByUserIDs(messageNumberAtOnce, senderId, receiverId);
		fillMessages();
		messagesPanel.setScrollTop(scroll);
		MessageBroadcastService.register((MainUI)this.getUI(), sender.getUsername());
		userListSelectionChange(userDiv);		
	}

	public void fillMessages() {
		messagesList=messageService.findAllByUserIDs(20, senderId, receiverId);
		if(messagesList!=null){
			if(!messagesList.isEmpty()){
				messagesLayout.removeAllComponents();
				for (int i=messagesList.size()-1;i>=0;i--) {
					message=messagesList.get(i);				
					if(message.getSenderId()==this.senderId){
						scroll+=scrollGrowth;
						Label newMessage=new Label("<span id=\"messageSpan\">"+message.getMessage()+"</span>", ContentMode.HTML);
						newMessage.setHeight(messagesPanel.getHeight()/6, messagesPanel.getHeightUnits());
						newMessage.setWidth(messagesPanel.getWidth()/2, messagesPanel.getWidthUnits());
						newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_SENT);			
						messagesLayout.addComponent(newMessage);
//						panel.setScrollTop(scroll);
					}
					else{
						scroll+=scrollGrowth;
						Label newMessage=new Label("<span id=\"messageSpan\">"+message.getMessage()+"</span>", ContentMode.HTML);
						newMessage.setHeight(messagesPanel.getHeight()/6, messagesPanel.getHeightUnits());
						newMessage.setWidth(messagesPanel.getWidth()/2, messagesPanel.getWidthUnits());
						newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_RECEIVED);			
						messagesLayout.addComponent(newMessage);
						messagesLayout.setComponentAlignment(newMessage, Alignment.MIDDLE_LEFT);
//						panel.setScrollTop(scroll);
					}
				}
			}
		}		
	}

	private void userClickListener(LayoutClickEvent event){
//		receiverId=Integer.parseInt(event.getClickedComponent().getId());
	}
	
	private void sendButtonClicked(Button.ClickEvent event){
		sendMessage();
		
	}

	private void sendMessage() {
		message=new Message();
		message.setMessage(textField.getValue());
		
		if(message.getMessage().length()!=0){
			message.setSenderId(senderId);
			message.setReceiverId(receiverId);
			java.util.Date date=new java.util.Date();
			message.setTime(new Timestamp(date.getTime()));
			textField.clear();		
			messageService.saveMessage(message);
			MessageBroadcastService.sendMessage(message.getMessage(), senderId, receiver.getUsername());
//			messagesList.add(message);
			scroll+=scrollGrowth;
			Label newMessage=new Label("<span id=\"messageSpan\">"+message.getMessage()+"</span>", ContentMode.HTML);
			newMessage.setHeight(messagesPanel.getHeight()/6, messagesPanel.getHeightUnits());
			newMessage.setWidth(messagesPanel.getWidth()/2, messagesPanel.getWidthUnits());
			newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_SENT);			
			messagesLayout.addComponent(newMessage);
			messagesPanel.setScrollTop(scroll);
			
			for (Component userDiv : userList) {
				if(userDiv.getId().equals(String.valueOf(receiverId))) {
					userList.removeComponent(userDiv);
					changeUserDivMessage((CssLayout)userDiv, message.getMessage());
					userList.addComponent(userDiv, 0);
					userListSelectionChange(userDiv);
					break;
				}
			}
		}
		
	}

	public void receiveMessage(String message2, int senderId) {
		if(senderId==receiverId){
			Label newMessage=new Label("<span id=\"messageSpan\">"+message2+"</span>", ContentMode.HTML);
			newMessage.setHeight(messagesPanel.getHeight()/6, messagesPanel.getHeightUnits());
			newMessage.setWidth(messagesPanel.getWidth()/2, messagesPanel.getWidthUnits());
			newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_RECEIVED);			
			messagesLayout.addComponent(newMessage);
			messagesLayout.setComponentAlignment(newMessage, Alignment.MIDDLE_LEFT);
			messagesPanel.setScrollTop(scroll);	
			
		}
		boolean exists=false;
		for (Component userDiv : userList) {
			if(userDiv.getId().equals(String.valueOf(senderId))) {
				userList.removeComponent(userDiv);
				changeUserDivMessage((CssLayout)userDiv, message2);
				userList.addComponent(userDiv, 0);
				exists=true;
				break;
			}
		}
		if(!exists) {
			User user=userService.findUserById(senderId);
			CssLayout newDiv=createUserDiv(user, message2);
			userList.addComponent(newDiv, 0);
		}
	}
	
	private void userListSelectionChange(Component userDiv) {
		userList.forEach(userDivr->userDivr.removeStyleName(ThemeConstants.BORDERED_GREEN));
		userDiv.addStyleName(ThemeConstants.BORDERED_GREEN);
//		changeUserDivMessage(
//				(CssLayout)userDiv, 
//				messageService.findLastByUserIDs(senderId, Integer.parseInt(userDiv.getId())).getMessage());
	}
	
	private void searchValueChangeListener(ValueChangeEvent<String> event) {
		if(event.getValue().equals("")) {
			userList.removeAllComponents();
			userList.setSizeUndefined();
//			userList.removeStyleName(ThemeConstants.MESSAGES_USER_LIST_SHOW_LABEL);
			fillUserList();
		}else {
			userList.removeAllComponents();
			ldapService.findByFullNameContaining(event.getValue())
			.forEach(user->userList.addComponent(
					createUserDiv(user, "")));
//			userList.setWidth("100%");
//			userList.addStyleName(ThemeConstants.MESSAGES_USER_LIST_SHOW_LABEL);
		}
	}
	
}
