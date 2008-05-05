/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Sep 22, 2004
 *
 */
package org.kuali.module.pdp.service;

import java.io.Serializable;

/**
 * @author jsissom
 */
public class SecurityRecord implements Serializable {
    private boolean sysAdminRole = false;
    private boolean rangesRole = false;
    private boolean cancelRole = false;
    private boolean submitRole = false;
    private boolean processRole = false;
    private boolean holdRole = false;
    private boolean viewAllRole = false;
    private boolean viewIdRole = false;
    private boolean viewBankRole = false;
    private boolean limitedViewRole = false;
    private boolean taxHoldersRole = false;
    private boolean viewIdPartialBank = false;

    public SecurityRecord() {
        super();
    }

    @Override
    public String toString() {
        String out = "sysAdminRole = " + sysAdminRole + " rangesRole = " + rangesRole + " cancelRole = " + cancelRole + " submitRole = " + submitRole;
        out = out + " processRole = " + processRole + " holdRole = " + holdRole + " viewAllRole = " + viewAllRole + " viewIdRole = " + viewIdRole;
        out = out + " viewBankRole = " + viewBankRole + " limitedViewRole = " + limitedViewRole + " taxHoldersrole = " + taxHoldersRole;
        return out;
    }

    public boolean isAnyRole() {
        return sysAdminRole || rangesRole || cancelRole || submitRole || processRole || holdRole || viewAllRole || viewIdRole || viewBankRole || limitedViewRole || taxHoldersRole;
    }

    public boolean isAnyViewRole() {
        return sysAdminRole || viewAllRole || viewIdRole || viewBankRole || limitedViewRole;
    }

    public boolean isCancelRole() {
        return cancelRole;
    }

    public void setCancelRole(boolean cancelRole) {
        this.cancelRole = cancelRole;
    }

    public boolean isHoldRole() {
        return holdRole;
    }

    public void setHoldRole(boolean holdRole) {
        this.holdRole = holdRole;
    }

    public boolean isLimitedViewRole() {
        return limitedViewRole;
    }

    public void setLimitedViewRole(boolean limitedViewRole) {
        this.limitedViewRole = limitedViewRole;
    }

    public boolean isProcessRole() {
        return processRole;
    }

    public void setProcessRole(boolean processRole) {
        this.processRole = processRole;
    }

    public boolean isRangesRole() {
        return rangesRole;
    }

    public void setRangesRole(boolean rangesRole) {
        this.rangesRole = rangesRole;
    }

    public boolean isSubmitRole() {
        return submitRole;
    }

    public void setSubmitRole(boolean submitRole) {
        this.submitRole = submitRole;
    }

    public boolean isSysAdminRole() {
        return sysAdminRole;
    }

    public void setSysAdminRole(boolean sysAdminRole) {
        this.sysAdminRole = sysAdminRole;
    }

    public boolean isTaxHoldersRole() {
        return taxHoldersRole;
    }

    public void setTaxHoldersRole(boolean taxHoldersRole) {
        this.taxHoldersRole = taxHoldersRole;
    }

    public boolean isViewAllRole() {
        return viewAllRole;
    }

    public void setViewAllRole(boolean viewAllRole) {
        this.viewAllRole = viewAllRole;
    }

    public boolean isViewIdRole() {
        return viewIdRole;
    }

    public void setViewIdRole(boolean viewIdRole) {
        this.viewIdRole = viewIdRole;
    }

    public boolean isViewBankRole() {
        return viewIdRole;
    }

    public void setViewBankRole(boolean viewBankRole) {
        this.viewBankRole = viewBankRole;
    }

    public boolean isViewIdPartialBank() {
        return viewIdPartialBank;
    }

    public void setViewIdPartialBank(boolean viewIdPartialBank) {
        this.viewIdPartialBank = viewIdPartialBank;
    }
}
