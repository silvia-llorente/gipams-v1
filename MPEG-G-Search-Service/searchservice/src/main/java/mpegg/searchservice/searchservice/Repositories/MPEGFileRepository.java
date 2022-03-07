package mpegg.searchservice.searchservice.Repositories;

import mpegg.searchservice.searchservice.Models.MPEGFile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MPEGFileRepository extends CrudRepository<MPEGFile,Long> {
    List<MPEGFile> findByOwner(String Owner);

    Optional<MPEGFile> findById(Long Id);

}
