package repository;

import java.util.HashMap;

public class BaseRepository<T> implements Repository<T> {

    private final HashMap<Integer, T> repo;

    public BaseRepository() {
        this.repo = new HashMap<>();
    }

    @Override
    public T getItemWithId(int id) {
        return this.repo.get(id);
    }

    @Override
    public void insertItem(int id, T item) {
        this.repo.put(id, item);
    }
}
