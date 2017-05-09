package hu.mik.navigation;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.views.MainView;
import hu.mik.views.MessagesView;

public class NaviBar {
	@Autowired
	private UI usedUI;
	
	public CssLayout getNaviBar(UI ui){
		usedUI=ui;
		CssLayout naviBar=new CssLayout();
		naviBar.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		naviBar.addComponent(createNavigationButton("Main", MainView.NAME));
		naviBar.addComponent(createNavigationButton("Messages", MessagesView.NAME));
		Button logoutButton=new Button("Logout");
		logoutButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				usedUI.getPage().setLocation("/login");
				VaadinService.getCurrentRequest().getWrappedSession().invalidate();			
				
			}
		});		
		logoutButton.setStyleName(ValoTheme.BUTTON_SMALL);
		naviBar.addComponent(logoutButton);
		return naviBar;
	}
	
	private Button createNavigationButton(String caption, final String ViewName){
		Button button=new Button(caption);
		button.addStyleName(ValoTheme.BUTTON_SMALL);
		button.addClickListener(event -> usedUI.getNavigator().navigateTo(ViewName));
		return button;
	}

}
