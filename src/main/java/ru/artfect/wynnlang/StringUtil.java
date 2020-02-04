package ru.artfect.wynnlang;

import ru.artfect.translates.TranslateType;
import ru.artfect.wynnlang.translate.ReverseTranslation;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String handleString(TranslateType type, String str) {
        String replace = findReplace(type.getClass(), str);
        if (replace != null) {
            if (replace.isEmpty()) {
                return null;
            } else {
                return replaceFound(type, str, replace);
            }
        } else {
            Log.addString(type.getClass(), str);
            return null;
        }
    }

    private static String replaceFound(TranslateType type, String str, String replace) {
        if (Reference.modEnabled && !ReverseTranslation.enabled) {
            ReverseTranslation.translated.get(type.getClass()).put(replace, str);
            return replace;
        } else {
            ReverseTranslation.translated.get(type.getClass()).put(str, replace);
            return null;
        }
    }

    public static String findReplace(Class<? extends TranslateType> type, String str) {
        String replace = WynnLang.common.get(type).get(str);
        if (replace == null) {
            replace = findReplaceRegex(WynnLang.regex.get(type), str);
        }
        return replace;
    }

    private static String findReplaceRegex(Map<Pattern, String> map, String str) {
        for (Pattern pat : map.keySet()) {
            Matcher mat = pat.matcher(str);
            if (mat.matches()) {
                String repl = map.get(pat);
                for (int gr = 0; gr != mat.groupCount() + 1; gr++) {
                    repl = repl.replace("(r" + gr + ")", mat.group(gr));
                }
                return repl;
            }
        }
        return null;
    }
}
