package edu.arizona.kfs.module.ld.util;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * This class is a working unit of labor origin entry. It is formed by a group of similar labor origin entries. If any two entries
 * have the same values for the given fields, then they are similar and can be grouped.
 */
public class LaborLedgerUnitOfWork  extends org.kuali.kfs.module.ld.util.LaborLedgerUnitOfWork{

    /**
     * Get the default key fields as a list
     */
    @Override 
    protected List<String> getDefaultKeyFields() {
        List<String> defaultKeyFields = new ArrayList<String>(LaborConstants.consolidationAttributesOfOriginEntry());
        defaultKeyFields.remove(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
        defaultKeyFields.remove(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
        defaultKeyFields.remove(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID);
        defaultKeyFields.remove(KFSPropertyConstants.REFERENCE_FINANCIAL_DOCUMENT_TYPE_CODE);
        defaultKeyFields.remove(KFSPropertyConstants.REFERENCE_FINANCIAL_SYSTEM_ORIGINATION_CODE);
        defaultKeyFields.remove(KFSPropertyConstants.REFERENCE_FINANCIAL_DOCUMENT_NUMBER);

        return defaultKeyFields;
    }
}
