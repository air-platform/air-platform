package net.aircommunity.platform.service.internal;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;

import io.micro.common.io.MoreFiles;
import net.aircommunity.platform.service.TemplateService;

/**
 * Template service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
public class TemplateServiceImpl implements TemplateService {
	private static final Logger LOG = LoggerFactory.getLogger(TemplateServiceImpl.class);

	private final Jinjava templateEngine;

	public TemplateServiceImpl() {
		templateEngine = new Jinjava(
				JinjavaConfig.newBuilder().withCharset(StandardCharsets.UTF_8).withLocale(Locale.CHINESE).build());
	}

	@Override
	public String renderFile(String templateFile, Map<String, Object> bindings) {
		try {
			String template = MoreFiles.toString(templateFile);
			return render(template, bindings);
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to render file template %s, with bindings %s, cause: %s", templateFile,
					bindings, e.getMessage()), e);
		}
		return null;
	}

	@Override
	public String render(String template, Map<String, Object> bindings) {
		return templateEngine.render(template, bindings);
	}

}
