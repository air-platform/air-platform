package net.aircommunity.platform.common.ua;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Operating System parser using ua-parser. Extracts OS information from user agent strings.
 */
class OSParser {
	private final List<OSPattern> patterns;

	static OSParser fromList(List<Map<String, String>> configList) {
		List<OSPattern> configPatterns = new ArrayList<OSPattern>();
		for (Map<String, String> configMap : configList) {
			configPatterns.add(OSParser.patternFromMap(configMap));
		}
		return new OSParser(configPatterns);
	}

	private OSParser(List<OSPattern> patterns) {
		this.patterns = patterns;
	}

	OS parse(String agentString) {
		if (agentString == null) {
			return null;
		}
		for (OSPattern p : patterns) {
			OS os = p.match(agentString);
			if (os != null) {
				return os;
			}
		}
		return OS.OTHER;
	}

	private static OSPattern patternFromMap(Map<String, String> configMap) {
		String regex = configMap.get("regex");
		if (regex == null) {
			throw new IllegalArgumentException("OS is missing regex");
		}
		return new OSPattern(Pattern.compile(regex), configMap.get("os_replacement"),
				configMap.get("os_v1_replacement"), configMap.get("os_v2_replacement"),
				configMap.get("os_v3_replacement"));
	}

	private static class OSPattern {
		private final Pattern pattern;
		private final String osReplacement;
		private final String v1Replacement;
		private final String v2Replacement;
		private final String v3Replacement;

		public OSPattern(Pattern pattern, String osReplacement, String v1Replacement, String v2Replacement,
				String v3Replacement) {
			this.pattern = pattern;
			this.osReplacement = osReplacement;
			this.v1Replacement = v1Replacement;
			this.v2Replacement = v2Replacement;
			this.v3Replacement = v3Replacement;
		}

		public OS match(String agentString) {
			String family = null, v1 = null, v2 = null, v3 = null, v4 = null;
			Matcher matcher = pattern.matcher(agentString);
			if (!matcher.find()) {
				return null;
			}
			int groupCount = matcher.groupCount();
			if (osReplacement != null) {
				if (groupCount >= 1) {
					family = Pattern.compile("(" + Pattern.quote("$1") + ")").matcher(osReplacement)
							.replaceAll(matcher.group(1));
				}
				else {
					family = osReplacement;
				}
			}
			else if (groupCount >= 1) {
				family = matcher.group(1);
			}

			if (v1Replacement != null) {
				v1 = v1Replacement;
			}
			else if (groupCount >= 2) {
				v1 = matcher.group(2);
			}
			if (v2Replacement != null) {
				v2 = v2Replacement;
			}
			else if (groupCount >= 3) {
				v2 = matcher.group(3);
			}
			if (v3Replacement != null) {
				v3 = v3Replacement;
			}
			else if (groupCount >= 4) {
				v3 = matcher.group(4);
			}
			if (groupCount >= 5) {
				v4 = matcher.group(5);
			}
			return family == null ? null : new OS(family, v1, v2, v3, v4);
		}
	}
}
