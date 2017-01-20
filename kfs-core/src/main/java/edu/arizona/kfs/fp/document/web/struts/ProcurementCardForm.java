package edu.arizona.kfs.fp.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSParameterKeyConstants;

public class ProcurementCardForm extends org.kuali.kfs.fp.document.web.struts.ProcurementCardForm {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardForm.class);

    /**
     * Constructs a ProcurementCardForm
     */
    public ProcurementCardForm() {
        super();
        setDocument(new ProcurementCardDocument());
    }

    /**
     * Gets the docuwareTableParam attribute.
     *   
     * @return Returns the docuwareTableParam.
     */
    public String getDocuwareTableParam() {
        return SpringContext.getBean(ParameterService.class).getParameterValueAsString(ProcurementCardDocument.class, KFSConstants.DOCUWARE_TABLE_PARAMETER);
    }

    /**
     * @return an array, parallel to the ProcurementCardDocument#getTransactionEntries, which holds whether the
     *         current user can see the credit card number or not.
     *         In this case, the credit card number should be masked at all times.
     */
    @Override
    public List<Boolean> getTransactionCreditCardNumbersViewStatus() {
        if (this.transactionCreditCardNumbersViewStatus == null) {
            this.transactionCreditCardNumbersViewStatus = new ArrayList<Boolean>();
            this.transactionCreditCardNumbersViewStatus.add(Boolean.FALSE);
        }
        return transactionCreditCardNumbersViewStatus;
    }

    /**
     * Customization for UA Sales Tax Amount and Tax Exempt Indicator.
     * This method returns the value of the ENABLE_SALES_TAX_AMOUNT_TAX_EXEMPT_IND system parameter.
     * For use with the procurementCardTransactions.tag file.
     *
     * @return
     */
    public boolean getEnableSalesTaxIndicator() {
        boolean parameterValue = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(ProcurementCardDocument.class, KFSParameterKeyConstants.ENABLE_SALES_TAX_AMOUNT_TAX_EXEMPT_IND);
        return parameterValue;
    }
}
