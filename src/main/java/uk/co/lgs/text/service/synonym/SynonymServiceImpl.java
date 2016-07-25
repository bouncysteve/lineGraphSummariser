package uk.co.lgs.text.service.synonym;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

/**
 * I am responsible for returning random words or phrases equivalent to the
 * passed word. TODO: remove duplicate lists, and their references in other
 * classes
 *
 * @author bouncysteve
 *
 */
@Component
public class SynonymServiceImpl implements SynonymService {
    private static final List<String> AT_SYNONYMS = Arrays.asList(Constants.AT);
    private static final List<String> AT_THE_SAME_RATE_SYNONYMS = Arrays.asList(Constants.AT_THE_SAME_RATE, "together",
            "equally");
    private static final List<String> BE_CALLED_SYNONYMS = Arrays.asList(Constants.BE_CALLED, "be named",
            "has the title");
    private static final List<String> BE_HIGHER_SYNONYMS = Arrays.asList(Constants.BE_HIGHER);
    private static final List<String> BOTH_SYNONYMS = Arrays.asList(Constants.BOTH, "the two lines");
    private static final List<String> BUT_SYNONYMS = Arrays.asList(Constants.BUT, "however", "nevertheless",
            "despite this");
    private static final List<String> BY_SYNONYMS = Arrays.asList(Constants.BY);
    private static final List<String> CROSS_SYNONYMS = Arrays.asList(Constants.CROSS, "intersect");
    private static final List<String> DECREASE_GAP_SYNONYMS = Arrays.asList(Constants.DECREASE_GAP, "fall", "decline",
            "descend", "drop", "shrink", "reduce", "show a downward trend"); // ("weaken","diminish",
    private static final List<String> DECREASE_SERIES_SYNONYMS = Arrays.asList(Constants.DECREASE_SERIES, "fall",
            "decline", "descend", "drop", "shrink", "reduce", "show a downward trend"); // ("weaken","diminish",
    private static final List<String> HAS_SYNONYMS = Arrays.asList(Constants.HAS, "has the value");
    private static final List<String> INCREASE_GAP_SYNONYMS = Arrays.asList(Constants.INCREASE_GAP, "rise", "grow",
            "improve", "progress", "go up", "get larger", "show an upward trend"); // ("ascend","extend","build"
    private static final List<String> INCREASE_SERIES_SYNONYMS = Arrays.asList(Constants.INCREASE_SERIES, "rise",
            "grow", "improve", "progress", "go up", "get larger", "show an upward trend"); // ("ascend","extend","build"
    private static final List<String> MORE_STEEPLY_SYNONYMS = Arrays.asList(Constants.MORE_STEEPLY, "more rapidly",
            "at a greater rate");
    private static final List<String> NEXT_SYNONYMS = Arrays.asList(Constants.NEXT, "in the following period",
            "afterwards", "after that", "following that");
    private static final List<String> REMAIN_GAP_SYNONYMS = Arrays.asList(Constants.REMAIN_GAP, "be constant",
            "stay the same", "continue to be", "freeze", "persist", "rest", "hold", "do not change");// ("endure",
    private static final List<String> REMAIN_SERIES_SYNONYMS = Arrays.asList(Constants.REMAIN_SERIES, "be constant",
            "stay the same", "continue to be", "freeze", "persist", "rest", "hold", "do not change");// ("endure",
    private static final List<String> SHOW_SYNONYMS = Arrays.asList(Constants.SHOW, "display", "demonstrate", "detail");
    private static final List<String> SO_SYNONYMS = Arrays.asList(Constants.SO, "consequently", "as a consequence",
            "thus");
    private static final List<String> SO_THAT_SYNONYMS = Arrays.asList(Constants.SO_THAT, Constants.SO, "consequently",
            "as a consequence", "thus");
    private static final List<String> THE_GAP_BETWEEN_THEM_SYNONYMS = Arrays.asList(Constants.THE_GAP_BETWEEN_THEM,
            "the difference between them", "their difference", "their gap");
    private static final List<String> THEY_SYNONYMS = Arrays.asList(Constants.THEY, "the series", "the lines");
    private static final List<String> TO_SYNONYMS = Arrays.asList(Constants.TO);
    private static final List<String> TO_THE_SAME_VALUE_SYNONYMS = Arrays.asList(Constants.TO_THE_SAME_VALUE);
    private static final List<String> UNTIL_SYNONYMS = Arrays.asList(Constants.UNTIL, "up to",
            "in the period leading up to", "leading up to");
    private static final List<String> WHILE_SYNONYMS = Arrays.asList(Constants.WHILE, "and");
    private static final List<String> WITH_SYNONYMS = Arrays.asList(Constants.WITH);
    private static final Random random = new Random();

