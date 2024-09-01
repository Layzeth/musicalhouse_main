package ec.edu.espe.main.service;

import ec.edu.espe.main.dto.TrackMetadata;
import ec.edu.espe.main.exceptions.ClientException;
import ec.edu.espe.main.model.Release;
import ec.edu.espe.main.model.ReleaseType;
import ec.edu.espe.main.model.Track;
import ec.edu.espe.main.repository.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Log4j2
@Validated
@Service
public class ReleaseService {
    private final ReleaseRepository repository;
    private final ArtistRepository artistRepository;
    private final MicroTwoServiceSender sender;
    private final GenreRepository genreRepository;
    private final TrackRepository trackRepository;

    public ReleaseService(ReleaseRepository repository, ArtistRepository artistRepository, MicroTwoServiceSender sender, GenreRepository genreRepository, TrackRepository trackRepository) {
        this.repository = repository;
        this.artistRepository = artistRepository;
        this.sender = sender;
        this.genreRepository = genreRepository;
        this.trackRepository = trackRepository;
    }

    public void save(
            @NotNull @NotBlank String name,
            @NotNull ReleaseType type,
            @NotNull Long artistId,
            @NotNull @NotEmpty List<MultipartFile> tracksFiles,
            @NotNull @NotEmpty List<TrackMetadata> tracksMetadata
    ) {

        var artistOptional = artistRepository.findById(artistId);
        if (artistOptional.isEmpty()) {
            throw ClientException.error("Artist not found");
        }


        var artist = artistOptional.get();

        if (tracksFiles.size() != tracksMetadata.size()) {
            throw ClientException.error("The number of tracks and metadata must be the same");
        }

        for (int i = 0; i < tracksFiles.size(); i++) {

            var trackMetadata = tracksMetadata.get(i);
            var trackFile = tracksFiles.get(i);

            if (trackFile != null && trackFile.getOriginalFilename() != null && !trackFile.getOriginalFilename().endsWith(".mp3")) {
                log.error("Invalid file format, required .mp3, file: {}", trackFile.getOriginalFilename());
                throw ClientException.error("Invalid file format, required .mp3");
            }

            if (trackMetadata == null) {
                throw ClientException.error("Metadata is required");
            }

            trackMetadata.setFileId(UUID.randomUUID().toString());
        }


        var genreIds = tracksMetadata.stream().map(TrackMetadata::getGenreId).distinct().toList();
        log.info("Checking if genres exist: {}", genreIds);
        if (!genreIds.stream().allMatch(genreRepository::existsById)) {
            throw ClientException.error("Invalid genre id");
        }

        List<Long> artistIds = Stream.concat(
                tracksMetadata.stream().flatMap(trackMetadata -> trackMetadata.getContributorsId().stream()),
                Stream.of(artistId)
        ).distinct().toList();

        List<Long> collaborators = artistIds.stream().filter(id -> !id.equals(artistId)).toList();
        if (!collaborators.isEmpty()) {
            log.info("Checking if collaborators exist: {}", collaborators);
            if (!collaborators.stream().allMatch(artistRepository::existsById)) {
                throw ClientException.error("Invalid collaborator id");
            }
        }

        log.info("Checking if artists exist: {}", artistIds);
        if (!artistIds.stream().allMatch(artistRepository::existsById)) {
            throw ClientException.error("Invalid artist id");
        }

        if (type == ReleaseType.SINGLE && tracksFiles.size() > 1) {
            throw ClientException.error("A single release must have only one track");
        }

        var body = new LinkedMultiValueMap<String, Object>();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        tracksFiles.forEach(file -> body.add("tracks", getTestFile(file)));
        body.add("metadatas", tracksMetadata);
        sender.post("/tracksInformation", body, headers);

        var tracks = new ArrayList<Track>();

        for (var metadata : tracksMetadata) {
            var genre = genreRepository.findById(metadata.getGenreId()).orElseThrow(() -> ClientException.status(HttpStatus.NOT_FOUND).error("Genre not found"));
            var contributors = artistRepository.findAllById(metadata.getContributorsId());
            var track = Track.builder()
                    .name(metadata.getName())
                    .genre(genre)
                    .contributors(contributors)
                    .fileIdentifier(metadata.getFileId())
                    .build();
            tracks.add(track);
        }


        var release = Release.builder()
                .name(name)
                .releaseDate(LocalDate.now())
                .type(type)
                .artist(artist)
                .build();

        repository.save(release);

        tracks.forEach(track -> track.setRelease(release));
        trackRepository.saveAll(tracks);
    }

    public Iterable<Release> findAll() {
        return repository.findAll();
    }


    public Release findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ClientException.status(HttpStatus.NOT_FOUND).error("Release not found"));
    }

    public static Resource getTestFile(MultipartFile file) {
        try {
            var tempFile = Files.createTempFile("temp", file.getOriginalFilename());
            file.transferTo(tempFile);
            return new FileSystemResource(tempFile);
        } catch (IOException e) {
            throw ClientException.error("Error creating temp file");
        }
    }
}
