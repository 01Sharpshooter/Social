package hu.mik.components;

import java.util.List;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.News;
import hu.mik.beans.User;
import hu.mik.enums.ScrollDirection;
import hu.mik.services.NewsService;
import hu.mik.utils.UserUtils;
import lombok.Getter;

@SuppressWarnings("serial")
public class NewsPanelScrollable extends AbstractScrollablePanel {
	private LdapGroup ldapGroup;
	private User user;

	private VerticalLayout content;
	private NewsService newsService;
	private UserUtils userUtils;
	@Getter
	private boolean immediateFetch;

	public NewsPanelScrollable(UserUtils userUtils, NewsService newsService, boolean immediateFetch) {
		super(ScrollDirection.DOWN);
		this.newsService = newsService;
		this.immediateFetch = immediateFetch;
		this.content = new VerticalLayout();
		this.content.setMargin(false);
		this.setSizeFull();
		this.setContent(this.content);
		this.userUtils = userUtils;
	}

	public NewsPanelScrollable init() {
		if (this.immediateFetch) {
			this.refresh();
		}
		return this;
	}

	public NewsPanelScrollable init(LdapGroup ldapGroup) {
		this.ldapGroup = ldapGroup;
		return this.init();
	}

	public NewsPanelScrollable init(User user) {
		this.user = user;
		return this.init();
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

		if (pagedResultList != null && !pagedResultList.isEmpty()) {
			this.offset = pagedResultList.get(pagedResultList.size() - 1).getId();
		} else {
			this.content.addComponent(new Label("No news here :("));
		}

		this.addConvertedComponents(pagedResultList);
	}

	private void addConvertedComponents(List<News> pagedResultList) {
		if (!pagedResultList.isEmpty()) {
			pagedResultList.forEach(object -> {
				this.content.addComponent(new NewsComponent(object,
						this.userUtils.getLoggedInUser().getDbUser().getId().equals(object.getUser().getId()),
						this.newsService));
			});
		}
	}

	public void changeLdapGroup(LdapGroup ldapGroup) {
		this.ldapGroup = ldapGroup;
		this.refresh();

	}

	public void refresh() {
		this.content.removeAllComponents();
		this.offset = this.newsService.getMaxNewsId() + 1;
		this.loadNextPage();
		this.scrollToTop();
	}

	public void addNews(News news) {
		if (this.offset == 1) {
			this.content.removeAllComponents();
		}
		this.content.addComponent(new NewsComponent(news, true, this.newsService), 0);
	}

}
