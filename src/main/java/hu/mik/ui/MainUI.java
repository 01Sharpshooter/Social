package hu.mik.ui;



import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;


import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.FontIcon;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.navigation.NaviBar;
import hu.mik.navigation.SideMenu;


@SpringUI(path="/main")
@SpringViewDisplay
@Theme(ThemeConstants.UI_THEME)
public class MainUI extends UI implements ViewDisplay{
	
	private static List<User> onlineUsers=new CopyOnWriteArrayList<>();
	private Panel viewDisplay;
	private WrappedSession session=VaadinService.getCurrentRequest().getWrappedSession();
	
	@Override
	protected void init(VaadinRequest request){
		if(session.getAttribute("User")!=null){			
			User user=(User)session.getAttribute("User");	
			final HorizontalLayout base=new HorizontalLayout();
			final VerticalLayout sideMenu=new SideMenu(user, this).getSideMenu();
			final VerticalLayout workingSpace=new VerticalLayout();
			final HorizontalLayout upperMenu=new HorizontalLayout();	
			VaadinService.getCurrentRequest().getWrappedSession().setAttribute("UI", this);
			sideMenu.setSpacing(true);
			sideMenu.setSizeFull();
			workingSpace.setSizeFull();
			base.setSizeFull();
			NaviBar naviBar=new NaviBar();
			final CssLayout navigationBar=naviBar.getNaviBar(getUI());
			upperMenu.addComponent(navigationBar);
			viewDisplay=new Panel();
			viewDisplay.setSizeFull();
			workingSpace.addComponent(upperMenu);
			workingSpace.addComponent(viewDisplay);
			workingSpace.setExpandRatio(upperMenu, 1);
			workingSpace.setExpandRatio(viewDisplay, 9);
			base.addComponent(sideMenu);
			base.addComponent(workingSpace);
			base.setExpandRatio(sideMenu, 15);
			base.setExpandRatio(workingSpace, 85);
			setContent(base);
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

	public static List<User> getOnlineUsers() {
		return onlineUsers;
	}
	
	
	
	
	
}
