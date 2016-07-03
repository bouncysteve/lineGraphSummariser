package uk.co.lgs.text.service.synonym;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * I am responsible for returning random words or phrases equivalent to the
 * passed word.
 *
 * @author bouncysteve
 *
 */
@Component
public class SynonymServiceImpl implements SynonymService {

    private static final Set<String> FALL_SYNONYMS = new HashSet<>(
            Arrays.asList("fall", "decline", "drop", "shrink", "decrease", "lower", "reduce", "show a downward trend")); // ("weaken","diminish",
    // "slide",)
    private static final Set<String> RISE_SYNONYMS = new HashSet<>(
            Arrays.asList("rise", "increase", "grow", "improve", "progress", "build", "go up", "show an upward trend")); // ("ascend","extend",
    // "advance",)
    private static final Set<String> CONSTANT_SYNONYMS = new HashSet<>(Arrays.asList("be constant", "remain",
            "continue", "freeze", "stop", "persist", "rest", "hold a constant value"));// ("endure",
    private static final Set<String> CONVERGING_SYNONYMS = new HashSet<>(Arrays.asList("decreases", "reduces"));
    private static final Set<String> DIVERGING_SYNONYMS = new HashSet<>(Arrays.asList("increases", "gets larger"));
    private static final Set<String> PARALLEL_SYNONYMS = new HashSet<>(
            Arrays.asList("stay the same", "be constant", "does not change"));
    private static final Random random = new Random();

    private static final Map<String, Set<String>> LOOKUPS;

    static {
        LOOKUPS = new HashMap<>();
        LOOKUPS.put(Constants.FALL, FALL_SYNONYMS);
        LOOKUPS.put(Constants.RISE, RISE_SYNONYMS);
        LOOKUPS.put(Constants.CONSTANT, CONSTANT_SYNONYMS);
        LOOKUPS.put(Constants.CONVERGE, CONVERGING_SYNONYMS);
        LOOKUPS.put(Constants.DIVERGE, DIVERGING_SYNONYMS);
        LOOKUPS.put(Constants.PARALLEL, PARALLEL_SYNONYMS);
    }

    private boolean randomise = true;

    @Override
    public String getSynonym(final String replaceMe) {
        String replacement = "";
        final Set<String> synonyms = LOOKUPS.get(replaceMe);
        if (null != synonyms && !synonyms.isEmpty()) {
            replacement = getSynonym(synonyms);
        }
        return replacement;
    }

    private String getSynonym(final Set<String> synonyms) {
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
