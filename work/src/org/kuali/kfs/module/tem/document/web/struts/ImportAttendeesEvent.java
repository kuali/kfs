/*
 * Copyright 2011 The Kuali Foundation
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

import static org.kuali.kfs.module.tem.TemPropertyConstants.NEW_ATTENDEE_LINE;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.kuali.kfs.module.tem.businessobject.Attendee;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.service.TravelEntertainmentDocumentService;
import org.kuali.kfs.module.tem.document.validation.event.AddAttendeeLineEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelEntertainmentMvcWrapperBean;
import org.kuali.kfs.module.tem.exception.UploadParserException;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Event that handles import of attendees from CSV File. Activated by the "Import Attendees" link
 *  
 *  
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class ImportAttendeesEvent implements Observer {
    private static final int WRAPPER_ARG_IDX       = 0;
    private static final int FILE_CONTENTS_ARG_IDX = 1;

    protected KualiRuleService kualiRuleService;
    protected TravelDocumentService travelDocumentService;
    protected TravelEntertainmentDocumentService travelEntertainmentDocumentService;

    @Override
    public void update(final Observable observable, Object arg) {
        if (!(arg instanceof TravelEntertainmentMvcWrapperBean)) {
            return;
        }
        
        final Object[] args = (Object[]) arg1;
        debug(args[WRAPPER_ARG_IDX]);
        if (!(args[WRAPPER_ARG_IDX] instanceof TravelEntertainmentMvcWrapperBean)) {
            return;
        }

        final TravelEntertainmentMVCWrapperBean wrapper = (TravelEntertainmentMVCWrapperBean) args[WRAPPER_ARG_IDX];
        final String fileContents                       = (String) args[FILE_CONTENTS_ARG_IDX];
        final TravelEntertainmentDocument document      = (TravelEntertainmentDocument) wrapper.getTravelDocument();

        List<Attendee> importedAttendees = null;
        final String tabErrorKey = "attendee";
        
        try {
            final Map<String, List<String>> defaultValues = new HashMap<String, List<String>>();
            final List<String> defaultList                = new ArrayList<String>();
            final AttendeeTypeValuesFinder finder         = new AttendeeTypeValuesFinder();
            final List<KeyLabelPair> values               = finder.getKeyValues();
            for (final KeyLabelPair pair : values) {
                if (!pair.getLabel().equals("")) {
                    defaultList.add(pair.getKey().toString());
                    defaultList.add(pair.getLabel());
                }
            }
            
            defaultValues.put(TemPropertyConstants.AttendeeProperties.ATTENDEE_TYPE, defaultList);
            importedAttendees = getTravelDocumentService().importFile(reqForm.getAttendeesImportFile(), Attendee.class, ATTENDEE_ATTRIBUTE_NAMES, defaultValues, MAX_LENGTH, tabErrorKey);
            // importedAttendees = UploadParser.importFile(reqForm.getAttendeesImportFile(), Attendee.class, ATTENDEE_ATTRIBUTE_NAMES, tabErrorKey);
            
            // validate imported items
            boolean allPassed = true;
            int itemLineNumber = 0;
            for (final Attendee o : importedAttendees) {
                allPassed &= getRuleService().applyRules(new AddAttendeeLineEvent<Attendee>(NEW_ATTENDEE_LINE, document, o));
            }
            if (allPassed) {
                for (final Attendee attendee : importedAttendees) {
                    attendee.setDocumentNumber(document.getDocumentNumber());
                    document.getAttendee().add(attendee);
                }
            }
        }
        catch (UploadParserException e) {
            GlobalVariables.getMessageMap().putError(tabErrorKey, e.getErrorKey(), e.getErrorParameters());
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


    public TravelEntertainmentDocumentService getTravelEntertainmentDocumentService() {
        return travelEntertainmocumentumentService;
    }


    public void setTravelEntertainmentDocumentService(TravelEntertainmentDocumentService travelEntertainmentDocumentService) {
        this.travelEntertainmentDocumentService = travelEntertainmentDocumentService;
    }


    /**
     * Sets the kualiRulesService attribute.
     * 
     * @return Returns the kualiRuleService.
     */
    public void setRuleService(final KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    /**
     * Gets the kualiRulesService attribute.
     * 
     * @return Returns the kualiRuleseService.
     */
    protected KualiRuleService getRuleService() {
        return kualiRuleService;
    }
}