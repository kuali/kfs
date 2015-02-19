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
package org.kuali.kfs.sys.document.validation.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.Validation;

/**
 * Simple test to at least validation that all of the validation beans are at least retrievable from
 * the SpringContext...ie, that the Spring configuration is correct for validation beans at least
 */
@ConfigureContext
public class ValidationTest extends KualiTestBase {
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ValidationTest.class);
    
    /**
     * Tests that all validations are at least configured correctly
     */
    public void testAllValidationRetrieval() {
        Set<String> badValidationNames = new HashSet<String>();
        
        final Map<String, Validation> validations = SpringContext.getBeansOfType(Validation.class);
        
        for (String validationName : validations.keySet()) {
            try {
                final Validation validation = validations.get(validationName);
            } catch (Exception e) {
                badValidationNames.add(validationName+" "+e.toString());
            }
        }

        LOG.info(StringUtils.join(badValidationNames, "\n"));
        
        assertEquals("Bad validations: "+StringUtils.join(badValidationNames, ","), new Integer(0), new Integer(badValidationNames.size()));
    }
}
