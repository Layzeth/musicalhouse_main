package ec.edu.espe.main.service;

import ec.edu.espe.main.exceptions.ClientException;
import ec.edu.espe.main.model.Artist;
import ec.edu.espe.main.repository.ArtistRepository;
import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Date;


@Service
@Validated
public class ArtistService {

    private final ArtistRepository repository;

    public ArtistService(ArtistRepository repository) {
        this.repository = repository;
    }

    public void save(
            @NotNull @NotEmpty String name,
            @NotNull @NotEmpty String country,
            @NotNull LocalDate birthday) {
        repository.save(Artist.builder()
                .name(name)
                .country(country)
                .birthday(birthday)
                .build()
        );
    }

    public void update(
            @NotNull Long id,
            String name,
            String country
    ) {
        var artistOptional = repository.findById(id);
        if (artistOptional.isEmpty()) {
            throw ClientException.status(HttpStatus.NOT_FOUND).error("Artist not found");
        }
        var artist = artistOptional.get();
        artist.setName(name);
        artist.setCountry(country);
        repository.save(artist);
    }

    public void delete(
            @NotNull Long id
    ) {
        repository.deleteById(id);
    }

    public Artist findById(
            @NotNull Long id
    ) {
        var artistOptional = repository.findById(id);
        if (artistOptional.isEmpty()) {
            throw ClientException.status(HttpStatus.NOT_FOUND).error("Artist not found");
        }
        return artistOptional.get();
    }

    public Iterable<Artist> findAll() {
        return repository.findAll();
    }


}
