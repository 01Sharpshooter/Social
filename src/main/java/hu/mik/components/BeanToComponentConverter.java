package hu.mik.components;

public interface BeanToComponentConverter<T> {
	public BeanToComponentConverter<T> convert(T object);
}
