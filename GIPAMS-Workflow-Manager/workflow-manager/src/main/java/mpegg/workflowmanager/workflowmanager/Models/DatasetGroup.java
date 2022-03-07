package mpegg.workflowmanager.workflowmanager.Models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="datasetgroup")
public class DatasetGroup {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Integer dg_id;

    private String title;

    private String type;

    @Column(columnDefinition="VARCHAR(2048)")
    private String description;

    private String center;

    private String path;

    private String owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mpegfile_id", nullable = false)
    private MPEGFile mpegfile;

    @OneToMany(mappedBy = "datasetGroup", fetch = FetchType.LAZY)
    private List<Dataset> datasets;

    public DatasetGroup(MPEGFile mpegfile, String owner) {
        this.mpegfile = mpegfile;
        this.owner = owner;
    }

    public DatasetGroup() {
    }

    public Long getId() {
        return id;
    }

    public MPEGFile getMpegfile() {
        return mpegfile;
    }

    public Integer getDg_id() {
        return dg_id;
    }

    public void setDg_id(Integer dg_id) {
        this.dg_id = dg_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
