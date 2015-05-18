/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary;

import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.krad.datadictionary.HelpDefinition;

import java.io.Serializable;

/**
            The headerNavigation element defines a set of additional
            tabs which will appear on the document.
 *
 *                The headerNavigationTab defines an additional tab which
                will appear on the document.

 */
@Deprecated
public class HeaderNavigation extends DataDictionaryDefinitionBase implements Serializable {
    private static final long serialVersionUID = 4317341111287854436L;
    
	protected String headerTabMethodToCall;
    protected String headerTabNavigateTo;
    protected String headerTabDisplayName;
    protected HelpDefinition helpDefinition;
    protected boolean disabled = false;
    
    public HeaderNavigation() {}

    public HeaderNavigation(String headerTabNavigateTo, String headerTabDisplayName) {
        this.headerTabNavigateTo = headerTabNavigateTo;
        this.headerTabDisplayName = headerTabDisplayName;
    }

    /**
     * Gets the navigationKey attribute.
     * 
     * @return Returns the navigationKey.
     */
    public String getHeaderTabMethodToCall() {
        return headerTabMethodToCall;
    }

    /**
     * Sets the navigationKey attribute value.
     * 
     * @param navigationKey The navigationKey to set.
     */
    public void setHeaderTabMethodToCall(String navigationKey) {
        this.headerTabMethodToCall = navigationKey;
    }

    /**
     * Gets the navigationStyle attribute.
     * 
     * @return Returns the navigationStyle.
     */
    public String getHeaderTabDisplayName() {
        return headerTabDisplayName;
    }

    /**
     * The displayName element is the name of the additional tab.
     */
    public void setHeaderTabDisplayName(String headerTabDisplayName) {
        this.headerTabDisplayName = headerTabDisplayName;
    }

    /**
     * Gets the suffix attribute.
     * 
     * @return Returns the suffix.
     */
    public String getHeaderTabNavigateTo() {
        return headerTabNavigateTo;
    }
    
    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    /**
     * The pageHelp element provides the keys to
                    obtain a help description from the database.

                    On document JSP pages, a help icon may be rendered.  If this tag is specified, then
                    the filename of this page will be located in the value of the parameter specified by the namespace, detail type, and name.

                    The value of the parameter is relative to the value of the "externalizable.help.url" property in ConfigurationService (see KualiHelpAction).
                    parameterNamespace: namespace of the parameter that has the path to the help page
                    parameterName: name of the parameter that has the path to the help page
                    parameterDetailType: detail type of the parameter that has the path to the help page
     */
    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }

    /**
     * The navigateTo element is the forward name in the struts-config file.
     */
    public void setHeaderTabNavigateTo(String suffix) {
        this.headerTabNavigateTo = suffix;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(Class, Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        // No real validation to be done here other than perhaps checking to be
        // sure that the security workgroup is a valid workgroup.
    }
}
