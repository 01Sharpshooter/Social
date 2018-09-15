package hu.mik.views;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.LdapUser;
import hu.mik.beans.User;
import hu.mik.constants.LdapConstants;
import hu.mik.constants.SystemConstants;
import hu.mik.services.LdapService;
import hu.mik.services.UserService;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name = AdminView.NAME)
public class AdminView extends VerticalLayout implements View {
	public static final String NAME = "admin";

	@Autowired
	private UserService userService;
	@Autowired
	private LdapService ldapService;

	private Grid<User> grid;

	private RadioButtonGroup<String> radioButtonGroup;

	@SuppressWarnings("unchecked")
	@Override
	public void enter(ViewChangeEvent event) {
		String username = (String) VaadinService.getCurrentRequest().getWrappedSession()
				.getAttribute(SystemConstants.SESSION_ATTRIBUTE_LDAP_USER);
		LdapUser ldapUser = this.ldapService.findUserByUsername(username);
		LdapGroup group = this.ldapService.findGroupByGroupName(LdapConstants.GROUP_ADMIN_NAME);
		if (!group.getListOfMembers().contains(ldapUser.getId())) {
			this.getUI().getNavigator().navigateTo(MainView.NAME);
		}
		HorizontalLayout filtersLayout = new HorizontalLayout();
		VerticalLayout nameSearchLayout = new VerticalLayout();
		nameSearchLayout.setMargin(false);
		TextField nameTf = new TextField("Search by username:");
		nameSearchLayout.addComponent(nameTf);
		Label notFoundLbl = new Label("No user found :(");
		notFoundLbl.setVisible(false);
		Button nameBtn = new Button("Search");
		nameBtn.addClickListener(clickEvent -> {
			List<User> usersWithName = this.userService.findByFullNameContaining(nameTf.getValue());
			if (usersWithName != null) {
				this.grid.setDataProvider(new ListDataProvider<>(usersWithName));
				notFoundLbl.setVisible(false);
			} else {
				notFoundLbl.setVisible(true);
			}
			this.radioButtonGroup.setSelectedItem("All");
		});
		nameSearchLayout.addComponent(nameBtn);
		nameSearchLayout.addComponent(notFoundLbl);
		this.radioButtonGroup = new RadioButtonGroup<>("Filter");
		this.radioButtonGroup.setItems("All", "Enabled", "Disabled");
		this.radioButtonGroup.setSelectedItem("All");
		this.radioButtonGroup.addSelectionListener(event1 -> {
			if (event1.getSource().getValue().equals("Enabled")) {
				((ListDataProvider<User>) AdminView.this.grid.getDataProvider()).clearFilters();
//					((ListDataProvider<User>)grid.getDataProvider()).addFilterByValue(User::getEnabled, 1);
			} else if (event1.getSource().getValue().equals("Disabled")) {
				((ListDataProvider<User>) AdminView.this.grid.getDataProvider()).clearFilters();
//					((ListDataProvider<User>)grid.getDataProvider()).addFilterByValue(User::getEnabled, 0);
			} else {
				((ListDataProvider<User>) AdminView.this.grid.getDataProvider()).clearFilters();
			}

		});
		Button resetButton = new Button("Reset");
		resetButton.addClickListener(clickEvent -> {
			notFoundLbl.setVisible(false);
			this.radioButtonGroup.setSelectedItem("All");
			nameTf.clear();
			this.grid.setDataProvider(new ListDataProvider<>(this.userService.listAll()));
		});
		nameSearchLayout.addComponent(resetButton);
		filtersLayout.addComponent(nameSearchLayout);
		filtersLayout.addComponent(this.radioButtonGroup);
		this.grid = new Grid<User>(new ListDataProvider<>(this.userService.listAll()));
		this.grid.addColumn(User::getId).setCaption("Id");
		this.grid.addColumn(User::getUsername).setCaption("Username");
//		grid.addColumn(user -> {
//			if(user.getEnabled()==0){
//				return "Enable";
//			}
//			else{
//				return "Disable";
//			}
//		}, new ButtonRenderer<>(clickEvent->{
//			User user=clickEvent.getItem();
//			if(user.getEnabled()==1){
//				user.setEnabled(0);
//			}else{
//				user.setEnabled(1);
//			}
//			userService.saveChanges(user);
//			String selected=radioButtonGroup.getSelectedItem().get();
//			radioButtonGroup.setSelectedItem("All");
//			radioButtonGroup.setSelectedItem(selected);
//			((ListDataProvider<User>)grid.getDataProvider()).refreshItem(user);
//		}))
//		.setCaption("Enable/Disable users");
		this.grid.setSizeFull();
		this.addComponent(filtersLayout);
		this.addComponent(this.grid);
	}
}
