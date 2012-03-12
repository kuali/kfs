/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * A validation that checks whether the given accounting line is accessible to the given user or not
 */
public class PurchaseOrderAssignSensitiveDataValidation extends GenericValidation {

    private PurchaseOrderDocument accountingDocumentForValidation;
    private String sensitiveDataAssignmentReason;
    private List<SensitiveData> sensitiveDatasAssigned;
    
    /**
     * Applies rules for validation of sensitive data assignment to the PurchaseOrder document:
     * The assignment reason must not be empty; 
     * The assigned sensitive data entries must be active and not redundant.
     * 
     * @param document  A PurchaseOrderDocument (or one of its children)
     * @return      True if all relevant validation rules are passed.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        GlobalVariables.getMessageMap().clearErrorPath();        
        HashSet<String> sdset = new HashSet<String>();
        
        if (StringUtils.isEmpty(sensitiveDataAssignmentReason)) {
            GlobalVariables.getMessageMap().putError(PurapConstants.ASSIGN_SENSITIVE_DATA_TAB_ERRORS, PurapKeyConstants.ERROR_ASSIGN_SENSITIVE_DATA_REASON_EMPTY);
            valid = false;                            
        }
        
        for (Object sdobj : sensitiveDatasAssigned) {
            SensitiveData sd = (SensitiveData)sdobj;
            if (!sd.isActive()) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ASSIGN_SENSITIVE_DATA_TAB_ERRORS, PurapKeyConstants.ERROR_ASSIGN_SENSITIVE_DATA_INACTIVE, sd.getSensitiveDataDescription());
                valid = false;                
            }
            else if (!sdset.add(sd.getSensitiveDataCode())) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ASSIGN_SENSITIVE_DATA_TAB_ERRORS, PurapKeyConstants.ERROR_ASSIGN_SENSITIVE_DATA_REDUNDANT, sd.getSensitiveDataDescription());
                valid = false;                                    
            }            
        }

        GlobalVariables.getMessageMap().clearErrorPath();
        return valid;
    }
 
    public PurchaseOrderDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    public void setAccountingDocumentForValidation(PurchaseOrderDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
    
    public String getSensitiveDataAssignmentReason() {
        return sensitiveDataAssignmentReason;
    }

    public void setSensitiveDataAssignmentReason(String sensitiveDataAssignmentReason) {
        this.sensitiveDataAssignmentReason = sensitiveDataAssignmentReason;
    }

    public List<SensitiveData> getSensitiveDatasAssigned() {
        return sensitiveDatasAssigned;
    }

    public void setSensitiveDatasAssigned(List<SensitiveData> sensitiveDatasAssigned) {
        this.sensitiveDatasAssigned = sensitiveDatasAssigned;
    }

}

