package pl.lodz.p.it.ssbd2020.ssbd05.mor.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "status", schema = "ssbd05schema")
@NamedQueries({
    @NamedQuery(name = "Status.findAll", query = "SELECT s FROM Status s"),
    @NamedQuery(name = "Status.findById", query = "SELECT s FROM Status s WHERE s.id = :id"),
    @NamedQuery(name = "Status.findByStatusName", query = "SELECT s FROM Status s WHERE s.statusName = :statusName"),
    @NamedQuery(name = "Status.findByVersion", query = "SELECT s FROM Status s WHERE s.version = :version")})
public class Status implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "status_name", nullable = false, length = 32)
    private String statusName;

    @Getter(lombok.AccessLevel.NONE)
    @Setter(lombok.AccessLevel.NONE)
    @Basic(optional = false)
    @Version
    @NotNull
    @Column(name = "version", nullable = false, columnDefinition = "bigint default 1")
    private long version;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "statusId")
    private Collection<Reservation> reservationCollection = new ArrayList<>();

    public Status() {
    }

    public Status(Long id) {
        this.id = id;
    }

    public Status(Long id, String statusName, long version) {
        this.id = id;
        this.statusName = statusName;
        this.version = version;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Status)) {
            return false;
        }
        Status other = (Status) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.it.ssbd2020.ssbd05.mor.entities.Status[ id=" + id + " ]";
    }
    
}
