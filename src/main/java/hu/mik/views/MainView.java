package hu.mik.views;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Panel;

import hu.mik.components.NewsFeedComponent;

@SuppressWarnings("serial")
@SpringView(name = MainView.NAME)
public class MainView extends Panel implements View {
	public static final String NAME = "home";
	@Autowired
	private NewsFeedComponent newsFeedComponent;

	@Override
	public void enter(ViewChangeEvent event) {
		this.setSizeFull();
		this.newsFeedComponent.firstLoad(null);
		this.setContent(this.newsFeedComponent);
	}

}
