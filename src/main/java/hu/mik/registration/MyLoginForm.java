package hu.mik.registration;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public abstract class MyLoginForm extends LoginForm {
        @Override
        protected Component createContent(TextField userNameField, PasswordField passwordField, Button loginButton) {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);
            layout.setMargin(true);

            layout.addComponent(userNameField);
            layout.addComponent(passwordField);
            layout.addComponent(loginButton);
            layout.setComponentAlignment(loginButton, Alignment.BOTTOM_LEFT);
            return layout;
        }       
        
    }