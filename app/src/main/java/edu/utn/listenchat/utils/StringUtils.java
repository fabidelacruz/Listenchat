package edu.utn.listenchat.utils;

import java.text.Normalizer;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * Created by fabian on 9/10/17.
 */

public class StringUtils {

    public static boolean safeEquals(String s1, String s2) {
        return normalized(s1).equals(normalized(s2));
    }

    public static boolean safeContains(String s1, String s2) {
        return normalized(s1).contains(normalized(s2));
    }

    public static String normalized(String s) {
        String lower =  Normalizer.normalize(trimToEmpty(s), Normalizer.Form.NFD).toLowerCase();
        return org.apache.commons.lang3.StringUtils.stripAccents(lower);
    }

}
