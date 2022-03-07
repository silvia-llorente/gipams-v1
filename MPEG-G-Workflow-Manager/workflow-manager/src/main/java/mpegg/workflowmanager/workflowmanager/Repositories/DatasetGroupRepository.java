package mpegg.workflowmanager.workflowmanager.Repositories;

import mpegg.workflowmanager.workflowmanager.Models.DatasetGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface DatasetGroupRepository extends CrudRepository<DatasetGroup,Long> {
    @Query(value = "SELECT MAX(dg_id) FROM datasetgroup WHERE mpegfile_id = :file_id",nativeQuery = true)
    Collection<Integer> getMaxDgId(@Param("file_id")long file_id);

    Optional<DatasetGroup> findById(Long Id);
}
