/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.web.struts.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.module.financial.bo.DepositWizardHelper;

/**
 * This class is the action form for the deposit document wizard.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DepositWizardForm extends KualiForm {
    private static final long serialVersionUID = 1L;

    private String cashDrawerVerificationUnit;

    private boolean cashDrawerClosed;
    private String cashDrawerStatusMessage;

    private List cashReceiptsReadyForDeposit;
    private ArrayList selectedCashReceipts;


    /**
     * Constructs a DepositWizardForm class instance.
     */
    public DepositWizardForm() {
        cashReceiptsReadyForDeposit = new ArrayList();
        selectedCashReceipts = new ArrayList();
    }

    /**
     * @return ArrayList
     */
    public List getCashReceiptsReadyForDeposit() {
        return cashReceiptsReadyForDeposit;
    }

    /**
     * @param cashReceiptsReadyForDeposit
     */
    public void setCashReceiptsReadyForDeposit(List cashReceiptsReadyForDeposit) {
        this.cashReceiptsReadyForDeposit = cashReceiptsReadyForDeposit;
    }

    /**
     * @return ArrayList
     */
    public ArrayList getSelectedCashReceipts() {
        return selectedCashReceipts;
    }

    /**
     * @param selectedCashReceipts
     */
    public void setSelectedCashReceipts(ArrayList selectedCashReceipts) {
        this.selectedCashReceipts = selectedCashReceipts;
    }

    /**
     * This method retrieves whether the cash receipt at the specified index will be selected or not.
     * 
     * @param index
     * @return DepositWizarHelper
     */
    public DepositWizardHelper getSelectedCashReceipt(int index) {
        while (this.selectedCashReceipts.size() <= index) {
            this.selectedCashReceipts.add(new DepositWizardHelper());
            // default to no check
        }
        return (DepositWizardHelper) this.selectedCashReceipts.get(index);
    }


    public void setCashDrawerStatusMessage(String cashDrawerStatusMessage) {
        this.cashDrawerStatusMessage = cashDrawerStatusMessage;
    }

    public String getCashDrawerStatusMessage() {
        return cashDrawerStatusMessage;
    }


    public boolean hasCashDrawerVerificationUnit() {
        return StringUtils.isNotBlank(cashDrawerVerificationUnit);
    }

    public void setCashDrawerVerificationUnit(String cashDrawerVerificationUnit) {
        this.cashDrawerVerificationUnit = cashDrawerVerificationUnit;
    }

    public String getCashDrawerVerificationUnit() {
        return cashDrawerVerificationUnit;
    }


    public boolean isCashDrawerClosed() {
        return cashDrawerClosed;
    }

    public void setCashDrawerClosed(boolean cashDrawerClosed) {
        this.cashDrawerClosed = cashDrawerClosed;
    }
}