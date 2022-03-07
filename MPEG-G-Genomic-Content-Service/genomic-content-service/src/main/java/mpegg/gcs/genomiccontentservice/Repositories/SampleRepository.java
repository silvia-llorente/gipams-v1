package mpegg.gcs.genomiccontentservice.Repositories;

import mpegg.gcs.genomiccontentservice.Models.Dataset;
import mpegg.gcs.genomiccontentservice.Models.Sample;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SampleRepository extends CrudRepository<Sample,Long> {
    Optional<Sample> findById(Long Id);
}
