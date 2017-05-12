package hu.mik.views;


import static org.hamcrest.CoreMatchers.theInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;
import org.springframework.web.servlet.mvc.ServletForwardingController;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
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

import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.ui.MainUI;

@ViewScope
@SpringView(name=MessagesView.NAME)
public class MessagesView extends VerticalLayout implements View {
	public static final String NAME="messages";
	private List<User> users=MainUI.getOnlineUsers();
	private Panel panel=new Panel();
	private VerticalLayout messages;
	private String message;
	private TextField textField;
	private Button sendButton;
	private int scroll=100;
	private int scrollGrowth=50;
	
	@PostConstruct
	public void init(){		
		this.addStyleName(ThemeConstants.BORDERED);
		this.setSizeFull();
		HorizontalLayout base=new HorizontalLayout();
		HorizontalLayout userDiv;
		VerticalLayout userList=new VerticalLayout();
		VerticalLayout chat=new VerticalLayout();
		messages=new VerticalLayout();
		HorizontalLayout textWriter=new HorizontalLayout();
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
		panel.addStyleName(ThemeConstants.BORDERED);
		panel.setSizeFull();
//		messages.setHeight(panel.getHeight(), panel.getHeightUnits());
//		messages.setWidth(panel.getWidth(), panel.getWidthUnits());
		base.addComponent(userList);
		base.addComponent(chat);	
		
		base.setExpandRatio(userList, 2);
		base.setExpandRatio(chat, 8);
		
		
		for(int i=0; i<users.size();i++){			
			userDiv=new HorizontalLayout();
			userDiv.addStyleName(ThemeConstants.BORDERED);
			userDiv.setSizeFull();
			userDiv.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
			userDiv.setMargin(false);
			userDiv.addLayoutClickListener(this::layoutClickListener);
			Image image=new Image("", new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+users.get(i).getImage())));
			image.setWidth(60, Unit.PIXELS);
			image.setHeight(60, Unit.PIXELS);
			image.setSizeFull();
			userDiv.addComponent(image);
			Label label=new Label(users.get(i).getUsername());
			label.setWidthUndefined();
			userDiv.addComponent(label);
			userList.addComponent(userDiv);
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	private void layoutClickListener(LayoutClickEvent event){
		
	}
	
	private void sendButtonClicked(Button.ClickEvent event){
		message=textField.getValue();
		textField.clear();		
		if(message.length()!=0){
			scroll+=scrollGrowth;
			Label newMessage=new Label(message);
			newMessage.setHeight(panel.getHeight()/6, panel.getHeightUnits());
			newMessage.setWidth(panel.getWidth(), panel.getWidthUnits());
			newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE);			
			messages.addComponent(newMessage);
			panel.setScrollTop(scroll);
		}
	}
	
}
