package hu.mik.components;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.News;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.constants.ThemeConstants;
import hu.mik.factories.NewsPanelFactory;
import hu.mik.services.NewsService;
import hu.mik.utils.UserUtils;

@SuppressWarnings("serial")
@SpringComponent
@Scope("prototype")
public class NewsFeedComponent extends VerticalLayout {
	private SocialUserWrapper socialUser;
	private NewsService newsService;
	private NewsPanelFactory newsPanelFactory;
	private TabSheet tsGroups;

	@Autowired
	private NewsFeedComponent(UserUtils userUtils, NewsService newsService, NewsPanelFactory pagingPanelFactory) {
		super();
		this.setSizeFull();
		this.newsService = newsService;
		this.socialUser = userUtils.getLoggedInUser();
		this.newsPanelFactory = pagingPanelFactory;
	}

	public NewsFeedComponent init() {
		this.createContent();
		return this;
	}

	private void createContent() {
		this.createTextWriter();
		this.createTabSheet();
	}

	private void createTabSheet() {
		List<LdapGroup> groupList = this.socialUser.getLdapUser().getLdapGroups();
		this.tsGroups = new TabSheet();
		this.tsGroups.addTab(this.newsPanelFactory.getDefaultInstance(), "All");
		this.tsGroups.setSizeFull();
		groupList.forEach(
				group -> this.tsGroups.addTab(this.newsPanelFactory.getLdapGroupInstance(group), group.getGroupName()));
		this.addComponent(this.tsGroups);
		this.setExpandRatio(this.tsGroups, 1f);
	}

	public void createTextWriter() {
		HorizontalLayout textWriter = new HorizontalLayout();
		textWriter.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		textWriter.setWidth("100%");
		textWriter.setCaption("Share your thoughts...");

		TextField textField = new TextField();
		textField.setSizeFull();
		Button sendButton = new Button("Send", e -> {
			this.addNewNewsComponent(textField.getValue());
			textField.clear();
		});
		sendButton.addStyleName(ThemeConstants.BLUE_TEXT);
		sendButton.setClickShortcut(KeyCode.ENTER);
		textWriter.addComponent(textField);
		textField.setWidth("100%");
		textWriter.addComponent(sendButton);
		sendButton.setSizeUndefined();
		textWriter.setExpandRatio(textField, 1f);

		this.addComponent(textWriter);
	}

	private void addNewNewsComponent(String message) {
		News news = new News();
		news.setMessage(message);
		news.setUser(this.socialUser.getDbUser());
		Date date = new Date();
		news.setTime(new Timestamp(date.getTime()));
		if (message.length() != 0) {
			this.newsService.saveNews(news);
			this.newMessage(news);
		}
	}

	private void newMessage(News sentNews) {
		((NewsPanelScrollable) this.tsGroups.getSelectedTab()).addNews(sentNews);
	}

}
