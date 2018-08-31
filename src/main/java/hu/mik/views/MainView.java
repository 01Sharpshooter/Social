package hu.mik.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Panel;

import hu.mik.components.NewsFeedComponent;

@SuppressWarnings("serial")
@SpringView(name = MainView.NAME)
public class MainView extends Panel implements View {
	public static final String NAME = "home";

	@Override
	public void enter(ViewChangeEvent event) {
		this.setSizeFull();
		this.setContent(new NewsFeedComponent());
	}

}
