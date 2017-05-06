package hu.mik.views;

import javax.annotation.PostConstruct;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.AbstractSingleComponentContainer;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.VerticalLayout;

@ViewScope
@SpringView(name=LoginView.NAME)
public class LoginView extends LoginForm implements View{
	public static final String NAME="login";

	@PostConstruct
	public void init(){
		
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
