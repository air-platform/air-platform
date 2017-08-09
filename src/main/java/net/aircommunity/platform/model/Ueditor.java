package net.aircommunity.platform.model;

/**
 * Created by kongxiangwen on 8/8/17.
 */
public class Ueditor {
    public enum ActionType {
        CONFIG, UPLOADSCRAWL, UPLOADIMAGE;

        public static ActionType fromString(String value) {
            for (ActionType e : values()) {
                if (e.name().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
}
