package hu.mik.components;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Scope;

import com.vaadin.server.FileResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.News;
import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.services.LdapService;
import hu.mik.utils.ApplicationContextHolder;
import hu.mik.utils.ProfileImageHelper;
import hu.mik.views.ProfileView;

@Scope(scopeName = "singleton")
public class NewsComponentConverter {
	private static User user;

	public static VerticalLayout convert(News news) {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setSizeFull();
		layout.addStyleName(ThemeConstants.BORDERED);

		createContent(news, layout);
		return layout;
	}

	private static void createContent(News news, VerticalLayout layout) {
		createHeader(news, layout);
		createMessageBlock(news, layout);
		createDateBlock(news, layout);

	}

	private static void createDateBlock(News news, VerticalLayout layout) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateString = df.format(news.getTime());
		Label date = new Label(dateString);
		layout.addComponent(date);
	}

	private static void createMessageBlock(News news, VerticalLayout layout) {
		Label message = new Label(news.getMessage());
		message.setSizeFull();
		message.setStyleName(ThemeConstants.BLUE_TEXT);
		message.addStyleName(ThemeConstants.RESPONSIVE_FONT);
		layout.addComponent(message);
	}

	private static void createHeader(News news, VerticalLayout layout) {
		HorizontalLayout header = new HorizontalLayout();
		header.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		user = news.getUser();
		createImage(header);
		createNameButton(header);
		layout.addComponent(header);
	}

	private static void createNameButton(HorizontalLayout header) {
		LdapService ldapService = ApplicationContextHolder.getApplicationContext().getBean(LdapService.class);
		Button nameButton = new Button(ldapService.findUserByUsername(user.getUsername()).getFullName(),
				e -> UI.getCurrent().getNavigator().navigateTo(ProfileView.NAME + "/" + e.getButton().getId()));
		nameButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		nameButton.addStyleName(ValoTheme.LABEL_H1);
		nameButton.setId(user.getUsername());
		header.addComponent(nameButton);
	}

	private static void createImage(HorizontalLayout header) {
		Image image = new Image(null, new FileResource(ProfileImageHelper.loadUserImage(user.getImageName())));
		image.setWidth("100%");
		image.setHeight("100%");
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);

		header.addComponent(image);
	}
}
