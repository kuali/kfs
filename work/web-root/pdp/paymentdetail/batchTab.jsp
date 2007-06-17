
<%@ page language="java"%>

<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-template" prefix="template" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

	  			<!-- BATCH TAB -->

					<tr>
	          <td>
				      <table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
				        <tbody>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Batch ID:
				            </th>
				            <td align=left class="datacell">						
				              <c:out value="${PaymentDetail.paymentGroup.batch.id}"/>
				            	&nbsp;
										</td>	
				            <th align=right nowrap>
				            	Physical Campus Process Location:
				            </th>
				            <td width="20%" class="datacell">
				              <c:out value="${PaymentDetail.paymentGroup.physCampusProcessCd}"/>
				              &nbsp;
										</td>
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Payment Detail ID:
				            </th>
				            <td align=left class="datacell">						
				              <c:out value="${PaymentDetail.id}"/>
				            	&nbsp;
										</td>	
				            <th align=right nowrap>
				            	Process ID:
				            </th>
				            <td class="datacell">
				              <c:out value="${PaymentDetail.paymentGroup.process.id}"/>
				              &nbsp;
										</td>
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Source File Name:
				            </th>
				            <td align=left class="datacell">						
				              <c:out value="${PaymentDetail.paymentGroup.batch.paymentFileName}"/>
				              &nbsp;
				            </td>	
				            <th align=right valign=top nowrap>
				            	Formatted by User:
				            </th>
				            <td align=left class="datacell">						
				              <c:out value="${PaymentDetail.paymentGroup.process.processUser.networkId}"/>
				            	&nbsp;
										</td>	
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Source File Creation Time:
				            </th>
				            <td align=left class="datacell">						
				              <fmt:formatDate value="${PaymentDetail.paymentGroup.batch.customerFileCreateTimestamp}" pattern="hh:mm a'  on  'MM/dd/yyyy"/>
				              &nbsp;
				            </td>	
				            <th align=right valign=top nowrap>
				            	Time Payment was Formatted:
				            </th>
				            <td align=left class="datacell">						
				              <fmt:formatDate value="${PaymentDetail.paymentGroup.process.processTimestamp}" pattern="hh:mm a'  on  'MM/dd/yyyy"/>
				            	&nbsp;
										</td>	
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
				            	File Processed Time:
				            </th>
				            <td colspan="3" align=left class="datacell">						
				              <fmt:formatDate value="${PaymentDetail.paymentGroup.batch.fileProcessTimestamp}" pattern="hh:mm a'  on  'MM/dd/yyyy"/>
				              &nbsp;
				            </td>	
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Submitter User ID:
				            </th>
				            <td colspan="3" align=left class="datacell">						
				              <c:out value="${PaymentDetail.paymentGroup.batch.submiterUser.networkId}"/>
				            	&nbsp;
										</td>	
				      		</tr>
								</tbody>
							</table>
						</td>
					</tr>
