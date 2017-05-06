package hu.mik.views;

import javax.annotation.PostConstruct;

import org.springframework.web.context.annotation.SessionScope;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@ViewScope
@SpringView(name=MessagesView.NAME)
public class MessagesView extends VerticalLayout implements View {
	public static final String NAME="messages";
	
	@PostConstruct
	public void init(){
		addComponent(new Label("Messages"));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
}
