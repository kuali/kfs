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

import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.document.authorization.FinancialProcessingAccountingLineAuthorizer;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.service.KualiConfigurationService;

public class CustomerInvoiceDocumentSourceLinesAuthorizer extends FinancialProcessingAccountingLineAuthorizer {

    private static final String RECALCULATE_METHOD_NAME = "recalculateSourceLine";
    private static final String RECALCULATE_LABEL = "Recalculate Source Accounting Line";
    private static final String RECALCULATE_BUTTON_IMAGE = "tinybutton-recalculate.gif";
    private static final String DISCOUNT_METHOD_NAME = "discountSourceLine";
    private static final String DISCOUNT_LABEL = "Discount a Source Accounting Line";
    private static final String DISCOUNT_BUTTON_IMAGE = "tinybutton-discount.gif";
    private static final String REFRESH_METHOD_NAME = "refreshNewSourceLine";
    private static final String REFRESH_LABEL = "Refresh New Source Line";
    private static final String REFRESH_BUTTON_IMAGE = "tinybutton-refresh.gif";
    
    public List<AccountingLineViewAction> getActions(AccountingDocument accountingDocument, AccountingLine line, String accountingLineProperty, Integer accountingLineIndex, Person currentUser, Map editModesForDocument, String groupTitle) {
        List<AccountingLineViewAction> actions = super.getActions(accountingDocument, line, accountingLineProperty, accountingLineIndex, currentUser, editModesForDocument, groupTitle);

        String editMode = super.getEditModeForAccountingLine(accountingDocument, line, (accountingLineIndex == null), currentUser, editModesForDocument);        
        if (AuthorizationConstants.EditMode.FULL_ENTRY.equals(editMode)) {
            
            CustomerInvoiceDetail invoiceLine = (CustomerInvoiceDetail) line;
            
            //  get the images base directory
            String kfsImagesPath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString("externalizable.images.url");
            
            //  show the Refresh button on the New Line Actions
            if (isNewLine(accountingLineIndex)) {
                actions.add(new AccountingLineViewAction(REFRESH_METHOD_NAME, REFRESH_LABEL, kfsImagesPath + REFRESH_BUTTON_IMAGE));
            }
            
            else {
                //  always add the Recalculate button if its in edit mode
                String groupName = super.getActionInfixForExtantAccountingLine(line, accountingLineProperty);
                String methodName = methodName(line, accountingLineProperty, accountingLineIndex, RECALCULATE_METHOD_NAME);
                actions.add(new AccountingLineViewAction(methodName, RECALCULATE_LABEL, kfsImagesPath + RECALCULATE_BUTTON_IMAGE));
                
                //  only add the Discount button if its not a Discount Line or a Discount Line Parent
                if (showDiscountButton(invoiceLine)) {
                    methodName = methodName(line, accountingLineProperty, accountingLineIndex, DISCOUNT_METHOD_NAME);
                    actions.add(new AccountingLineViewAction(methodName, DISCOUNT_LABEL, kfsImagesPath + DISCOUNT_BUTTON_IMAGE));
                }
            }
        }
        
        return actions;
    }
    
    private boolean showDiscountButton(CustomerInvoiceDetail invoiceLine) {
        return (!invoiceLine.isDiscountLine() && !invoiceLine.isDiscountLineParent());
    }
    
    private String methodName(AccountingLine line, String accountingLineProperty, Integer accountingLineIndex, String methodName) {
        String infix = super.getActionInfixForExtantAccountingLine(line, accountingLineProperty);
        return methodName + ".line" + accountingLineIndex.toString() + ".anchoraccounting" + infix + "Anchor";
    }
    
    private boolean isNewLine(Integer accountingLineIndex) {
        return (accountingLineIndex == null || accountingLineIndex.intValue() < 0);
    }
    
}
