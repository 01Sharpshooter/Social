package hu.mik.components;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.vaadin.server.FileResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.News;
import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.LdapService;
import hu.mik.services.UserService;
import hu.mik.views.ProfileView;

@SuppressWarnings("serial")
@SpringComponent(value = "newsComponent")
@Scope(scopeName = "prototype")
public class NewsComponent extends VerticalLayout implements LazyLoadingComponent<News> {
	private UserService userService;
	private LdapService ldapService;
	private User user;

	@Autowired
	public NewsComponent(UserService userService, LdapService ldapService) {
		this.setSpacing(true);
		this.setMargin(true);
		this.setSizeFull();
		this.addStyleName(ThemeConstants.BORDERED);
		this.userService = userService;
		this.ldapService = ldapService;
	}

	@Override
	public LazyLoadingComponent<News> construct(News news) {
		this.createContent(news);
		return this;
	}

	private void createContent(News news) {
		this.createHeader(news);
		this.createMessageBlock(news);
		this.createDateBlock(news);

	}

	private void createDateBlock(News news) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateString = df.format(news.getTime());
		Label date = new Label(dateString);
		this.addComponent(date);
	}

	private void createMessageBlock(News news) {
		Label message = new Label(news.getMessage());
		message.setSizeFull();
		message.setStyleName(ThemeConstants.BLUE_TEXT);
		message.addStyleName(ThemeConstants.RESPONSIVE_FONT);
		this.addComponent(message);
	}

	private void createHeader(News news) {
		HorizontalLayout header = new HorizontalLayout();
		header.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		this.user = this.userService.findUserById(news.getUserId());
		this.createImage(header);
		this.createNameButton(header);
		this.addComponent(header);
	}

	private void createNameButton(HorizontalLayout header) {
		Button nameButton = new Button(this.ldapService.findUserByUsername(this.user.getUsername()).getFullName(),
				e -> this.getUI().getNavigator().navigateTo(ProfileView.NAME + "/" + e.getButton().getId()));
		nameButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		nameButton.addStyleName(ValoTheme.LABEL_H1);
		nameButton.setId(this.user.getUsername());
		header.addComponent(nameButton);
	}

	private void createImage(HorizontalLayout header) {
		Image image = new Image(null,
				new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION + this.user.getImageName())));
		image.setWidth("100%");
		image.setHeight("100%");
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);

		header.addComponent(image);
	}
}
