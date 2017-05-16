package hu.mik.views;


import static org.hamcrest.CoreMatchers.theInstance;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;
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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.Message;
import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.MessageService;
import hu.mik.ui.MainUI;

@ViewScope
@SpringView(name=MessagesView.NAME)
public class MessagesView extends VerticalLayout implements View {
	public static final String NAME="messages";
	
	@Autowired
	MessageService messageService;
	private List<User> users=MainUI.getOnlineUsers();
	private Panel panel=new Panel();
	private VerticalLayout messages;
	private Message message=new Message();
	private TextField textField;
	private Button sendButton;
	private int scroll=100;
	private int scrollGrowth=50;
	private List<String> names=new CopyOnWriteArrayList<>();
	private int senderId;
	private int receiverId;
	private User sender;
	
	@PostConstruct
	public void init(){		
		sender=(User) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("User");
		senderId=sender.getId();
		this.addStyleName(ThemeConstants.BORDERED);
		this.setSizeFull();
		HorizontalLayout base=new HorizontalLayout();
		HorizontalLayout userDiv;
		VerticalLayout userList=new VerticalLayout();
		VerticalLayout chat=new VerticalLayout();
		messages=new VerticalLayout();
		HorizontalLayout textWriter=new HorizontalLayout();
		base.setMargin(false);
		base.setSpacing(false);
		chat.setSizeFull();
		textField=new TextField();
		textField.setSizeFull();
		sendButton=new Button("Send", this::sendButtonClicked);
		sendButton.addStyleName(ThemeConstants.BLUE_TEXT);
		sendButton.setClickShortcut(KeyCode.ENTER);
		textWriter.addComponent(textField);
		textWriter.addComponent(sendButton);
		textWriter.setExpandRatio(textField, 9);
		textWriter.setExpandRatio(sendButton, 1);	
		panel.setContent(messages);
		messages.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
		chat.addComponent(panel);
		chat.addComponent(textWriter);
		chat.setExpandRatio(panel, 9);
		chat.setExpandRatio(textWriter, 1);
		base.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		base.setSizeFull();
		addComponent(base);
		userList.setSpacing(false);
		userList.setSizeFull();
		panel.addStyleName(ThemeConstants.BORDERED);
		panel.setSizeFull();
//		messages.setHeight(panel.getHeight(), panel.getHeightUnits());
//		messages.setWidth(panel.getWidth(), panel.getWidthUnits());
		base.addComponent(userList);
		base.addComponent(chat);	
		
		base.setExpandRatio(userList, 3);
		base.setExpandRatio(chat, 7);
		
		
		for(int i=0; i<users.size();i++){			
			userDiv=new HorizontalLayout();
			userDiv.setWidth("100%");
			userDiv.setHeight("20%");
			userDiv.addStyleName(ThemeConstants.BORDERED);
			userDiv.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
			userDiv.setMargin(false);
			userDiv.setId(users.get(i).getId().toString());
//			userDiv.addLayoutClickListener(this::userClickListener);
			Image image=new Image("", new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+users.get(i).getImageName())));
			image.setWidth(60, Unit.PIXELS);
			image.setHeight(60, Unit.PIXELS);
			image.setSizeFull();
			userDiv.addComponent(image);
//			Label label=new Label(users.get(i).getUsername());
//			label.setWidthUndefined();
			Button button=new Button(users.get(i).getUsername(),this::userBtnClickListener);
			button.setWidthUndefined();
			button.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			userDiv.addComponent(button);
//			userDiv.addComponent(label);
//			names.add(users.get(i).getUsername());
			userList.addComponent(userDiv);
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	private void userBtnClickListener(Button.ClickEvent event){
		messages.removeAllComponents();
		System.out.println(event.getButton().getParent().getId());
		receiverId=Integer.parseInt(event.getButton().getParent().getId());
		List<Message> messagesList=messageService.findAllByUserIDs(20, senderId, receiverId);
		if(!messagesList.isEmpty()){
			for (Message message : messagesList) {
				if(message.getSenderId()==this.senderId){
					scroll+=scrollGrowth;
					Label newMessage=new Label(message.getMessage());
					newMessage.setHeight(panel.getHeight()/6, panel.getHeightUnits());
					newMessage.setWidth(panel.getWidth()/2, panel.getWidthUnits());
					newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE);			
					messages.addComponent(newMessage);
					panel.setScrollTop(scroll);
				}
				else{
					scroll+=scrollGrowth;
					Label newMessage=new Label(message.getMessage());
					newMessage.setHeight(panel.getHeight()/6, panel.getHeightUnits());
					newMessage.setWidth(panel.getWidth()/2, panel.getWidthUnits());
					newMessage.addStyleName(ThemeConstants.BORDERED_THICK);			
					messages.addComponent(newMessage);
					messages.setComponentAlignment(newMessage, Alignment.MIDDLE_LEFT);
					panel.setScrollTop(scroll);
				}
			}
		}
	}
	
	private void userClickListener(LayoutClickEvent event){
		System.out.println(event.getClickedComponent().getParent().getId());
//		receiverId=Integer.parseInt(event.getClickedComponent().getId());
	}
	
	private void sendButtonClicked(Button.ClickEvent event){
		message.setMessage(textField.getValue());
//		System.out.println(sender.getId());
		message.setSenderId(senderId);
		message.setReceiverId(receiverId);
		java.util.Date date=new java.util.Date();
		message.setTime(new Timestamp(date.getTime()));
		textField.clear();		
		if(message.getMessage().length()!=0){
			messageService.saveMessage(message);
			scroll+=scrollGrowth;
			Label newMessage=new Label(message.getMessage());
			newMessage.setHeight(panel.getHeight()/6, panel.getHeightUnits());
			newMessage.setWidth(panel.getWidth()/2, panel.getWidthUnits());
			newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE);			
			messages.addComponent(newMessage);
			panel.setScrollTop(scroll);
		}
	}
	
}
