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
/*
 * Created on Aug 19, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * 
 */
public class PaymentProcess extends TimestampedBusinessObjectBase {
    private KualiInteger id;
    private Timestamp processTimestamp;
    private String campus;
    private String processUserId;
    private Person processUser;
    private boolean extractedInd;
    private boolean formattedIndicator;
    
    public PaymentProcess() {
        super();
        this.setExtractedInd(false);
    }

    public void updateUser(org.kuali.rice.kim.service.PersonService userService) throws UserNotFoundException {
        Person u = userService.getPerson(processUserId);
        setProcessUser(u);
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public KualiInteger getId() {
        return id;
    }

    public void setId(KualiInteger id) {
        this.id = id;
    }

    public Timestamp getProcessTimestamp() {
        return processTimestamp;
    }

    public void setProcessTimestamp(Timestamp processTimestamp) {
        this.processTimestamp = processTimestamp;
    }

    public Person getProcessUser() {
        return processUser;
    }

    public void setProcessUser(Person processUser) {
        if (processUser != null) {
            processUserId = processUser.getPrincipalId();
        }
        this.processUser = processUser;
    }

    public String getProcessUserId() {
        return processUserId;
    }

    public void setProcessUserId(String processUserId) {
        this.processUserId = processUserId;
    }
    
    /**
     * 
     * returns extractedInd
     * @return
     */
    public boolean isExtractedInd() {
        return extractedInd;
    }
    
    /**
     * 
     * Sets extractedInd
     * @param extractedInd
     */
    public void setExtractedInd(boolean extractedInd) {
        this.extractedInd = extractedInd;
    }

    public boolean isFormattedIndicator() {
        return formattedIndicator;
    }

    public void setFormattedIndicator(boolean formattedIndicator) {
        this.formattedIndicator = formattedIndicator;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        
        m.put(KFSPropertyConstants.ID, this.id);

        return m;
    }

}

