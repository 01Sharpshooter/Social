package hu.mik.views;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;

import hu.mik.beans.User;
import hu.mik.services.UserService;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name=AdminView.NAME)
public class AdminView extends VerticalLayout implements View{
	public static final String NAME="admin";

	@Autowired
	private UserService userService;
	
	private ListDataProvider<User> baseDataProvider;
	
//	private ListDataProvider<User> nameDataProvider;
	
	Grid<User> grid;
	
	@SuppressWarnings("unchecked")
	@Override
	public void enter(ViewChangeEvent event) {
		HorizontalLayout filtersLayout=new HorizontalLayout();
		VerticalLayout nameSearchLayout=new VerticalLayout();
		nameSearchLayout.setMargin(false);
		baseDataProvider=new ListDataProvider<>(userService.listAll());
		TextField nameTf=new TextField("Search by username:");
		nameSearchLayout.addComponent(nameTf);
		Label notFoundLbl=new Label("No user found :(");
		notFoundLbl.setVisible(false);
		Button nameBtn=new Button("Search");
		nameBtn.addClickListener(clickEvent->{
				List<User> usersWithName=userService.findAllLike(nameTf.getValue());
				if(usersWithName!=null){
					grid.setDataProvider(new ListDataProvider<>(usersWithName));
					notFoundLbl.setVisible(false);
				}else{
					notFoundLbl.setVisible(true);
				}
			});
		nameSearchLayout.addComponent(nameBtn);
		nameSearchLayout.addComponent(notFoundLbl);
		RadioButtonGroup<String> radioButtonGroup=new RadioButtonGroup<>("Filter");
		radioButtonGroup.setItems("All", "Enabled", "Disabled");
		radioButtonGroup.setSelectedItem("All");
		radioButtonGroup.addSelectionListener(new SingleSelectionListener<String>() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void selectionChange(SingleSelectionEvent<String> event) {
				if(event.getSource().getValue().equals("Enabled")){
					((ListDataProvider<User>)grid.getDataProvider()).clearFilters();
					((ListDataProvider<User>)grid.getDataProvider()).addFilterByValue(User::getEnabled, 1);
				}else if(event.getSource().getValue().equals("Disabled")){
					((ListDataProvider<User>)grid.getDataProvider()).clearFilters();
					((ListDataProvider<User>)grid.getDataProvider()).addFilterByValue(User::getEnabled, 0);
				}else{
					((ListDataProvider<User>)grid.getDataProvider()).clearFilters();
				}
				
			}
		});
		Button resetButton=new Button("Reset");
		resetButton.addClickListener(clickEvent->{
			notFoundLbl.setVisible(false);
			radioButtonGroup.setSelectedItem("All");
			nameTf.clear();
			grid.setDataProvider(baseDataProvider);
			baseDataProvider.clearFilters();
		});
		nameSearchLayout.addComponent(resetButton);
		filtersLayout.addComponent(nameSearchLayout);
		filtersLayout.addComponent(radioButtonGroup);
		grid=new Grid<User>(baseDataProvider);
		grid.addColumn(User::getId).setCaption("Id");
		grid.addColumn(User::getUsername).setCaption("Username");
		grid.addColumn(user -> {
			if(user.getEnabled()==0){
				return "Enable";
			}
			else{
				return "Disable";
			}
		}, new ButtonRenderer<>(clickEvent->{
			User user=clickEvent.getItem();
			if(user.getEnabled()==1){
				user.setEnabled(0);
			}else{
				user.setEnabled(1);
			}
			userService.saveChanges(user);
			((ListDataProvider<User>)grid.getDataProvider()).refreshAll();
			if(!grid.getDataProvider().equals(baseDataProvider)){
				baseDataProvider.refreshItem(user);
			}
		}))
		.setCaption("Enable/Disable users");
		grid.setSizeFull();
		addComponent(filtersLayout);
		addComponent(grid);
	}
}
