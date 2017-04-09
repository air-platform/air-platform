package net.aircommunity;

import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.aircommunity.common.base.Strings;
import net.aircommunity.common.collect.ImmutableCollectors;

/**
 * Global configuration used by all services and rest resources.
 * 
 * @author Bin.Zhang
 */
@Component
public class Configuration {

	private static final String AUTH_METHODS_SEP = ",";

	// @Value("${air.security.auth-methods}")
	private String authMethods;

	@Value("${air.common.file-upload-dir}")
	private String fileUploadDir;

	public String getFileUploadDir() {
		return fileUploadDir;
	}

	public Set<String> getAuthMethods() {
		return Stream.of(authMethods.split(AUTH_METHODS_SEP)).filter(auth -> Strings.isNotBlank(auth)).map(String::trim)
				.collect(ImmutableCollectors.toSet());
	}

}
