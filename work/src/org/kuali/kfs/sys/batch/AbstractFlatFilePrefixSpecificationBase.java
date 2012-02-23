package org.kuali.kfs.sys.batch;

import java.util.List;

import org.springframework.util.StringUtils;

/**
 * The abstract base class of the configuration element which specifies how to parse flat files
 */
public abstract class AbstractFlatFilePrefixSpecificationBase extends AbstractFlatFileSpecificationBase {
    protected List<String> insignificantPrefixes;
    protected int prefixStartingPosition = 0;

    /**
     * This method determine the class of the given line. It checks the prefix list to see if the line started with what ever the
     * prefix of the line and returns the class; if not , it returns the default class.
     * @param line the line to determine the class of
     * @see org.kuali.kfs.sys.batch.FlatFileSpecification#determineClassForLine(String)
     */
    public Class<?> determineClassForLine(String line) {
        if (line != null) {
            if (objectSpecifications != null && !objectSpecifications.isEmpty()) {
                for (FlatFileObjectSpecification objectSpecification : objectSpecifications) {
                    final FlatFilePrefixObjectSpecification prefixObjectSpecification = (FlatFilePrefixObjectSpecification)objectSpecification;
                    String prefix = prefixObjectSpecification.getLinePrefix();
                    if ((prefix != null) && (line.length() >= (prefixStartingPosition + prefix.length())) && 
                            (line.substring(prefixStartingPosition, prefixStartingPosition + prefix.length()).equals(prefix))) {
                        return objectSpecification.getBusinessObjectClass();
                    }
                }
            }
            if (insignificantPrefixes != null && !insignificantPrefixes.isEmpty()) {
                for (String insignificantPrefix : insignificantPrefixes) {
                    if ((line.length() >= (prefixStartingPosition + insignificantPrefix.length())) && 
                            line.substring(prefixStartingPosition, prefixStartingPosition + insignificantPrefix.length()).
                            equals(insignificantPrefix)) {
                        return null; // don't return any class for an insignificant prefix
                    }
                }
            }
            return defaultBusinessObjectClass;
        }

        return null;
    }

    /**
     * Sets the list of prefixes which mean that the line is not to be parsed
     * @param insignificantPrefixes
     */
    public void setInsignificantPrefixes(List<String> insignificantPrefixes) {
        this.insignificantPrefixes = insignificantPrefixes;
    }

    /**
     * Determines where the starting position in the String to look for the prefix substring is; if not set, defaults to 0, the beginning of the String
     * @param prefixStartingPosition the starting position in the String of the prefix substring
     */
    public void setPrefixStartingPosition(int prefixStartingPosition) {
        this.prefixStartingPosition = prefixStartingPosition;
    }
}
