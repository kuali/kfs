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
package org.kuali.rice.kns.datadictionary.control;

import org.apache.commons.lang.StringUtils;

/**
 The kualiUser element defines a control that identifies
 a Kuali user. As an example, consider a person with the
 following:
 * User ID = JPJONES
 * Universal User ID = 3583663872
 * Employee ID = 0000123456
 * Name = JONES,JOHN p
 This control defines a field in which the user can enter the User Id or choose a
 user using the magnifying glass lookup.  After a user is selected, user name
 will be displayed under the User ID.

 When using this control, the names of other attributes must be specified
 to allow the control to function:
 * universalIdAttributeName  -
 attribute that provides the Universal User Id - e.g. 3583663872
 * userIdAttributeName -
 attribute that provides the User Id - e.g. JPJONES
 * personNameAttributeName -
 attribute that provides the User Name - e.g. JONES,JOHN P
 */
@Deprecated
public class KualiUserControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = 4749994521411547705L;

    protected String universalIdAttributeName;
    protected String userIdAttributeName;
    protected String personNameAttributeName;

    public KualiUserControlDefinition() {
        this.type = ControlDefinitionType.KUALI_USER;
    }

    /**
     *
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isKualiUser()
     */
    public boolean isKualiUser() {
        return true;
    }

    /**
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "KualiUserControlDefinition";
    }

    /**
     * Gets the personNameAttributeName attribute.
     *
     * @return Returns the personNameAttributeName.
     */
    public String getPersonNameAttributeName() {
        return personNameAttributeName;
    }

    /**
     * personNameAttributeName -
     attribute that provides the User Name - e.g. JONES,JOHN P
     */
    public void setPersonNameAttributeName(String personNameAttributeName) {
        if (StringUtils.isBlank(personNameAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) personNameAttributeName");
        }
        this.personNameAttributeName = personNameAttributeName;
    }

    /**
     * Gets the universalIdAttributeName attribute.
     *
     * @return Returns the universalIdAttributeName.
     */
    public String getUniversalIdAttributeName() {
        return universalIdAttributeName;
    }

    /**
     * universalIdAttributeName  -
     attribute that provides the Universal User Id - e.g. 3583663872
     */
    public void setUniversalIdAttributeName(String universalIdAttributeName) {
        if (StringUtils.isBlank(universalIdAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) universalIdAttributeName");
        }
        this.universalIdAttributeName = universalIdAttributeName;
    }

    /**
     * Gets the userIdAttributeName attribute.
     *
     * @return Returns the userIdAttributeName.
     */
    public String getUserIdAttributeName() {
        return userIdAttributeName;
    }

    /**
     * userIdAttributeName -
     attribute that provides the User Id - e.g. JPJONES
     */
    public void setUserIdAttributeName(String userIdAttributeName) {
        if (StringUtils.isBlank(userIdAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) userIdAttributeName");
        }
        this.userIdAttributeName = userIdAttributeName;
    }

}
