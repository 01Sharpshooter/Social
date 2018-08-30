package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.News;
import hu.mik.beans.User;
import hu.mik.dao.NewsDao;

@Service
public class NewsServiceImpl implements NewsService {
	@Autowired
	NewsDao newsDao;

	@Override
	public List<News> getPagedNewsOfUser(int offset, int pageSize, User user) {
		return this.newsDao.getPagedNewsOfUser(offset, pageSize, user);
	}

	@Override
	public void saveNews(News news) {
		this.newsDao.saveNews(news);

	}

	@Override
	public List<News> findAllPaged(int offset, int pageSize) {
		return this.newsDao.getPagedNews(offset, pageSize);
	}

	@Override
	public List<News> getPagedNewsByLdapGroup(int offset, int pageSize, LdapGroup ldapGroup) {
		return this.newsDao.getPagedNewsByLdapGroup(offset, pageSize, ldapGroup);
	}

}
