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
 * Interface that declares extra methods needed for renderable elements that have headers
 */
public interface TableJoiningWithHeader extends TableJoining {
    /**
     * Returns the property of the accounting line business object that can be used to find the label for the given renderable element
     * @param renderingContext the context the header will be rendered to
     * @return the property to lookup the label in the data dictionary
     */
    public abstract HeaderLabel createHeaderLabel();
    
    /**
     * Will this table joining element actually end up hidden?  Then we best not create a header cell for it
     * @return true if the table joiner will be hidden, false otherwise - in which case a header cell will be rendered
     */
    public abstract boolean isHidden();
}
