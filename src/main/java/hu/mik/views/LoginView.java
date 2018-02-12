package hu.mik.views;

import javax.annotation.PostConstruct;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.AbstractSingleComponentContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@ViewScope
@SpringView(name=LoginView.NAME)
public class LoginView extends VerticalLayout implements View{
	public static final String NAME="login";

	@PostConstruct
	public void init(){
		TextField nameTF=new TextField("Name");
		nameTF.setIcon(VaadinIcons.USER);
		nameTF.setRequiredIndicatorVisible(true);
		addComponent(nameTF);
		
		PasswordField pwTF=new PasswordField("Password");
		pwTF.setIcon(VaadinIcons.PASSWORD);
		pwTF.setRequiredIndicatorVisible(true);			
		addComponent(pwTF);		
		
		Button submit=new Button("Login");
		submit.setStyleName(ValoTheme.BUTTON_LARGE);
		addComponent(submit);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
