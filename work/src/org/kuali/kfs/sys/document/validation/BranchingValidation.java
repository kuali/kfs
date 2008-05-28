/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.validation;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;

/**
 * An abstract class that creates an easy way to branch between validations.  Basically,
 * extenders set a branch map - a map where the key is the name of the branch and the value
 * is the validation to perform to check on that branch.  Extenders also implement the
 * determineBranch method, which returns the name of the branch to validate against;
 * if null is returned, then no validation will occur.
 */
public abstract class BranchingValidation extends ParameterizedValidation implements Validation {
    private Map<String, Validation> branchMap;
    private List<ValidationFieldConvertible> parameterProperties;
    private boolean shouldQuitOnFail = false;

    /**
     * Determines which branch, if any, within the branchMap should be used as the validation to take.
     * @param event the event which triggered this validation
     * @return the name of the branch to take, or a null or empty string to not take any branch and simply pass validation as true
     */
    protected abstract String determineBranch(AttributedDocumentEvent event);
    
    /**
     * Note: these parameter properties only help determine what branching should take place; these properties will not affect in anyway the branch children
     * @see org.kuali.kfs.validation.Validation#getParameterProperties()
     */
    public List<ValidationFieldConvertible> getParameterProperties() {
        return this.parameterProperties;
    }

    /**
     * Sets the parameterProperties attribute value.
     * @param parameterProperties The parameterProperties to set.
     */
    public void setParameterProperties(List<ValidationFieldConvertible> parameterProperties) {
        this.parameterProperties = parameterProperties;
    }

    /**
     * @see org.kuali.kfs.validation.Validation#shouldQuitOnFail()
     */
    public boolean shouldQuitOnFail() {
        return shouldQuitOnFail;
    }

    /**
     * Sets the shouldQuitOnFail attribute value.
     * @param shouldQuitOnFail The shouldQuitOnFail to set.
     */
    public void setShouldQuitOnFail(boolean shouldQuitOnFail) {
        this.shouldQuitOnFail = shouldQuitOnFail;
    }

    /**
     * 
     * @see org.kuali.kfs.validation.Validation#stageValidation(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean stageValidation(AttributedDocumentEvent event) {
        populateParametersFromEvent(event);
        return validate(event);
    }

    /**
     * Using the branch name returned by determineBranch(), validates the event against the corresponding
     * branch in the branch map.  If a null or empty string is returned from determineBrach(), this method
     * simply returns true; if there is no validation in the branchMap for the given name, an IllegalStateException
     * is thrown.
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        String branchName = determineBranch(event);
        if (!StringUtils.isBlank(branchName)) {
            Validation validation = branchMap.get(branchName);
            if (validation == null) {
                throw new IllegalStateException("Branching Validation "+this.getClass().getName()+" cannot find a branch named "+branchName);
            }
            return validation.stageValidation(event);
        } else {
            return true;
        }
    }

    /**
     * Gets the branchMap attribute. 
     * @return Returns the branchMap.
     */
    public Map<String, Validation> getBranchMap() {
        return branchMap;
    }

    /**
     * Sets the branchMap attribute value.
     * @param branchMap The branchMap to set.
     */
    public void setBranchMap(Map<String, Validation> branchMap) {
        this.branchMap = branchMap;
    }

}
