package hu.mik.views;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.services.UserService;

@SuppressWarnings("serial")
@UIScope
@SpringView(name = RegistrationView.NAME)
public class RegistrationView extends VerticalLayout implements View {
	public static final String NAME = "reg";
	private Label success = new Label();

	@Autowired
	private UserService userService;

	@PostConstruct
	public void init() {

		TextField nameTF = new TextField("Name");
		nameTF.setIcon(VaadinIcons.USER);
		nameTF.setRequiredIndicatorVisible(true);
		this.addComponent(nameTF);

		PasswordField pwTF = new PasswordField("Password");
		pwTF.setIcon(VaadinIcons.PASSWORD);
		pwTF.setRequiredIndicatorVisible(true);
		this.addComponent(pwTF);

		PasswordField pwTFAgain = new PasswordField("Repeat password");
		pwTFAgain.setIcon(VaadinIcons.PASSWORD);
		pwTFAgain.setRequiredIndicatorVisible(true);
		this.addComponent(pwTFAgain);

		Button submit = new Button("Register");
		submit.setStyleName(ValoTheme.BUTTON_LARGE);
		this.addComponent(submit);

		submit.addClickListener(event -> {
			if (nameTF.isEmpty() || pwTF.isEmpty() || pwTFAgain.isEmpty()) {
				RegistrationView.this.success.setCaption("Empty field!");
			} else if (RegistrationView.this.userService.takenUserName(nameTF.getValue())) {
				RegistrationView.this.success.setCaption("This username is already taken. Please choose another one!");
			} else if (!pwTF.getValue().equals(pwTFAgain.getValue())) {
				RegistrationView.this.success.setCaption("Passwords are not matching!");
			} else {
				RegistrationView.this.success.setCaption("Successful registration!");
//					userService.registerUser(nameTF.getValue(), encService.encryptPw(pwTF.getValue()));
			}
		});
		this.addComponent(this.success);
		for (int i = 0; i < this.getComponentCount(); i++) {
			this.setComponentAlignment(this.getComponent(i), Alignment.MIDDLE_CENTER);
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
