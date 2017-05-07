package hu.mik.ui;


import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.constants.ThemeConstants;
import hu.mik.services.EncryptService;
import hu.mik.services.UserService;

@Theme(ThemeConstants.UI_THEME)
@SpringUI(path="/registration")
public class RegistrationUI extends UI{
	@Autowired
	private UserService userService;
	@Autowired
	private EncryptService encService;
	
	private Label success=new Label();
	
	private WrappedSession session=VaadinService.getCurrentRequest().getWrappedSession();
	
	@Override
	protected void init(VaadinRequest request) {
		if(session.getAttribute("Username")==null){
			final FormLayout layout=new FormLayout();
			setContent(layout);	
			
			TextField nameTF=new TextField("Name");
			nameTF.setIcon(VaadinIcons.USER);
			nameTF.setRequiredIndicatorVisible(true);
			layout.addComponent(nameTF);
			
			PasswordField pwTF=new PasswordField("Password");
			pwTF.setIcon(VaadinIcons.PASSWORD);
			pwTF.setRequiredIndicatorVisible(true);		
			layout.addComponent(pwTF);
			
			PasswordField pwTFAgain=new PasswordField("Repeat password");
			pwTFAgain.setIcon(VaadinIcons.PASSWORD);
			pwTFAgain.setRequiredIndicatorVisible(true);		
			layout.addComponent(pwTFAgain);
			
			Button submit=new Button("Register");
			submit.setStyleName(ValoTheme.BUTTON_LARGE);
			layout.addComponent(submit);
			
			submit.addClickListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
					if(nameTF.isEmpty() || pwTF.isEmpty() || pwTFAgain.isEmpty()){
						success.setCaption("Empty field!");
					}else if(userService.takenUserName(nameTF.getValue())){	
						success.setCaption("This username is already taken. Please choose another one!");					
					}else if(!pwTF.getValue().equals(pwTFAgain.getValue())){
						success.setCaption("Passwords are not matching!");
					}else{
						success.setCaption("Successful registration!");
						userService.registerUser(nameTF.getValue(), encService.encryptPw(pwTF.getValue()));					
					}				
				}
			});
			layout.addComponent(success);
			
			layout.addComponent(submit);
		}else{
			getPage().setLocation("/main");
		}	
	}
}





