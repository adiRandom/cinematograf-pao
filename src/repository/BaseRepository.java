package repository;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    @Override
    public T where(Predicate<T> predicate) {

        Optional<Map.Entry<Integer, T>> optionalResult = repo.entrySet()
                .stream()
                .filter(entry -> predicate.test(entry.getValue()))
                .findFirst();
        return optionalResult.map(Map.Entry::getValue).orElse(null);
    }

    @Override
    public List<T> whereAll(Predicate<T> predicate) {
        return repo.entrySet()
                .stream()
                .filter(entry -> predicate.test(entry.getValue())).map(Map.Entry::getValue).collect(Collectors.toList());

    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(this.repo.values());
    }
}
