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
<%@page import="java.util.List,
								edu.iu.uis.pdp.bo.PaymentDetail,
								edu.iu.uis.pdp.bo.PaymentGroup" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
<head>
<html:base />
<title>Payment Details</title>
<script language="JavaScript">
function exit() {
  this.window.close()
  return true
}
</script>

</head>
  
<body>
  	<h1><strong>Payment Details for Payment Detail ID: <c:out value="${PaymentDetail.id}"/></strong></h1> <br>
<jsp:include page="${request.contextPath}/TestEnvironmentWarning.jsp" flush="true"/>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
 		<tr>
   		<td width="20">
  			&nbsp;
		   </td>
		   <td>
		    <br>
		  	<font color="#800000">
			    <html:errors/>&nbsp;
				</font>&nbsp;
				<br>
			</td>
		</tr>
	</table>
<table width="100%" border=0 cellspacing=0 cellpadding=0>
  <tr>
    <td width="20">&nbsp;</td>
    <td>
      <table width="100%" height=40 border=0 cellpadding=0 cellspacing=0>
        <tr>
          <td colspan="2">&nbsp;</td>
        </tr>
      </table>
    </td>
    <td width=20>&nbsp;</td>
  </tr>
  <tr>
    <td width=20>&nbsp;</td>
    <td>		
    	<table width="100%" border=0 cellspacing=0 cellpadding=0>
			  <tr>
			    <td>
			      <table width="100%" border=0 cellpadding=4 cellspacing=0 class="tabtable">
			        <tr>
						<c:if test="${btnPressed == 'btnSummaryTab'}" >
							<td class="tab-selected" >
						</c:if>
						<c:if test="${btnPressed != 'btnSummaryTab'}" >
							<td class="tab-notselected" >
						</c:if>
			            <div align="center">
										<c:if test="${btnPressed != 'btnSummaryTab'}" >
			             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnSummaryTab=param" class="HdrScrLnk">
			            	</c:if>
										Summary
										<c:if test="${btnPressed != 'btnSummaryTab'}" >
			             		</a>
			            	</c:if>
			            </div>
			          </td>
								<c:if test="${btnPressed == 'btnBatchTab'}" >
									<td class="tab-selected" >
								</c:if>
								<c:if test="${btnPressed != 'btnBatchTab'}" >
									<td class="tab-notselected" >
								</c:if>
			            <div align="center">
										<c:if test="${btnPressed != 'btnBatchTab'}" >
			             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnBatchTab=param" class="HdrScrLnk">
			            	</c:if>
										Batch
										<c:if test="${btnPressed != 'btnBatchTab'}" >
			             		</a>
			            	</c:if>
			            </div>
			          </td>
								<c:if test="${btnPressed == 'btnPaymentTab'}" >
									<td class="tab-selected" >
								</c:if>
								<c:if test="${btnPressed != 'btnPaymentTab'}" >
									<td class="tab-notselected" >
								</c:if>
			            <div align="center">
										<c:if test="${btnPressed != 'btnPaymentTab'}" >
			             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnPaymentTab=param" class="HdrScrLnk">
			            	</c:if>
										Payment
										<c:if test="${btnPressed != 'btnPaymentTab'}" >
			             		</a>
			            	</c:if>
			            </div>
			          </td>
								<c:if test="${btnPressed == 'btnPayeeTab'}" >
									<td class="tab-selected" >
								</c:if>
								<c:if test="${btnPressed != 'btnPayeeTab'}" >
									<td class="tab-notselected" >
								</c:if>
			            <div align="center">
										<c:if test="${btnPressed != 'btnPayeeTab'}" >
			             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnPayeeTab=param" class="HdrScrLnk">
			            	</c:if>
										Payee
										<c:if test="${btnPressed != 'btnPayeeTab'}" >
			             		</a>
			            	</c:if>
			            </div>
			          </td>
								<c:if test="${btnPressed == 'btnAccountingTab'}" >
									<td class="tab-selected" >
								</c:if>
								<c:if test="${btnPressed != 'btnAccountingTab'}" >
									<td class="tab-notselected" >
								</c:if>
									<div align=center>
										<c:if test="${btnPressed != 'btnAccountingTab'}" >
			             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnAccountingTab=param" class="HdrScrLnk">
			            	</c:if>
										Accounting
										<c:if test="${btnPressed != 'btnAccountingTab'}" >
			             		</a>
			            	</c:if>
			            </div>
			        	</td>
								<c:if test="${btnPressed == 'btnAchInfoTab'}" >
									<td class="tab-selected" >
								</c:if>
								<c:if test="${btnPressed != 'btnAchInfoTab'}" >
									<td class="tab-notselected" >
								</c:if>
									<div align=center>
										<c:if test="${btnPressed != 'btnAchInfoTab'}" >
			             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnAchInfoTab=param" class="HdrScrLnk">
			            	</c:if>
										ACH Info
										<c:if test="${btnPressed != 'btnAchInfoTab'}" >
			             		</a>
			            	</c:if>
			            </div>
			        	</td>								
			        	<c:if test="${btnPressed == 'btnHistoryTab'}" >
									<td class="tab-selected" >
								</c:if>
								<c:if test="${btnPressed != 'btnHistoryTab'}" >
									<td class="tab-notselected" >
								</c:if>
			            <div align=center>
										<c:if test="${btnPressed != 'btnHistoryTab'}" >
			             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnHistoryTab=param" class="HdrScrLnk">
			            	</c:if>
										History
										<c:if test="${btnPressed != 'btnHistoryTab'}" >
			             		</a>
			            	</c:if>
			            </div>
			          </td>
								<c:if test="${btnPressed == 'btnNotesTab'}" >
									<td class="tab-selected" >
								</c:if>
								<c:if test="${btnPressed != 'btnNotesTab'}" >
									<td class="tab-notselected" >
								</c:if>
			            <div align=center>
										<c:if test="${btnPressed != 'btnNotesTab'}" >
			             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnNotesTab=param" class="HdrScrLnk">
			            	</c:if>
										Notes
										<c:if test="${btnPressed != 'btnNotesTab'}" >
			             		</a>
			            	</c:if>
			            </div>
			          </td>
			    		</tr>
			    	</table>
			    </td>
			  </tr>
			</table>
		</td>
    <td width=20>&nbsp;</td>
	</tr>
  <tr>
  	<td width="20">&nbsp</td>
    <td>
      <table width="100%" border=0 cellpadding=0 cellspacing=0>
					<!-- SUMMARY TAB -->
					<jsp:include page="summaryTab.jsp" flush="true"/>
					<c:if test="${btnPressed == 'btnBatchTab'}" >
	  				<!-- BATCH TAB -->
						<jsp:include page="../batchTab.jsp" flush="true"/>
	  			</c:if>
					<c:if test="${btnPressed == 'btnPaymentTab'}" >
	  				<!-- PAYMENT TAB -->
						<jsp:include page="../paymentTab.jsp" flush="true"/>
					</c:if>
					<c:if test="${btnPressed == 'btnPayeeTab'}" >
			  		<!-- PAYEE TAB -->
						<jsp:include page="../payeeTab.jsp" flush="true"/>
					</c:if>
					<c:if test="${btnPressed == 'btnAccountingTab'}" >
			  		<!-- ACCOUNTING TAB -->
						<jsp:include page="../accountingTab.jsp" flush="true"/>
					</c:if>
					<c:if test="${btnPressed == 'btnAchInfoTab'}" >
			  		<!-- ACH INFO TAB -->
						<jsp:include page="../achInfoTab.jsp" flush="true"/>
					</c:if>
					<c:if test="${btnPressed == 'btnHistoryTab'}" >
			  		<!-- HISTORY TAB -->
						<jsp:include page="../historyTab.jsp" flush="true"/>
					</c:if>
					<c:if test="${btnPressed == 'btnNotesTab'}" >
			  		<!-- NOTES TAB -->
						<jsp:include page="../notesTab.jsp" flush="true"/>
					</c:if>
