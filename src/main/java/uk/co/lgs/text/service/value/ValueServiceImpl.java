package uk.co.lgs.text.service.value;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * I am responsible for formatting values (e.g. 1 day, not 1.0 days).
 *
 * @author bouncysteve
 *
 */
@Component
public class ValueServiceImpl implements ValueService {

    private static final List<String> PREFIX = Arrays.asList("£", "$");

    /**
     * Only apply decimal places as needed.
     *
     */
    private static final DecimalFormat f = new DecimalFormat("0.##");

    @Override
    public String formatValueWithUnits(final double value, final String units) {
        String valueString = f.format(value);
        if (isPrefixable(units)) {
            valueString = units + valueString;
        } else {
            valueString = valueString + (needsLeadingSpace(units) ? " " : "") + units;
        }
        return valueString;
    }

    private boolean isPrefixable(final String units) {
        for (final String prefix : PREFIX) {
            if (prefix.equals(units)) {
                return true;
            }
        }
        return false;
    }

    private boolean needsLeadingSpace(final String units) {
        if ("%".equals(units)) {
            return false;
        }
        return true;
    }
}
