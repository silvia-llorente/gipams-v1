package mpegg.searchservice.searchservice.Repositories;


import mpegg.searchservice.searchservice.Models.Dataset;
import mpegg.searchservice.searchservice.Models.DatasetGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DatasetGroupRepository extends CrudRepository<DatasetGroup,Long> {
    @Query(value = "SELECT MAX(dg_id) FROM datasetgroup WHERE mpegfile_id = :file_id",nativeQuery = true)
    Collection<Integer> getMaxDgId(@Param("file_id")long file_id);

    @Query(value = "SELECT * FROM datasetgroup WHERE " +
                   "(:center IS NULL OR LOWER(center) LIKE LOWER(:center)) " +
                   "AND (:description IS NULL OR LOWER(description) LIKE LOWER(:description)) " +
                   "AND (:title IS NULL OR LOWER(title) LIKE LOWER(:title)) " +
                   "AND (:type IS NULL OR LOWER(type) LIKE LOWER(:type))",
                    nativeQuery = true)
    List<DatasetGroup> findByMetadata(@Param("center") String center, @Param("description") String description, @Param("title") String title, @Param("type") String type);

    List<DatasetGroup> findByOwner(String Owner);

    Optional<DatasetGroup> findById(Long Id);
}
