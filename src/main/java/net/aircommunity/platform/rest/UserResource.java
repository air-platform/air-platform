package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import net.aircommunity.platform.model.Address;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.rest.annotation.Authenticated;
import net.aircommunity.rest.annotation.RESTful;

/**
 * User resource.
 * 
 * @author Bin.Zhang
 */
@RESTful
@Path("user")
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class UserResource {

	@Resource
	private AccountService accountService;

	/**
	 * List all User addresses
	 */
	@GET
	@Path("addresses")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response listUserAddresses(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return Response.ok(accountService.listUserAddresses(accountId)).build();
	}

	/**
	 * Add User address
	 */
	@POST
	@Path("addresses")
	@Consumes(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response addUserAddress(@NotNull @Valid Address address, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.addUserAddress(accountId, address);
		return Response.noContent().build();
	}

	/**
	 * Delete User address
	 */
	@DELETE
	@Path("addresses/{addressId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response removeUserAddress(@PathParam("addressId") String addressId, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.removeUserAddress(accountId, addressId);
		return Response.noContent().build();
	}

	/**
	 * List all User passengers
	 */
	@GET
	@Path("passengers")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response listUserPassengers(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return Response.ok(accountService.listUserPassengers(accountId)).build();
	}

	/**
	 * Add User passenger
	 */
	@POST
	@Path("passengers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response addPassenger(@NotNull @Valid Passenger passenger, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.addUserPassenger(accountId, passenger);
		return Response.noContent().build();
	}

	/**
	 * Delete User passenger
	 */
	@DELETE
	@Path("passengers/{passengerId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response removeUserPassenger(@PathParam("passengerId") String passengerId,
			@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.removeUserPassenger(accountId, passengerId);
		return Response.noContent().build();
	}

	// ***********************
	// Orders
	// ***********************
	@Resource
	private AirTransportOrderResource airTransportOrderResource;

	@Path("transport-orders")
	public AirTransportOrderResource airTransportOrders() {
		return airTransportOrderResource;
	}

}
