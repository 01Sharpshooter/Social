package hu.mik.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@ViewScope
@SpringView(name=RequestsView.NAME)
public class RequestsView extends VerticalLayout implements View{
	public static final String NAME="RequestsView";
	private Panel panel=new Panel();
	private HorizontalLayout row=new HorizontalLayout();
	private VerticalLayout rows=new VerticalLayout();
	private HorizontalLayout userDiv=new HorizontalLayout();

	@Override
	public void enter(ViewChangeEvent event) {
		fill();
		
	}

	private void fill() {
		panel.setContent(rows);
		
	}

}
