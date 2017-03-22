package edu.arizona.kfs.module.purap.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;

import edu.arizona.kfs.module.purap.PurapKeyConstants;
import edu.arizona.kfs.module.purap.PurapParameterConstants;


public class PurapAccountingServiceImpl extends org.kuali.kfs.module.purap.service.impl.PurapAccountingServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapAccountingServiceImpl.class);
    
    @Override
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale, Class clazz) {
        String methodName = "generateAccountDistributionForProration()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
        
        List<PurApAccountingLine> newAccounts = new ArrayList<PurApAccountingLine>();
        
        if(totalAmount.isZero()) {
            PurApAccountingLine newAccountingLine = null;
            try {
                newAccountingLine = (PurApAccountingLine) clazz.newInstance();
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            newAccountingLine.setChartOfAccountsCode(getParameterValue(PurapParameterConstants.ADDITIONAL_CHARGES_CLEARING_CHART_OF_ACCOUNTS));
            newAccountingLine.setAccountNumber(getParameterValue(PurapParameterConstants.ADDITIONAL_CHARGES_CLEARING_ACCOUNT));
            newAccountingLine.setFinancialObjectCode(getParameterValue(PurapParameterConstants.ADDITIONAL_CHARGES_CLEARING_OBJECT_CODE));
            
            newAccountingLine.setAccountLinePercent(ONE_HUNDRED);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(methodName + " adding " + newAccountingLine.getAccountLinePercent());
            }
            newAccounts.add(newAccountingLine);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(methodName + " total = " + newAccountingLine.getAccountLinePercent());
            }
            
            generateWarningsOnDocument();
            
            return newAccounts;
        } else {
            return super.generateAccountDistributionForProration(accounts, totalAmount, percentScale, clazz);
        }
    }
    
    private void generateWarningsOnDocument() {
        MessageMap messages = GlobalVariables.getMessageMap();
        messages.putWarning(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.WARNING_ADDITIONAL_CHARGES_ACCOUNT_DEFAULTED);
        messages.putWarning(KRADConstants.GLOBAL_ERRORS, PurapKeyConstants.WARNING_ADDITIONAL_CHARGES_ACCOUNT_DEFAULTED);
    }

    private String getParameterValue(String parameterName) {
        return parameterService.getParameterValueAsString(PaymentRequestDocument.class, parameterName);
    }
    
}
