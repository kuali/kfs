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
package org.kuali.kfs.fp.document.authorization;

import java.util.Set;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentPresentationControllerBase;
import org.kuali.rice.krad.document.Document;

public class AdvanceDepositDocumentPresentationController extends AccountingDocumentPresentationControllerBase{

    @Override
    public Set<String> getDocumentActions(Document document) {

        Set<String> documentActions = super.getDocumentActions(document);

        if (document instanceof FinancialSystemTransactionalDocument) {
            if (canErrorCorrect((FinancialSystemTransactionalDocument) document)) {
                documentActions.add(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT);
            }

            documentActions.add(KFSConstants.KFS_ACTION_CAN_EDIT_BANK);
        }

        return documentActions;
    }

    @Override
    public Set<String> getEditModes(Document document) {

        Set<String> editModes = super.getEditModes(document);
        if (document instanceof AmountTotaling) {
            editModes.add(KFSConstants.AMOUNT_TOTALING_EDITING_MODE);
        }

        editModes.add(KFSConstants.BANK_ENTRY_VIEWABLE_EDITING_MODE);

        return editModes;

    }
}
