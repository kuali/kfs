/*
 * Copyright 2007 The Kuali Foundation
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
