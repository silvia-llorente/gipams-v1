package mpegg.gcs.genomiccontentservice.Models;

import mpegg.gcs.genomiccontentservice.Models.DatasetGroup;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="dataset")
public class Dataset {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Integer dt_id;

    private String path;

    private Boolean metadata;

    private Boolean protection;

    private String owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "datasetgroup_id", nullable = false)
    private DatasetGroup datasetGroup;

    @OneToMany(mappedBy = "dataset", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Sample> samples;

    public Dataset() {
    }

    public Dataset(DatasetGroup datasetGroup, String owner) {
        this.datasetGroup = datasetGroup;
        this.metadata = false;
        this.protection = false;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public DatasetGroup getDatasetGroup() {
        return datasetGroup;
    }

    public Integer getDt_id() {
        return dt_id;
    }

    public void setDt_id(Integer dt_id) {
        this.dt_id = dt_id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getMetadata() {
        return metadata;
    }

    public void setMetadata(Boolean metadata) {
        this.metadata = metadata;
    }

    public Boolean getProtection() {
        return protection;
    }

    public void setProtection(Boolean protection) {
        this.protection = protection;
    }

    public String getOwner() {
        return owner;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}