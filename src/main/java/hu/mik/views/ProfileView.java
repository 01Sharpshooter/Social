package hu.mik.views;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;import com.vaadin.data.provider.DataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.UserService;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name=ProfileView.NAME)
public class ProfileView extends VerticalLayout implements View{
	public static final String NAME="profile";
	
	@Autowired
	UserService userService;

	@Override
	public void enter(ViewChangeEvent event) {
		if(event.getParameters().length()>0){
			String parameters[]=event.getParameters().split("/");
			int userId=Integer.parseInt(parameters[0]);
			User user=userService.findUserById(userId);
			if(user==null) {
				Label lblMissing=new Label("Sorry, we could not find the person you were looking for.");
				this.addComponent(lblMissing);
			}else {
				CssLayout header=new CssLayout();
				
				Image image=new Image(null, new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImageName())));
				image.addStyleName(ThemeConstants.BORDERED_IMAGE);
				Label lblName=new Label(user.getUsername());
				lblName.addStyleName(ThemeConstants.BLUE_TEXT_H1);
				header.setId("profileHeader");
				header.addComponent(image);
				header.addComponent(lblName);
				FormLayout form=new FormLayout();
				form.setSizeFull();
				
				this.addComponent(header);
				this.addComponent(form);
				
				this.setComponentAlignment(header, Alignment.MIDDLE_CENTER);
				
				this.setExpandRatio(header, 20);
				this.setExpandRatio(form, 80);
				
				TextField tfName=new TextField("Name:", user.getUsername());
				tfName.addStyleName(ThemeConstants.BLUE_TEXT);
				tfName.setReadOnly(true);
				form.addComponent(tfName);
			}
		}
		
	}
	
	
}
