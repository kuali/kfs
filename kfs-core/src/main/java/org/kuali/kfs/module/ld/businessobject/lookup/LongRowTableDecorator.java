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
package org.kuali.kfs.module.ld.businessobject.lookup;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.displaytag.decorator.TableDecorator;
import org.displaytag.properties.MediaTypeEnum;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;

/**
 * Decorator class for the Long Row Table.
 */

public class LongRowTableDecorator extends TableDecorator {

    private static final int numOfColumnInEachRow = 12;
    private static final int numOfColumnInFirstRow = 14;

    /**
     * @see org.displaytag.decorator.TableDecorator#finishRow()
     * 
     * KRAD Conversion: Performs customization of the row.
     * 
     * No uses data dictionary.
     */
    @Override
    public String finishRow() {
        MediaTypeEnum mediaType = (MediaTypeEnum) getPageContext().getAttribute("mediaType");
        ResultRow row = (ResultRow) getCurrentRowObject();

        if (MediaTypeEnum.HTML.equals(mediaType)) {
            StringBuffer rowBuffer = new StringBuffer();

            List columns = row.getColumns();
            int columnCount = columns.size();
            int numOfRows = (int) Math.ceil(1.0 * (columnCount - numOfColumnInFirstRow) / numOfColumnInEachRow);

            rowBuffer.append("<tr>").append("<td colspan='" + numOfColumnInFirstRow + "'><br/><center>");
            rowBuffer.append("<table class='datatable-80' cellspacing='0' cellpadding='0' >");

            for (int rowIndex = 0; rowIndex < numOfRows; rowIndex++) {

                rowBuffer.append("<tr>");
                for (int columnIndex = 0; columnIndex < numOfColumnInEachRow; columnIndex++) {

                    int currentPosition = rowIndex * numOfColumnInEachRow + columnIndex + numOfColumnInFirstRow;
                    String title = currentPosition < columnCount ? ((Column) columns.get(currentPosition)).getColumnTitle() : null;

                    rowBuffer.append("<th>");
                    rowBuffer.append(!StringUtils.isBlank(title) ? title : "&nbsp;");
                    rowBuffer.append("</th>");
                }
                rowBuffer.append("</tr>");

                rowBuffer.append("<tr>");
                for (int columnIndex = 0; columnIndex < numOfColumnInEachRow; columnIndex++) {
                    int currentPosition = rowIndex * numOfColumnInEachRow + columnIndex + numOfColumnInFirstRow;
                    String value = currentPosition < columnCount ? ((Column) columns.get(currentPosition)).getPropertyValue() : null;

                    rowBuffer.append("<td class='infocell'>");
                    rowBuffer.append(!StringUtils.isBlank(value) ? value : "&nbsp;");
                    rowBuffer.append("</td>");
                }
                rowBuffer.append("</tr>");
            }
            rowBuffer.append("</table></center><br/><br/></td></tr>");
            return rowBuffer.toString();
        }
        return super.finishRow();
    }
}
