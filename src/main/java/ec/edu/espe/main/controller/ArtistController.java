package ec.edu.espe.main.controller;

import ec.edu.espe.main.model.Artist;
import ec.edu.espe.main.service.ArtistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/artists")
public class ArtistController {
    private final ArtistService service;

    public ArtistController(ArtistService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> save(
            @RequestParam String name,
            @RequestParam String country,
            @RequestParam String birthday
    ) {
        service.save(name, country, LocalDate.parse(birthday));
        return ResponseEntity.ok("Saved");
    }

    @PutMapping
    public ResponseEntity<String> update(
            Long id,
            String name,
            String country
    ) {
        service.update(id, name, country);
        return ResponseEntity.ok("Updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping
    public ResponseEntity<Iterable<Artist>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artist> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }
}
