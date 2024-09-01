package ec.edu.espe.main.service;

import ec.edu.espe.main.exceptions.ClientException;
import ec.edu.espe.main.model.Track;
import ec.edu.espe.main.repository.TrackRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
@Service
public class TrackService {

    private final TrackRepository repository;

    public TrackService(TrackRepository repository) {
        this.repository = repository;
    }

    public Iterable<Track> findAll() {
        return repository.findAll();
    }

    public Track findById(@NotNull Long id) {
        return repository.findById(id).orElseThrow(() -> ClientException.status(HttpStatus.NOT_FOUND).error("Track not found"));
    }

    public Track findByFileIdentifier(@NotNull String fileIdentifier) {
        try {
            return repository.findByFileIdentifier(fileIdentifier).orElseThrow(() -> ClientException.status(HttpStatus.NOT_FOUND).error("Track not found"));
        } catch (IllegalArgumentException e) {
            throw ClientException.error("Invalid file identifier");
        }
    }

}
