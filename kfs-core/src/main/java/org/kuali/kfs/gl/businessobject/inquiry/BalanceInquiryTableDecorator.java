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
package org.kuali.kfs.gl.businessobject.inquiry;

import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.TableDecorator;
import org.displaytag.properties.MediaTypeEnum;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;

/**
 * A decorator meant to help display balance inquiry information for the displaytag tag library.
 */
public class BalanceInquiryTableDecorator extends TableDecorator {

    private int numOfNonMonthField;
    private int numOfMonthField = 13;
    private int rowCounter = 0;

    /**
     * Generates the first row of the table, which acts as the headers for the data.
     * 
     * @return the String to display as the first row in the table
     * @see org.displaytag.decorator.TableDecorator#startRow()
     *
     * KRAD Conversion: Inquirable customizes the first row with columns.
     * No use of data dictionary here.
     */
    @Override
    public String startRow() {
        // TableTagParameters.
        PageContext pageContext = getPageContext();
        MediaTypeEnum mediaType = (MediaTypeEnum) pageContext.getAttribute("mediaType");
        ResultRow row = (ResultRow) getCurrentRowObject();

        if (MediaTypeEnum.HTML.equals(mediaType)) { // Display the nested table.

            StringBuffer rowBuffer = new StringBuffer("<tr>");
            rowBuffer.append("<tr>");

            List columns = row.getColumns();
            int columnCount = 0;
            numOfNonMonthField = columns.size() - numOfMonthField;
            for (Iterator i = columns.iterator(); i.hasNext() && columnCount++ < numOfNonMonthField;) {
                Column column = (Column) i.next();

                if (rowCounter > 0) {
                    rowBuffer.append("<th>");
                    rowBuffer.append(column.getColumnTitle());
                    rowBuffer.append("</th>");
                }
            }
            rowBuffer.append("</tr>");
            return rowBuffer.toString();
        }
        return super.startRow();
    }

    /**
     * Generates the last row of the displayed table...which displays some kind of footer...
     * @return a String representing the last row of the table
     * @see org.displaytag.decorator.TableDecorator#finishRow()
     * 
     * KRAD Conversion: Inquirable customizes the last row with columns.
     * No use of data dictionary here.
     */
    @Override
    public String finishRow() {
        rowCounter++;
        PageContext pageContext = getPageContext();
        MediaTypeEnum mediaType = (MediaTypeEnum) pageContext.getAttribute("mediaType");
        ResultRow row = (ResultRow) getCurrentRowObject();

        if (MediaTypeEnum.HTML.equals(mediaType)) {

            // Display the nested table.
            StringBuffer rowBuffer = new StringBuffer("<tr>");

            rowBuffer.append("<td colspan='" + numOfNonMonthField + "' class=\"infocell\"><br><center>");
            rowBuffer.append("<table class=\"datatable-80\" cellspacing=\"0\" cellpadding=\"0\">");

            for (int o = 0; o < 3; o++) {
                rowBuffer.append("<tr>");

                for (int i = 0; i < 4; i++) {
                    int index = this.numOfNonMonthField + o + (3 * i);
                    Column column = (Column) row.getColumns().get(index);

                    rowBuffer.append("<th class=\"infocell\" width=\"10%\">");
                    rowBuffer.append(column.getColumnTitle());
                    rowBuffer.append("</th>");
                    rowBuffer.append("<td class=\"numbercell\" width=\"15%\">");
                    rowBuffer.append("<a href=\"").append(column.getPropertyURL()).append("\" target=\"blank\">");
                    rowBuffer.append(column.getPropertyValue()).append("</a>");
                    rowBuffer.append("</td>");

                }
                rowBuffer.append("</tr>");
            }

            rowBuffer.append("<tr>");
            rowBuffer.append("<td colspan='6'></td>");

            Column column = (Column) row.getColumns().get(numOfNonMonthField + numOfMonthField - 1);

            rowBuffer.append("<th class=\"infocell\" width=\"10%\">");
            rowBuffer.append(column.getColumnTitle());
            rowBuffer.append("</th>");
            rowBuffer.append("<td class=\"numbercell\" width=\"15%\">");
            rowBuffer.append("<a href=\"").append(column.getPropertyURL()).append("\" target=\"blank\">");
            rowBuffer.append(column.getPropertyValue()).append("</a>");
            rowBuffer.append("</td>");

            rowBuffer.append("</tr>");
            return rowBuffer.append("</table></center><br /></td></tr>").toString();
        }
        return super.finishRow();
    }
}
