package hu.mik.views;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.web.context.annotation.SessionScope;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
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
	
	@PostConstruct
	public void init(){		
		addComponent(new Label("Messages"));
		HorizontalLayout base=new HorizontalLayout();
		HorizontalLayout userDiv;
		VerticalLayout userList=new VerticalLayout();
		VerticalLayout messages=new VerticalLayout();
		base.setSizeFull();
		addComponent(base);
		base.addComponent(userList);
		base.addComponent(messages);	
		
		base.setExpandRatio(userList, 2);
		base.setExpandRatio(messages, 8);
		
		
		for(int i=0; i<users.size();i++){			
			userDiv=new HorizontalLayout();
			userDiv.setStyleName(ThemeConstants.BORDERED);
			userDiv.setSizeFull();
			userDiv.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
			Image image=new Image("", new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+users.get(i).getImage())));
//			image.setWidth("80%");
//			image.setHeight("80%");
			image.setSizeFull();
			userDiv.addComponent(image);
			Label label=new Label(users.get(i).getUsername());
			label.setSizeUndefined();
			userDiv.addComponent(label);
			userList.addComponent(userDiv);
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
}
