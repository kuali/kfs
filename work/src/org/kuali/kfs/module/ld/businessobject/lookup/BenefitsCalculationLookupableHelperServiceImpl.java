/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.businessobject.lookup;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.LaborEnterpriseFeedStep;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;


public class BenefitsCalculationLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {

    private ParameterService parameterService;
    
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        // remove hidden fields
        LookupUtils.removeHiddenCriteriaFields(getBusinessObjectClass(), fieldValues);

        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KFSConstants.REFERENCES_TO_REFRESH));

        List searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, false);
        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return searchResults;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getColumns()
     */
    @Override
    public List<Column> getColumns() {
        List<Column> columns =  super.getColumns();
        
        String offsetParmValue = parameterService.getParameterValueAsString(LaborEnterpriseFeedStep.class, LaborConstants.BenefitCalculation.LABOR_BENEFIT_CALCULATION_OFFSET_IND);
        
        if(offsetParmValue.equalsIgnoreCase("n")) {
            for(Iterator<Column> it = columns.iterator(); it.hasNext(); ) {
                Column column = (Column)it.next();
                if(column.getPropertyName().equalsIgnoreCase(LaborConstants.BenefitCalculation.ACCOUNT_CODE_OFFSET_PROPERTY_NAME) || column.getPropertyName().equalsIgnoreCase(LaborConstants.BenefitCalculation.OBJECT_CODE_OFFSET_PROPERTY_NAME)) {
                    it.remove();
                }
            }
        }
        
        return columns;
    }
    
    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getRows()
     */
    @Override
    public List<Row> getRows() {
        List<Row> rows =  super.getRows();
        
        String offsetParmValue = parameterService.getParameterValueAsString(LaborEnterpriseFeedStep.class, LaborConstants.BenefitCalculation.LABOR_BENEFIT_CALCULATION_OFFSET_IND);
        
        if(offsetParmValue.equalsIgnoreCase("n")) {
            for(Iterator<Row> it = rows.iterator(); it.hasNext(); ) {
                Row row = (Row)it.next();
                for(Field field : row.getFields()) {
                    if(field.getPropertyName().equalsIgnoreCase(LaborConstants.BenefitCalculation.ACCOUNT_CODE_OFFSET_PROPERTY_NAME) || field.getPropertyName().equalsIgnoreCase(LaborConstants.BenefitCalculation.OBJECT_CODE_OFFSET_PROPERTY_NAME)) {
                        it.remove();
                    }
                }
            }
        }
        
        return rows;
    }
    
    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);
        
        HashMap<String, String> fieldsMap = new HashMap<String, String>();
        
        String accountNumber = (String) fieldValues.get("accountCodeOffset");
        String objectCode = (String) fieldValues.get("objectCodeOffset");
        
        // Validate the Account Number field is a valid Account Number in the DB
        if (StringUtils.isNotEmpty(accountNumber)) {
            fieldsMap.clear();
            fieldsMap.put(GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER, accountNumber);
            List<Account> accountNums = (List<Account>) SpringContext.getBean(BusinessObjectService.class).findMatching(Account.class, fieldsMap);
           
            
            if (accountNums == null || accountNums.size() <= 0) {
                GlobalVariables.getMessageMap().putError("accountNumber", KFSKeyConstants.ERROR_CUSTOM, new String[] { "Invalid Account Number: " + accountNumber });
                throw new ValidationException("errors in search criteria");
            }
        }
        
        // Validate the Object Code field is a valid Object Code in the DB
        if (StringUtils.isNotEmpty(objectCode)) {
            fieldsMap.clear();
            fieldsMap.put(GeneralLedgerConstants.ColumnNames.OBJECT_CODE, objectCode);
            List<ObjectCode> objCodes = (List<ObjectCode>) SpringContext.getBean(BusinessObjectService.class).findMatching(ObjectCode.class, fieldsMap);
           
            
            if (objCodes == null || objCodes.size() <= 0) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.OBJECT_CODE, KFSKeyConstants.ERROR_CUSTOM, new String[] { "Invalid Object Code: " + objectCode });
                throw new ValidationException("errors in search criteria");
            }
        }
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
