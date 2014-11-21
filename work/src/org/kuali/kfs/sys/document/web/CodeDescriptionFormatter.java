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
