package uk.co.lgs.text.service.synonym;

/**
 * For performing lookups of common words and phrases.
 *
 * @author bouncysteve
 *
 */
public interface SynonymService {

    /**
     * Given a string, perform a lookup and return a synonym, a bit like a
     * thesarus.
     *
     * @param lookup
     * @return
     */
    String getSynonym(String lookup);

    /**
     * Enable or disable the ability to randomise the returned words or phrases.
     * 
     * @param randomise
     */
    void setRandomise(boolean randomise);

}
