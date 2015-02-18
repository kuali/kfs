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
package org.kuali.kfs.sys.businessobject;


/**
 * This class represents the "source" grouping of accounting lines in a given document. Its counterpart is the TargetAccountingLine
 * class. Both objects' data is stored in the DB in a single table named "FP_ACCT_LINES_T." Most documents follow the "source" and
 * "target" patterns. Source Accounting Line Business Object
 */
public class SourceAccountingLine extends AccountingLineBase {

    private static final long serialVersionUID = -2699347311790831686L;

    /**
     * This constructor needs to initialize the financialDocumentLineTypeCode attribute to the value for this class.
     */
    public SourceAccountingLine() {
        super();
        super.financialDocumentLineTypeCode = "F";
    }
}
