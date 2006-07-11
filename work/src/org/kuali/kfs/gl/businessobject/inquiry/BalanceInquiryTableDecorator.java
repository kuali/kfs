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
package org.kuali.module.gl.web;

import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.TableDecorator;
import org.displaytag.properties.MediaTypeEnum;
import org.displaytag.tags.TableTagParameters;
import org.kuali.core.web.uidraw.Column;
import org.kuali.core.web.uidraw.ResultRow;

/**
 * @author Kuali General Ledger Team (kualigltech@oncourse.iu.edu)
 */
public class BalanceInquiryTableDecorator extends TableDecorator {

    private int rowCounter;
    
    /**
     * Constructs a BalanceInquiryTableDecorator.java.
     */
    public BalanceInquiryTableDecorator() {
        super();
    }
    
    @Override
    public String startRow() {
        //TableTagParameters.
        PageContext pageContext = getPageContext();
        MediaTypeEnum mediaType = (MediaTypeEnum) pageContext.getAttribute("mediaType");
        
        ResultRow row = (ResultRow) getCurrentRowObject();
        
        if(MediaTypeEnum.HTML.equals(mediaType)) { // Display the nested table.
            
            StringBuffer rowBuffer = new StringBuffer("<tr>");
            
            if(1 <= rowCounter) {
                
                rowBuffer.append("<tr>");
                
                List columns = row.getColumns();
                
                int columnCount = 0;
                for(Iterator i = columns.iterator(); i.hasNext() && columnCount++ <= 11;) {
                    
                    Column column = (Column) i.next();
                    
                    if(columnCount > 1) {
                        
                        rowBuffer.append("<th>");
                        
//                        if(column.getSortable()) {
//                        
//                        }
                        
                        rowBuffer.append(column.getColumnTitle());
                        
//                        if(column.getSortable()) {
//                            
//                            rowBuffer.append("</a>");
//                            
//                        }
                        
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
        
        if(MediaTypeEnum.HTML.equals(mediaType)) {
            
            // Display the nested table.
            StringBuffer rowBuffer = new StringBuffer("<tr>");
            
            rowBuffer.append("<td colspan=\"12\" class=\"infocell\"><br><center>");
            rowBuffer.append("<table class=\"datatable-80\" cellspacing=\"0\" cellpadding=\"0\">");
            
            for(int o = 0; o < 3; o++) {
                
                rowBuffer.append("<tr>");
                
                for(int i = 0; i < 4; i++) {
                    
                    int index = 12 + o + (3 * i);
                    
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
            
            return rowBuffer.append("</table></center><br /></td></tr>").toString();
            
        }
        
        return super.finishRow();
    }

}
