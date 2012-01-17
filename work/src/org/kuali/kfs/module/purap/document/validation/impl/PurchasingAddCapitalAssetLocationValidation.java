/*
 * Copyright 2009 The Kuali Foundation
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

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class PurchasingAddCapitalAssetLocationValidation extends GenericValidation {

    protected CapitalAssetLocation location;
    protected ParameterService parameterService;
    protected PurchasingService purchasingService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        // TODO: Move this into CABModuleService?
        // Retrieve and evaluate the parameter which determines whether location's address is required.
        // CHARTS_REQUIRING_LOCATIONS_ADDRESS_ON_(REQUISITION/PURCHASE_ORDER)
        Map<String, String> fieldValues = new HashMap<String, String>();
        
        //List<Parameter> results = getParameterService().retrieveParametersGivenLookupCriteria(fieldValues);
        // If the location's address is required, enforce the validation of the individual fields of the address.
        
        valid = getPurchasingService().checkCapitalAssetLocation(getLocation());
        valid &= getPurchasingService().checkValidRoomNumber(getLocation());
        
        //valid = purchasingService.checkCapitalAssetLocation(getLocation());
        //valid &= purchasingService.checkValidRoomNumber(getLocation());
        return valid;
    }

    public CapitalAssetLocation getLocation() {
        return location;
    }

    public void setLocation(CapitalAssetLocation location) {
        this.location = location;
    }

    protected ParameterService getParameterService() {
        if ( parameterService == null ) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }
    
    protected PurchasingService getPurchasingService() {
        if ( parameterService == null ) {
            purchasingService = SpringContext.getBean(PurchasingService.class);
        }
        return purchasingService;
    }

}
