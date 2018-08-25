package hu.mik.services;

import java.util.List;

@FunctionalInterface
public interface PageableService<T> {
	public List<T> findAllPaged(int offset, int pageSize);
}
