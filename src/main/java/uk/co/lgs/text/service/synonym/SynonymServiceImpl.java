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
            Arrays.asList("fall", "decline", "drop", "shrink", "decrease", "lower", "reduce")); // ("weaken","diminish",
                                                                                                // "slide",)
    private static final Set<String> RISE_SYNONYMS = new HashSet<>(
            Arrays.asList("rise", "increase", "grow", "improve", "progress", "build", "go up")); // ("ascend","extend",
                                                                                                 // "advance",)
    private static final Set<String> CONSTANT_SYNONYMS = new HashSet<>(
            Arrays.asList("is constant", "remain", "continue", "freeze", "stop", "persist", "rest"));// ("endure",
    private static final Random random = new Random();

    private static final Map<String, Set<String>> LOOKUPS;

    static {
        LOOKUPS = new HashMap<>();
        LOOKUPS.put(Constants.FALL, FALL_SYNONYMS);
        LOOKUPS.put(Constants.RISE, RISE_SYNONYMS);
        LOOKUPS.put(Constants.CONSTANT, CONSTANT_SYNONYMS);
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
