package hu.mik.navigation;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.views.MainView;
import hu.mik.views.MessagesView;
import hu.mik.views.RegistrationView;

public class NaviBar {
	@Autowired
	private UI usedUI;

	public CssLayout getNaviBar(UI ui) {
		this.usedUI = ui;
		CssLayout naviBar = new CssLayout();
		naviBar.setCaption("Menu");
		naviBar.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		naviBar.addComponent(this.createNavigationButton("Main", MainView.NAME));
		naviBar.addComponent(this.createNavigationButton("Messages", MessagesView.NAME));
		naviBar.addComponent(this.createNavigationButton("Registration", RegistrationView.NAME));
		Button logoutButton = new Button("Logout");
		logoutButton.addClickListener(event -> {
			NaviBar.this.usedUI.getPage().setLocation("/login");
			VaadinService.getCurrentRequest().getWrappedSession().invalidate();

		});
		logoutButton.setStyleName(ValoTheme.BUTTON_SMALL);
		naviBar.addComponent(logoutButton);
		return naviBar;
	}

	private Button createNavigationButton(String caption, final String ViewName) {
		Button button = new Button(caption);
		button.addStyleName(ValoTheme.BUTTON_SMALL);
		button.addClickListener(event -> this.usedUI.getNavigator().navigateTo(ViewName));
		return button;
	}

}
