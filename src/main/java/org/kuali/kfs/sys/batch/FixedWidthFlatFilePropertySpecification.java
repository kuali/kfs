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


/**
 * The FixedWidthFlatFilePropertySpecification is used to
 * specify details relating to record in the given line of the input file.
 */
public class FixedWidthFlatFilePropertySpecification extends AbstractFlatFilePropertySpecificationBase {
    protected int start;
    protected int end;

    /**
     * @return the beginning index of the substring to parse
     */
    public int getStart() {
        return start;
    }

    /**
     * Sets the beginning index of the substring to parse
     * @param start the beginning index of the substring to parse
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return the ending index of the substring to parse; if empty, line will be parsed to end of String
     */
    public int getEnd() {
        return end;
    }

    /**
     * Sets the ending index of the substring to parse; if not set, line will be parsed to end of String
     * @param end the ending index of the substring to parse
     */
    public void setEnd(int end) {
        this.end = end;
    }
}
