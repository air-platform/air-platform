package net.aircommunity.platform.common.ua;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Device parser using ua-parser regexes. Extracts device information from user agent strings.
 */
class DeviceParser {
	private final List<DevicePattern> patterns;

	static DeviceParser fromList(List<Map<String, String>> configList) {
		List<DevicePattern> configPatterns = new ArrayList<DevicePattern>();
		for (Map<String, String> configMap : configList) {
			configPatterns.add(DeviceParser.patternFromMap(configMap));
		}
		return new DeviceParser(configPatterns);
	}

	private DeviceParser(List<DevicePattern> patterns) {
		this.patterns = patterns;
	}

	Device parse(String agentString) {
		if (agentString == null) {
			return null;
		}
		for (DevicePattern p : patterns) {
			String device = p.match(agentString);
			if (device != null) {
				return new Device(device);
			}
		}
		return Device.OTHER;
	}

	private static DevicePattern patternFromMap(Map<String, String> configMap) {
		String regex = configMap.get("regex");
		if (regex == null) {
			throw new IllegalArgumentException("Device is missing regex");
		}
		Pattern pattern = "i".equals(configMap.get("regex_flag")) // no ohter flags used (by now)
				? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) : Pattern.compile(regex);
		return new DevicePattern(pattern, configMap.get("device_replacement"));
	}

	private static class DevicePattern {
		private static final Pattern SUBSTITUTIONS_PATTERN = Pattern.compile("\\$\\d");
		private final Pattern pattern;
		private final String deviceReplacement;

		DevicePattern(Pattern pattern, String deviceReplacement) {
			this.pattern = pattern;
			this.deviceReplacement = deviceReplacement;
		}

		private String match(String agentString) {
			Matcher matcher = pattern.matcher(agentString);
			if (!matcher.find()) {
				return null;
			}
			String device = null;
			if (deviceReplacement != null) {
				if (deviceReplacement.contains("$")) {
					device = deviceReplacement;
					for (String substitution : getSubstitutions(deviceReplacement)) {
						int i = Integer.valueOf(substitution.substring(1));
						String replacement = matcher.groupCount() >= i && matcher.group(i) != null
								? Matcher.quoteReplacement(matcher.group(i)) : "";
						device = device.replaceFirst("\\" + substitution, replacement);
					}
					device = device.trim();
				}
				else {
					device = deviceReplacement;
				}
			}
			else if (matcher.groupCount() >= 1) {
				device = matcher.group(1);
			}
			return device;
		}

		private List<String> getSubstitutions(String deviceReplacement) {
			Matcher matcher = SUBSTITUTIONS_PATTERN.matcher(deviceReplacement);
			List<String> substitutions = new ArrayList<String>();
			while (matcher.find()) {
				substitutions.add(matcher.group());
			}
			return substitutions;
		}
	}

}