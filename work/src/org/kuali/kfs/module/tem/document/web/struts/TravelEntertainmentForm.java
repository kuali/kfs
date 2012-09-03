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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.Attendee;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.bean.TravelEntertainmentMvcWrapperBean;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.web.ui.ExtraButton;

public class TravelEntertainmentForm extends TravelFormBase implements TravelEntertainmentMvcWrapperBean {

    private Date startDate;
    private Date endDate;
    private boolean canPrintHostCertification;
    private boolean displayNonEmployeeCheckBox;
    private boolean canUnmask = false;

    private Attendee newAttendeeLine;
    private List<Attendee> newAttendeeLines;
    private FormFile attendeesImportFile;

    private String attendesTagGroupLabelName = TemConstants.Attendee.ATTENDEES_GROUP_LABEL_NAME;

    public TravelEntertainmentForm() {
        super();

        setNewAttendeeLine(new Attendee());
        List<Attendee> attendee = new ArrayList<Attendee>();
        attendee.add(new Attendee());
        setNewAttendeeLines(attendee);
    }

    @Override
    public void populate(final HttpServletRequest request) {
        super.populate(request);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public TravelEntertainmentDocument getEntertainmentDocument() {
        return (TravelEntertainmentDocument) getDocument();
    }

    @Override
    protected String getDocumentIdentifierFieldName() {
        return "travelDocumentIdentifier";
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "ENT";
    }

    public String getAttendesTagGroupLabelName() {
        return attendesTagGroupLabelName;
    }

    public void setAttendesTagGroupLabelName(String attendesTagGroupLabelName) {
        this.attendesTagGroupLabelName = attendesTagGroupLabelName;
    }

    protected Map<String, ExtraButton> createButtonsMap() {
        final HashMap<String, ExtraButton> result = new HashMap<String, ExtraButton>();

        result.putAll(createDVREQSExtraButtonMap());

        return result;
    }

    @Override
    public List<ExtraButton> getExtraButtons() {
        super.getExtraButtons();
        final Map<String, ExtraButton> buttonsMap = createButtonsMap();
        
        debug("Creating button map");
        if (!SpringContext.getBean(TravelDocumentService.class).isUnsuccessful(this.getTravelDocument())) {
            if (getEntertainmentDocument().canPayDVToVendor()) {
                extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.payDVToVendor"));
            }
            if (getEntertainmentDocument().canCreateREQSForVendor()) {
                extraButtons.add((ExtraButton) buttonsMap.get("methodToCall.createREQSForVendor"));
            }
        }
        return extraButtons;
    }

    public boolean isCanUnmask() {
        return canUnmask;
    }

    public void setCanUnmask(boolean canUnmask) {
        this.canUnmask = canUnmask;
    }

    @Override
    public Attendee getNewAttendeeLine() {
        return newAttendeeLine;
    }

    @Override
    public void setNewAttendeeLine(Attendee newAttendeeLine) {
        this.newAttendeeLine = newAttendeeLine;
    }

    @Override
    public List<Attendee> getNewAttendeeLines() {
        return newAttendeeLines;
    }

    @Override
    public void setNewAttendeeLines(List<Attendee> newAttendeeLines) {
        this.newAttendeeLines = newAttendeeLines;
    }

    public FormFile getAttendeesImportFile() {
        return attendeesImportFile;
    }

    public void setAttendeesImportFile(FormFile attendeesImportFile) {
        this.attendeesImportFile = attendeesImportFile;
    }

    public boolean isCanPrintHostCertification() {
        return getEntertainmentDocument().canShowHostCertification();
    }

    public void setCanPrintHostCertification(boolean canPrintHostCertification) {
        this.canPrintHostCertification = canPrintHostCertification;
    }

    public boolean isDisplayNonEmployeeCheckBox() {
        return getEntertainmentDocument().canDisplayNonEmployeeCheckbox();
    }

    public void setDisplayNonEmployeeCheckBox(boolean displayNonEmployeeCheckBox) {
        this.displayNonEmployeeCheckBox = displayNonEmployeeCheckBox;
    }

}
