/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.web;

import java.util.Set;

public interface CodeDescriptionFormatter {
    /**
     * Given a set of codes, this class will form a format a string that includes their description Example: Codes A, B, C may be
     * formatted to "A, descA; B, descB; and C; descC"
     * 
     * @param values
     * @param startConjunction a conjunction or phrase to be used for the beginning of the series (e.g. "either", "neither", "any 3
     *        of" etc.)
     * @param endConjunction a conjunction to be used for the beginning of the series (e.g. "and", "or", "and/or")
     * @return
     */
    public String getFormattedStringWithDescriptions(Set values, String startConjunction, String endConjunction);
}
