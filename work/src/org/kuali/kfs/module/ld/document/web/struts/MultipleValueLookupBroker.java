/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.labor.web.struts.form;

import org.kuali.core.web.struts.form.MultipleValueLookupForm;

/**
 * Interface for defining a <code>{@link ActionForm}</code> that has multiple value lookups on it; however,
 * it should not be implemented by a class that extends <code>{@link LookupForm}</code> or <code>{@link MultipleValueLookupForm}</code>
 * unless you have a really, really good reason.
 *
 * @see MultipleValueLookupForm
 */
public interface MultipleValueLookupBroker {

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#getRefreshCaller()
     */
    public String getRefreshCaller();

    /**
     *
     * @return String
     */
    public String getLookupResultsSequenceNumber();


    /**
     *
     * @param lookupResultsSequenceNumber
     */
    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber);


    /**
     *
     * @return String
     */
    public String getLookupResultsBOClassName();


    /**
     *
     * @param lookupResultsSequenceNumber
     */
    public void setLookupResultsBOClassName(String lookupResultsBOClassName);


    /**
     *
     * @return String
     */
    public String getLookedUpCollectionName();


    /**
     *
     * @param lookupResultsSequenceNumber
     */
    public void setLookedUpCollectionName(String lookedUpCollectionName);
}
