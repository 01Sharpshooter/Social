package hu.mik.views;


import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.ServletForwardingController;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.Message;
import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
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
	
	private List<User> users=MainUI.getOnlineUsers();
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
	private VerticalLayout chat;
	private List<Message> messagesList;
	private int prevUserDivId=-1;
	private VerticalLayout userList;
	private VerticalLayout empty;
	
	@PostConstruct
	public void init(){				
		sender=(User) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("User");
		senderId=sender.getId();
		
		this.addStyleName(ThemeConstants.BORDERED);
		this.setSizeFull();
		
		HorizontalLayout base=new HorizontalLayout();
		base.setMargin(false);
		base.setSpacing(false);
//		base.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		base.setSizeFull();
		
		userList=new VerticalLayout();	
//		userList.addStyleName(ThemeConstants.BORDERED);
		
		userListPanel.setSizeFull();
		userListPanel.setContent(userList);
		userListPanel.addStyleName(ThemeConstants.BORDERED);
		
		textWriter=this.createTextWriter();
		
		messagesLayout=new VerticalLayout();
		messagesLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
		messagesPanel.setContent(messagesLayout);
		messagesPanel.addStyleName(ThemeConstants.BORDERED);
		messagesPanel.setSizeFull();
		
		chat=this.createChat();
		chat.setSizeFull();
		
		addComponent(base);
		base.addComponent(userListPanel);
		base.addComponent(chat);			
		base.setExpandRatio(userListPanel, 3);
		base.setExpandRatio(chat, 7);
		
		if(users.size()>1){
			for(User user: users){	
				if(user!=this.sender){
					HorizontalLayout userDiv=createUserDiv(user);
					userList.addComponent(userDiv);	
					userDiv=createUserDiv(user);
				}
			}
		}else{
			userList.addComponent(new Label("There's no other online user. Check back later! :)"));
		}
	}

	private HorizontalLayout createUserDiv(User user) {
		HorizontalLayout userDiv=new HorizontalLayout();
		userDiv.setWidth("100%");
		userDiv.setHeight(userListPanel.getHeight()/6, userListPanel.getHeightUnits());
		userDiv.addStyleName(ThemeConstants.BORDERED);
		userDiv.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
//		userDiv.setMargin(false);
		Image image=new Image(null, new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImageName())));
		image.setHeight("80%");
		image.setWidth("80");
		userDiv.addComponent(image);
		Button button=new Button(user.getUsername(),this::userBtnClickListener);
		button.setSizeFull();
		button.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		button.setId(user.getId().toString());
		userDiv.addComponent(button);
		userDiv.setExpandRatio(image, 1);
		userDiv.setExpandRatio(button, 2);
		return userDiv;
		
	}

	private VerticalLayout createChat() {
		VerticalLayout chat=new VerticalLayout();
		chat.addComponent(messagesPanel);
		chat.addComponent(textWriter);
		chat.setExpandRatio(messagesPanel, 9);
		chat.setExpandRatio(textWriter, 1);		
		return chat;
	}

	private HorizontalLayout createTextWriter() {
		HorizontalLayout textWriter=new HorizontalLayout();
		textField=new TextField();
		textField.setSizeFull();
		sendButton=new Button("Send", this::sendButtonClicked);
		sendButton.addStyleName(ThemeConstants.BLUE_TEXT);
		sendButton.setClickShortcut(KeyCode.ENTER);
		sendButton.setSizeUndefined();
		textWriter.addComponent(textField);
		textWriter.addComponent(sendButton);
		textWriter.setExpandRatio(textField, 9);
		textWriter.setExpandRatio(sendButton, 1);	
		textWriter.setSizeFull();
		textWriter.setEnabled(false);
		return textWriter;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	private void userBtnClickListener(Button.ClickEvent event){
		messagesLayout.setCaption(event.getButton().getCaption());
		event.getButton().getParent().addStyleName(ThemeConstants.BORDERED_GREEN);
		textWriter.setEnabled(true);
		messagesLayout.removeAllComponents();
		receiverId=Integer.parseInt(event.getButton().getId());
		receiver=userService.findUserById(receiverId);
		messagesList=messageService.findAllByUserIDs(20, senderId, receiverId);
		fillMessages();
		messagesPanel.setScrollTop(scroll);
		MessageBroadcastService.register((MainUI)this.getUI(), sender.getUsername());
		if(prevUserDivId!=-1 && prevUserDivId!=userList.getComponentIndex(event.getButton().getParent())){
			userList.getComponent(prevUserDivId).removeStyleName(ThemeConstants.BORDERED_GREEN);			
		}
		prevUserDivId=userList.getComponentIndex(event.getButton().getParent());
		textField.focus();
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
						Label newMessage=new Label(message.getMessage());
						newMessage.setHeight(messagesPanel.getHeight()/6, messagesPanel.getHeightUnits());
						newMessage.setWidth(messagesPanel.getWidth()/2, messagesPanel.getWidthUnits());
						newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE);			
						messagesLayout.addComponent(newMessage);
//						panel.setScrollTop(scroll);
					}
					else{
						scroll+=scrollGrowth;
						Label newMessage=new Label(message.getMessage());
						newMessage.setHeight(messagesPanel.getHeight()/6, messagesPanel.getHeightUnits());
						newMessage.setWidth(messagesPanel.getWidth()/2, messagesPanel.getWidthUnits());
						newMessage.addStyleName(ThemeConstants.BORDERED_THICK);			
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
			Label newMessage=new Label(message.getMessage());
			newMessage.setHeight(messagesPanel.getHeight()/6, messagesPanel.getHeightUnits());
			newMessage.setWidth(messagesPanel.getWidth()/2, messagesPanel.getWidthUnits());
			newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE);			
			messagesLayout.addComponent(newMessage);
			messagesPanel.setScrollTop(scroll);
		}
		
	}

	public void receiveMessage(String message2, int senderId) {
		if(senderId==receiverId){
			System.out.println("Incomiiiing");
			Label newMessage=new Label(message2);
			newMessage.setHeight(messagesPanel.getHeight()/6, messagesPanel.getHeightUnits());
			newMessage.setWidth(messagesPanel.getWidth()/2, messagesPanel.getWidthUnits());
			newMessage.addStyleName(ThemeConstants.BORDERED_THICK);			
			messagesLayout.addComponent(newMessage);
			messagesLayout.setComponentAlignment(newMessage, Alignment.MIDDLE_LEFT);
			messagesPanel.setScrollTop(scroll);
		}
		
	}
	
}
