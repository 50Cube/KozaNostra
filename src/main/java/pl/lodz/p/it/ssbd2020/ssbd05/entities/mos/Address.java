package pl.lodz.p.it.ssbd2020.ssbd05.entities.mos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "address", schema = "ssbd05schema", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"city", "street", "street_no"})
})
@TableGenerator(name = "AddressIdGen", table = "id_generator", schema = "ssbd05schema", pkColumnName = "class_name", pkColumnValue = "address", valueColumnName = "id_range")
@NamedQueries({
    @NamedQuery(name = "Address.findAll", query = "SELECT a FROM Address a"),
    @NamedQuery(name = "Address.findById", query = "SELECT a FROM Address a WHERE a.id = :id"),
    @NamedQuery(name = "Address.findByStreet", query = "SELECT a FROM Address a WHERE a.street = :street"),
    @NamedQuery(name = "Address.findByStreetNo", query = "SELECT a FROM Address a WHERE a.streetNo = :streetNo"),
    @NamedQuery(name = "Address.findByCity", query = "SELECT a FROM Address a WHERE a.city = :city")
})
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter(lombok.AccessLevel.NONE)
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "AddressIdGen")
    @NotNull
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "street", nullable = false, length = 32, updatable = false)
    private String street;

    @Basic(optional = false)
    @NotNull
    @Column(name = "street_no", nullable = false, updatable = false)
    private int streetNo;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "city", nullable = false, length = 32, updatable = false)
    private String city;

    @Getter(lombok.AccessLevel.NONE)
    @Setter(lombok.AccessLevel.NONE)
    @Basic(optional = false)
    @Version
    @NotNull
    @Column(name = "version", nullable = false, columnDefinition = "bigint default 1")
    private long version;

    public Address() {
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
        if (!(object instanceof Address)) {
            return false;
        }
        Address other = (Address) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.p.it.ssbd2020.ssbd05.entities.mos.Address[ id=" + id + " ]";
    }
    
}
