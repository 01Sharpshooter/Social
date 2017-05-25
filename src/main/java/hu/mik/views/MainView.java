package hu.mik.views;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.News;
import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.NewsService;
import hu.mik.services.UserService;
import hu.mik.ui.MainUI;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name=MainView.NAME)
public class MainView extends VerticalLayout implements View{
	@Autowired
	UserService userService;
	@Autowired
	private NewsService newsService;
	
	public static final String NAME="";
	private VerticalLayout feed;
	private VerticalLayout userDiv;
	private User user;
	private TextField textField;
	private Button sendButton;
	private String message;
	private Panel panel=new Panel();
	private List<News> newsList;
	private HorizontalLayout textWriter;
	
	@PostConstruct
	public void init(){
		user=(User) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("User");
		newsList=newsService.lastGivenNewsAll(20);
		feed=createFeed(newsList);	
		textWriter=createTextWriter();
		panel.setSizeFull();
		panel.setContent(feed);
		this.addComponent(textWriter);
		this.addComponent(panel);		
		
		
	}

	private VerticalLayout createFeed(List<News> newsList) {
		VerticalLayout feed=new VerticalLayout();
		for(int i=newsList.size()-1;i>=0;i--){
			userDiv=new VerticalLayout();
			userDiv.setHeight(panel.getHeight()/6, panel.getHeightUnits());
			userDiv.addComponent(createNewsLayout(newsList.get(i)));
			userDiv.addStyleName(ThemeConstants.BORDERED);
			feed.addComponent(userDiv);
		}
		
		return feed;
	}

	private void newMessage(News sentNews) {
		userDiv=new VerticalLayout();
		userDiv.setHeight(panel.getHeight()/6, panel.getHeightUnits());
		userDiv.addComponent(createNewsLayout(sentNews));
		userDiv.addStyleName(ThemeConstants.BORDERED);
		feed.addComponent(userDiv, 0);		
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if(event.getParameters().length()>0){
			String parameters[]=event.getParameters().split("/");
			int userId=Integer.parseInt(parameters[0]);
			changeToUser(userService.findUserById(userId));
		}
		
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
	
	public HorizontalLayout createTextWriter(){
		HorizontalLayout textWriter=new HorizontalLayout();
		textWriter.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		textWriter.setWidth("100%");
		textWriter.setCaption("Share your thoughts...");
		
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
		
		return textWriter;
	}
	
	public VerticalLayout createNewsLayout(News news){
		VerticalLayout layout=new VerticalLayout();
		HorizontalLayout header=new HorizontalLayout();
		header.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		layout.addComponent(header);
		layout.setSpacing(false);
		layout.setMargin(false);
		User user=news.getNewsUser();
		Image image=new Image(null, new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImageName())));
		header.addComponent(image);
		image.setWidth("100%");
		image.setHeight("100%");
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);
		layout.setSizeFull();
		Button nameButton=new Button(user.getUsername(), this::userNameListener);
		nameButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		nameButton.addStyleName(ValoTheme.LABEL_H1);
		header.addComponent(nameButton);
		Label message=new Label(news.getMessage());
		message.setSizeFull();
		message.setStyleName(ThemeConstants.BLUE_TEXT_H3);
		layout.addComponent(message);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateString=df.format(news.getTime());
		Label date=new Label(dateString);
		layout.addComponent(date);
		return layout;
	}
	
	private void userNameListener(Button.ClickEvent event){
		((MainUI)getUI()).changeSideMenu(userService.findUserByUsername(
				event.getButton().getCaption()));
		changeNews(newsService.lastGivenNewsUser(20,
				userService.findUserByUsername(event.getButton().getCaption())));
		this.removeComponent(textWriter);
	}
	
	private void changeNews(List<News> newsList){
		this.panel.setContent(createFeed(newsList));
	}
	
	public void changeToUser(User user){
		changeNews(newsService.lastGivenNewsUser(20, user));
	}
}

