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
package org.kuali.kfs.coa.businessobject.inquiry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRate;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Inquirable class for {@link Account}
 */
public class AccountInquirable extends KualiInquirableImpl {

	private ParameterService parameterService;
	
	/**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER.equals(attributeName)) {
            String baseUrl = KRADConstants.INQUIRY_ACTION;

            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, IndirectCostRecoveryRate.class.getName());
            parameters.put(KFSConstants.DOC_FORM_KEY, "88888888");

            Map<String, String> inquiryFields = new HashMap<String, String>();
            String icrIdentifier = (String) ObjectUtils.getPropertyValue(businessObject, attributeName);
            if (StringUtils.isBlank(icrIdentifier)) {
                return new AnchorHtmlData();
            }
            inquiryFields.put(KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER, icrIdentifier);
            parameters.put(KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER, icrIdentifier);

            Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
            parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear.toString());

            return getHyperLink(IndirectCostRecoveryRate.class, inquiryFields, UrlFactory.parameterizeUrl(baseUrl, parameters));
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }
    
    @Override
    @Deprecated
    public List<Section> getSections(BusinessObject arg0) {
        List<Section> sections = super.getSections(arg0);
	    for(Section section : sections) {
	        for (Row row : section.getRows()) {
			    List<Field> updatedFields = new ArrayList<Field>();
			    for (Field field : row.getFields()) {
				    if(shouldIncludeField(field)) {
				        updatedFields.add(field);
				    }
			    }
			    row.setFields(updatedFields);
		    }
	    }
		
	    return sections;
    }
    
    protected boolean shouldIncludeField(Field field) {
    	boolean includeField = true;
        if (field.getPropertyName().equalsIgnoreCase(KFSPropertyConstants.SOURCE_OF_FUNDS_TYPE_CODE)) {
        
            if (getParameterService().parameterExists(Account.class,  KFSParameterKeyConstants.CoaParameterConstants.DISPLAY_SOURCE_OF_FUNDS_IND)) {
        	    String sourceOfFundsParmValue = getParameterService().getParameterValueAsString(Account.class, KFSParameterKeyConstants.CoaParameterConstants.DISPLAY_SOURCE_OF_FUNDS_IND);
    			
            	if (sourceOfFundsParmValue.equalsIgnoreCase(KFSConstants.ParameterValues.YES)) {
            	    includeField = true;
    			} else {
    				includeField = false;
    			}
        	} else {
        		includeField = false;
        	}       	
        }
        return includeField;
    }
    
    public ParameterService getParameterService() {
        if(parameterService == null){
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

}
