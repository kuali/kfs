package edu.arizona.kfs.gl.service;

import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.rice.krad.util.MessageMap;

public interface GlobalTransactionEditService {

    /**
     * This method will check the accounting line parameters against the ones in the db to see if there is a match
     * Warning - this should only be used by the online transaction system as it defaults to using the home origin code.
     * If you need to specify an alternate origin code then use the overloaded method.
     * 
     * @param accountingLine
     * @param docTypeCd - type of the document that contains the accounting line
     * @return True if Accounting Line violates rule
     */
    public Message isAccountingLineAllowable(AccountingLineBase accountingLine, String docTypeCd);

    /**
     * This method will check the accounting line parameters against the ones in the db to see if there is a match
     *
     * @param accountingLine
     * @return True if Accounting Line violates rule
     */
    public Message isAccountingLineAllowable(AccountingLineBase accountingLine, String originCode, String docTypeCd);
    
    public Message isAccountingLineAllowable(String originCd, String fundGrpCd, String subFundGrpCd, String docTypeCd, String objTypCd, String subObjTypCd);

    public boolean isAccountingLineBatchAllowable(CollectorBatch batch, MessageMap messageMap);

}
