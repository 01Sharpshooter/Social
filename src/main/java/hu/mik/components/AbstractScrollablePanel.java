package hu.mik.components;

import org.springframework.context.ApplicationContext;
import org.vaadin.addons.scrollablepanel.ScrollablePanel;

import hu.mik.utils.ApplicationContextHolder;

@SuppressWarnings("serial")
public abstract class AbstractScrollablePanel extends ScrollablePanel {
	protected int scrollValueToRefresh = 500;
	protected int pageSize = 10;
	protected int offset = 0;

	protected ApplicationContext appCtx;

	public AbstractScrollablePanel() {
		this.appCtx = ApplicationContextHolder.getApplicationContext();
		this.addScrollListener(this::scrollListener);
		this.setSizeFull();
	}

	private void scrollListener(ScrollEvent e) {
		if (e.getBottom() < this.scrollValueToRefresh) {
			this.loadNextPage();
		}
	}

	protected abstract void loadNextPage();
}
