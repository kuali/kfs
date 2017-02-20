package edu.arizona.kfs.fp.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kew.api.document.node.RouteNodeInstance;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSParameterKeyConstants;

public class ProcurementCardForm extends org.kuali.kfs.fp.document.web.struts.ProcurementCardForm {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardForm.class);
    
    private static final String PROCUREMENT_CARD_RECONCILER = "ProcurementCardReconciler";
    private static final String BUTTON_PROPERTY = "methodToCall.returnToReconciler";
    private static final String BUTTON_SOURCE = "${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_return.gif";
    private static final String BUTTON_ALT_TEXT = "Return To Reconciler";
    
    private ExtraButton returnButton;

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
    
    @Override
    public List<ExtraButton> getExtraButtons() {
        extraButtons.clear();

        //Needed to refresh workflow doc to get the state and approval flag set correctly
        getProcurementCardDocument().getDocumentHeader().getWorkflowDocument().refresh();

        List<RouteNodeInstance> routeNodeInstances = getProcurementCardDocument().getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeInstances();
        String node = routeNodeInstances.get(0).getName();
        
        //Show RETURN to Reconciler button only if user is an approver (and not reconciler)
        if (getProcurementCardDocument().getDocumentHeader().getWorkflowDocument().isEnroute() &&
                getProcurementCardDocument().getDocumentHeader().getWorkflowDocument().isApprovalRequested() &&
                !node.equalsIgnoreCase(PROCUREMENT_CARD_RECONCILER)) {
            extraButtons.add(returnToReconcilerButton());
       }
        return extraButtons;
    }

    private ExtraButton returnToReconcilerButton() {
        if(returnButton == null) {
            returnButton = new ExtraButton();
            returnButton.setExtraButtonProperty(BUTTON_PROPERTY);
            returnButton.setExtraButtonSource(BUTTON_SOURCE);
            returnButton.setExtraButtonAltText(BUTTON_ALT_TEXT);
        }
        return returnButton;
    }
    
    public ProcurementCardDocument getProcurementCardDocument() {
        return (ProcurementCardDocument) getDocument();
    }

    public void setProcurementCardDocument(ProcurementCardDocument procurementCardDocument) {
        setDocument(procurementCardDocument);
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
