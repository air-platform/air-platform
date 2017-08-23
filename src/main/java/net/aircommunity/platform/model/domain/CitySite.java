package net.aircommunity.platform.model.domain;

import io.micro.annotation.constraint.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * At an airport, the apron is the area of hard ground where aircraft are parked.
 *
 * @author Xiangwen.Kong
 */
@Entity
@Table(name = "air_platform_citysite")
@XmlAccessorType(XmlAccessType.FIELD)
public class CitySite extends Persistable {
    private static final long serialVersionUID = 1L;

    // apron name
    @NotEmpty
    @Size(max = PRODUCT_NAME_LEN)
    @Column(name = "name", length = PRODUCT_NAME_LEN, nullable = false)
    private String name;


    // in which city (city can be empty according to current requirement)
    @Size(max = CITY_NAME_LEN)
    @Column(name = "city", length = CITY_NAME_LEN, nullable = true)
    private String city;


    // apron GPS location
    @Embedded
    private Location location;

    // apron address
    @Lob
    @Column(name = "address")
    private String address;

    // apron description
    @Lob
    @Column(name = "description")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Apron [id=").append(id).append(", name=").append(name)
                .append(", city=").append(city).append(", address=").append(address).append(", location=").append(location)
                .append(", description=").append(description).append("]");
        return builder.toString();
    }


}
