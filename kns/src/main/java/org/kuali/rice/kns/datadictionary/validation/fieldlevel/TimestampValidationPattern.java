/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary.validation.fieldlevel;

import org.kuali.rice.krad.datadictionary.validation.FieldLevelValidationPattern;

/**
 * Validation pattern for matching standard-format (yyyy-mm-dd hh:mm:ss.m) timestamps
 * 
 * 
 */
public class TimestampValidationPattern extends FieldLevelValidationPattern {

    /**
     * @see FieldLevelValidationPattern#getPatternTypeName()
     */
    protected String getPatternTypeName() {
        return "timestamp";
    }
}
