package pt.ubi.gruposd.loja.service;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class FuzzyMatcher {
    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern NON_WORD = Pattern.compile("[^\\p{L}\\p{N}]+");
    public static final double MIN_RATIO = 0.6;

    private FuzzyMatcher() {}

    public static String normalize(String s) {
        if (s == null) return "";
        String nfd = Normalizer.normalize(s, Normalizer.Form.NFD);
        return DIACRITICS.matcher(nfd).replaceAll("").toLowerCase();
    }

    /** Highest similarity (0..1) between any token in {@code text} and the normalized query. */
    public static double bestScore(String text, String queryNormalized) {
        if (text == null || text.isBlank() || queryNormalized.isBlank()) return 0;
        double best = 0;
        for (String token : NON_WORD.split(normalize(text))) {
            if (token.length() < 2) continue;
            double r = ratio(token, queryNormalized);
            if (r > best) best = r;
        }
        return best;
    }

    static double ratio(String a, String b) {
        int max = Math.max(a.length(), b.length());
        if (max == 0) return 1.0;
        return 1.0 - ((double) levenshtein(a, b) / max);
    }

    static int levenshtein(String a, String b) {
        int n = a.length(), m = b.length();
        if (n == 0) return m;
        if (m == 0) return n;
        int[] prev = new int[m + 1];
        int[] curr = new int[m + 1];
        for (int j = 0; j <= m; j++) prev[j] = j;
        for (int i = 1; i <= n; i++) {
            curr[0] = i;
            for (int j = 1; j <= m; j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                curr[j] = Math.min(
                    Math.min(curr[j - 1] + 1, prev[j] + 1),
                    prev[j - 1] + cost
                );
            }
            int[] tmp = prev; prev = curr; curr = tmp;
        }
        return prev[m];
    }
}
