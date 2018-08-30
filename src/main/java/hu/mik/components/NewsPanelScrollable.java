package hu.mik.components;

import java.util.List;

import com.vaadin.ui.VerticalLayout;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.News;
import hu.mik.beans.User;
import hu.mik.enums.ScrollDirection;
import hu.mik.services.NewsService;

@SuppressWarnings("serial")
public class NewsPanelScrollable extends AbstractScrollablePanel {
	private LdapGroup ldapGroup;
	private User user;

	private NewsService newsService;

	private VerticalLayout content;

	public NewsPanelScrollable(LdapGroup ldapGroup) {
		super(ScrollDirection.DOWN);
		this.ldapGroup = ldapGroup;

		this.content = new VerticalLayout();
		this.content.setSizeFull();
		this.content.setMargin(false);
		this.setContent(this.content);

		this.newsService = this.appCtx.getBean(NewsService.class);
	}

	@Override
	protected void loadNextPage() {
		List<News> pagedResultList;
		if (this.user != null) {
			pagedResultList = this.newsService.getPagedNewsOfUser(this.offset, this.pageSize, this.user);
		} else if (this.ldapGroup != null) {
			pagedResultList = this.newsService.getPagedNewsByLdapGroup(this.offset, this.pageSize, this.ldapGroup);
		} else {
			pagedResultList = this.newsService.findAllPaged(this.offset, this.pageSize);
		}

		this.addConvertedComponents(pagedResultList);
	}

	private void addConvertedComponents(List<News> pagedResultList) {
		if (!pagedResultList.isEmpty()) {
			pagedResultList.forEach(object -> this.content.addComponent(NewsComponentConverter.convert(object)));
			this.offset += this.pageSize;
		}
	}

	public void changeLdapGroup(LdapGroup ldapGroup) {
		this.content.removeAllComponents();
		this.offset = 0;
		this.ldapGroup = ldapGroup;
		this.loadNextPage();
		this.scrollToTop();
	}

	public void addNews(News news) {
		this.content.addComponent(NewsComponentConverter.convert(news), 0);
	}

	public void firstLoad(User user) {
		this.user = user;
		this.loadNextPage();
	}

}
