package hu.mik.components;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.vaadin.icons.VaadinIcons;
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
import hu.mik.services.NewsService;
import hu.mik.utils.ProfileImageHelper;
import hu.mik.views.ProfileView;

@SuppressWarnings("serial")
public class NewsComponent extends VerticalLayout {
	private User user;
	private News news;
	private NewsService newsService;

	public NewsComponent(News news, boolean withDelete, NewsService newsService) {
		this.setSpacing(true);
		this.setMargin(true);
		this.setSizeFull();
		this.news = news;
		this.newsService = newsService;
		this.addStyleName(ThemeConstants.BORDERED);

		this.createContent(withDelete);
	}

	private void createContent(boolean withDelete) {
		this.createHeader(withDelete);
		this.createMessageBlock(this.news);
		this.createDateBlock(this.news);

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

	private void createHeader(boolean withDelete) {
		HorizontalLayout header = new HorizontalLayout();
		header.setWidth("100%");
		header.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		this.user = this.news.getUser();
		this.createImage(header);
		this.createNameButton(header, withDelete);
		if (withDelete) {
			this.createDeleteButton(header);
		}
		this.addComponent(header);
	}

	private void createDeleteButton(HorizontalLayout header) {
		Button btnDelete = new Button(VaadinIcons.CLOSE_CIRCLE_O);
		btnDelete.addStyleName(ValoTheme.BUTTON_LINK);
		btnDelete.addClickListener(e -> {
			ConfirmDialog window = new ConfirmDialog("Are you sure you want to remove this message?");
			window.addCloseListener(closeEvent -> {
				if (((ConfirmDialog) closeEvent.getWindow()).isConfirmed()) {
					this.newsService.deleteNews(this.news);
					this.setVisible(false);
				}
			});
			UI.getCurrent().addWindow(window);
		});
		header.addComponent(btnDelete);
		header.setExpandRatio(btnDelete, 1f);
		header.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);

	}

	private void createNameButton(HorizontalLayout header, boolean withDelete) {
		Button nameButton = new Button(this.news.getUser().getFullName(),
				e -> UI.getCurrent().getNavigator().navigateTo(ProfileView.NAME + "/" + e.getButton().getId()));
		nameButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		nameButton.addStyleName(ValoTheme.LABEL_H1);
		nameButton.setId(this.user.getUsername());
		header.addComponent(nameButton);
		if (!withDelete) {
			header.setExpandRatio(nameButton, 1f);
		}
	}

	private void createImage(HorizontalLayout header) {
		Image image = new Image(null, new FileResource(ProfileImageHelper.loadUserImage(this.user.getImageName())));
		image.setHeight("100%");
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);

		header.addComponent(image);
	}

	public User getUser() {
		return this.user;
	}

}
