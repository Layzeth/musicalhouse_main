package ec.edu.espe.main.controller;

import ec.edu.espe.main.dto.TrackMetadata;
import ec.edu.espe.main.model.Release;
import ec.edu.espe.main.model.ReleaseType;
import ec.edu.espe.main.service.ReleaseService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


//@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/releases")
public class ReleaseController {

    private final ReleaseService service;

    public ReleaseController(ReleaseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> save(
            @RequestParam String name,
            @RequestParam ReleaseType type,
            @RequestParam Long artistId,
            @RequestParam @NotEmpty List<MultipartFile> tracksFiles,
            @RequestPart List<TrackMetadata> tracksMetadata
    ){
        service.save(name, type, artistId, tracksFiles, tracksMetadata);
        return ResponseEntity.ok("Saved");
    }

    @GetMapping
    public ResponseEntity<Iterable<Release>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Release> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }
}
