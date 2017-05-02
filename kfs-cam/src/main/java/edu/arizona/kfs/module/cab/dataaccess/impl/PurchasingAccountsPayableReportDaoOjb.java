package edu.arizona.kfs.module.cab.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import edu.arizona.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;

public class PurchasingAccountsPayableReportDaoOjb extends org.kuali.kfs.module.cab.dataaccess.impl.PurchasingAccountsPayableReportDaoOjb {
	
	@Override
	protected Collection<String> getDocumentType(Map fieldValues) {
        Collection<String> docTypeCodes = new ArrayList<String>();

        if (fieldValues.containsKey(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE)) {
            String fieldValue = (String) fieldValues.get(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE);
            if (StringUtils.isEmpty(fieldValue)) {
                docTypeCodes.add(CabConstants.PREQ);
                docTypeCodes.add(CabConstants.CM);
                docTypeCodes.add(CabConstants.PRNC); 
            }
            else {
                docTypeCodes.add(fieldValue);
            }
            // truncate the non-property filed
            fieldValues.remove(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE);
        }

        return docTypeCodes;
    }
}