package hu.mik.views;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import hu.mik.beans.News;
import hu.mik.beans.User;
import hu.mik.components.NewsLayout;
import hu.mik.constants.ThemeConstants;
import hu.mik.services.NewsService;

@ViewScope
@SpringView(name=MainView.NAME)
public class MainView extends VerticalLayout implements View{
	public static final String NAME="";
	private VerticalLayout feed;
	private VerticalLayout userDiv;
	private User user;
	private TextField textField;
	private Button sendButton;
	private String message;
	private Panel panel=new Panel();
	@Autowired
	private NewsService newsService;
	private List<News> newsList;
	
	@PostConstruct
	public void init(){
		user=(User) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("User");
		newsList=newsService.lastGivenNewsAll(20);
		feed=new VerticalLayout();	
		HorizontalLayout textWriter=new HorizontalLayout();
		textWriter.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		textWriter.setWidth("100%");
		textWriter.setCaption("Share your thoughts...");
		this.addComponent(textWriter);
		this.addComponent(panel);
		panel.setSizeFull();
		panel.setContent(feed);
		
		textField=new TextField();
		textField.setSizeFull();
		sendButton=new Button("Send", this::sendButtonClicked);
		sendButton.addStyleName(ThemeConstants.BLUE_TEXT);
		sendButton.setClickShortcut(KeyCode.ENTER);
		textWriter.addComponent(textField);
		textField.setWidth("100%");
		textWriter.addComponent(sendButton);
		sendButton.setSizeUndefined();
		textWriter.setExpandRatio(textField, 9);
		textWriter.setExpandRatio(sendButton, 1);
		
		for(int i=newsList.size()-1;i>=0;i--){
			userDiv=new VerticalLayout();
			userDiv.setHeight(panel.getHeight()/6, panel.getHeightUnits());
			userDiv.addComponent(new NewsLayout().getNewsLayout(newsList.get(i)));
			userDiv.addStyleName(ThemeConstants.BORDERED);
			feed.addComponent(userDiv);
		}
	}

	private void newMessage(News sentNews) {
		userDiv=new VerticalLayout();
		userDiv.setHeight(panel.getHeight()/6, panel.getHeightUnits());
		userDiv.addComponent(new NewsLayout().getNewsLayout(sentNews));
		userDiv.addStyleName(ThemeConstants.BORDERED);
		feed.addComponent(userDiv, 0);		
	}

	@Override
	public void enter(ViewChangeEvent event) {
				
	}
	
	private void sendButtonClicked(Button.ClickEvent event){
		message=textField.getValue();
		News news=new News();
		news.setMessage(message);
		news.setNewsUser(user);
		java.util.Date date=new java.util.Date();
		news.setTime(new Timestamp(date.getTime()));
		textField.clear();		
		if(message.length()!=0){			
			newsService.saveNews(news);
			newMessage(news);
		}
	}
}

