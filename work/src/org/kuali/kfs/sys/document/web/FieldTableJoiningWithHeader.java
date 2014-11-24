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
