package ec.edu.espe.main.repository;

public interface MemoryRepository<T, ID> {
    void save(T entity);

    void update(T entity);

    void delete(T entity);

    void deleteById(ID id);

    T findById(ID id);

    Iterable<T> findAll();
}
