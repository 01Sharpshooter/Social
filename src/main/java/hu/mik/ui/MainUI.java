package hu.mik.ui;


import com.vaadin.annotations.Theme;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


import hu.mik.constants.ThemeConstants;
import hu.mik.navigation.NaviBar;


@Theme(ThemeConstants.UI_THEME)
@SpringUI(path="/main")
@SpringViewDisplay
public class MainUI extends UI implements ViewDisplay{

	private Panel viewDisplay;
	private WrappedSession session=VaadinService.getCurrentRequest().getWrappedSession();
	
	@Override
	protected void init(VaadinRequest request){
		if(session.getAttribute("Username")!=null){
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
		else{
			getPage().setLocation("/login");
		}
	}

	@Override
	public void showView(View view) {
		if(viewDisplay!=null){
			viewDisplay.setContent((Component) view);
		}
		
	}

	
	
	
}
