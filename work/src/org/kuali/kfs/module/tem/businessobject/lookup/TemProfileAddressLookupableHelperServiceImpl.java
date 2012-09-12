/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.lookup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddress;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TemProfileAddress;
import org.kuali.kfs.module.tem.dataaccess.TravelerDao;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.entity.impl.KimEntityAddressImpl;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.BeanPropertyComparator;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.springframework.util.ObjectUtils;

public class TemProfileAddressLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
	
    public static Logger LOG = Logger.getLogger(TemProfileAddressLookupableHelperServiceImpl.class);
    
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 5438499177869581678L;
	private TravelerDao travelerDao;
    private TravelerService travelerService;
	private IdentityService identityService;
	private AccountsReceivableModuleService accountsReceivableModuleService;
    private Map<String, String> temProfileAddressToKimAddress;
    private Map<String, String> temProfileAddressToCustomerAddress;
	
	/**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm, java.util.Collection, boolean)
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        lookupForm.setSuppressActions(false);
        lookupForm.setSupplementalActionsEnabled(true);
        lookupForm.setHideReturnLink(false);
        lookupForm.setShowMaintenanceLinks(true);
        return super.performLookup(lookupForm, resultTable, bounded);
    }


	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#performClear(org.kuali.rice.kns.web.struts.form.LookupForm)
	 */
	@Override
	public void performClear(LookupForm arg0) {
		for (Iterator iter = this.getRows().iterator(); iter.hasNext();) {
			 Row row = (Row) iter.next();
			 for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
				 Field field = (Field) iterator.next();
				 
				 if (field.isSecure()) {
					 field.setSecure(false);
					 field.setDisplayMaskValue(null);
					 field.setEncryptedValue(null);
				 }

				if (!field.getFieldType().equals(Field.RADIO) && !field.getFieldType().equals(Field.HIDDEN)) {
					field.setPropertyValue(field.getDefaultValue());
				}
			}
		 }
	}


    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        
        List<TemProfileAddress> searchResults = new ArrayList<TemProfileAddress>();
        
        if (fieldValues.containsKey(TEMProfileProperties.PRINCIPAL_ID) && !StringUtils.isEmpty(fieldValues.get(TEMProfileProperties.PRINCIPAL_ID))) {
        	final Map<String, String> kimFieldsForLookup = this.getPersonFieldValues(fieldValues);
        	kimFieldsForLookup.put(KFSPropertyConstants.KUALI_USER_PERSON_ACTIVE_INDICATOR, KFSConstants.ACTIVE_INDICATOR);
        	
            List<KimEntityAddressImpl> addresses = (List<KimEntityAddressImpl>) getLookupService().findCollectionBySearchHelper(KimEntityAddressImpl.class, kimFieldsForLookup, false);
        	
        	for (KimEntityAddressImpl address : addresses) {
        		TemProfileAddress temAddress = getTravelerService().convertToTemProfileAddressFromKimAddress(address);
        		temAddress.setPrincipalId(fieldValues.get(TEMProfileProperties.PRINCIPAL_ID));
                searchResults.add(temAddress);
            }
        }
        
        if (ObjectUtils.isEmpty(searchResults.toArray()) && fieldValues.containsKey(TEMProfileProperties.CUSTOMER_NUMBER) && !StringUtils.isEmpty(fieldValues.get(TEMProfileProperties.CUSTOMER_NUMBER))) {
        	final Map<String, String> customerFieldsForLookup = this.getCustomerFieldValues(fieldValues);
            
            LOG.debug("Using fieldsForLookup "+ customerFieldsForLookup);
               
            Collection<AccountsReceivableCustomerAddress> customerAddresses = getAccountsReceivableModuleService().searchForCustomerAddresses(customerFieldsForLookup);

        	boolean active;
            for (AccountsReceivableCustomerAddress customerAddress : customerAddresses) {
            	active = true;
            	if (org.kuali.rice.kns.util.ObjectUtils.isNotNull(customerAddress)) {
                    if (org.kuali.rice.kns.util.ObjectUtils.isNotNull(customerAddress.getCustomerAddressEndDate())) {
                        Timestamp currentDateTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
                        Timestamp addressEndDateTimestamp = new Timestamp(customerAddress.getCustomerAddressEndDate().getTime());
                        if (addressEndDateTimestamp.before(currentDateTimestamp)) {
                        	active = false;
                        }
                    }
                }
            	
            	if(active) {
            		searchResults.add(getTravelerService().convertToTemProfileAddressFromCustomer(customerAddress));
            	}
            }
        }
        
        CollectionIncomplete results = new CollectionIncomplete(searchResults, Long.valueOf(searchResults.size()));

        // sort list if default sort column given
        List<String> defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }

        return results;
    }

    /**
     * Generates a {@link Map} of field values where object properties are mapped to values for
     * search purposes.
     *
     * @param fieldValues original field values passed to the lookup
     * @return Map of values
     */
    protected Map<String, String> getCustomerFieldValues(final Map<String, String> fieldValues) {
    	Map<String, String> customerFieldValues = new HashMap<String, String>();
    	
    	for(Entry<String, String> entry : fieldValues.entrySet()) {
    		if(temProfileAddressToCustomerAddress.containsKey(entry.getKey()) && !StringUtils.isEmpty(entry.getValue())) {
    			customerFieldValues.put(temProfileAddressToCustomerAddress.get(entry.getKey()), entry.getValue());
    		}
    	}
    	
        return customerFieldValues;
    }
    
    /**
     * Generates a {@link Map} of field values where object properties are mapped to values for
     * search purposes.
     *
     * @param fieldValues original field values passed to the lookup
     * @return Map of values
     */
    protected Map<String, String> getPersonFieldValues(final Map<String, String> fieldValues) {
    	Map<String, String> kimFieldValues = new HashMap<String, String>();
    	
    	for(Entry<String, String> entry : fieldValues.entrySet()) {
    		if (entry.getKey().equalsIgnoreCase(TEMProfileProperties.PRINCIPAL_ID) && !StringUtils.isEmpty(entry.getValue())) {
    	    	KimPrincipalInfo principal = this.getIdentityService().getPrincipal(entry.getValue());
    			kimFieldValues.put(temProfileAddressToKimAddress.get(entry.getKey()), principal.getEntityId());
    		}
    		else if(temProfileAddressToKimAddress.containsKey(entry.getKey()) && !StringUtils.isEmpty(entry.getValue())) {
    			kimFieldValues.put(temProfileAddressToKimAddress.get(entry.getKey()), entry.getValue());
    		}
    	}
    	
        return kimFieldValues;
    }


	/**
	 * Sets the travelerDao attribute value.
	 * @param travelerDao The travelerDao to set.
	 */
	public void setTravelerDao(TravelerDao travelerDao) {
		this.travelerDao = travelerDao;
	}


	/**
	 * Gets the travelerDao attribute. 
	 * @return Returns the travelerDao.
	 */
	public TravelerDao getTravelerDao() {
		return travelerDao;
	}


	/**
	 * Sets the travelerService attribute value.
	 * @param travelerService The travelerService to set.
	 */
	public void setTravelerService(TravelerService travelerService) {
		this.travelerService = travelerService;
	}


	/**
	 * Gets the travelerService attribute. 
	 * @return Returns the travelerService.
	 */
	public TravelerService getTravelerService() {
		return travelerService;
	}


	/**
	 * Gets the temProfileAddressToKimAddress attribute. 
	 * @return Returns the temProfileAddressToKimAddress.
	 */
	public Map<String, String> getTemProfileAddressToKimAddress() {
		return temProfileAddressToKimAddress;
	}


	/**
	 * Sets the temProfileAddressToKimAddress attribute value.
	 * @param temProfileAddressToKimAddress The temProfileAddressToKimAddress to set.
	 */
	public void setTemProfileAddressToKimAddress(
			Map<String, String> temProfileAddressToKimAddress) {
		this.temProfileAddressToKimAddress = temProfileAddressToKimAddress;
	}


	/**
	 * Gets the temProfileAddressToCustomerAddress attribute. 
	 * @return Returns the temProfileAddressToCustomerAddress.
	 */
	public Map<String, String> getTemProfileAddressToCustomerAddress() {
		return temProfileAddressToCustomerAddress;
	}


	/**
	 * Sets the temProfileAddressToCustomerAddress attribute value.
	 * @param temProfileAddressToCustomerAddress The temProfileAddressToCustomerAddress to set.
	 */
	public void setTemProfileAddressToCustomerAddress(
			Map<String, String> temProfileAddressToCustomerAddress) {
		this.temProfileAddressToCustomerAddress = temProfileAddressToCustomerAddress;
	}


	/**
	 * Gets the identityService attribute. 
	 * @return Returns the identityService.
	 */
	public IdentityService getIdentityService() {
		return identityService;
	}


	/**
	 * Sets the identityService attribute value.
	 * @param identityService The identityService to set.
	 */
	public void setIdentityService(IdentityService identityService) {
		this.identityService = identityService;
	}
	
    protected AccountsReceivableModuleService getAccountsReceivableModuleService() {
        if (accountsReceivableModuleService == null) {
            this.accountsReceivableModuleService = SpringContext.getBean(AccountsReceivableModuleService.class);
        }
        
        return accountsReceivableModuleService;
    }

    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }
}
