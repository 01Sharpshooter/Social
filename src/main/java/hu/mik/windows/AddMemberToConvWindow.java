package hu.mik.windows;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.enums.Texts;

@SuppressWarnings("serial")
@Theme(ThemeConstants.UI_THEME)
public class AddMemberToConvWindow extends Window {
	private VerticalLayout content;
	private List<User> choosableUsers;
	private List<User> chosenUsers;
	private ComboBox<User> cbUsers;
	private SaveAction saveAction;
	private CssLayout memberLayout;
	private TextArea textArea;
	private boolean validateTextArea;

	public AddMemberToConvWindow(List<User> choosableUsers, SaveAction saveAction, boolean validateTextArea) {
		super();
		this.saveAction = saveAction;
		this.choosableUsers = choosableUsers;
		this.validateTextArea = validateTextArea;
		this.setWidth("80%");
		this.setHeight("80%");
		this.setStyleName(ThemeConstants.ADD_MEMBER_TO_CONV_WINDOW);
		this.center();
		this.setModal(true);
		this.setResizable(false);
		this.setClosable(false);
		this.setCaption("Partners to add");
		this.chosenUsers = new ArrayList<>();
		this.createContent();
	}

	private void createContent() {
		this.content = new VerticalLayout();
		this.content.setSizeFull();
		this.createMemberLayout();
		this.createTextArea();
		this.createActionLayout();
		this.setContent(this.content);
	}

	private void createTextArea() {
		this.textArea = new TextArea();
		this.textArea.setCaption("Write some words to the people joining this conversation");
		this.textArea.setWidth("100%");
		this.textArea.setHeight("100px");
		this.textArea.setPlaceholder("Message to be sent...");
		if (this.validateTextArea) {
			this.textArea.setRequiredIndicatorVisible(true);
		}
		this.content.addComponent(this.textArea);

	}

	private void createMemberLayout() {
		this.memberLayout = new CssLayout();
		this.memberLayout.setStyleName(ThemeConstants.ADD_MEMBER_TO_CONV_WINDOW_MEMBER_LAYOUT);
		this.createComboBox();

		this.content.addComponent(this.memberLayout);
		this.content.setExpandRatio(this.memberLayout, 1);
	}

	private void createActionLayout() {
		HorizontalLayout actionLayout = new HorizontalLayout();
		Button btnSave = this.createSaveButton();
		Button btnCancel = this.createCancelButton();
		actionLayout.addComponents(btnSave, btnCancel);
		actionLayout.setWidth("100%");
		actionLayout.setExpandRatio(btnSave, 1f);
		actionLayout.setComponentAlignment(btnSave, Alignment.MIDDLE_RIGHT);
		actionLayout.setComponentAlignment(btnCancel, Alignment.MIDDLE_RIGHT);

		this.content.addComponent(actionLayout);

	}

	private Button createCancelButton() {
		Button btnCancel = new Button(Texts.BTN_CANCEL.getText());
		btnCancel.setIcon(VaadinIcons.CLOSE);
		btnCancel.addClickListener(e -> this.close());
		return btnCancel;
	}

	private Button createSaveButton() {
		Button btnSave = new Button(Texts.BTN_CONFIRM.getText());
		btnSave.setIcon(VaadinIcons.CHECK);
		btnSave.addStyleName(ThemeConstants.BLUE_BUTTON);
		btnSave.addClickListener(e -> this.save());
		return btnSave;

	}

	private void save() {
		if (this.validateTextArea && this.textArea.getValue().trim().isEmpty()) {
			this.textArea.setComponentError(new UserError("Write some words before confirming changes, please."));
			return;
		}
		this.saveAction.save(this.chosenUsers, this.textArea.getValue());
		this.close();
	}

	private void createComboBox() {
		this.cbUsers = new ComboBox<>();
		this.cbUsers.setItems(this.choosableUsers);
		this.cbUsers.setEmptySelectionAllowed(false);
		this.cbUsers.setItemIconGenerator(user -> this.getUserImageIcon(user));
		this.cbUsers.setItemCaptionGenerator(user -> user.getFullName());
		this.cbUsers.addSelectionListener(e -> this.userSelectionListener());
		this.cbUsers.setPlaceholder("User to add...");
		this.memberLayout.addComponent(this.cbUsers);
	}

	private Resource getUserImageIcon(User user) {
		File file = new File(UserConstants.getImageLocation() + user.getImageName());
		if (file.exists()) {
			return new FileResource(file);
		} else {
			return new ThemeResource(UserConstants.DEFAULT_PROFILE_PICTURE);
		}
	}

	private void userSelectionListener() {
		if (this.cbUsers.getValue() != null) {
			Button btnUser = this.createUserButton();
			this.memberLayout.addComponent(btnUser, this.memberLayout.getComponentIndex(this.cbUsers));
			this.chosenUsers.add(this.cbUsers.getValue());
			this.cbUsers.clear();
			this.refreshComboBoxItems();
		}

	}

	private Button createUserButton() {
		Button btnUser = new Button(this.cbUsers.getValue().getFullName());
		btnUser.setIcon(VaadinIcons.CLOSE);
		btnUser.setId(this.cbUsers.getValue().getId().toString());
		btnUser.addClickListener(btnE -> this.userButtonClick(btnE));
		return btnUser;
	}

	private void userButtonClick(ClickEvent e) {
		this.memberLayout.removeComponent(e.getButton());
		this.chosenUsers.removeIf(user -> user.getId().equals(Integer.valueOf(e.getButton().getId())));
		this.refreshComboBoxItems();
	}

	private void refreshComboBoxItems() {
		this.cbUsers.setItems(this.choosableUsers.stream().filter(user -> !this.chosenUsers.contains(user)));
	}

	@FunctionalInterface
	public interface SaveAction {
		public void save(List<User> users, String messageText);
	}

}
