/*
 * Copyright 2012 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_UPLOADFILE_NULL;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.validation.event.AddGroupTravelLineEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Fires when the import group travelers button is clicked. Handles importing {@link GroupTraveler}
 * instances from a CSV formatted file
 *  
 */
public class UploadGroupTravelersEvent implements Observer {
    
    public static Logger LOG = Logger.getLogger(UploadGroupTravelersEvent.class);
    
    private static final int WRAPPER_ARG_IDX       = 0;
    private static final int FILE_CONTENTS_ARG_IDX = 1;
    protected static final String[] GROUP_TRAVELER_ATTRIBUTE_NAMES = { "travelerTypeCode", "groupTravelerEmpId", "name" };
    
    protected TravelDocumentService travelDocumentService;
    protected KualiRuleService ruleService;
    
    @Override
    public void update(Observable arg0, Object arg1) {
        if (!(arg1 instanceof Object[])) {
            return;
        }
        final Object[] args = (Object[]) arg1;
        LOG.debug(args[WRAPPER_ARG_IDX]);
        if (!(args[WRAPPER_ARG_IDX] instanceof TravelMvcWrapperBean)) {
            return;
        }
        final TravelMvcWrapperBean wrapper = (TravelMvcWrapperBean) args[WRAPPER_ARG_IDX];
        final String fileContents          = (String) args[FILE_CONTENTS_ARG_IDX];
        final TravelDocument document = wrapper.getTravelDocument();
        
        final String tabErrorKey = "groupTraveler";
        try {
            final List<GroupTraveler> importedGroupTravelers = getTravelDocumentService().importGroupTravelers(document, fileContents);
            
            // validate imported items
            boolean allPassed = true;
            int itemLineNumber = 0;
            for (final GroupTraveler traveler : importedGroupTravelers) {
                final AddGroupTravelLineEvent event = new AddGroupTravelLineEvent("newGroupTravelerLine", document, traveler);
                allPassed &= getRuleService().applyRules(event);
            }
            if (allPassed) {
                for (final GroupTraveler traveler : importedGroupTravelers) {
                    document.addGroupTravelerLine(traveler);
                }
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            GlobalVariables.getMessageMap().putError(tabErrorKey, ERROR_UPLOADFILE_NULL);
        }
    }
  
  
    /**
     * Gets the travelReimbursementService attribute.
     * 
     * @return Returns the travelReimbursementService.
     */
    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }
    
    public void setTravelDocumentService(final TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    /**
     * Gets the kualiRulesService attribute.
     * 
     * @return Returns the kualiRuleseService.
     */
    public KualiRuleService getRuleService() {
        return ruleService;
    }
    
    public void setRuleService(final KualiRuleService ruleService) {
        this.ruleService = ruleService;
    }
}
