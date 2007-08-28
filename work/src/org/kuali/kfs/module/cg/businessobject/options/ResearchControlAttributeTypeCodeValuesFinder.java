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
package org.kuali.module.kra.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.kra.routingform.bo.ControlAttributeType;
import org.kuali.module.kra.service.ResearchDocumentControlAttributeTypeService;

/**
 * This class is used to acquire and build a collection of possible ControlAttributeTypes that can be used to generate 
 * user drop downs.
 */
public class ResearchControlAttributeTypeCodeValuesFinder extends KeyValuesBase {
    
    /**
     * 
     * Constructs a ResearchControlAttributeTypeCodeValuesFinder.java.
     */
    public ResearchControlAttributeTypeCodeValuesFinder() {
        super();
    }
    
    /**
     * Retrieves the list of possible control attribute type codes from the DB and builds a KeyLabelPair collection 
     * from these values.
     * 
     * @return A collection of KeyLabelPair objects representing the possible ControlAttributeType codes.
     * 
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        
        List<ControlAttributeType> controlAttributeTypeCodes = new ArrayList(SpringContext.getBean(ResearchDocumentControlAttributeTypeService.class).getControlAttributeTypeCodes());
        List<KeyLabelPair> controlAttributeTypeCodePairs = new ArrayList(3);

        for(ControlAttributeType controlAttributeType : controlAttributeTypeCodes) {
            
            String description = "";
            if(ControlAttributeType.TYPE_CODE_A.equalsIgnoreCase(controlAttributeType.getControlAttributeTypeCode())) {
                description = ControlAttributeType.TYPE_CODE_A_DESC;
            } else if(ControlAttributeType.TYPE_CODE_S.equalsIgnoreCase(controlAttributeType.getControlAttributeTypeCode())) {
                description = ControlAttributeType.TYPE_CODE_S_DESC;
            } else if(ControlAttributeType.TYPE_CODE_D.equals(controlAttributeType.getControlAttributeTypeCode())) {
                description = ControlAttributeType.TYPE_CODE_D_DESC;
            }
            
            controlAttributeTypeCodePairs.add(new KeyLabelPair(controlAttributeType.getControlAttributeTypeCode(), controlAttributeType.getControlAttributeTypeCode()+" - "+description));
        }

        return controlAttributeTypeCodePairs;
    }

}
