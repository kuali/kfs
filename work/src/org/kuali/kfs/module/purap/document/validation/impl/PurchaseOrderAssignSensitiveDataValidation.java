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

