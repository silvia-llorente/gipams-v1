package mpegg.workflowmanager.workflowmanager.Models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="mpegfile",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"owner","name"}))

public class MPEGFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String path;

    private String owner;

    @OneToMany(mappedBy = "mpegfile", fetch = FetchType.LAZY)
    private List<DatasetGroup> datasetGroups;

    public MPEGFile() {
    }

    public MPEGFile(String owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DatasetGroup> getDatasetGroups() {
        return datasetGroups;
    }
}