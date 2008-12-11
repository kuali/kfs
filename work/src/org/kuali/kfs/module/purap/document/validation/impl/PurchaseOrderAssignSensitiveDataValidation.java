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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * A validation that checks whether the given accounting line is accessible to the given user or not
 */
public class PurchaseOrderAssignSensitiveDataValidation extends GenericValidation {

    private PurchaseOrderDocument accountingDocumentForValidation;
    private List<SensitiveData> sensitiveDatas;
    
    /**
     * Applies rules for validation of the Split of PO and PO child documents
     * 
     * @param document  A PurchaseOrderDocument (or one of its children)
     * @return      True if all relevant validation rules are passed.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();        
        HashSet sdset = new HashSet();
        
        for (Object sdobj : sensitiveDatas) {
            SensitiveData sd = (SensitiveData)sdobj;
            if (!sd.isActive()) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ASSIGN_SENSITIVE_DATA_TAB_ERRORS, PurapKeyConstants.ERROR_ASSIGN_INACTIVE_SENSITIVE_DATA, sd.getSensitiveDataDescription());
                valid = false;                
            }
            else if (!sdset.add(sd.getSensitiveDataCode())) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ASSIGN_SENSITIVE_DATA_TAB_ERRORS, PurapKeyConstants.ERROR_ASSIGN_REDUNDANT_SENSITIVE_DATA, sd.getSensitiveDataDescription());
                valid = false;                                    
            }            
        }

        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }

 
    public PurchaseOrderDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    public void setAccountingDocumentForValidation(PurchaseOrderDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    public List<SensitiveData> getSensitiveDatas() {
        return sensitiveDatas;
    }

    public void setSensitiveDatas(List<SensitiveData> sensitiveDatas) {
        this.sensitiveDatas = sensitiveDatas;
    }

}

