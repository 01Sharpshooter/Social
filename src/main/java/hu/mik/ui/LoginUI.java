package hu.mik.ui;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.EncryptService;
import hu.mik.services.UserService;

@Theme(ThemeConstants.UI_THEME)
@SpringUI(path="/login")
public class LoginUI extends UI{
	@Autowired
	private UserService userService;
	@Autowired
	private EncryptService encService;
	
	private WrappedSession session=VaadinService.getCurrentRequest().getWrappedSession();

	@Override
	protected void init(VaadinRequest request) {
		if(session.getAttribute("User")==null){
			final VerticalLayout layout=new VerticalLayout();
			Label title=new Label("Login");
			title.setStyleName(ValoTheme.LABEL_H1);
			setContent(layout);	
			layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
			layout.addComponent(title);
			Label success=new Label();
			layout.addComponent(success);
			
			TextField nameTF=new TextField("Name");
			nameTF.setIcon(VaadinIcons.USER);
			nameTF.setRequiredIndicatorVisible(true);
			layout.addComponent(nameTF);
			
			PasswordField pwTF=new PasswordField("Password");
			pwTF.setIcon(VaadinIcons.PASSWORD);
			pwTF.setRequiredIndicatorVisible(true);			
			layout.addComponent(pwTF);		
			
			Button submit=new Button("Login");
			submit.setStyleName(ValoTheme.BUTTON_LARGE);
			submit.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					String userName=nameTF.getValue();
					String passWord=pwTF.getValue();
					
					if(IsvalidName(userName) && IsvalidPassword(passWord)){
						User user=userService.findUserByUsername(userName);
						if(user!=null){
							if(encService.comparePW(passWord, user.getPassword())){	
//								user.setImage(new Image(null, new FileResource(
//										new File(UserConstants.PROFILE_PICTURE_LOCATION+user.getImageName()))));
				                session.setAttribute("User", user);
				    			MainUI.getOnlineUsers().add(user);				    			
								getPage().setLocation("/main");
							}else{
								success.setValue("Wrong username or password.");
								pwTF.clear();
								setFocusedComponent(pwTF);
							}
						}else{
							success.setValue("Wrong username or password.");
							pwTF.clear();
							setFocusedComponent(pwTF);
						}
					}else{
						success.setValue("One or more fields are empty.");						
					}
				}
			});
			submit.setClickShortcut(KeyCode.ENTER);
			layout.addComponent(submit);
			Button register=new Button("Registration");
			register.setStyleName(ValoTheme.BUTTON_SMALL);
			register.addClickListener(event->getPage().setLocation("/registration"));
			layout.addComponent(register);
		}else{
			getPage().setLocation("/main");
		}
	}
	
	private boolean IsvalidName(String userName){
		if(userName.length()!=0){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean IsvalidPassword(String password){
		if(password.length()!=0){
			return true;
		}else{
			return false;
		}
	}
}





