package mpegg.searchservice.searchservice.Repositories;

import mpegg.searchservice.searchservice.Models.Dataset;
import mpegg.searchservice.searchservice.Models.MPEGFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DatasetRepository extends CrudRepository<Dataset,Long> {
    @Query(value = "SELECT MAX(dt_id) FROM dataset WHERE datasetgroup_id = :dg_id",nativeQuery = true)
    Collection<Integer> getMaxDtId(@Param("dg_id")long dg_id);

    List<Dataset> findByOwner(String Owner);

    Optional<Dataset> findById(Long Id);
}
