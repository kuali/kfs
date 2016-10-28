package edu.arizona.kfs.fp.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.fp.businessobject.ProcurementCardHolder;

public class ProcurementCardDocument extends org.kuali.kfs.fp.document.ProcurementCardDocument {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardDocument.class);
	
	/**
	 * Default serial version UID, courtesy of Eclipse
	 */
	private static final long serialVersionUID = 1L;
	
    private static final String HAS_RECONCILER_NODE = "HasReconciler";
    
    protected ProcurementCardHolder procurementCardHolder;
	
    @Override
	public ProcurementCardHolder getProcurementCardHolder() {
		return procurementCardHolder;
	}

	public void setProcurementCardHolder(ProcurementCardHolder procurementCardHolder) {
		this.procurementCardHolder = procurementCardHolder;
	}

	/**
     * Answers true when invoice recurrence details are provided by the user
     * 
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
    	if(LOG.isDebugEnabled()){
    		LOG.debug("Answering split node question '" + nodeName +"'.");	
    	}
        if (HAS_RECONCILER_NODE.equalsIgnoreCase(nodeName)) {
            return hasReconciler();
        }       
        return super.answerSplitNodeQuestion(nodeName);
    }
    
    /**
	 * @return <code>true</code> if this {@link ProcurementCardDocument} has a
	 *         reconciler entity, <code>false</code> otherwise.
	 */
    private boolean hasReconciler() {
        boolean retCode = true;
        if (ObjectUtils.isNull(getProcurementCardHolder()) || 
            ObjectUtils.isNull(getProcurementCardHolder().getProcurementCardDefault()) ||
            ObjectUtils.isNull(getProcurementCardHolder().getProcurementCardDefault().getReconcilerGroupId()) ||
            ObjectUtils.isNull(getProcurementCardHolder().getProcurementCardDefault().getCardHolderSystemId())) {
            retCode = false;
        }
        else {
            List<String> groupMembers = new ArrayList<String>();
            groupMembers = SpringContext.getBean(GroupService.class).getMemberPrincipalIds(getProcurementCardHolder().getProcurementCardDefault().getReconcilerGroupId());
            if (groupMembers.isEmpty() ||
                (groupMembers.size() == 1 &&
                 groupMembers.get(0).equals(getProcurementCardHolder().getProcurementCardDefault().getCardHolderSystemId()))) {
                retCode = false;
            }
        }
        return retCode;
    } 

}
