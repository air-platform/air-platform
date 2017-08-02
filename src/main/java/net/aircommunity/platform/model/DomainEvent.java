package net.aircommunity.platform.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.eventbus.Subscribe;

/**
 * Domain CURD Event.
 * 
 * @author Bin.Zhang
 */
@Immutable
public class DomainEvent implements Serializable {
	private static final long serialVersionUID = 1L;

	private final DomainType type;
	private final Operation operation;
	// the domain object may be null, only include when necessary
	private final Optional<Serializable> data;

	public DomainEvent(DomainType type, Operation operation) {
		this(type, null, operation);
	}

	public DomainEvent(@Nonnull DomainType type, @Nullable Serializable data, @Nonnull Operation operation) {
		this.type = Objects.requireNonNull(type, "type cannot null");
		this.operation = Objects.requireNonNull(operation, "operation cannot null");
		this.data = Optional.ofNullable(data);
	}

	@Nonnull
	public DomainType getType() {
		return type;
	}

	@Nonnull
	public Operation getOperation() {
		return operation;
	}

	@Nonnull
	public Optional<Serializable> getData() {
		return data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DomainEvent [type=").append(type).append(", operation=").append(operation).append(", data=")
				.append(data.isPresent() ? data.get() : null).append("]");
		return builder.toString();
	}

	public enum Operation {
		CREATION, UPDATE, DELETION;
	}

	public enum DomainType {
		ACCOUNT, USER, TENANT, AIRCRAFT, PRODUCT
	}

	/**
	 * Domain event handler.
	 */
	public static class Handler {

		private final Consumer<DomainEvent> consumer;

		public Handler(Consumer<DomainEvent> consumer) {
			this.consumer = Objects.requireNonNull(consumer, "consumer cannot null");
		}

		@Subscribe
		private void onDomainEvent(DomainEvent event) {
			consumer.accept(event);
		}
	}

}
