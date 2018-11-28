package hu.mik.components;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

import hu.mik.constants.ThemeConstants;
import hu.mik.enums.Texts;

@SuppressWarnings("serial")
public class ConfirmDialog extends Window {
	private boolean confirmed = false;

	public ConfirmDialog(String message) {
		this.setModal(true);
		this.setResizable(false);
		this.setClosable(false);
		this.setCaption(message);
		this.setStyleName("confirmDialog");
		this.setResponsive(true);
		this.createContent();
	}

	private void createContent() {
		this.setContent(this.createButtonLayout());
	}

	private HorizontalLayout createButtonLayout() {
		Button btnConfirm = new Button(Texts.BTN_CONFIRM.getText(), e -> {
			this.confirmed = true;
			this.close();
		});
		btnConfirm.setIcon(VaadinIcons.CHECK);
		btnConfirm.addStyleName(ThemeConstants.BLUE_BUTTON);
		Button btnCancel = new Button(Texts.BTN_CANCEL.getText(), e -> this.close());
		btnCancel.setIcon(VaadinIcons.CLOSE);
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
