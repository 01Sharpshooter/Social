package hu.mik.services;

import java.util.List;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.News;
import hu.mik.beans.User;

public interface NewsService extends PageableService<News> {

	public List<News> getPagedNewsByLdapGroup(int offset, int pageSize, LdapGroup ldapGroup);

	public List<News> getPagedNewsOfUser(int offset, int pageSize, User user);

	public void saveNews(News news);

	public void deleteNews(News news);
}
