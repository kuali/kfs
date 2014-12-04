/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.DocumentHeader;

public class PurapSearchUtils {

    public static String getWorkFlowStatusString(DocumentHeader documentHeader) {
        if (documentHeader.getWorkflowDocument().isInitiated()) {
            return "INITIATED";
        }
        else if (documentHeader.getWorkflowDocument().isEnroute()) {
            return "ENROUTE";
        }
        else if (documentHeader.getWorkflowDocument().isDisapproved()) {
            return "DISAPPROVED";
        }
        else if (documentHeader.getWorkflowDocument().isCanceled()) {
            return "CANCELLED";
        }
        else if (documentHeader.getWorkflowDocument().isApproved()) {
            return "APPROVED";
        }
        else {
            return StringUtils.EMPTY;
        }
    }
}
