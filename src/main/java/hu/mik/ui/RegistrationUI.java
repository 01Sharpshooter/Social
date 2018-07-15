package hu.mik.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.constants.ThemeConstants;
import hu.mik.services.UserService;

@SuppressWarnings("serial")
@Theme(ThemeConstants.UI_THEME)
@SpringUI(path = "/registration")
//@Widgetset("hu.mik.widgetset.WidgetSet")
public class RegistrationUI extends UI {
	@Autowired
	private UserService userService;

	private Label success = new Label();

	@Override
	protected void init(VaadinRequest request) {
		this.getPage().setTitle("Registration");
		final VerticalLayout layout = new VerticalLayout();
		this.setContent(layout);
		Label title = new Label("Registration");
		title.setStyleName(ValoTheme.LABEL_H1);
		layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		layout.addComponent(title);

		TextField nameTF = new TextField("Name");
		nameTF.setIcon(VaadinIcons.USER);
		nameTF.setRequiredIndicatorVisible(true);
		layout.addComponent(nameTF);

		PasswordField pwTF = new PasswordField("Password");
		pwTF.setIcon(VaadinIcons.PASSWORD);
		pwTF.setRequiredIndicatorVisible(true);
		layout.addComponent(pwTF);

		PasswordField pwTFAgain = new PasswordField("Repeat password");
		pwTFAgain.setIcon(VaadinIcons.PASSWORD);
		pwTFAgain.setRequiredIndicatorVisible(true);
		layout.addComponent(pwTFAgain);

		Button submit = new Button("Register");
		submit.setStyleName(ValoTheme.BUTTON_LARGE);
		layout.addComponent(submit);

		submit.addClickListener(event -> {
			if (nameTF.isEmpty() || pwTF.isEmpty() || pwTFAgain.isEmpty()) {
				RegistrationUI.this.success.setValue("Empty field!");
			} else if (RegistrationUI.this.userService.takenUserName(nameTF.getValue())) {
				RegistrationUI.this.success.setValue("This username is already taken. Please choose another one!");
				RegistrationUI.this.setFocusedComponent(nameTF);
				nameTF.setStyleName(ThemeConstants.WRONG_FIELD);
			} else if (!pwTF.getValue().equals(pwTFAgain.getValue())) {
				nameTF.setStyleName("");
				RegistrationUI.this.success.setValue("Passwords are not matching!");
				RegistrationUI.this.setFocusedComponent(pwTF);
				pwTF.clear();
				pwTFAgain.clear();
				pwTF.setStyleName(ThemeConstants.WRONG_FIELD);
				pwTFAgain.setStyleName(ThemeConstants.WRONG_FIELD);
			} else {
				layout.removeAllComponents();
				RegistrationUI.this.success.setValue("Successful registration!");
				RegistrationUI.this.success.setStyleName(ValoTheme.LABEL_H1);
				layout.addComponent(RegistrationUI.this.success);
				Link link = new Link("Login", new ExternalResource("/login"));
				layout.addComponent(link);
//						userService.registerUser(nameTF.getValue(), encService.encryptPw(pwTF.getValue()));
			}
		});
		submit.setClickShortcut(KeyCode.ENTER);
		layout.addComponent(this.success);

		layout.addComponent(submit);
	}
}
