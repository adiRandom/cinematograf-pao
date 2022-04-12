package repository;

import java.util.List;
import java.util.function.Predicate;

public interface Repository<T> {
    public T getItemWithId(int id);
    public void insertItem(int id, T item);
    public T where(Predicate<T> predicate);
    public List<T> whereAll(Predicate<T> predicate);
}
