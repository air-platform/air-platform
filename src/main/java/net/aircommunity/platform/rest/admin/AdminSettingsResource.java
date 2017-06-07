package net.aircommunity.platform.rest.admin;

import java.util.Base64;
import java.util.Set;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.micro.annotation.RESTful;
import io.micro.core.security.AccessTokenService;
import net.aircommunity.platform.model.AccessToken;
import net.aircommunity.platform.model.Contact;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.PlatformService;

/**
 * Settings RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminSettingsResource {

	@Resource
	private PlatformService platformService;

	@Resource
	private AccessTokenService accessTokenService;

	// **************************************
	// Platform generic settings
	// **************************************

	/**
	 * Get access token Secret
	 */
	@GET
	@Path("accesstoken/secret")
	@Consumes(MediaType.APPLICATION_JSON)
	public AccessToken getAccessTokenPublicKey() {
		String key = Base64.getEncoder().encodeToString(accessTokenService.getPublicKey().getEncoded());
		return new AccessToken(key);
	}

	/**
	 * set platform client managers
	 */
	@PUT
	@Path("client-managers")
	@Consumes(MediaType.APPLICATION_JSON)
	public void setPlatformClientManagers(@NotNull @Valid Set<Contact> clientManagers) {
		platformService.setPlatformClientManagers(clientManagers);
	}

	/**
	 * set platform client managers
	 */
	@GET
	@Path("client-managers")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Contact> getPlatformClientManagers() {
		return platformService.getPlatformClientManagers();
	}

}
