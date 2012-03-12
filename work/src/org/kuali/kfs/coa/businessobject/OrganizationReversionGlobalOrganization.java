/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetailBase;

/**
 * An organization which is related to a Global Organization Reversion Detail.
 */
public class OrganizationReversionGlobalOrganization extends GlobalBusinessObjectDetailBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionGlobalOrganization.class);
    private String documentNumber;
    private String chartOfAccountsCode;
    private String organizationCode;

    private Chart chartOfAccounts;
    private Organization organization;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap stringMapper = new LinkedHashMap();
        stringMapper.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        stringMapper.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.chartOfAccountsCode);
        stringMapper.put(KFSPropertyConstants.ORGANIZATION_CODE, this.organizationCode);
        return stringMapper;
    }

    /**
     * Constructs a OrganizationReversionGlobalOrganization
     */
    public OrganizationReversionGlobalOrganization() {
        super();
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute value.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization.
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute value.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute value.
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * This utility method converts the name of a property into a string suitable for being part of a locking representation.
     * 
     * @param keyName the name of the property to convert to a locking representation
     * @return a part of a locking representation
     */
    private String convertKeyToLockingRepresentation(String keyName) {
        StringBuffer sb = new StringBuffer();
        sb.append(keyName);
        sb.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
        String keyValue = "";
        try {
            Object keyValueObj = PropertyUtils.getProperty(this, keyName);
            if (keyValueObj != null) {
                keyValue = keyValueObj.toString();
            }
        }
        catch (IllegalAccessException iae) {
            LOG.info("Illegal access exception while attempting to read property " + keyName, iae);
        }
        catch (InvocationTargetException ite) {
            LOG.info("Illegal Target Exception while attempting to read property " + keyName, ite);
        }
        catch (NoSuchMethodException nsme) {
            LOG.info("There is no such method to read property " + keyName + " in this class.", nsme);
        }
        finally {
            sb.append(keyValue);
        }
        sb.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
        return sb.toString();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((this.getChartOfAccountsCode() == null) ? 0 : this.getChartOfAccountsCode().hashCode());
        result = PRIME * result + ((this.getDocumentNumber() == null) ? 0 : this.getDocumentNumber().hashCode());
        result = PRIME * result + ((this.getOrganizationCode() == null) ? 0 : this.getOrganizationCode().hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final OrganizationReversionGlobalOrganization other = (OrganizationReversionGlobalOrganization) obj;
        if (this.getChartOfAccountsCode() == null) {
            if (other.getChartOfAccountsCode() != null)
                return false;
        }
        else if (!this.getChartOfAccountsCode().equals(other.getChartOfAccountsCode()))
            return false;
        if (this.getDocumentNumber() == null) {
            if (other.getDocumentNumber() != null)
                return false;
        }
        else if (!this.getDocumentNumber().equals(other.getDocumentNumber()))
            return false;
        if (this.getOrganizationCode() == null) {
            if (other.getOrganizationCode() != null)
                return false;
        }
        else if (!this.getOrganizationCode().equals(other.getOrganizationCode()))
            return false;
        return true;
    }

}
