package uk.co.lgs.text.service.value;

/**
 * For formatting values.
 * 
 * @author bouncysteve
 *
 */
public interface ValueService {

    /**
     * Apply the given units to the value and return a string with the value
     * appropriately formatted, with units prefixed or post-fixed as
     * appropriate.
     *
     * @param value
     * @param units
     * @return
     */
    String formatValueWithUnits(double value, String units);

}
