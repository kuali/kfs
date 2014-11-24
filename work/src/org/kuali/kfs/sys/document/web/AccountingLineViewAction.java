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
