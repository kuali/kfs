/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.KualiCode;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.inquiry.KualiInquirableImpl;
import org.kuali.kfs.util.SpringServiceLocator;

public class KualiSystemCode extends PersistableBusinessObjectBase implements KualiCode {

    static {
        KualiInquirableImpl.HACK_LIST.add(KualiSystemCode.class);
    }
    
    private String code;
    private String name;
    private boolean active;
    protected String className;
    private boolean codeRetrieved;

    public KualiSystemCode() {
        this.active = true;
        this.codeRetrieved = false;
        this.className = this.getClass().getName();
    }

    public KualiSystemCode(String code) {
        this.code = code;
        this.active = true;
        this.codeRetrieved = false;
        this.className = this.getClass().getName();
    }

    /**
     * @return Getter for the Code.
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code - Setter for the Code.
     */
    public void setCode(String code) {
        this.code = code;
    }


    /**
     * @return Getter for the Name.
     */
    public String getName() {
        if (!codeRetrieved && getCode() != null) {
            KualiSystemCode code = (KualiSystemCode) SpringServiceLocator.getKualiCodeService().getSystemCode(this.getClass(), getCode());
            code.setCodeRetrieved(true);
            this.name = code.getName();
            this.active = code.isActive();
            this.codeRetrieved = true;
        }
        return name;
    }


    /**
     * @param name - Setter for the name.
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return Getter for the active field.
     */
    public boolean isActive() {
        if (!codeRetrieved && getCode() != null) {
            KualiSystemCode code = (KualiSystemCode) SpringServiceLocator.getKualiCodeService().getSystemCode(this.getClass(), getCode());
            code.setCodeRetrieved(true);
            name = code.getName();
            active = code.isActive();
            codeRetrieved = true;
        }
        return active;
    }


    /**
     * @param name - Setter for the active field.
     */
    public void setActive(boolean a) {
        this.active = a;
    }

    /**
     * @return Returns the className.
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param ojbConcreteClass The ojbConcreteClass to set.
     */
    public void setClassName(String ojbConcreteClass) {
        this.className = ojbConcreteClass;
    }

    /**
     * @return Returns the codeRetrieved.
     */
    public boolean isCodeRetrieved() {
        return codeRetrieved;
    }

    /**
     * @param codeRetrieved The codeRetrieved to set.
     */
    public void setCodeRetrieved(boolean codeRetrieved) {
        this.codeRetrieved = codeRetrieved;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    final protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("code", getCode());

        return m;
    }

    /**
     * Implements equals comparing code to code.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof KualiSystemCode) {
            return StringUtils.equals(this.getCode(), ((KualiSystemCode) obj).getCode());
        }
        return false;
    }

    /**
     * Overriding equals requires writing a hashCode method.
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hashCode = 0;

        if (getCode() != null) {
            hashCode = getCode().hashCode();
        }

        return hashCode;
    }

}