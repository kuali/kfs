/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.bo;

import org.kuali.core.bo.AttributeReferenceDummy;

public class GenericAttributes extends AttributeReferenceDummy {

    private String moduleCode;
    private String searchType;
    private String displayType;
    private String documentTotalAmount;
    private String routingAttributeTitle;

    public GenericAttributes() {
        super();
    }

    public String getDocumentTotalAmount() {
        return documentTotalAmount;
    }

    public void setDocumentTotalAmount(String documentTotalAmount) {
        this.documentTotalAmount = documentTotalAmount;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String changedModuleCodes) {
        this.moduleCode = changedModuleCodes;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getRoutingAttributeTitle() {
        return routingAttributeTitle;
    }

    public void setRoutingAttributeTitle(String routingAttributeTitle) {
        this.routingAttributeTitle = routingAttributeTitle;
    }
}
