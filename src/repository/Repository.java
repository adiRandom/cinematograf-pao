package repository;

import java.util.HashMap;

public interface Repository<T> {
    public T getItemWithId(int id);
    public void insertItem(int id, T item);
}
