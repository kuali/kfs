/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.document;

/**
 * A set of constants that show where in an array of a parsed collection criterion certain
 * portions of the criterion reside.
 */
public interface CorrectionCriterionIndices {
    /**
     * The index of the field name.
     */
    static final public int CRITERION_INDEX_FIELD_NAME = 0;
    /**
     * The index of the match operator.
     */
    static final public int CRITERION_INDEX_MATCH_OPERATOR = 1;
    /**
     * The index of the field value.
     */
    static final public int CRITERION_INDEX_FIELD_VALUE = 2;
}
