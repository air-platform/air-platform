package net.aircommunity.platform.service;

import java.util.Map;

/**
 * Template service.
 * 
 * @author Bin.Zhang
 */
public interface TemplateService {

	/**
	 * Render the template with the given model.
	 * 
	 * @param template the template string
	 * @param bindings the model used
	 * @return the rendered result string
	 */
	String render(String template, Map<String, Object> bindings);

	/**
	 * Render the template file with the given model.
	 * 
	 * @param templateFile the templateFile
	 * @param bindings the model used
	 * @return the rendered result string
	 */
	String renderFile(String templateFile, Map<String, Object> bindings);

}
