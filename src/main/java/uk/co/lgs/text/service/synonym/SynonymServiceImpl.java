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
    private static final List<String> AT_SYNONYMS = Arrays.asList("at");
    private static final List<String> BUT_SYNONYMS = Arrays.asList("but");
    private static final List<String> CONSTANT_SYNONYMS = Arrays.asList("be constant", "remain", "continue", "freeze",
            "stop", "persist", "rest", "hold a constant value");// ("endure",
    private static final List<String> CONVERGING_SYNONYMS = Arrays.asList("decreases", "reduces");
    private static final List<String> DECREASE_SYNONYMS = Arrays.asList("decrease", "fall", "decline", "drop", "shrink",
            "lower", "reduce", "show a downward trend"); // ("weaken","diminish",
    private static final List<String> DIVERGING_SYNONYMS = Arrays.asList("increases", "gets larger");

    private static final List<String> FALL_SYNONYMS = Arrays.asList("fall", "decline", "drop", "shrink", "decrease",
            "lower", "reduce", "show a downward trend"); // ("weaken","diminish",
    // "slide",)
    private static final List<String> INCREASE_SYNONYMS = Arrays.asList("increase", "rise", "grow", "improve",
            "progress", "build", "go up", "show an upward trend"); // ("ascend","extend",
    private static final List<String> PARALLEL_SYNONYMS = Arrays.asList("stay the same", "be constant",
            "does not change");

    private static final List<String> RISE_SYNONYMS = Arrays.asList("rise", "increase", "grow", "improve", "progress",
            "build", "go up", "show an upward trend"); // ("ascend","extend",
    // "advance",)
    private static final List<String> SO_SYNONYMS = Arrays.asList("so");
    private static final List<String> STAY_SAME_SYNONYMS = Arrays.asList("stay the same");
    private static final List<String> UNTIL_SYNONYMS = Arrays.asList("until", "up to", "in the period leading up to");
    private static final Random random = new Random();

    private static final Map<String, List<String>> LOOKUPS;

    static {
        LOOKUPS = new HashMap<>();
        LOOKUPS.put(Constants.AT, AT_SYNONYMS);
        LOOKUPS.put(Constants.BUT, BUT_SYNONYMS);
        LOOKUPS.put(Constants.CONSTANT, CONSTANT_SYNONYMS);
        LOOKUPS.put(Constants.CONVERGE, CONVERGING_SYNONYMS);
        LOOKUPS.put(Constants.DECREASE, DECREASE_SYNONYMS);
        LOOKUPS.put(Constants.DIVERGE, DIVERGING_SYNONYMS);
        LOOKUPS.put(Constants.FALL, FALL_SYNONYMS);
        LOOKUPS.put(Constants.INCREASE, INCREASE_SYNONYMS);
        LOOKUPS.put(Constants.PARALLEL, PARALLEL_SYNONYMS);
        LOOKUPS.put(Constants.RISE, RISE_SYNONYMS);
        LOOKUPS.put(Constants.SO, SO_SYNONYMS);
        LOOKUPS.put(Constants.STAY_SAME, STAY_SAME_SYNONYMS);
        LOOKUPS.put(Constants.UNTIL, UNTIL_SYNONYMS);
    }

    private boolean randomise = true;

    @Override
    public String getSynonym(final String replaceMe) {
        String replacement = "";
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
