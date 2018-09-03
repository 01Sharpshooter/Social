package hu.mik.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import hu.mik.enums.Texts;

@SuppressWarnings("serial")
public class ConfirmDialog extends Window {
	private String message;
	private boolean confirmed = false;

	public ConfirmDialog(String message) {
		this.setModal(true);
		this.setWidth("40%");
		this.setHeight("40%");
		this.setResizable(false);
		this.message = message;
		this.createContent();
	}

	private void createContent() {
		VerticalLayout content = new VerticalLayout();
		Label message = new Label(this.message);
		content.addComponent(message);
		content.addComponent(this.createButtonLayout());
		content.setExpandRatio(message, 1f);
		this.setContent(content);
	}

	private HorizontalLayout createButtonLayout() {
		Button btnConfirm = new Button(Texts.BTN_CONFIRM.getText(), e -> {
			this.confirmed = true;
			this.close();
		});
		Button btnCancel = new Button(Texts.BTN_CANCEL.getText(), e -> this.close());
		HorizontalLayout layoutBtn = new HorizontalLayout(btnConfirm, btnCancel);
		layoutBtn.setWidth("100%");
		layoutBtn.setExpandRatio(btnConfirm, 1f);
		layoutBtn.setComponentAlignment(btnConfirm, Alignment.MIDDLE_RIGHT);
		return layoutBtn;
	}

	public boolean isConfirmed() {
		return this.confirmed;
	}

}
