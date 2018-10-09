package hu.mik.views;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name = DefaultView.NAME)
public class DefaultView implements View {
	public static final String NAME = "";
}
