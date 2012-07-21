/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.service;

import java.util.Collection;

import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.rice.kew.exception.WorkflowException;

public interface TravelRelocationService {
    
    /**
     * Adding properties listeners
     * 
     * @param relocation
     */
    void addListenersTo(final TravelRelocationDocument relocation);
    
    /**
     * Locate all {@link TravelRelocationDocument} instances with the same
     * <code>travelDocumentIdentifier</code>
     *
     * @param travelDocumentIdentifier to locate {@link TravelRelocationDocument} instances
     * @return {@link Collection} of {@link TravelRelocationDocument} instances
     */
    public Collection<TravelRelocationDocument> findByIdentifier(String travelDocumentIdentifier);
    
    public TravelRelocationDocument find(final String documentNumber)throws WorkflowException;
       
    /**
     * This method uses the values provided to build and populate a cover sheet associated with a given {@link Document}.
     * 
     * @param document {@link TravelRelocationDocument} to generate a coversheet for 
     * @return {@link Coversheet} instance
     */
    Coversheet generateCoversheetFor(final TravelRelocationDocument document) throws Exception;
    
}
