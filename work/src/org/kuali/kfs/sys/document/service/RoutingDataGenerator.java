/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.service;

import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kns.document.Document;

/**
 * Contract for classes which intend to create RoutingData from document content
 */
public interface RoutingDataGenerator {
    
    /**
     * Given a document, generates a RoutingData object 
     * @param document the document to get RoutingData from
     * @return a populated RoutingData object
     */
    public RoutingData generateRoutingData(Document document);
}
