
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

			  	<!-- ACCOUNTING TAB -->
					<tr>
				    <td><br><br>
				    	<div align="center">
				      <table width="80%" border="0" cellpadding="4" cellspacing="0" class="bord-r-t">
				        <tr>
				          <th width="8%" height="32" class="thfont">
							      COA
							    </th>
				          <th width="12%" height="32"  class="thfont">
				            Account Number
				          </th>
				          <th width="8%" height="32" class="thfont">
				      			Sub-Account Number
				      		</th>
				          <th width="8%" height="32" class="thfont">
				      			Object Code
									</th>
				          <th width="8%" height="32" class="thfont">
				      			Sub Object Code
				      		</th>
				          <th width="15%" height="32" class="thfont">
				      			Organization Document Number
				      		</th>
				          <th width="15%" height="32" class="thfont">
				      			Organization Reference ID
				      		</th>
				          <th width="14%" height="32" class="thfont">
				      			Project Code
				      		</th>
				      		<th width="12%" height="32" class="thfont">
				      			Net Amount
				      		</th>
				        </tr>
				        </table>
				        	<c:forEach items="${PaymentDetail.accountDetail}" var="item" >
				        	<table width="80%" border="0" cellpadding="4" cellspacing="0" class="bord-r-t">
					          <tr>
					            <td width="8%" class="datacell">
					              <c:out value="${item.finChartCode}"/>&nbsp;
											</td>
										  <td width="12%" class="datacell">
					              <c:out value="${item.accountNbr}"/>&nbsp;
											</td>
					            <td width="8%" class="datacell">
					              <c:out value="${item.subAccountNbr}"/>&nbsp;
											</td>
					            <td width="8%" class="datacell">
					              <c:out value="${item.finObjectCode}"/>&nbsp;
											</td>
					            <td width="8%" class="datacell">
					              <c:out value="${item.finSubObjectCode}"/>&nbsp;
											</td>
											<td width="15%" class="datacell">
												<c:out value="${PaymentDetail.organizationDocNbr}"/>&nbsp;
											</td>
					            <td width="15%" class="datacell">
					              <c:out value="${item.orgReferenceId}"/>&nbsp;
											</td>
					            <td width="14%" class="datacell">
					              <c:out value="${item.projectCode}"/>&nbsp;
											</td>
					            <td width="12%" class="datacell">
					              <fmt:formatNumber value="${item.accountNetAmount}" type="currency" />&nbsp;
											</td>
										</tr>
									</table>
									<c:if test="${!(empty item.accountHistory)}" >
										<table width="80%" border="0" cellpadding="4" cellspacing="0">
										 <tr>
											<td colspan="9">
												<div align="right">
									      <table width="80%" border="0" cellpadding="0" cellspacing="0" class="bord-r-t">
									        <tr>
									          <th height="20" class="thfont">
												      Accounting Change Code
												    </th>
									          <th height="20"  class="thfont">
									            Attribute Name
									          </th>
									          <th height="20" class="thfont">
									      			Original Value
									      		</th>
									          <th height="20" class="thfont">
									      			New Value
														</th>
									          <th height="20" class="thfont">
									      			Change Date
									      		</th>
									        </tr>
									        <tbody>
									        	<c:forEach items="${item.accountHistory}" var="subItem" >
										          <tr>
										            <td class="datacell">
										              <c:out value="${subItem.accountingChange.description}"/>&nbsp;
																</td>
															  <td class="datacell">
										              <c:out value="${subItem.acctAttributeName}"/>&nbsp;
																</td>
										            <td class="datacell">
										              <c:out value="${subItem.acctAttributeOrigValue}"/>&nbsp;
																</td>
										            <td class="datacell">
										              <c:out value="${subItem.acctAttributeNewValue}"/>&nbsp;
																</td>
										            <td class="datacell">
										            	<fmt:formatDate value="${subItem.acctChangeDate}" pattern="MM/dd/yyyy" />&nbsp;
																</td>
															</tr>
														</c:forEach>
													</tbody>
												</table>
												</div>
											</td>
										 </tr>
										</table>
									</c:if>
									</c:forEach>
							</div><br>
						</td>
					</tr>
