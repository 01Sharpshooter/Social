package hu.mik.components;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import hu.mik.services.PageableService;

@SuppressWarnings("serial")
@SpringComponent
@Scope("prototype")
public class PagingLayout<T> extends VerticalLayout {
	private int pageSize = 10;
	private int offset = 0;
	@Autowired
	protected PageableService<T> pageableService;
	@Autowired
	private ApplicationContext appCtx;
	@Autowired
	private BeanToComponentConverter<T> lazyComponent;

	@Autowired
	public PagingLayout() {
		this.setMargin(false);
	}

	@SuppressWarnings("unchecked")
	public void loadNextPage() {
		List<T> pagedResultList = this.pageableService.findAllPaged(this.offset, this.pageSize);
		if (!pagedResultList.isEmpty()) {
			pagedResultList.forEach(object -> this
					.addComponent((Component) this.appCtx.getBean(this.lazyComponent.getClass()).convert(object)));
			this.offset += this.pageSize;
		}
	}

	@SuppressWarnings("unchecked")
	public void addNewComponent(T object, int i) {
		this.addComponent((Component) this.appCtx.getBean(this.lazyComponent.getClass()).convert(object), 0);
	}
}
