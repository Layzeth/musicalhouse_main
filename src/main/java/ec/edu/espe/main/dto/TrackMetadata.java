package ec.edu.espe.main.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TrackMetadata {
    private String name;
    private Long genreId;
    private String fileId;
    private List<Long> contributorsId;
}