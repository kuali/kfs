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
package org.kuali.module.labor.web;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.displaytag.decorator.TableDecorator;
import org.displaytag.properties.MediaTypeEnum;
import org.kuali.core.web.ui.Column;
import org.kuali.core.web.ui.ResultRow;

public class LongRowTableDecorator extends TableDecorator {

    private final int numOfColumnInEachRow = 12;
    private final int numOfColumnInFirstRow = 14;

    /**
     * @see org.displaytag.decorator.TableDecorator#finishRow()
     */
    @Override
    public String finishRow() {
        MediaTypeEnum mediaType = (MediaTypeEnum) getPageContext().getAttribute("mediaType");
        ResultRow row = (ResultRow) getCurrentRowObject();

        if (MediaTypeEnum.HTML.equals(mediaType)) {
            StringBuffer rowBuffer = new StringBuffer();
            
            List columns = row.getColumns();
            int columnCount = columns.size();
            int numOfRows = new Double(Math.ceil(1.0 * (columnCount - numOfColumnInFirstRow) / numOfColumnInEachRow)).intValue() ;           
            
            rowBuffer.append("<tr>").append("<td colspan='" + numOfColumnInFirstRow +"'><br/><center>");
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
