package mpegg.workflowmanager.workflowmanager.Repositories;

import mpegg.workflowmanager.workflowmanager.Models.Dataset;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface DatasetRepository extends CrudRepository<Dataset,Long> {
    @Query(value = "SELECT MAX(dt_id) FROM dataset WHERE datasetgroup_id = :dg_id",nativeQuery = true)
    Collection<Integer> getMaxDtId(@Param("dg_id")long dg_id);

    Optional<Dataset> findById(Long Id);
}
