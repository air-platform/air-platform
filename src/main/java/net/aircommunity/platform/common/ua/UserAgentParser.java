package net.aircommunity.platform.common.ua;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User Agent parser using ua-parser regexes
 */
class UserAgentParser {
	private final List<UAPattern> patterns;

	static UserAgentParser fromList(List<Map<String, String>> configList) {
		List<UAPattern> configPatterns = new ArrayList<>();
		for (Map<String, String> configMap : configList) {
			configPatterns.add(patternFromMap(configMap));
		}
		return new UserAgentParser(configPatterns);
	}

	private UserAgentParser(List<UAPattern> patterns) {
		this.patterns = patterns;
	}

	UserAgent parse(String agentString) {
		if (agentString == null) {
			return null;
		}
		for (UAPattern p : patterns) {
			UserAgent agent = p.match(agentString);
			if (agent != null) {
				return agent;
			}
		}
		return UserAgent.OTHER;
	}

	private static UAPattern patternFromMap(Map<String, String> configMap) {
		String regex = configMap.get("regex");
		if (regex == null) {
			throw new IllegalArgumentException("User agent is missing regex");
		}
		return new UAPattern(Pattern.compile(regex), configMap.get("family_replacement"),
				configMap.get("v1_replacement"), configMap.get("v2_replacement"));
	}

	private static class UAPattern {
		private final Pattern pattern;
		private final String familyReplacement;
		private final String v1Replacement;
		private final String v2Replacement;

		public UAPattern(Pattern pattern, String familyReplacement, String v1Replacement, String v2Replacement) {
			this.pattern = pattern;
			this.familyReplacement = familyReplacement;
			this.v1Replacement = v1Replacement;
			this.v2Replacement = v2Replacement;
		}

		public UserAgent match(String agentString) {
			String family = null, v1 = null, v2 = null, v3 = null;
			Matcher matcher = pattern.matcher(agentString);
			if (!matcher.find()) {
				return null;
			}
			int groupCount = matcher.groupCount();
			if (familyReplacement != null) {
				if (familyReplacement.contains("$1") && groupCount >= 1 && matcher.group(1) != null) {
					family = familyReplacement.replaceFirst("\\$1", Matcher.quoteReplacement(matcher.group(1)));
				}
				else {
					family = familyReplacement;
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
				if (groupCount >= 4) {
					v3 = matcher.group(4);
				}
			}
			return family == null ? null : new UserAgent(family, v1, v2, v3);
		}
	}
}
