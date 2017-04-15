package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.model.Account;
import net.aircommunity.platform.model.CurrencyUnit;
import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.repository.ProductRepository;
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.platform.service.CharterOrderService;
import net.aircommunity.platform.service.FleetService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Fleet RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
@Path("test")
public class TestResource {
	private static final Logger LOG = LoggerFactory.getLogger(TestResource.class);

	@Resource
	private FleetService fleetService;

	@Resource
	private AccountService accountService;

	@Resource
	private CharterOrderService charterOrderService;

	@Resource
	private ProductRepository productRepository;

	@GET
	@Path("{id}")
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") String id) {
		Product prd = productRepository.findOne(id);
		return Response.ok(prd).build();
	}

	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response create() {
		Account account = accountService.createAccount("deerjet", "p0o9i8u7", Role.TENANT);
		Fleet fleet = new Fleet();
		fleet.setAircraftType("G550");
		fleet.setBeds(2);
		fleet.setCapacity("14-16");
		fleet.setCurrencyUnit(CurrencyUnit.RMB);
		fleet.setDescription("desc");
		fleet.setFlightNo("1");
		fleet.setFullloadRange(8000);
		fleet.setName("DeerJet G550");
		fleet.setPrice(100);
		fleet.setWeight(111);
		Fleet created = fleetService.createFleet(account.getId(), fleet);
		LOG.debug("Created {}", created);
		return Response.ok(account).build();
	}

}
