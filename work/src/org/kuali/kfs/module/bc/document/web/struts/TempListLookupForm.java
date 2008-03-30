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
package org.kuali.module.budget.web.struts.form;

import org.kuali.core.web.struts.form.LookupForm;

/**
 * Action Form for budget special lookup screens.
 */
public class TempListLookupForm extends LookupForm {
    private Integer universityFiscalYear;
    private String personUniversalIdentifier;
    private String reportMode;
    private String currentPointOfViewKeyCode;
    private boolean buildControlList;
    private boolean reportConsolidation;
    private boolean showInitialResults;
    private int tempListLookupMode;

    /**
     * Gets the currentPointOfViewKeyCode attribute.
     * 
     * @return Returns the currentPointOfViewKeyCode.
     */
    public String getCurrentPointOfViewKeyCode() {
        return currentPointOfViewKeyCode;
    }

    /**
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return Returns the personUniversalIdentifier.
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    /**
     * Gets the reportMode attribute.
     * 
     * @return Returns the reportMode.
     */
    public String getReportMode() {
        return reportMode;
    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Gets the buildControlList attribute.
     * 
     * @return Returns the buildControlList.
     */
    public boolean isBuildControlList() {
        return buildControlList;
    }

    /**
     * Gets the reportConsolidation attribute.
     * 
     * @return Returns the reportConsolidation.
     */
    public boolean isReportConsolidation() {
        return reportConsolidation;
    }

    /**
     * Gets the showInitialResults attribute.
     * 
     * @return Returns the showInitialResults.
     */
    public boolean isShowInitialResults() {
        return showInitialResults;
    }

    /**
     * Sets the buildControlList attribute value.
     * 
     * @param buildControlList The buildControlList to set.
     */
    public void setBuildControlList(boolean buildControlList) {
        this.buildControlList = buildControlList;
    }

    /**
     * Sets the currentPointOfViewKeyCode attribute value.
     * 
     * @param currentPointOfViewKeyCode The currentPointOfViewKeyCode to set.
     */
    public void setCurrentPointOfViewKeyCode(String currentPointOfViewKeyCode) {
        this.currentPointOfViewKeyCode = currentPointOfViewKeyCode;
    }

    /**
     * Sets the personUniversalIdentifier attribute value.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }

    /**
     * Sets the reportConsolidation attribute value.
     * 
     * @param reportConsolidation The reportConsolidation to set.
     */
    public void setReportConsolidation(boolean reportConsolidation) {
        this.reportConsolidation = reportConsolidation;
    }

    /**
     * Sets the reportMode attribute value.
     * 
     * @param reportMode The reportMode to set.
     */
    public void setReportMode(String reportMode) {
        this.reportMode = reportMode;
    }

    /**
     * Sets the showInitialResults attribute value.
     * 
     * @param showInitialResults The showInitialResults to set.
     */
    public void setShowInitialResults(boolean showInitialResults) {
        this.showInitialResults = showInitialResults;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the tempListLookupMode attribute.
     * 
     * @return Returns the tempListLookupMode.
     */
    public int getTempListLookupMode() {
        return tempListLookupMode;
    }

    /**
     * Sets the tempListLookupMode attribute value.
     * 
     * @param tempListLookupMode The tempListLookupMode to set.
     */
    public void setTempListLookupMode(int tempListLookupMode) {
        this.tempListLookupMode = tempListLookupMode;
    }

}
