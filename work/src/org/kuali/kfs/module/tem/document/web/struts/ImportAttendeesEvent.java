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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.TemPropertyConstants.NEW_ATTENDEE_LINE;
import static org.kuali.kfs.module.tem.TemPropertyConstants.AttendeeProperties.ATTENDEE_TYPE;
import static org.kuali.kfs.module.tem.TemPropertyConstants.AttendeeProperties.COMPANY;
import static org.kuali.kfs.module.tem.TemPropertyConstants.AttendeeProperties.NAME;
import static org.kuali.kfs.module.tem.TemPropertyConstants.AttendeeProperties.TITLE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.businessobject.Attendee;
import org.kuali.kfs.module.tem.businessobject.options.AttendeeTypeValuesFinder;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelEntertainmentDocumentService;
import org.kuali.kfs.module.tem.document.validation.event.AddAttendeeLineEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelEntertainmentMvcWrapperBean;
import org.kuali.kfs.module.tem.exception.UploadParserException;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Event that handles import of attendees from CSV File. Activated by the "Import Attendees" link
 */
public class ImportAttendeesEvent implements Observer {

    public static Logger LOG = Logger.getLogger(ImportAttendeesEvent.class);

    private static final int WRAPPER_ARG_IDX = 0;
    private static final int FILE_CONTENTS_ARG_IDX = 1;

    public static final String[] ATTENDEE_ATTRIBUTE_NAMES = { ATTENDEE_TYPE, COMPANY, TITLE, NAME };
    public static final Integer[] MAX_LENGTH = { 10, 40, 40, 40 };

    protected KualiRuleService kualiRuleService;
    protected TravelDocumentService travelDocumentService;
    protected TravelEntertainmentDocumentService travelEntertainmentDocumentService;

    @Override
    public void update(final Observable observable, Object arg) {

        final Object[] args = (Object[]) arg;
        LOG.debug(args[WRAPPER_ARG_IDX]);
        if (!(args[WRAPPER_ARG_IDX] instanceof TravelEntertainmentMvcWrapperBean)) {
            return;
        }

        final TravelEntertainmentMvcWrapperBean wrapper = (TravelEntertainmentMvcWrapperBean) args[WRAPPER_ARG_IDX];
        final String fileContents = (String) args[FILE_CONTENTS_ARG_IDX];
        final TravelEntertainmentDocument document = (TravelEntertainmentDocument) wrapper.getTravelDocument();

        List<Attendee> importedAttendees = null;
        final String tabErrorKey = "attendee";

        try {
            final Map<String, List<String>> defaultValues = new HashMap<String, List<String>>();
            final AttendeeTypeValuesFinder finder = new AttendeeTypeValuesFinder();

            final List<String> defaultList = new ArrayList<String>();
            for (final KeyValue pair : finder.getKeyValues()) {
                if (!pair.getValue().equals("")) {
                    defaultList.add(pair.getKey().toString());
                    defaultList.add(pair.getValue());
                }
            }

            defaultValues.put(ATTENDEE_TYPE, defaultList);
            importedAttendees = getTravelDocumentService().importFile(fileContents, Attendee.class, ATTENDEE_ATTRIBUTE_NAMES, defaultValues, MAX_LENGTH, tabErrorKey);

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

    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    public void setTravelDocumentService(final TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    public TravelEntertainmentDocumentService getTravelEntertainmentDocumentService() {
        return travelEntertainmentDocumentService;
    }

    public void setTravelEntertainmentDocumentService(TravelEntertainmentDocumentService travelEntertainmentDocumentService) {
        this.travelEntertainmentDocumentService = travelEntertainmentDocumentService;
    }

    public void setRuleService(final KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    protected KualiRuleService getRuleService() {
        return kualiRuleService;
    }
}
