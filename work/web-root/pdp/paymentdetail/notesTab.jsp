
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

			  	<!-- NOTES TAB -->
					<tr>
				    <td><br><br>
				    	<div align="center">
		          <c:if test="${!(empty PaymentDetail.notes)}">
					      <table width="75%" border="0" cellpadding="4" cellspacing="0"  class="bord-r-t">
					        <tr>
					          <th width="40" height="32" class="thfont">
								      Note<br>Line
								    </th>
					          <th height="32" class="thfont">
					            Note Text
					          </th>
	  				    	</tr>
					        <tbody>
					        	<c:forEach items="${PaymentDetail.notes}" var="item" >
						          <tr>
						            <td class="datacell">
						              <c:out value="${item.customerNoteLineNbr}"/>&nbsp;
												</td>
											  <td class="datacell">
						              <c:out value="${item.customerNoteText}"/>&nbsp;
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</c:if>
							<c:if test="${empty PaymentDetail.notes}">
								<strong>There are no notes for this payment.</strong><br><br>
			        </c:if>
							</div><br>
						</td>
					</tr>
						