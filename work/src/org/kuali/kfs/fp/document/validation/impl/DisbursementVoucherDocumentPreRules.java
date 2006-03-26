/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.financial.document.DisbursementVoucherDocument;

/**
 * Checks warnings and prompt conditions for dv document.
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherDocumentPreRules extends PreRulesContinuationBase implements DisbursementVoucherRuleConstants {
    private KualiConfigurationService kualiConfiguration;


    /**
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.MaintenanceDocument)
     */
    public boolean doRules(Document document) {
        boolean preRulesOK = true;
 
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) document;
        checkSpecialHandlingIndicator(dvDocument);
        
        return preRulesOK;
    }
    
    /**
     * If the special handling name and address 1 fields have value, this will mark the special
     * handling indicator for the user.
     * @param dvDocument
     */
    private void checkSpecialHandlingIndicator(DisbursementVoucherDocument dvDocument) {
        if (StringUtils.isNotBlank(dvDocument.getDvPayeeDetail().getDisbVchrRemitPersonName()) &&
            StringUtils.isNotBlank(dvDocument.getDvPayeeDetail().getDisbVchrRemitLine1Addr())) {
                dvDocument.setDisbVchrSpecialHandlingCode(true);
        }
    }

}