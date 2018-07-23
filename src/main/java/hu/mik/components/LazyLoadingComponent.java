package hu.mik.components;

public interface LazyLoadingComponent<T> {
	public LazyLoadingComponent<T> construct(T object);
}
