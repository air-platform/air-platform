package net.aircommunity.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Persistable support for entities.
 * 
 * @author Bin.Zhang
 */
@MappedSuperclass
public abstract class Persistable implements Serializable {
	private static final long serialVersionUID = 1L;

	// timestamp based UUID
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
			@Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy") })
	@Column(name = "id", updatable = false, nullable = false)
	protected String id;

	/**
	 * Returns the id of the entity.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns if the {@code Persistable} is new or was persisted already.
	 * 
	 * @return if the object is new
	 */
	public boolean isNew() {
		return id == null;
	}
}
