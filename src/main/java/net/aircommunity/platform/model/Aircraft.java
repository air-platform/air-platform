package net.aircommunity.platform.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.TenantAdapter;

/**
 * Aircraft for (Taxi, Transportation, Tour).
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_aircraft")
@XmlAccessorType(XmlAccessType.FIELD)
public class Aircraft extends Persistable {
	private static final long serialVersionUID = 1L;

	// Flight NO. global unique, e.g. 353252
	@NotEmpty
	@Column(name = "flight_no", nullable = false, unique = true)
	private String flightNo;

	// Flight name
	@NotEmpty
	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "image")
	private String image;

	// e.g. G550
	@NotEmpty
	@Column(name = "type", nullable = false)
	private String type;

	// e.g. fixed-wing / helicopter / balloon
	@NotEmpty
	@Column(name = "category", nullable = false)
	private String category;

	// Number of seats
	@Min(1)
	@Column(name = "seats")
	private int seats;

	// min passengers required
	@Min(1)
	@Column(name = "min_passengers")
	private int minPassengers;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	private Date creationDate;

	@Lob
	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	@XmlJavaTypeAdapter(TenantAdapter.class)
	private Tenant vendor;

	public Aircraft() {
	}

	public Aircraft(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public int getMinPassengers() {
		return minPassengers;
	}

	public void setMinPassengers(int minPassengers) {
		this.minPassengers = minPassengers;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Tenant getVendor() {
		return vendor;
	}

	public void setVendor(Tenant vendor) {
		this.vendor = vendor;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Aircraft [flightNo=").append(flightNo).append(", type=").append(type).append(", category=")
				.append(category).append(", seats=").append(seats).append(", minPassengers=").append(minPassengers)
				.append(", image=").append(image).append(", description=").append(description).append(", id=")
				.append(id).append("]");
		return builder.toString();
	}
}
