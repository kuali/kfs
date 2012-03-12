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
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetailBase;

/**
 * This is a representation of an Organization Reversion Detail, made specifically for Global Organization Reversions. However, as
 * OrganizationReversionDetail lists Organization as a primary key and Global Organization Reversions deal with several
 * Organizations, that class could not be re-used for Globals.
 */
public class OrganizationReversionGlobalDetail extends GlobalBusinessObjectDetailBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionGlobalDetail.class);

    private String documentNumber;
    private String organizationReversionCategoryCode;
    private String organizationReversionObjectCode;
    private String organizationReversionCode;

    private OrganizationReversionCategory organizationReversionCategory;
    private OrganizationReversionGlobal parentGlobalOrganizationReversion;
    private ObjectCode organizationReversionObject;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap stringMapper = new LinkedHashMap();
        stringMapper.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        stringMapper.put("OrganizationReversionCategoryCode", this.organizationReversionCategoryCode);
        return stringMapper;
    }

    /**
     * Constructs an OrganizationReversionGlobalDocumentDetail.
     */
    public OrganizationReversionGlobalDetail() {
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
     * Gets the organizationReversionCode attribute.
     * 
     * @return Returns the organizationReversionCode.
     */
    public String getOrganizationReversionCode() {
        return organizationReversionCode;
    }

    /**
     * Sets the organizationReversionCode attribute value.
     * 
     * @param organizationReversionCode The organizationReversionCode to set.
     */
    public void setOrganizationReversionCode(String organizationReversionCode) {
        this.organizationReversionCode = organizationReversionCode;
    }

    /**
     * Gets the organizationReversionCategory attribute.
     * 
     * @return Returns the organizationReversionCategory.
     */
    public OrganizationReversionCategory getOrganizationReversionCategory() {
        return organizationReversionCategory;
    }

    /**
     * Sets the organizationReversionCategory attribute value.
     * 
     * @param organizationReversionCategory The organizationReversionCategory to set.
     */
    public void setOrganizationReversionCategory(OrganizationReversionCategory organizationReversionCategory) {
        this.organizationReversionCategory = organizationReversionCategory;
    }

    /**
     * Gets the organizationReversionCategoryCode attribute.
     * 
     * @return Returns the organizationReversionCategoryCode.
     */
    public String getOrganizationReversionCategoryCode() {
        return organizationReversionCategoryCode;
    }

    /**
     * Sets the organizationReversionCategoryCode attribute value.
     * 
     * @param organizationReversionCategoryCode The organizationReversionCategoryCode to set.
     */
    public void setOrganizationReversionCategoryCode(String organizationReversionCategoryCode) {
        this.organizationReversionCategoryCode = organizationReversionCategoryCode;
    }

    /**
     * Gets the organizationReversionObjectCode attribute.
     * 
     * @return Returns the organizationReversionObjectCode.
     */
    public String getOrganizationReversionObjectCode() {
        return organizationReversionObjectCode;
    }

    /**
     * Sets the organizationReversionObjectCode attribute value.
     * 
     * @param organizationReversionObjectCode The organizationReversionObjectCode to set.
     */
    public void setOrganizationReversionObjectCode(String organizationReversionObjectCode) {
        this.organizationReversionObjectCode = organizationReversionObjectCode;
    }

    /**
     * Gets the parentGlobalOrganizationReversion attribute. This field does not persist, and is populated by
     * OrganzationReversionChangeMaintainable.
     * 
     * @return Returns the parentGlobalOrganizationReversion.
     */
    public OrganizationReversionGlobal getParentGlobalOrganizationReversion() {
        return parentGlobalOrganizationReversion;
    }

    /**
     * Sets the parentGlobalOrganizationReversion attribute value. This field does not persist, and is populated by
     * OrganizationReversionGlobalMaintainableImpl.
     * 
     * @param parentGlobalOrganizationReversion The parentGlobalOrganizationReversion to set.
     */
    public void setParentGlobalOrganizationReversion(OrganizationReversionGlobal parentGlobalOrganizationReversion) {
        this.parentGlobalOrganizationReversion = parentGlobalOrganizationReversion;
    }

    /**
     * Gets the organizationReversionObject attribute. 
     * @return Returns the organizationReversionObject.
     */
    public ObjectCode getOrganizationReversionObject() {
        return organizationReversionObject;
    }

    /**
     * Sets the organizationReversionObject attribute value.
     * @param organizationReversionObject The organizationReversionObject to set.
     */
    public void setOrganizationReversionObject(ObjectCode organizationReversionObject) {
        this.organizationReversionObject = organizationReversionObject;
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
     * This returns a string of object code names associated with the object code in this org rev change detail.
     * 
     * @return String of distinct object code names
     */
    public String getObjectCodeNames() {
        String objectCodeNames = "";
        if (!StringUtils.isBlank(this.getOrganizationReversionObjectCode())) {
            if (this.getParentGlobalOrganizationReversion().getUniversityFiscalYear() != null && this.getParentGlobalOrganizationReversion().getOrganizationReversionGlobalOrganizations() != null && this.getParentGlobalOrganizationReversion().getOrganizationReversionGlobalOrganizations().size() > 0) {
                // find distinct chart of account codes
                SortedSet<String> chartCodes = new TreeSet<String>();
                for (OrganizationReversionGlobalOrganization org : this.getParentGlobalOrganizationReversion().getOrganizationReversionGlobalOrganizations()) {
                    chartCodes.add(org.getChartOfAccountsCode());
                }
                String[] chartCodesArray = new String[chartCodes.size()];
                int i = 0;
                for (String chartCode : chartCodes) {
                    chartCodesArray[i] = chartCode;
                    i++;
                }
                objectCodeNames = (String) SpringContext.getBean(ObjectCodeService.class).getObjectCodeNamesByCharts(this.getParentGlobalOrganizationReversion().getUniversityFiscalYear(), chartCodesArray, this.getOrganizationReversionObjectCode());
            }
        }
        return objectCodeNames;
    }
}
