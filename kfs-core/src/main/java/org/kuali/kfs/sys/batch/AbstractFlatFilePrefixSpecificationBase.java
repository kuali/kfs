/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.batch;

import java.util.List;

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
    @Override
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
