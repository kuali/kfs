/*
 * Copyright 2008 The Kuali Foundation
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

