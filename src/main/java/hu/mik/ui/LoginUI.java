package hu.mik.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.risto.formsender.FormSender;
import org.vaadin.risto.formsender.widgetset.client.shared.Method;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.constants.ThemeConstants;
import hu.mik.utils.UserUtils;
import hu.mik.views.DefaultView;

@SuppressWarnings("serial")
@Theme(ThemeConstants.UI_THEME)
@SpringUI(path = "/login")
@Viewport("width=device-width,initial-scale=1")
@Widgetset("hu.mik.gwt.SocialWidgetset")
public class LoginUI extends UI {
	@Autowired
	private UserUtils userUtils;

	@Override
	protected void init(VaadinRequest request) {
		if (this.userUtils.getLoggedInUser() != null) {
			this.getPage().setLocation("/main#!home");
			return;
		}
		this.getNavigator().addViewChangeListener(e -> this.viewChange(e));
		this.getPage().setTitle("Login");
		final VerticalLayout layout = new VerticalLayout();
		Label title = new Label("Login");
		title.setStyleName(ValoTheme.LABEL_H1);
		this.setContent(layout);
		layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		layout.addComponent(title);
		Label success = new Label();
		layout.addComponent(success);

		if (request.getParameter("error") != null) {
//				success.setValue("We're sorry to inform you that your account has been suspended.");
			success.setValue("Wrong username or password.");
		}

		TextField nameTF = new TextField("Name");
		nameTF.setIcon(VaadinIcons.USER);
		nameTF.setRequiredIndicatorVisible(true);
		layout.addComponent(nameTF);

		PasswordField pwTF = new PasswordField("Password");
		pwTF.setIcon(VaadinIcons.KEY);
		pwTF.setRequiredIndicatorVisible(true);
		layout.addComponent(pwTF);

		Button submit = new Button("Login");
		submit.setStyleName(ValoTheme.BUTTON_LARGE);
		submit.addClickListener(event -> {
			String userName = nameTF.getValue();
			String passWord = pwTF.getValue();
			if (this.IsvalidName(userName) && this.IsvalidPassword(passWord)) {
//						User user=userService.findUserByUsername(userName);
//						if(user!=null){
//							if(encService.comparePW(passWord, user.getPassword())){
				FormSender formSender = new FormSender();
				formSender.setFormAction(
						VaadinServlet.getCurrent().getServletContext().getContextPath() + "/j_spring_security_check");
				formSender.setFormMethod(Method.POST);
				formSender.addValue("username", nameTF.getValue());
				formSender.addValue("password", pwTF.getValue());
				formSender.setFormTarget("_top");
				formSender.extend(this.getUI());
				formSender.submit();
//							}else{
//								success.setValue("Wrong username or password.");
//								pwTF.clear();
//								setFocusedComponent(pwTF);
//							}
//						}else{
//							success.setValue("Wrong username or password.");
//							pwTF.clear();
//							setFocusedComponent(pwTF);
//						}
			} else {
				success.setValue("One or more fields are empty.");
			}
		});
		submit.setClickShortcut(KeyCode.ENTER);
		layout.addComponent(submit);
//			Button register=new Button("Registration");
//			register.setStyleName(ValoTheme.BUTTON_SMALL);
//			register.addClickListener(event->getPage().setLocation("/registration"));
//			layout.addComponent(register);

	}

	private boolean viewChange(ViewChangeEvent e) {
		if (!e.getViewName().equals(DefaultView.NAME)) {
			return false;
		}
		return true;
	}

	private boolean IsvalidName(String userName) {
		if (userName.length() != 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean IsvalidPassword(String password) {
		if (password.length() != 0) {
			return true;
		} else {
			return false;
		}
	}
}
