package mpegg.searchservice.searchservice.Repositories;

import mpegg.searchservice.searchservice.Models.Dataset;
import mpegg.searchservice.searchservice.Models.DatasetGroup;
import mpegg.searchservice.searchservice.Models.Sample;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SampleRepository extends CrudRepository<Sample,Long> {
    @Query(value = "SELECT DISTINCT dataset FROM sample WHERE " +
            "(:title IS NULL OR LOWER(title) LIKE LOWER(:title)) " +
            "AND (:taxon_id IS NULL OR LOWER(taxon_id) LIKE LOWER(:taxon_id))",
            nativeQuery = true)
    List<Long> findByMetadata(@Param("title") String title, @Param("taxon_id") String taxon_id);

    Optional<Sample> findById(Long Id);
}
