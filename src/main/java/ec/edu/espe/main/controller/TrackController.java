package ec.edu.espe.main.controller;

import ec.edu.espe.main.model.Track;
import ec.edu.espe.main.service.TrackService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@Log4j2
@RequestMapping("/tracks")
public class TrackController {
    private final TrackService service;

    public TrackController(TrackService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Iterable<Track>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Track> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/file-identifier/{fileIdentifier}")
    public ResponseEntity<Track> findByFileIdentifier(
            @PathVariable String fileIdentifier
    ) {
        return ResponseEntity.ok(service.findByFileIdentifier(fileIdentifier));
    }

    @RabbitListener(queues = "queue.download")
    private void receive(String fileIdentifier){
        log.info("Message received from queue.download ->{}", fileIdentifier);
        service.incrementDownloadCount(fileIdentifier);
    }

    @RabbitListener(queues = "queue.reproduction")
    private void receiveFromB(String fileIdentifier){
        log.info("Message received from queue.reproduction ->{}", fileIdentifier);
        service.incrementReproductionCount(fileIdentifier);
    }
}
