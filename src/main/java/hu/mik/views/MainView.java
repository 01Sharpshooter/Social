package hu.mik.views;

import javax.annotation.PostConstruct;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@ViewScope
@SpringView(name=MainView.NAME)
public class MainView extends VerticalLayout implements View{
	public static final String NAME="";
	
	@PostConstruct
	public void init(){
		addComponent(new Label("Welcome to the Main View!"));
	}

	@Override
	public void enter(ViewChangeEvent event) {
				
	}
	
}

