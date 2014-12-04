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
