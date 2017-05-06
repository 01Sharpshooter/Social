package hu.mik.ui;


import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.constants.ThemeConstants;
import hu.mik.navigation.NaviBar;
import hu.mik.registration.MyLoginForm;
import hu.mik.views.MainView;
import hu.mik.views.MessagesView;
import hu.mik.views.RegistrationView;

@Theme(ThemeConstants.UI_THEME)
@SpringUI(path="/main")
@SpringViewDisplay
public class MainUI extends UI implements ViewDisplay{

	private Panel viewDisplay;
	
	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout rootContainer=new VerticalLayout();
		rootContainer.setSizeFull();
		setContent(rootContainer);
		NaviBar naviBar=new NaviBar();
		final CssLayout navigationBar=naviBar.getNaviBar(getUI());
		rootContainer.addComponent(navigationBar);
		viewDisplay=new Panel();
		viewDisplay.setSizeFull();
		rootContainer.addComponent(viewDisplay);
		rootContainer.setExpandRatio(viewDisplay, 1.0f);		
		
	}

	@Override
	public void showView(View view) {
		viewDisplay.setContent((Component) view);
		
	}

	
	
	
}
