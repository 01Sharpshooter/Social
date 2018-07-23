package hu.mik.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.addons.scrollablepanel.ScrollablePanel;

import com.vaadin.spring.annotation.SpringComponent;

@SuppressWarnings("serial")
@SpringComponent
@Scope("prototype")
public class PagingPanel<T> extends ScrollablePanel {
	private int scrollValueToRefresh = 500;
	private PagingLayout<T> content;

	@Autowired
	public PagingPanel(PagingLayout<T> content) {
		super(content);
		this.setContent(content);
		this.content = content;
		this.setSizeFull();
		this.addScrollListener(this::scrollListener);
		this.content.loadNextPage();
	}

	private void scrollListener(ScrollEvent e) {
		if (e.getBottom() < this.scrollValueToRefresh) {
			this.content.loadNextPage();
		}
	}

	@Override
	public PagingLayout<T> getContent() {
		return this.content;
	}

	public void addNewObject(T sentNews, int i) {
		this.content.addNewComponent(sentNews, i);

	}

}
