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
package org.kuali.kfs.module.tem.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class AgencyCorrectionChangeGroup extends PersistableBusinessObjectBase implements Comparable<AgencyCorrectionChangeGroup> {

    private String documentNumber;
    private Integer correctionChangeGroupLineNumber;
    private Integer correctionCriteriaNextLineNumber;
    private Integer correctionChangeNextLineNumber;
    private List<AgencyCorrectionCriteria> correctionCriteria;
    private List<AgencyCorrectionChange> correctionChange;

    public AgencyCorrectionChangeGroup(String documentNumber, int groupNumber) {
        setCorrectionChangeGroupLineNumber(correctionChangeGroupLineNumber);

        correctionCriteria = new ArrayList<AgencyCorrectionCriteria>();
        correctionChange = new ArrayList<AgencyCorrectionChange>();
        correctionCriteriaNextLineNumber = new Integer(0);
        correctionChangeNextLineNumber = new Integer(0);

        setDocumentNumber(documentNumber);
    }

    public AgencyCorrectionChangeGroup() {
        super();
        correctionCriteria = new ArrayList<AgencyCorrectionCriteria>();
        correctionChange = new ArrayList<AgencyCorrectionChange>();
        correctionCriteriaNextLineNumber = new Integer(0);
        correctionChangeNextLineNumber = new Integer(0);
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

    @Override
    public int compareTo(AgencyCorrectionChangeGroup agency) {
        return 0;
    }

    /**
     * Gets the documentNumber attribute.
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the correctionChangeGroupLineNumber attribute.
     * @return Returns the correctionChangeGroupLineNumber.
     */
    public Integer getCorrectionChangeGroupLineNumber() {
        return correctionChangeGroupLineNumber;
    }

    /**
     * Sets the correctionChangeGroupLineNumber attribute value.
     * @param correctionChangeGroupLineNumber The correctionChangeGroupLineNumber to set.
     */
    public void setCorrectionChangeGroupLineNumber(Integer correctionChangeGroupLineNumber) {
        this.correctionChangeGroupLineNumber = correctionChangeGroupLineNumber;
    }

    /**
     * Gets the correctionCriteriaNextLineNumber attribute.
     * @return Returns the correctionCriteriaNextLineNumber.
     */
    public Integer getCorrectionCriteriaNextLineNumber() {
        return correctionCriteriaNextLineNumber;
    }

    /**
     * Sets the correctionCriteriaNextLineNumber attribute value.
     * @param correctionCriteriaNextLineNumber The correctionCriteriaNextLineNumber to set.
     */
    public void setCorrectionCriteriaNextLineNumber(Integer correctionCriteriaNextLineNumber) {
        this.correctionCriteriaNextLineNumber = correctionCriteriaNextLineNumber;
    }

    /**
     * Gets the correctionChangeNextLineNumber attribute.
     * @return Returns the correctionChangeNextLineNumber.
     */
    public Integer getCorrectionChangeNextLineNumber() {
        return correctionChangeNextLineNumber;
    }

    /**
     * Sets the correctionChangeNextLineNumber attribute value.
     * @param correctionChangeNextLineNumber The correctionChangeNextLineNumber to set.
     */
    public void setCorrectionChangeNextLineNumber(Integer correctionChangeNextLineNumber) {
        this.correctionChangeNextLineNumber = correctionChangeNextLineNumber;
    }

    /**
     * Gets the correctionCriteria attribute.
     * @return Returns the correctionCriteria.
     */
    public List<AgencyCorrectionCriteria> getCorrectionCriteria() {
        return correctionCriteria;
    }

    /**
     * Sets the correctionCriteria attribute value.
     * @param correctionCriteria The correctionCriteria to set.
     */
    public void setCorrectionCriteria(List<AgencyCorrectionCriteria> correctionCriteria) {
        this.correctionCriteria = correctionCriteria;
    }

    /**
     * Gets the correctionChange attribute.
     * @return Returns the correctionChange.
     */
    public List<AgencyCorrectionChange> getCorrectionChange() {
        return correctionChange;
    }

    /**
     * Sets the correctionChange attribute value.
     * @param correctionChange The correctionChange to set.
     */
    public void setCorrectionChange(List<AgencyCorrectionChange> correctionChange) {
        this.correctionChange = correctionChange;
    }




}
