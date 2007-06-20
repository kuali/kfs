<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>

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
						