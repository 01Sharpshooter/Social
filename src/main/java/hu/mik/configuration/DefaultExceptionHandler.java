package hu.mik.configuration;

import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import hu.mik.enums.Texts;

@SuppressWarnings("serial")
public class DefaultExceptionHandler extends DefaultErrorHandler {

	private UI ui;

	public DefaultExceptionHandler(UI ui) {
		super();
		this.ui = ui;
	}

	@Override
	public void error(ErrorEvent event) {
		StringBuilder stackTrace = new StringBuilder();
		for (int i = 0; i < event.getThrowable().getCause().getStackTrace().length; i++) {
			stackTrace.append(event.getThrowable().getCause().getStackTrace()[i].toString() + System.lineSeparator());
		}
		TextArea area = new TextArea("Error message: ", stackTrace.toString());
		area.setWordWrap(false);
		area.setSizeFull();
		area.setReadOnly(true);
		Window errorWindow = new Window(Texts.DEFAULT_ERROR_MESSAGE.getText(), area);
		errorWindow.setWidth("80%");
		errorWindow.setHeight("80%");
		errorWindow.setModal(true);
		errorWindow.center();
		this.ui.addWindow(errorWindow);
	}
}
