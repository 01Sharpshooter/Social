package hu.mik.components;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.News;
import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;

public class NewsLayout{
	public VerticalLayout getNewsLayout(News news){
		VerticalLayout layout=new VerticalLayout();
		HorizontalLayout header=new HorizontalLayout();
		header.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		layout.addComponent(header);
		layout.setSpacing(false);
		layout.setMargin(false);
		User user=news.getNewsUser();
		Image image=new Image(null, new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImageName())));
//		Image image=user.getImage();
//		System.out.println(image);
		header.addComponent(image);
		image.setWidth("75%");
		image.setHeight("75%");
		layout.setSizeFull();
		Label name=new Label(user.getUsername());
		name.setStyleName(ValoTheme.LABEL_H2);
		header.addComponent(name);
		Label message=new Label(news.getMessage());
		message.setSizeFull();
		message.setStyleName(ThemeConstants.BLUE_TEXT_H3);
		layout.addComponent(message);
		System.out.println(news.getTime());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateString=df.format(news.getTime());
		Label date=new Label(dateString);
		layout.addComponent(date);
		return layout;
	}

}
