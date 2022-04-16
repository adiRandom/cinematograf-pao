package repository;

import services.SerializationService;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BaseRepository<T extends Serializable> implements Repository<T> {

    private final HashMap<Integer, T> repo;
    private final String repoName;


    public void saveToDisk() {
        SerializationService.writeObject(repo, repoName + ".txt");
    }


    public BaseRepository(String repoName) {
        this.repoName = repoName;
        HashMap<Integer, T> _repo;

        //Try to load repo from disk
        try {
            _repo = (HashMap<Integer, T>) SerializationService.readObject(repoName + ".txt");
        } catch (Exception e) {
            _repo = new HashMap<>();
        }

        this.repo = _repo;
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

    @Override
    public void deleteItem(int id) {
        this.repo.remove(id);
    }
}
