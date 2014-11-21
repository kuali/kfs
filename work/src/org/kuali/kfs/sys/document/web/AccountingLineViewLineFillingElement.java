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

/**
 * A contract for accounting line view layout elements which join tables as part of an AccountingLineViewLines element
 */
public interface AccountingLineViewLineFillingElement extends TableJoining, ReadOnlyable {
    /**
     * Finds the number of table cells this line expects to take up
     * @return the number of displayed table cells this line expects to render as
     */
    public abstract int getDisplayingFieldWidth();
    
    /**
     * A way to ask the line filling element if it wants its cell to be stretched to fill the line
     * @return true if the line filling element should stretch its cell to fill the line; if false (or if the line contains more than a single cell), the line will be padded out with an empty cell
     */
    public abstract boolean shouldStretchToFillLine();
}
