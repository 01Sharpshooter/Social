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
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
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
import hu.mik.constants.SystemConstants;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.LdapService;
import hu.mik.services.NewsService;
import hu.mik.services.UserService;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name = MainView.NAME)
public class MainView extends VerticalLayout implements View {
	@Autowired
	UserService userService;
	@Autowired
	private NewsService newsService;
	@Autowired
	private LdapService ldapService;

	public static final String NAME = "";
	private VerticalLayout feed;
	private VerticalLayout userDiv;
	private User user;
	private TextField textField;
	private Button sendButton;
	private String message;
	private Panel panel = new Panel();
	private List<News> newsList;
	private HorizontalLayout textWriter;
	private int newsNumberAtOnce = 20;

	@PostConstruct
	public void init() {
		WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
		String username = (String) session.getAttribute(SystemConstants.SESSION_ATTRIBUTE_LDAP_USER);
		if (username != null) {
			this.user = this.userService.findUserByUsername(username);
		}
		this.newsList = this.newsService.lastGivenNewsAll(20);
		this.feed = this.createFeed(this.newsList);
		this.textWriter = this.createTextWriter();
		this.panel.setSizeFull();
		this.panel.setContent(this.feed);
		this.addComponent(this.textWriter);
		this.addComponent(this.panel);

	}

	private VerticalLayout createFeed(List<News> newsList) {
		VerticalLayout feed = new VerticalLayout();
		if (!newsList.isEmpty()) {
			for (News news : newsList) {
				this.userDiv = new VerticalLayout();
				this.userDiv.setHeight(this.panel.getHeight() / 6, this.panel.getHeightUnits());
				this.userDiv.addComponent(this.createNewsLayout(news));
				this.userDiv.addStyleName(ThemeConstants.BORDERED);
				feed.addComponent(this.userDiv);
			}
		} else {
			Label label = new Label("This user has not posted anything yet.");
			label.setStyleName(ThemeConstants.BLUE_TEXT_H3);
			feed.addComponent(label);
		}

		return feed;
	}

	private void newMessage(News sentNews) {
		this.userDiv = new VerticalLayout();
		this.userDiv.setHeight(this.panel.getHeight() / 6, this.panel.getHeightUnits());
		this.userDiv.addComponent(this.createNewsLayout(sentNews));
		this.userDiv.addStyleName(ThemeConstants.BORDERED);
		this.feed.addComponent(this.userDiv, 0);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if (event.getParameters().length() > 0) {
			String parameters[] = event.getParameters().split("/");
			int userId = Integer.parseInt(parameters[0]);
			this.changeToUser(this.userService.findUserById(userId));
		}

	}

	private void sendButtonClicked(Button.ClickEvent event) {
		this.message = this.textField.getValue();
		News news = new News();
		news.setMessage(this.message);
		news.setNewsUser(this.user);
		java.util.Date date = new java.util.Date();
		news.setTime(new Timestamp(date.getTime()));
		this.textField.clear();
		if (this.message.length() != 0) {
			this.newsService.saveNews(news);
			this.newMessage(news);
		}
	}

	public HorizontalLayout createTextWriter() {
		HorizontalLayout textWriter = new HorizontalLayout();
		textWriter.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		textWriter.setWidth("100%");
		textWriter.setCaption("Share your thoughts...");

		this.textField = new TextField();
		this.textField.setSizeFull();
		this.sendButton = new Button("Send", this::sendButtonClicked);
		this.sendButton.addStyleName(ThemeConstants.BLUE_TEXT);
		this.sendButton.setClickShortcut(KeyCode.ENTER);
		textWriter.addComponent(this.textField);
		this.textField.setWidth("100%");
		textWriter.addComponent(this.sendButton);
		this.sendButton.setSizeUndefined();
		textWriter.setExpandRatio(this.textField, 75);
		textWriter.setExpandRatio(this.sendButton, 25);

		return textWriter;
	}

	public VerticalLayout createNewsLayout(News news) {
		VerticalLayout layout = new VerticalLayout();
		HorizontalLayout header = new HorizontalLayout();
		header.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		layout.addComponent(header);
		layout.setSpacing(false);
		layout.setMargin(false);
		User user = news.getNewsUser();
		Image image = new Image(null,
				new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION + user.getImageName())));
		header.addComponent(image);
		image.setWidth("100%");
		image.setHeight("100%");
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);
		layout.setSizeFull();
		Button nameButton = new Button(this.ldapService.findUserByUsername(user.getUsername()).getFullName(),
				this::userNameListener);
		nameButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		nameButton.addStyleName(ValoTheme.LABEL_H1);
		nameButton.setId(user.getUsername());
		header.addComponent(nameButton);
		Label message = new Label(news.getMessage());
		message.setSizeFull();
		message.setStyleName(ThemeConstants.BLUE_TEXT);
		message.addStyleName(ThemeConstants.RESPONSIVE_FONT);
		Responsive.makeResponsive(message);
		layout.addComponent(message);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateString = df.format(news.getTime());
		Label date = new Label(dateString);
		layout.addComponent(date);
		return layout;
	}

	private void userNameListener(Button.ClickEvent event) {
//		((MainUI)getUI()).changeSideMenu(userService.findUserByUsername(
//				event.getButton().getCaption()));
		this.getUI().getNavigator().navigateTo(ProfileView.NAME + "/" + event.getButton().getId());
	}

	private void changeNews(List<News> newsList) {
		this.panel.setContent(this.createFeed(newsList));
	}

	public void changeToUser(User user) {
		this.changeNews(this.newsService.lastGivenNewsUser(this.newsNumberAtOnce, user));
		this.removeComponent(this.textWriter);
	}
}
