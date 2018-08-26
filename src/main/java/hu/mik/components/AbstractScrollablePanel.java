package hu.mik.components;

import org.springframework.context.ApplicationContext;
import org.vaadin.addons.scrollablepanel.ScrollablePanel;

import hu.mik.enums.ScrollDirection;
import hu.mik.utils.ApplicationContextHolder;

@SuppressWarnings("serial")
public abstract class AbstractScrollablePanel extends ScrollablePanel {
	protected int scrollValueToRefresh = 400;
	protected int pageSize = 10;
	protected int offset = 0;
	private int scrollToBottom = 1000000;
	private int scrollToTop = -1000000;

	private ScrollDirection scrollDirection;
	protected ApplicationContext appCtx;

	public AbstractScrollablePanel(ScrollDirection scrollDirection) {
		this.scrollDirection = scrollDirection;
		this.appCtx = ApplicationContextHolder.getApplicationContext();
		this.addScrollListener(this::scrollListener);
		this.setSizeFull();
	}

	private void scrollListener(ScrollEvent e) {
		switch (this.scrollDirection) {
		case DOWN:
			if (e.getBottom() < this.scrollValueToRefresh) {
				this.loadNextPage();
			}
			break;
		case UP:
			if (e.getTop() < this.scrollValueToRefresh) {
				this.loadNextPage();
			}
			break;
		}
	}

	public void scrollToTop() {
		this.setScrollTop(this.scrollToTop);
		this.scrollToTop--;
	}

	public void scrollToBottom() {
		this.setScrollTop(this.scrollToBottom);
		this.scrollToBottom++;
	}

	protected abstract void loadNextPage();
}
