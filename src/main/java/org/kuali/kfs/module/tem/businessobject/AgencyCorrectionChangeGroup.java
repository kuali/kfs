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
