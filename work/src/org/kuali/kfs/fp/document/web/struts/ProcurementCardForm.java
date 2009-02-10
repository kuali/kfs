/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.web.struts;

import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.DISPUTE_URL_PARM_NM;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This class is the form class for the ProcurementCard document. This method extends the parent KualiTransactionalDocumentFormBase
 * class which contains all of the common form methods and form attributes needed by the Procurment Card document.
 */
public class ProcurementCardForm extends KualiAccountingDocumentFormBase implements CapitalAssetEditable{
    private static final long serialVersionUID = 1L;
    private List newTargetLines;
    
    private CapitalAssetInformation capitalAssetInformation;

    /**
     * Override to accomodate multiple target lines.
     * 
     * @see org.kuali.rice.kns.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        //
        // now run through all of the accounting lines and make sure they've been uppercased and populated appropriately

        // handle new accountingLine, if one is being added
        String methodToCall = this.getMethodToCall();
        if (StringUtils.isNotBlank(methodToCall)) {
            if (methodToCall.equals(KFSConstants.INSERT_SOURCE_LINE_METHOD)) {
                populateSourceAccountingLine(getNewSourceLine());
            }

            if (methodToCall.equals(KFSConstants.INSERT_TARGET_LINE_METHOD)) {
                // This is the addition for the override: Handle multiple accounting lines ...
                for (Iterator newTargetLinesIter = getNewTargetLines().iterator(); newTargetLinesIter.hasNext();) {
                    TargetAccountingLine targetAccountingLine = (TargetAccountingLine) newTargetLinesIter.next();
                    populateTargetAccountingLine(targetAccountingLine);
                }
            }
        }

        // don't call populateAccountingLines if you are copying or errorCorrecting a document,
        // since you want the accountingLines in the copy to be "identical" to those in the original
        if (!StringUtils.equals(methodToCall, KFSConstants.COPY_METHOD) && !StringUtils.equals(methodToCall, KFSConstants.ERRORCORRECT_METHOD)) {
            populateAccountingLines();
        }

        setDocTypeName(discoverDocumentTypeName());
    }

    /**
     * Constructs a ProcurmentCardForm instance and sets up the appropriately casted document. Also, the newSourceLine needs to be
     * the extended ProcurementCardSourceAccountingLine, for the additional trans line nbr.
     */
    public ProcurementCardForm() {
        super();
        setDocument(new ProcurementCardDocument());
        this.newTargetLines = new TypedArrayList(ProcurementCardTargetAccountingLine.class);
        this.setCapitalAssetInformation(new CapitalAssetInformation());
    }

    /**
     * @return The retreived APC string used for the dispute url.
     */
    public String getDisputeURL() {
        return SpringContext.getBean(ParameterService.class).getParameterValue(ProcurementCardDocument.class, DISPUTE_URL_PARM_NM);
    }


    /**
     * @return Returns the newTargetLines.
     */
    public List getNewTargetLines() {
        return newTargetLines;
    }

    /**
     * @param newTargetLines The newTargetLines to set.
     */
    public void setNewTargetLines(List newTargetLines) {
        this.newTargetLines = newTargetLines;
    }


    /**
     * Override to return ProcurementCardTargetAccountingLine
     * 
     * @see org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase#getBaselineSourceAccountingLine(int)
     */
    public TargetAccountingLine getBaselineTargetAccountingLine(int index) {
        while (getBaselineTargetAccountingLines().size() <= index) {
            getBaselineTargetAccountingLines().add(new ProcurementCardTargetAccountingLine());
        }
        return (ProcurementCardTargetAccountingLine) getBaselineTargetAccountingLines().get(index);
    }
    
    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#getCapitalAssetInformation()
     */
    public CapitalAssetInformation getCapitalAssetInformation() {
        return this.capitalAssetInformation;
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#setCapitalAssetInformation(org.kuali.kfs.fp.businessobject.CapitalAssetInformation)
     */
    public void setCapitalAssetInformation(CapitalAssetInformation capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;        
    }
}
