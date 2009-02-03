/*
 * Copyright 2009 The Kuali Foundation.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.bo.Parameter;

public class PurchasingAddCapitalAssetLocationValidation extends GenericValidation {

    private PurapService purapService;
    private CapitalAssetLocation location;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        // TODO: Move this into CABModuleService?
        // Retrieve and evaluate the parameter which determines whether location's address is required.
        // CHARTS_REQUIRING_LOCATIONS_ADDRESS_ON_(REQUISITION/PURCHASE_ORDER)
        Map<String, String> fieldValues = new HashMap<String, String>();
        List<Parameter> results = purapService.getParametersGivenLikeCriteria(fieldValues);
        // If the location's address is required, enforce the validation of the individual fields of the address.
        return valid;
    }

    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public CapitalAssetLocation getLocation() {
        return location;
    }

    public void setLocation(CapitalAssetLocation location) {
        this.location = location;
    }

}
