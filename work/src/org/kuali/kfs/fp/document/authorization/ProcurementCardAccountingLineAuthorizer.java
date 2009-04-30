/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.authorization;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;

public class ProcurementCardAccountingLineAuthorizer extends FinancialProcessingAccountingLineAuthorizer {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardAccountingLineAuthorizer.class);

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getKimHappyPropertyNameForField(java.lang.String)
     */
    @Override
    protected String getKimHappyPropertyNameForField(String convertedName) {
        String name = stripDocumentPrefixFromName(convertedName);
        name = name.replaceAll("\\[\\d+\\]", StringUtils.EMPTY);
        name = name.replaceFirst("(.)*transactionEntries\\.", StringUtils.EMPTY);

        return name;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getAddMethod(org.kuali.kfs.sys.businessobject.AccountingLine,
     *      java.lang.String, java.lang.Integer)
     */
    @Override
    protected String getAddMethod(AccountingLine accountingLine, String accountingLineProperty) {
        String infix = getActionInfixForNewAccountingLine(accountingLine, accountingLineProperty);
        String lineIndex = this.getLineContainerIndex(accountingLineProperty);

        return KFSConstants.INSERT_METHOD + infix + "Line.line" + lineIndex + ".anchoraccounting" + infix + "Anchor";
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getBalanceInquiryMethod(org.kuali.kfs.sys.businessobject.AccountingLine,
     *      java.lang.String, java.lang.Integer)
     */
    @Override
    protected String getBalanceInquiryMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        String lineContainer = this.getLineContainer(accountingLineProperty) + ".";

        return KFSConstants.PERFORMANCE_BALANCE_INQUIRY_FOR_METHOD + infix + "Line." + lineContainer + "line" + accountingLineIndex + ".anchoraccounting" + infix + "existingLineLineAnchor" + accountingLineIndex;
    }

    protected String getLineContainer(String accountingLineProperty) {
        String lineContainer = stripDocumentPrefixFromName(accountingLineProperty);
        return StringUtils.substringBeforeLast(lineContainer, ".");
    }

    protected String getLineContainerIndex(String accountingLineProperty) {
        String lineContainer = this.getLineContainer(accountingLineProperty);
        return StringUtils.substringBetween(lineContainer, "[", "]");
    }
}
