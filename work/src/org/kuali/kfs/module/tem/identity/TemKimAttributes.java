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
package org.kuali.kfs.module.tem.identity;

import org.kuali.kfs.module.tem.businessobject.JobClassification;
import org.kuali.kfs.module.tem.businessobject.TravelerType;
import org.kuali.kfs.sys.identity.KfsKimAttributes;

public class TemKimAttributes extends KfsKimAttributes {

    public static final String PROFILE_PRINCIPAL_ID = "profilePrincipalId";
    public static final String TRAVELER_TYPE_CODE = "travelerTypeCode";
    public static final String JOB_CLASSIFICATION_CODE = "jobClsCode";

    protected Integer profilePrincipalId;
    protected String travelerTypeCode;
    protected String jobClsCode;

    protected TravelerType travelerType;
    protected JobClassification jobClassification;

    /**
     * Gets the profilePrincipalId attribute.
     * @return Returns the profilePrincipalId.
     */
    public Integer getProfilePrincipalId() {
        return profilePrincipalId;
    }

    /**
     * Sets the profilePrincipalId attribute value.
     * @param profilePrincipalId The profilePrincipalId to set.
     */
    public void setProfilePrincipalId(Integer profilePrincipalId) {
        this.profilePrincipalId = profilePrincipalId;
    }

    /**
     * @return the traveler type code
     */
    public String getTravelerTypeCode() {
        return travelerTypeCode;
    }

    /**
     * Sets the traveler type code
     * @param travelerTypeCode the traveler type code
     */
    public void setTravelerTypeCode(String travelerTypeCode) {
        this.travelerTypeCode = travelerTypeCode;
    }

    /**
     * @return the job classification code
     */
    public String getJobClsCode() {
        return jobClsCode;
    }

    /**
     * Sets the the job classification code
     * @param jobClassificationCode the job classification code
     */
    public void setJobClsCode(String jobClassificationCode) {
        this.jobClsCode = jobClassificationCode;
    }

    /**
     * @return the related traveler type
     */
    public TravelerType getTravelerType() {
        return travelerType;
    }

    /**
     * Sets the traveler type
     * @param travelerType the related traveler type
     */
    public void setTravelerType(TravelerType travelerType) {
        this.travelerType = travelerType;
    }

    /**
     * @return the related job classification
     */
    public JobClassification getJobClassification() {
        return jobClassification;
    }

    /**
     * Sets the related job classification
     * @param jobClassification the related job classification
     */
    public void setJobClassification(JobClassification jobClassification) {
        this.jobClassification = jobClassification;
    }

}