<html:form action="paymentupdate">
					<c:if test="${btnPressed != 'btnUpdate'}" >
					<tr valign=middle align=left>
	          <td>
				      <table cellpadding=3 width="100%" cellspacing=0 border=0  class="bord-r-t">
				        <tbody>
				          <tr valign=middle align=left>
                    <th align=nowrap>
                      <div align="center">
				              	<c:choose>
				              		<c:when test="${PaymentDetail.paymentGroup.paymentStatus.code == 'OPEN'}">
			              				<c:if test="${(SecurityRecord.cancelRole == true) or (SecurityRecord.holdRole == true) or (SecurityRecord.processRole == true)}">
								              <c:if test="${SecurityRecord.holdRole == true}">
															 <input type="image" name="btnHold" src="<%= request.getContextPath().toString() %>/images/button_holdpayment.gif" alt="Hold Payment" align="absmiddle"></a>
										        	</c:if>
								              <c:if test="${SecurityRecord.cancelRole == true}">
														    <input type="image" name="btnCancel" src="<%= request.getContextPath() + "/images/button_cancelpayment.gif" %>" alt="Cancel Payment" align="absmiddle"></a>
										        	</c:if>
								              <c:if test="${SecurityRecord.processRole == true}">
																<c:if test="${PaymentDetail.paymentGroup.processImmediate == true}" >
																	<input type="image" name="btnChangeImmediate" src="<%= request.getContextPath() + "/images/button_remimmediate.gif" %>" alt="Remove Immediate Print" align="absmiddle"></a>
																</c:if>
																<c:if test="${PaymentDetail.paymentGroup.processImmediate == false}" >
																	<input type="image" name="btnChangeImmediate" src="<%= request.getContextPath() + "/images/button_setimmediate.gif" %>" alt="Set Immediate Print" align="absmiddle"></a>
																</c:if>
										          </c:if>
					        		      </c:if>
				              		</c:when>
				              		<c:when test="${PaymentDetail.paymentGroup.paymentStatus.code == 'HELD'}">
				              			<c:if test="${(SecurityRecord.cancelRole == true) or (SecurityRecord.holdRole == true)}">
					              			<c:if test="${SecurityRecord.holdRole == true}">
																<input type="image" name="btnRemoveHold" src="<%= request.getContextPath() + "/images/button_removehold.gif" %>" alt="Remove Hold" align="absmiddle"></a>
							        		    </c:if>
					              			<c:if test="${SecurityRecord.cancelRole == true}">
																<input type="image" name="btnCancel" src="<%= request.getContextPath() + "/images/button_cancelpayment.gif" %>" alt="Cancel Payment" align="absmiddle"></a>
							        		    </c:if>
						        		    </c:if>
						          		</c:when>
		                      <c:when test="${PaymentDetail.paymentGroup.paymentStatus.code == 'PACH'}">
		                        <th nowrap=nowrap>
		                          <div align="center">
		                            <c:choose>
		                              <c:when test="${SecurityRecord.sysAdminRole == true}">
		                                <c:choose>
		                                  <c:when test="${PaymentDetail.disbursementActionAllowed}">
		                                    <input type="image" name="btnDisbursementCancel" src="<%= request.getContextPath() + "/images/button_canceldisb.gif" %>" alt="Cancel Disbursement" align="absmiddle">
		                                    <input type="image" name="btnReIssueCancel" src="<%= request.getContextPath() + "/images/button_cancreissuedis.gif" %>" alt="Cancel & Reissue Disbursement" align="absmiddle">
		                                  </c:when>
		                                  <c:otherwise>
		                                    <strong>This disbursement may not be cancelled or cancelled and reissued because the disbursement date is before <fmt:formatDate value="${PaymentDetail.lastDisbursementActionDate}" pattern="MM/dd/yyyy"/>.</strong>
		                                  </c:otherwise>
		                                </c:choose>
		                              </c:when>
		                              <c:when test="${SecurityRecord.cancelRole == true}">
		                                <strong>You do not have permissions to cancel or cancel and reissue this disbursement.  Please contact the PDP System Administrators to perform the required action.</strong>
		                              </c:when>
		                            </c:choose>
		                          </div>
		                        </th>
		                      </c:when>
		                      <c:when test="${PaymentDetail.paymentGroup.paymentStatus.code == 'EXTR'}">
		                        <c:if  test="${SecurityRecord.cancelRole == true}">
		                          <th nowrap=nowrap>
		                            <div align="center">
		                              <c:choose>
		                                <c:when test="${not empty PaymentDetail.paymentGroup.disbursementDate}">
		                                  <c:choose>
		                                    <c:when test="${PaymentDetail.disbursementActionAllowed}">
		                                      <input type="image" name="btnDisbursementCancel" src="<%= request.getContextPath() + "/images/button_canceldisb.gif" %>" alt="Cancel Disbursement" align="absmiddle">
		                                      <input type="image" name="btnReIssueCancel" src="<%= request.getContextPath() + "/images/button_cancreissuedis.gif" %>" alt="Cancel & Reissue Disbursement" align="absmiddle">
		                                    </c:when>
		                                    <c:otherwise>
		                                      <strong>This disbursement may not be cancelled or cancelled and reissued because the disbursement date is before <fmt:formatDate value="${PaymentDetail.lastDisbursementActionDate}" pattern="MM/dd/yyyy"/>.</strong>
		                                    </c:otherwise>
		                                  </c:choose>
		                                </c:when>
		                                <c:otherwise>
		                                  <strong>This disbursement may not be cancelled or cancelled and reissued because it has not been fully extracted yet.</strong>
		                                </c:otherwise>
		                              </c:choose>
		                            </div>
		                          </th>
		                        </c:if>
		                      </c:when>
				              		<c:when test="${(PaymentDetail.paymentGroup.paymentStatus.code == 'HTXE') or (PaymentDetail.paymentGroup.paymentStatus.code == 'HTXN') or (PaymentDetail.paymentGroup.paymentStatus.code == 'HTXB')}">
				              			<c:if test="${(SecurityRecord.taxHoldersRole == true) or (SecurityRecord.sysAdminRole == true)}">
															<input type="image" name="btnRemoveHold" src="<%= request.getContextPath() + "/images/button_removehold.gif" %>" alt="Remove Hold" align="absmiddle"></a>
															<input type="image" name="btnCancel" src="<%= request.getContextPath() + "/images/button_cancelpayment.gif" %>" alt="Cancel Payment" align="absmiddle"></a>
						        		    </c:if>
				              		</c:when>
												</c:choose>
                        <input type="image" name="btnExit" onclick="return exit()" src="<%= request.getContextPath() %>/images/button_exit.gif" alt="Exit" align="absmiddle">
                      </div>
                    </th>
				          </tr>
				        </tbody>
				      </table>
	          </td>
	        </tr>
					</c:if>
				</tbody>
			</table>
		</td>
    <td width=20>&nbsp;</td>
	</tr>
</table>
<table width="100%">
<tr>
<td>&nbsp;</td>
<td align="right">
	<c:if test="${((SecurityRecord.viewIdRole != true) or (SecurityRecord.viewBankRole != true)) and (SecurityRecord.viewAllRole != true)}">
		<font color="#800000">* You do not have access to fields with Red Asterisk</font>
	</c:if>
</td>
<td>&nbsp;</td>
</tr>
</table>

    	
</html:form>
<c:import url="/backdoor.jsp"/>     
  </body>
</html:html>
