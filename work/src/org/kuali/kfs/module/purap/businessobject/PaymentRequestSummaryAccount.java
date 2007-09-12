package org.kuali.module.purap.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCodeCurrent;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.purap.document.PaymentRequestDocument;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PaymentRequestSummaryAccount extends PaymentRequestAccount {

	private Integer purapDocumentIdentifier;
    
    private PaymentRequestDocument paymentRequest;
    
    /**
	 * Default constructor.
	 */
	public PaymentRequestSummaryAccount() {

	}

    public PaymentRequestSummaryAccount(SourceAccountingLine account, Integer purapDocumentIdentifier) {
        this.setPurapDocumentIdentifier(purapDocumentIdentifier);
        this.setChartOfAccountsCode(account.getChartOfAccountsCode());
        this.setAccountNumber(account.getAccountNumber());
        this.setSubAccountNumber(account.getSubAccountNumber());
        this.setFinancialObjectCode(account.getFinancialObjectCode());
        this.setFinancialSubObjectCode(account.getFinancialSubObjectCode());
        this.setProjectCode(account.getProjectCode());
        this.setOrganizationReferenceId(account.getOrganizationReferenceId());
        this.setAmount(account.getAmount());
    }

    public PaymentRequestDocument getPaymentRequest() {
        return paymentRequest;
    }

    public void setPaymentRequest(PaymentRequestDocument paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

    /**
     * ItemIdentifier is not a valid field in this table because it is the 
     * summary of accounts for the document, not per item
     * @deprecated
     */
    @Override
    public Integer getItemIdentifier() {
        return super.getItemIdentifier();
    }

    /**
     * ItemIdentifier is not a valid field in this table because it is the 
     * summary of accounts for the document, not per item
     * @deprecated
     */
    @Override
    public void setItemIdentifier(Integer itemIdentifier) {
        super.setItemIdentifier(itemIdentifier);
    }

    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }


}
