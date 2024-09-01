package ec.edu.espe.main.service;

import ec.edu.espe.main.exceptions.ClientException;
import ec.edu.espe.main.model.Genre;
import ec.edu.espe.main.repository.GenreRepository;
import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class GenreService {
    private final GenreRepository repository;

    public GenreService(GenreRepository repository) {
        this.repository = repository;
    }

    public void save(
            @NotNull @NotBlank String name
    ) {
        repository.save(Genre.builder()
                .name(name)
                .build()
        );
    }

    public void update(
            @NotNull Long id,
            String name
    ) {
        var genreOptional = repository.findById(id);
        if (genreOptional.isEmpty()) {
            throw ClientException.status(HttpStatus.NOT_FOUND).error("Genre not found");
        }
        var genre = genreOptional.get();
        genre.setName(name);
        repository.save(genre);
    }

    public void delete(
            @NotNull Long id
    ) {
        repository.deleteById(id);
    }

    public Genre findById(
            @NotNull Long id
    ) {
        var genreOptional = repository.findById(id);
        if (genreOptional.isEmpty()) {
            throw ClientException.status(HttpStatus.NOT_FOUND).error("Genre not found");
        }
        return genreOptional.get();
    }

    public Iterable<Genre> findAll() {
        return repository.findAll();
    }
}
