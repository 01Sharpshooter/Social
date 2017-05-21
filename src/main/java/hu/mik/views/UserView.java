package hu.mik.views;

import javax.annotation.PostConstruct;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;

@SpringView(name=UserView.NAME)
public class UserView extends VerticalLayout implements View{
	public static final String NAME="user";
	
	@PostConstruct
	public void init(){
		
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
}
