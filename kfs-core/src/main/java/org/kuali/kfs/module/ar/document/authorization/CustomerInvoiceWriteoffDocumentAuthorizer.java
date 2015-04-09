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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;

public class CustomerInvoiceWriteoffDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {

    @Override
    protected void addRoleQualification(Object businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        
        CustomerInvoiceWriteoffDocument writeoffDoc = (CustomerInvoiceWriteoffDocument) businessObject;
        String invoiceDocNumber = writeoffDoc.getFinancialDocumentReferenceInvoiceNumber();
        if (StringUtils.isBlank(invoiceDocNumber)) {
            return;
        }
        CustomerInvoiceDocumentService invoiceService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        
        Collection<CustomerInvoiceDetail> invoiceDetails = invoiceService.getCustomerInvoiceDetailsForCustomerInvoiceDocument(invoiceDocNumber);
        
        //  adds the chart/account for each account used on the original invoice that will be credited by this
        for (CustomerInvoiceDetail invoiceDetail : invoiceDetails) {
            if (StringUtils.isNotBlank(invoiceDetail.getChartOfAccountsCode()) && StringUtils.isNotBlank(invoiceDetail.getAccountNumber())) {
                attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, invoiceDetail.getChartOfAccountsCode());
                attributes.put(KfsKimAttributes.ACCOUNT_NUMBER, invoiceDetail.getAccountNumber());
            }
        }
    }

//TODO Dont remove this until we're sure the FAU stuff is really in KIM
//    /**
//     * @see org.kuali.rice.krad.document.authorization.DocumentAuthorizerBase#canInitiate(java.lang.String,
//     *      org.kuali.rice.kim.api.identity.Person)
//     */
//    @Override
//    public void canInitiate(String documentTypeName, Person user) throws DocumentTypeAuthorizationException {
//        super.canInitiate(documentTypeName, user);
//        
//        if (!ARUtil.isUserInArBillingOrg(user)) {
//            throw new DocumentInitiationAuthorizationException(ArKeyConstants.ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG, 
//                    new String[] { "(Users in an AR Billing Org)", "Customer Invoice WriteOff" });
//        }
//
//        // if writeoff option is set up for to use organization accounting default FAU, those values must exist before a writeoff
//        // document can be initiated
//        /*
//        String writeoffGenerationOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_GENERATION_METHOD);
//        boolean isUsingOrgAcctDefaultWriteoffFAU = ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_ORG_ACCT_DEFAULT.equals(writeoffGenerationOption);
//        if (isUsingOrgAcctDefaultWriteoffFAU) {
//
//            Integer currentUniversityFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
//
//            criteria = new HashMap<String, Object>();
//            criteria.put("universityFiscalYear", currentUniversityFiscalYear);
//            criteria.put("chartOfAccountsCode", chartUser.getChartOfAccountsCode());
//            criteria.put("organizationCode", chartUser.getOrganizationCode());
//            OrganizationAccountingDefault organizationAccountingDefault = (OrganizationAccountingDefault) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
//
//            // check if org. acct. default exists
//            if (ObjectUtils.isNull(organizationAccountingDefault)) {
//                throw new DocumentInitiationAuthorizationException(ArConstants.ERROR_ORG_ACCT_DEFAULT_FOR_USER_MUST_EXIST, new String[] {});
//            }
//            //check if org acct. default writeoff chart, object, or account number are empty
//            else if (StringUtils.isEmpty(organizationAccountingDefault.getWriteoffAccountNumber()) || StringUtils.isEmpty(organizationAccountingDefault.getWriteoffChartOfAccountsCode()) || StringUtils.isEmpty(organizationAccountingDefault.getWriteoffFinancialObjectCode())) {
//                throw new DocumentInitiationAuthorizationException(ArConstants.ERROR_ORG_ACCT_DEFAULT_WRITEOFF_MUST_EXIST, new String[] {});
//            }
//        }*/
//    }

}

