/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.web;

/**
 * A class that represents an action that can be taken on an accounting line.
 */
public class AccountingLineViewAction {
    private String actionMethod;
    private String actionLabel;
    private String imageName;
    
    public AccountingLineViewAction(String actionMethod, String actionLabel, String imageName) {
        this.actionMethod = actionMethod;
        this.actionLabel = actionLabel;
        this.imageName = imageName;
    }
    
    /**
     * Gets the actionLabel attribute. 
     * @return Returns the actionLabel.
     */
    public String getActionLabel() {
        return actionLabel;
    }
    /**
     * Sets the actionLabel attribute value.
     * @param actionLabel The actionLabel to set.
     */
    public void setActionLabel(String actionLabel) {
        this.actionLabel = actionLabel;
    }
    /**
     * Gets the actionMethod attribute. 
     * @return Returns the actionMethod.
     */
    public String getActionMethod() {
        return actionMethod;
    }
    /**
     * Sets the actionMethod attribute value.
     * @param actionMethod The actionMethod to set.
     */
    public void setActionMethod(String actionMethod) {
        this.actionMethod = actionMethod;
    }
    /**
     * Gets the imageName attribute. 
     * @return Returns the imageName.
     */
    public String getImageName() {
        return imageName;
    }
    /**
     * Sets the imageName attribute value.
     * @param imageName The imageName to set.
     */
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
