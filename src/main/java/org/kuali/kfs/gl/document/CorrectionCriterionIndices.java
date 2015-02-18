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
