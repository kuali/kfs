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
package org.kuali.kfs.module.ld.document.web.struts;

/**
 * Interface for defining a <code>{@link ActionForm}</code> that has multiple value lookups on it; however, it should not be
 * implemented by a class that extends <code>{@link LookupForm}</code> or <code>{@link MultipleValueLookupForm}</code> unless
 * you have a really, really good reason.
 * 
 * @see MultipleValueLookupForm
 */
public interface MultipleValueLookupBroker {

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#getRefreshCaller()
     */
    public String getRefreshCaller();

    /**
     * Get lookup results sequence number.
     * 
     * @return String
     */
    public String getLookupResultsSequenceNumber();


    /**
     * Set lookup results sequence number.
     * 
     * @param lookupResultsSequenceNumber
     */
    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber);


    /**
     * Get lookup results business object class name.
     * 
     * @return String
     */
    public String getLookupResultsBOClassName();


    /**
     * Set lookup results business object class name.
     * 
     * @param lookupResultsSequenceNumber
     */
    public void setLookupResultsBOClassName(String lookupResultsBOClassName);


    /**
     * Get looked up collection name.
     * 
     * @return String
     */
    public String getLookedUpCollectionName();


    /**
     * Set looked up collection name.
     * 
     * @param lookupResultsSequenceNumber
     */
    public void setLookedUpCollectionName(String lookedUpCollectionName);
}
