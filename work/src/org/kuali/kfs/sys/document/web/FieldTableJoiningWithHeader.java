/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.document.web;

import java.util.List;

/**
 * Extension of FieldTableJoining that adds header cells correctly 
 */
public abstract class FieldTableJoiningWithHeader extends FieldTableJoining implements TableJoiningWithHeader {

    /**
     * @see org.kuali.kfs.sys.document.web.FieldTableJoining#joinTable(java.util.List)
     */
    @Override
    public void joinTable(List<AccountingLineTableRow> rows) {
        int rowsTaken = 0;
        // 1. add header cell
        if (!isHidden()) {
            AccountingLineTableCell headerCell = createHeaderLabelTableCell();
            rows.get(0).addCell(headerCell);
            rowsTaken += 1;
        }
        // 2. add field cell
        AccountingLineTableCell cell = createTableCell();
        cell.setRowSpan(rows.size() - rowsTaken);
        rows.get(rowsTaken).addCell(cell);
    }
    
    /**
     * Creates a header label cell for this element
     * @return a table cell holding the header label to render
     */
    protected AccountingLineTableCell createHeaderLabelTableCell() {
        AccountingLineTableCell cell = new AccountingLineTableCell();
        cell.addRenderableElement(createHeaderLabel());
        cell.setRendersAsHeader(true);
        return cell;
    }

    /**
     * This joins a row but adds a header to the header label row
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinRow(org.kuali.kfs.sys.document.web.AccountingLineTableRow, org.kuali.kfs.sys.document.web.AccountingLineTableRow)
     */
    public void joinRow(AccountingLineTableRow headerLabelRow, AccountingLineTableRow row) {
        if (!isHidden()) {
            headerLabelRow.addCell(createHeaderLabelTableCell());
        }
        row.addCell(createTableCell());
    }

    /**
     * Always returns 2 - one row for the header, one for the row
     * @see org.kuali.kfs.sys.document.web.FieldTableJoining#getRequestedRowCount()
     */
    @Override
    public int getRequestedRowCount() {
        return 2;
    }

}
