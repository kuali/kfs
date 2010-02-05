/*
 * Copyright 2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