    private static final Map<String, List<String>> LOOKUPS;

    static {
        LOOKUPS = new HashMap<>();
        LOOKUPS.put(Constants.AT, AT_SYNONYMS);
        LOOKUPS.put(Constants.AT_THE_SAME_RATE, AT_THE_SAME_RATE_SYNONYMS);
        LOOKUPS.put(Constants.BE_CALLED, BE_CALLED_SYNONYMS);
        LOOKUPS.put(Constants.BE_HIGHER, BE_HIGHER_SYNONYMS);
        LOOKUPS.put(Constants.BOTH, BOTH_SYNONYMS);
        LOOKUPS.put(Constants.BUT, BUT_SYNONYMS);
        LOOKUPS.put(Constants.BY, BY_SYNONYMS);
        LOOKUPS.put(Constants.CROSS, CROSS_SYNONYMS);
        LOOKUPS.put(Constants.DECREASE_GAP, DECREASE_GAP_SYNONYMS);
        LOOKUPS.put(Constants.DECREASE_SERIES, DECREASE_SERIES_SYNONYMS);
        LOOKUPS.put(Constants.HAS, HAS_SYNONYMS);
        LOOKUPS.put(Constants.INCREASE_GAP, INCREASE_GAP_SYNONYMS);
        LOOKUPS.put(Constants.INCREASE_SERIES, INCREASE_SERIES_SYNONYMS);

        LOOKUPS.put(Constants.MORE_STEEPLY, MORE_STEEPLY_SYNONYMS);
        LOOKUPS.put(Constants.NEXT, NEXT_SYNONYMS);
        LOOKUPS.put(Constants.REMAIN_GAP, REMAIN_GAP_SYNONYMS);
        LOOKUPS.put(Constants.REMAIN_SERIES, REMAIN_SERIES_SYNONYMS);
        LOOKUPS.put(Constants.SHOW, SHOW_SYNONYMS);
        LOOKUPS.put(Constants.SO, SO_SYNONYMS);
        LOOKUPS.put(Constants.SO_THAT, SO_THAT_SYNONYMS);
        LOOKUPS.put(Constants.THE_GAP_BETWEEN_THEM, THE_GAP_BETWEEN_THEM_SYNONYMS);
        LOOKUPS.put(Constants.THEY, THEY_SYNONYMS);
        LOOKUPS.put(Constants.TO, TO_SYNONYMS);
        LOOKUPS.put(Constants.TO_THE_SAME_VALUE, TO_THE_SAME_VALUE_SYNONYMS);
        LOOKUPS.put(Constants.UNTIL, UNTIL_SYNONYMS);
        LOOKUPS.put(Constants.WHILE, WHILE_SYNONYMS);
        LOOKUPS.put(Constants.WITH, WITH_SYNONYMS);
    }

    private boolean randomise = true;

    @Override
    public String getSynonym(final String replaceMe) {
        String replacement = replaceMe;
        final List<String> synonyms = LOOKUPS.get(replaceMe);
        if (null != synonyms && !synonyms.isEmpty()) {
            replacement = getSynonym(synonyms);
        }
        return replacement;
    }

    private String getSynonym(final List<String> synonyms) {
        int randomNumber = 0;
        if (this.randomise) {
            randomNumber = random.nextInt(synonyms.size() - 0);
        }
        final Iterator<String> it = synonyms.iterator();
        int index = 0;
        while (index < randomNumber) {
            it.next();
            index++;
        }
        return it.next();
    }

    @Override
    public void setRandomise(final boolean randomise) {
        this.randomise = randomise;
    }

}
