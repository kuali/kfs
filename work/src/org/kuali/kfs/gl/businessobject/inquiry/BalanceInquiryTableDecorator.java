/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.web;

import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.TableDecorator;
import org.displaytag.properties.MediaTypeEnum;
import org.kuali.core.web.uidraw.Column;
import org.kuali.core.web.uidraw.ResultRow;

/**
 */
public class BalanceInquiryTableDecorator extends TableDecorator {

    private int numOfNonMonthField = 11;
    private int numOfMonthField = 13;
    private int rowCounter;

    /**
     * Constructs a BalanceInquiryTableDecorator.java.
     */
    public BalanceInquiryTableDecorator() {
        super();
    }

    @Override
    public String startRow() {
        // TableTagParameters.
        PageContext pageContext = getPageContext();
        MediaTypeEnum mediaType = (MediaTypeEnum) pageContext.getAttribute("mediaType");

        ResultRow row = (ResultRow) getCurrentRowObject();

        if (MediaTypeEnum.HTML.equals(mediaType)) { // Display the nested table.

            StringBuffer rowBuffer = new StringBuffer("<tr>");

            if (1 <= rowCounter) {

                rowBuffer.append("<tr>");

                List columns = row.getColumns();

                int columnCount = 0;
                for (Iterator i = columns.iterator(); i.hasNext() && columnCount++ < numOfNonMonthField;) {

                    Column column = (Column) i.next();

                    if (columnCount > 1) {

                        rowBuffer.append("<th>");

                        // if(column.getSortable()) {
                        //                        
                        // }

                        rowBuffer.append(column.getColumnTitle());

                        // if(column.getSortable()) {
                        //                            
                        // rowBuffer.append("</a>");
                        //                            
                        // }

                        rowBuffer.append("</th>");

                    }

                }

                rowBuffer.append("</tr>");

            }

            return rowBuffer.toString();

        }

        return super.startRow();
    }

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

                    int index = numOfNonMonthField + o + (3 * i);

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
