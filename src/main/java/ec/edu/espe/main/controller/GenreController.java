package ec.edu.espe.main.controller;

import ec.edu.espe.main.model.Genre;
import ec.edu.espe.main.service.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService service;

    public GenreController(GenreService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> save(
            @RequestParam String name
    ) {
        service.save(name);
        return ResponseEntity.ok("Saved");
    }

    @PutMapping
    public ResponseEntity<String> update(
            @RequestParam Long id,
            @RequestParam String name
    ) {
        service.update(id, name);
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
    public ResponseEntity<Iterable<Genre>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

}
