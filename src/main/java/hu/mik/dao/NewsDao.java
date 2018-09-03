package hu.mik.dao;

import java.util.List;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.News;
import hu.mik.beans.User;

public interface NewsDao {
	public List<News> getPagedNews(int offset, int pageSize);

	public List<News> getPagedNewsByLdapGroup(int offset, int pageSize, LdapGroup ldapGroup);

	public List<News> getPagedNewsOfUser(int offset, int pageSize, User user);

	public void saveNews(News news);

	public void deleteNews(News news);

}
