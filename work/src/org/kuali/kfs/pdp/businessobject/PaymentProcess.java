/*
 * Copyright 2007 The Kuali Foundation
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
/*
 * Created on Aug 19, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.campus.CampusService;


/**
 * This class represents a Payment Process.
 */
public class PaymentProcess extends TimestampedBusinessObjectBase {
    private KualiInteger id;
    private Timestamp processTimestamp;
    private String campusCode;
    private String processUserId;
    private Person processUser;
    private boolean extractedInd;
    private boolean formattedIndicator;

    private Campus campus;

    /**
     * Constructs a PaymentProcess.
     */
    public PaymentProcess() {
        super();
        this.setExtractedInd(false);
        this.setFormattedIndicator(false);
    }

    /**
     * This method updates the user based on processUserId.
     * 
     * @param userService
     */
    public void updateUser(org.kuali.rice.kim.api.identity.PersonService userService) {
        Person u = userService.getPerson(processUserId);
        setProcessUser(u);
    }

    /**
     * This method gets the campusCode.
     * 
     * @return campusCode
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * This method sets the campusCode.
     * 
     * @param campusCode
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    /**
     * This method gets the Id.
     * 
     * @return id
     */
    public KualiInteger getId() {
        return id;
    }

    /**
     * This method sets the id.
     * 
     * @param id
     */
    public void setId(KualiInteger id) {
        this.id = id;
    }

    /**
     * This method gets the processTimestamp
     * 
     * @return processTimestamp
     */
    public Timestamp getProcessTimestamp() {
        return processTimestamp;
    }

    /**
     * This method sets the processTimestamp.
     * 
     * @param processTimestamp
     */
    public void setProcessTimestamp(Timestamp processTimestamp) {
        this.processTimestamp = processTimestamp;
    }

    /**
     * This method gets the processUser.
     * 
     * @return processUser
     */
    public Person getProcessUser() {
        processUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(processUserId, processUser);
        return processUser;
    }

    /**
     * This method sets the processUser.
     * 
     * @param processUser
     */
    public void setProcessUser(Person processUser) {
        if (processUser != null) {
            processUserId = processUser.getPrincipalId();
        }
        this.processUser = processUser;
    }

    /**
     * This method gets the processUserId.
     * 
     * @return processUserId
     */
    public String getProcessUserId() {
        return processUserId;
    }

    /**
     * This method sets the processUserId.
     * 
     * @param processUserId
     */
    public void setProcessUserId(String processUserId) {
        this.processUserId = processUserId;
    }


    /**
     * This method gets the extractedInd.
     * 
     * @return extractedInd
     */
    public boolean isExtractedInd() {
        return extractedInd;
    }


    /**
     * This method sets the extractedInd.
     * 
     * @param extractedInd
     */
    public void setExtractedInd(boolean extractedInd) {
        this.extractedInd = extractedInd;
    }

    /**
     * This method gets the formattedIndicator.
     * 
     * @return formattedIndicator
     */
    public boolean isFormattedIndicator() {
        return formattedIndicator;
    }

    /**
     * This method sets the formattedIndicator.
     * 
     * @param formattedIndicator
     */
    public void setFormattedIndicator(boolean formattedIndicator) {
        this.formattedIndicator = formattedIndicator;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.ID, this.id);

        return m;
    }

    /**
     * This method gets the campus.
     * 
     * @return campus
     */
    public Campus getCampus() {
        return campus = StringUtils.isBlank( campusCode)?null:((campus!=null && campus.getCode().equals( campusCode))?campus:SpringContext.getBean(CampusService.class).getCampus( campusCode));
    }

    /**
     * This method sets the campus.
     * 
     * @param campus
     */
    public void setCampus(Campus campus) {
        this.campus = campus;
    }

}
