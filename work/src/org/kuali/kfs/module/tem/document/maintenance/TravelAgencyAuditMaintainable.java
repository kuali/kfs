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
package org.kuali.kfs.module.tem.document.maintenance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Section;

import static org.kuali.kfs.module.tem.util.BufferedLogger.*;

/**
 *
 * 
 * */
public class TravelAgencyAuditMaintainable extends FinancialSystemMaintainable {
    
	
    @Override
    public void setupNewFromExisting( MaintenanceDocument document, Map<String,String[]> parameters ) {
        super.setupNewFromExisting(document, parameters);
       
    }
    
    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getSections(org.kuali.rice.kns.document.MaintenanceDocument, org.kuali.rice.kns.maintenance.Maintainable)
     */
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(document, document.getOldMaintainableObject());
        return sections;
    }

  

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterEdit(document, parameters);
    }

  

    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);
    }

    @Override
    public Map populateBusinessObject(Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument, String methodToCall) {
        //populate maintenanceDocument with boNotes from this maintainable.
        List boNotes = maintenanceDocument.getBoNotes();
        if(boNotes == null){
            boNotes = new ArrayList();
        }else{
            boNotes.clear();
        }
        
        if(!this.getBusinessObject().getBoNotes().isEmpty()){
            boNotes.addAll(this.getBusinessObject().getBoNotes());
        }
        
        return super.populateBusinessObject(fieldValues, maintenanceDocument, methodToCall);
    }
    
    @Override
    public void prepareForSave() {            
        AgencyStagingData travelAgencyAudit = (AgencyStagingData) super.getBusinessObject();
        
        super.prepareForSave();
    }

	/**
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
	 */
	@Override
	public void saveBusinessObject() {
	    AgencyStagingData travelAgencyAudit = (AgencyStagingData) super.getBusinessObject();
        
        super.saveBusinessObject();
	}  

	/**
	 * 
	 * This method trims the descriptionText to 40 characters.
	 * @param descriptionText
	 * @return
	 */
	protected String trimDescription(String descriptionText) {
	    
        if (descriptionText.length() > 40) {
            descriptionText = descriptionText.substring(0, 39);
        }

        return descriptionText;
	}

}
