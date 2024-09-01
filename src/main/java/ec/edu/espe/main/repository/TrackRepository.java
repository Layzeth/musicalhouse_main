package ec.edu.espe.main.repository;

import ec.edu.espe.main.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

     Optional<Track> findByFileIdentifier(String id);
}
