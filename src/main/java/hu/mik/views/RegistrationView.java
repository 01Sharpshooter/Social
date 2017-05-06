package hu.mik.views;


import javax.annotation.PostConstruct;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@UIScope
@SpringView(name=RegistrationView.NAME)
public class RegistrationView extends VerticalLayout implements View {
	public static final String NAME="reg";
	private Label success=new Label();
	
	@PostConstruct
	public void init(){
		
		TextField nameTF=new TextField("Name");
		nameTF.setIcon(VaadinIcons.USER);
		nameTF.setRequiredIndicatorVisible(true);
		addComponent(nameTF);
		
		TextField pwTF=new TextField("Password");
		pwTF.setIcon(VaadinIcons.PASSWORD);
		pwTF.setRequiredIndicatorVisible(true);		
		addComponent(pwTF);
		
		Button submit=new Button("Register");
		submit.setStyleName(ValoTheme.BUTTON_LARGE);
		addComponent(submit);
		
		submit.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				if(!nameTF.isEmpty() && !pwTF.isEmpty()){
					success.setCaption("Sikeres regisztráció!");
				}else{
					success.setCaption("Üres mező!");					
				}				
			}
		});
		addComponent(success);
		for(int i=0;i<this.getComponentCount();i++){
			this.setComponentAlignment(this.getComponent(i), Alignment.MIDDLE_CENTER);
			}
		}
		


	@Override
	public void enter(ViewChangeEvent event) {
		
		
	}
	
}
