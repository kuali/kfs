/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.module.ar.util.ARUtil;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;
import org.kuali.rice.kns.exception.DocumentTypeAuthorizationException;

public class CustomerInvoiceWriteoffDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {

    @Override
    public Map getEditMode(Document document, Person user) {

        Map<String, String> editModeMap = super.getEditMode(document, user);
        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = (CustomerInvoiceWriteoffDocument) document;

        if (StringUtils.equals(customerInvoiceWriteoffDocument.getStatusCode(), ArConstants.CustomerInvoiceWriteoffStatuses.INITIATE))
            editModeMap.put(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB, "TRUE");
        else
            editModeMap.put(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB, "FALSE");

        return editModeMap;
    }

    // TODO fix for kim
//    /**
//     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getDocumentActionFlags(Document, Person)
//     */
//    @Override
//    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
//
//        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = (CustomerInvoiceWriteoffDocument) document;
//        if (StringUtils.equals(customerInvoiceWriteoffDocument.getStatusCode(), ArConstants.CustomerInvoiceWriteoffStatuses.INITIATE)) {
//            flags.setCanSave(false);
//            flags.setCanClose(true);
//            flags.setCanCancel(false);
//        }
//        return flags;
//    }

    // TODO remove - replaced by kim
//    /**
//     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#canInitiate(java.lang.String,
//     *      org.kuali.rice.kim.bo.Person)
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
//        String writeoffGenerationOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_GENERATION_METHOD);
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

