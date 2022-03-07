package mpegg.workflowmanager.workflowmanager.Models;

import javax.persistence.*;

@Entity
@Table(name="sample")
public class Sample {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition="VARCHAR(2048)")
    private String title;

    private String taxon_id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset", nullable = false)
    private Dataset dataset;

    public Sample(Dataset dataset) {
        this.dataset = dataset;
    }

    public Sample () {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTaxon_id() {
        return taxon_id;
    }

    public void setTaxon_id(String taxon_id) {
        this.taxon_id = taxon_id;
    }

    public Long getId() {
        return id;
    }

    public Dataset getDataset() {
        return dataset;
    }
}
