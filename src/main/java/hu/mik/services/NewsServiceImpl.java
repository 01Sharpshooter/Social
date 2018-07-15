package hu.mik.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.mik.beans.News;
import hu.mik.beans.User;
import hu.mik.dao.NewsDao;

@Service
public class NewsServiceImpl implements NewsService {
	@Autowired
	NewsDao newsDao;

	@Override
	public List<News> lastGivenNewsAll(int number) {
		return this.newsDao.lastGivenNewsAll(number);
	}

	@Override
	public List<News> lastGivenNewsUser(int number, User user) {
		return this.newsDao.lastGivenNewsUser(number, user);
	}

	@Override
	public void saveNews(News news) {
		this.newsDao.saveNews(news);

	}

}
