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
/*
 * Created on Aug 19, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.campus.CampusEbo;


/**
 * This class represents a Payment Process.
 */
public class PaymentProcess extends TimestampedBusinessObjectBase {
    protected KualiInteger id;
    protected Timestamp processTimestamp;
    protected String campusCode;
    protected String processUserId;
    protected Person processUser;
    protected boolean extractedInd;
    protected boolean formattedIndicator;

    protected CampusEbo campus;

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
     * This method gets the campus.
     *
     * @return campus
     */
    public CampusEbo getCampus() {
        if ( StringUtils.isBlank(campusCode) ) {
            campus = null;
        } else {
            if ( campus == null || !StringUtils.equals( campus.getCode(),campusCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CampusEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, campusCode);
                    campus = moduleService.getExternalizableBusinessObject(CampusEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return campus;
    }

    /**
     * This method sets the campus.
     *
     * @param campus
     */
    public void setCampus(CampusEbo campus) {
        this.campus = campus;
    }

}
