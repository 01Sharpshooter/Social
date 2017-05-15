package hu.mik.dao;

import java.util.List;

import hu.mik.beans.News;
import hu.mik.beans.User;

public interface NewsDao {
	public List<News> lastGivenNewsAll(int number);

	public List<News> lastGivenNewsUser(int number, User user);
	
	public void saveNews(News news);

}
