/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject;

import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.kim.api.identity.PersonService; import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is business object for Agency Note
 */
public class AgencyNote extends PersistableBusinessObjectBase {

    private Integer noteIdentifier;
    private String agencyNumber;
    private String noteText;
    private Date notePostedDate;
    private String authorPrincipalId;
    private boolean active;

    private Person authorUniversal;

    /**
     * Default constructor
     */
    public AgencyNote() {
        super();
        this.setNotePostedDateToCurrent();
        this.setAuthorUniversalToCurrentUser();
    }

    /**
     * Sets the {@link #setNotePostedDate(Date)} to the current time.
     */
    public void setNotePostedDateToCurrent() {
        final Date now = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        this.setNotePostedDate(now);
    }

    /**
     * Sets the authorUniversal attribute.
     * 
     * @param authorUniversal The authorUniversal to set.
     */
    public void setAuthorUniversalToCurrentUser() {
        if (ObjectUtils.isNotNull(GlobalVariables.getUserSession()) && ObjectUtils.isNotNull(GlobalVariables.getUserSession().getPerson())) {
            authorUniversal = GlobalVariables.getUserSession().getPerson();
            authorPrincipalId = authorUniversal.getPrincipalId();
        }
    }

    /**
     * Gets the noteIdentifier attribute.
     * 
     * @return Returns noteIdentifier.
     */
    public Integer getNoteIdentifier() {
        return noteIdentifier;
    }

    /**
     * Sets the noteIdentifier attribute.
     * 
     * @param noteIdentifier The noteIdentifier to set.
     */
    public void setNoteIdentifier(Integer noteIdentifier) {
        this.noteIdentifier = noteIdentifier;
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns agencyNumber.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     * 
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the noteText attribute.
     * 
     * @return Returns noteText.
     */
    public String getNoteText() {
        return noteText;
    }

    /**
     * Sets the noteText attribute.
     * 
     * @param noteText The noteText to set.
     */
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    /**
     * Gets the notePostedDate attribute.
     * 
     * @return Returns the notePostedDate.
     */
    public Date getNotePostedDate() {
        return notePostedDate;
    }

    /**
     * Sets the notePostedDate attribute.
     * 
     * @param notePostedDate The notePostedDate to set.
     */
    public void setNotePostedDate(Date notePostedDate) {
        this.notePostedDate = notePostedDate;
    }

    /**
     * Gets the authorPrincipalId attribute.
     * 
     * @return Returns the authorPrincipalId.
     */
    public String getAuthorPrincipalId() {
        return authorPrincipalId;
    }

    /**
     * Sets the authorPrincipalId attribute.
     * 
     * @param authorPrincipalId The authorPrincipalId to set.
     */
    public void setAuthorPrincipalId(String authorPrincipalId) {
        this.authorPrincipalId = authorPrincipalId;
    }

    /**
     * Gets the authorUniversal attribute.
     * 
     * @return Returns the authorUniversal.
     */
    public Person getAuthorUniversal() {
        authorUniversal = org.kuali.rice.kim.service.SpringContext.getBean(PersonService.class).updatePersonIfNecessary(authorPrincipalId, authorUniversal);
        return authorUniversal;
    }

    /**
     * Sets the authorUniversal attribute value.
     * 
     * @param authorUniversal The authorUniversal to set.
     */
    public void setAuthorUniversal(Person authorUniversal) {
        this.authorUniversal = authorUniversal;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.AGENCY_NUMBER, this.agencyNumber);
        m.put("noteIdentifier", this.noteIdentifier);
        if (ObjectUtils.isNotNull(this.noteText)) {
            m.put("noteText", this.noteText);
        }
        return m;
    }

}
