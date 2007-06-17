
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
  <head>
    <html:base />
    <title>Payment Details</title>

  </head>
  
  <body>
  	<h1><strong>
	    	<c:if test="${listType == 'group'}">
		    	Payment Group Information for Payment Detail ID: <c:out value="${PaymentDetail.id}"/>
				</c:if>
	    	<c:if test="${listType == 'disbursement'}">
		    	Disbursement Information for Payment Detail ID: <c:out value="${PaymentDetail.id}"/>
				</c:if>
  		
  	</strong></h1> <br>
<jsp:include page="${request.contextPath}/TestEnvironmentWarning.jsp" flush="true"/>
<table width="100%" border=0 cellspacing=0 cellpadding=0>
  <tr>
    <td width="20">&nbsp;</td>
    <td>
      <table width="100%" height=40 border=0 cellpadding=0 cellspacing=0>
        <tr>
          <td colspan="2">
          	Return to:&nbsp;&nbsp;<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnSummaryTab=param">Payment Detail Screen</a>
          </td>
          <td>
            <div align=right>
				    <jsp:include page="VendorAction.jsp" flush="true"/>
            </div>
          </td>
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
								<td class="tab-notselected" >
			            <div align="center">
		             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnSummaryTab=param" class="HdrScrLnk">Summary</a>
			            </div>
			          </td>
								<td class="tab-notselected" >
			            <div align="center">
		             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnBatchTab=param" class="HdrScrLnk">Batch</a>
			            </div>
			          </td>
								<td class="tab-notselected" >
			            <div align="center">
		             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnPaymentTab=param" class="HdrScrLnk">Payment</a>
			            </div>
			          </td>
								<td class="tab-notselected" >
			            <div align="center">
		             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnPayeeTab=param" class="HdrScrLnk">Payee</a>
			            </div>
			          </td>
								<td class="tab-notselected" >
									<div align=center>
		             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnAccountingTab=param" class="HdrScrLnk">Accounting</a>
			            </div>
			        	</td>
								<td class="tab-notselected" >
									<div align=center>
		             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnAchInfoTab=param" class="HdrScrLnk">ACH Info</a>
			            </div>
			        	</td>								
								<td class="tab-notselected" >
			            <div align=center>
		             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnHistoryTab=param" class="HdrScrLnk">History</a>
			            </div>
			          </td>
								<td class="tab-notselected" >
			            <div align=center>
		             		<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?btnNotesTab=param" class="HdrScrLnk">Notes</a>
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
					<!-- test is button pressed was btnSummaryTab -->
					<tr>
				    <td>
				      <table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
				        <tbody>
				     <!-- SUMMARY TAB -->
				          <tr>
				            <th align=right nowrap>
				            	Customer:
				            </th>
				            <td class="datacell">
				              <c:out value="${PaymentDetail.paymentGroup.batch.customerProfile.chartCode}"/>-<c:out value="${PaymentDetail.paymentGroup.batch.customerProfile.orgCode}"/>-<c:out value="${PaymentDetail.paymentGroup.batch.customerProfile.subUnitCode}"/>
											&nbsp;
										</td>
				            <th width="25%" align=right nowrap>
				            	Payee Name:
				            </th>
				            <td width="25%" class="datacell">
				            	<c:out value="${PaymentDetail.paymentGroup.payeeName}"/>
											&nbsp;
										</td>
				          </tr>
									<tr>
				            <th width="25%" align=right valign=top nowrap>
				            	Disbursement Type:
				            </th>
				            <td width="25%" align=left class="datacell">						
				              <c:out value="${PaymentDetail.paymentGroup.disbursementType.description}"/>
				            	&nbsp;
										</td>	
				            <th align=right valign=top nowrap>
				            	Payee ID Type:
				            </th>
				            <!-- set in session -->
				            <td align=left class="datacell">
				            	<c:out value="${payeeIdTypeDesc}" />
				            	&nbsp;
										</td>
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
											Disbursement Number: 
				            </th>
				            <td align=left class="datacell">						
											<c:out value="${PaymentDetail.paymentGroup.disbursementNbr}"/>
											&nbsp;
										</td>	
									<!-- Not to be seen by all users-->
				            <th align=right nowrap>
				            	<c:if test="${(SecurityRecord.viewIdRole != true)}">
						          	<font color="#800000">*&nbsp;</font>
						          </c:if>
						          Payee ID:
						        </th>
				            <td class="datacell">
						          <c:if test="${(SecurityRecord.viewIdRole == true)}">
					              <c:out value="${PaymentDetail.paymentGroup.payeeId}"/>&nbsp;
							        </c:if>
						          <c:if test="${(SecurityRecord.viewIdRole != true)}">
						          	<font color="#800000">
						          	<c:if test="${not empty PaymentDetail.paymentGroup.payeeId}">
					              	*************
					              </c:if>&nbsp;
					              </font>
							        </c:if>
										</td>	
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Disbursement Date:
				            </th>
				            <td colspan="3" align=left class="datacell">						
				              <fmt:formatDate value="${PaymentDetail.paymentGroup.disbursementDate}" pattern="MM/dd/yyyy"/>
				            	&nbsp;
										</td>	
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Payment Status:
				            </th>
				            <td colspan="3" align=left class="datacell">						
											<c:out value="${PaymentDetail.paymentGroup.paymentStatus.description}"/>
				            	&nbsp;
										</td>	
				          </tr>
									<tr>
										<th colspan="4" class="datacell">
											&nbsp; <!-- Spacer Row -->
										</th>
									</tr>
								</tbody>
							</table>
						</td>
					</tr>
				</table>
    </td>
  	<td width="20">&nbsp</td>
	</tr>
</table>
<table width="100%">
<tr>
<td>&nbsp;</td>
<td align="right">
							<c:if test="${(SecurityRecord.viewIdRole != true)}">
			 					<font color="#800000">* You do not have access to fields with Red Asterisk</font>
							</c:if>
</td>
<td>&nbsp;</td>
</tr>
</table>
<br>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="10">&nbsp;</td>
    <td><strong>This Payment:</strong></td>
    <td width="10">&nbsp;</td>
  </tr>
  <tr>
    <td width="10">&nbsp;</td>
    <td>&nbsp;</td>
    <td width="10">&nbsp;</td>
  </tr>  
  <tr>
    <td width="10">&nbsp;</td>
    <td>
      <div align="center">
      <table width="80%" border="0" cellpadding="4" cellspacing="0" class="bord-r-t">
        <tr>
          <th height="32" class="thfont">
			      Payment Detail ID
			    </th>
          <th height="32" class="thfont">
			      Source Payment Document Number
			    </th>
          <th height="32"  class="thfont">
            PO Number
          </th>
          <th height="32" class="thfont">
      			Invoice Number
      		</th>
          <th height="32" class="thfont">
      			Requisition Number
      		</th>
	        <th height="32" class="thfont">
	        	Customer Number for IU
	        </th>
          <th height="32" class="thfont">
      			Pay Date
      		</th>
          <th height="32" class="thfont">
      			Net Payment Amount
      		</th>
        </tr>
        <tbody>
						<tr>
							<td nowrap=nowrap class="datacell">
								<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?sourceDocNbr=<c:out value="${PaymentDetail.custPaymentDocNbr}"/>&docTypeCode=<c:out value="${PaymentDetail.financialDocumentTypeCode}"/>"><c:out value="${PaymentDetail.id}"/></a>&nbsp;
							</td>
							<td nowrap=nowrap class="datacell">
								<c:out value="${PaymentDetail.custPaymentDocNbr}" />&nbsp;
							</td>
							<td nowrap=nowrap class="datacell">	
								<c:out value="${PaymentDetail.purchaseOrderNbr}" />&nbsp;
							</td>
							<td nowrap=nowrap class="datacell">
								<c:out value="${PaymentDetail.invoiceNbr}" />&nbsp;
							</td>
							<td nowrap=nowrap class="datacell">
								<c:out value="${PaymentDetail.requisitionNbr}" />&nbsp;
							</td>
			        <td nowrap=nowrap class="datacell">
			        	<c:out value="${PaymentDetail.paymentGroup.customerIuNbr}"/>
								&nbsp;
							</td>
							<td nowrap=nowrap class="datacell">
								<fmt:formatDate value="${PaymentDetail.paymentGroup.paymentDate}" pattern="MM/dd/yyyy" />&nbsp;
							</td>
							<td nowrap=nowrap class="datacell" align="right" >
								<fmt:formatNumber value="${PaymentDetail.netPaymentAmount}" type="currency" />&nbsp;
							</td>
						</tr>
		  	</tbody>
	  	</table>
	  	</div>
  	</td>
  </tr>
  <tr>
	  <td width="10">&nbsp;</td>
	  <td>&nbsp;</td>
	  <td width="10">&nbsp;</td>
  </tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="10">&nbsp;</td>
      <td>
      	<strong>
	    	<c:if test="${(size > 1) and (param.listType == 'group')}">
		    	Other Payments Associated with this Payment Group:
				</c:if>
	    	<c:if test="${(size <= 1) and (param.listType == 'group')}">
		    	There are no other payments associated with this Payment Group.
				</c:if>
	    	<c:if test="${(disbNbrTotalPayments > 1) and (param.listType == 'disbursement')}">
		    	Other Payments Associated with this Disbursement:
				</c:if>
	    	<c:if test="${(disbNbrTotalPayments <= 1) and (param.listType == 'disbursement')}">
		    	There are no other payments associated with this Disbursement.
				</c:if>
				</strong>
	    </td>
    <td width="10">&nbsp;</td>
  </tr>
  <tr>
    <td width="10">&nbsp;</td>
    <td>&nbsp;</td>
    <td width="10">&nbsp;</td>
  </tr>  
	<c:if test="${(size > 1) and (param.listType == 'group')}">
	  <tr>
	    <td width="10">&nbsp;</td>
	    <td>
	      <div align="center">
	      <table width="80%" border="0" cellpadding="4" cellspacing="0" class="bord-r-t">
	        <tr>
	          <th height="32" class="thfont">
				      Payment Detail ID
				    </th>
	          <th height="32" class="thfont">
				      Source Payment Document Number
				    </th>
	          <th height="32"  class="thfont">
	            PO Number
	          </th>
	          <th height="32" class="thfont">
	      			Invoice Number
	      		</th>
	          <th height="32" class="thfont">
	      			Requisition Number
	      		</th>
		        <th height="32" class="thfont">
		        	Customer Number for IU:
		        </th>
	          <th height="32" class="thfont">
	      			Pay Date
	      		</th>
	          <th height="32" class="thfont">
	      			Net Payment Amount
	      		</th>
	        </tr>
	        <tbody>
						<c:forEach items="${PaymentDetail.paymentGroup.paymentDetails}" var="item">
						  <c:if test="${item.id != PaymentDetail.id}">
							<tr>
								<td nowrap=nowrap class="datacell">
									<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?sourceDocNbr=<c:out value="${item.custPaymentDocNbr}"/>&docTypeCode=<c:out value="${item.financialDocumentTypeCode}"/>"><c:out value="${item.id}"/></a>&nbsp;
								</td>
								<td nowrap=nowrap class="datacell">
									<c:out value="${item.custPaymentDocNbr}" />&nbsp;
								</td>
								<td nowrap=nowrap class="datacell">	
									<c:out value="${item.purchaseOrderNbr}" />&nbsp;
								</td>
								<td nowrap=nowrap class="datacell">
									<c:out value="${item.invoiceNbr}" />&nbsp;
								</td>
								<td nowrap=nowrap class="datacell">
									<c:out value="${item.requisitionNbr}" />&nbsp;
								</td>
				        <td nowrap=nowrap class="datacell">
				        	<c:out value="${PaymentDetail.paymentGroup.customerIuNbr}"/>&nbsp;
								</td>
								<td nowrap=nowrap class="datacell">
									<fmt:formatDate value="${item.paymentGroup.paymentDate}" pattern="MM/dd/yyyy" />&nbsp;
								</td>
								<td nowrap=nowrap class="datacell" align="right" >
									<fmt:formatNumber value="${item.netPaymentAmount}" type="currency" />&nbsp;
								</td>
							</tr>
							</c:if>
						</c:forEach>
			  	</tbody>
		  	</table>
		  	</div>
	  	</td>
	  </tr>
	</c:if>
	<c:if test="${(disbNbrTotalPayments > 1) and (param.listType == 'disbursement')}">
	  <tr>
	    <td width="10">&nbsp;</td>
	    <td>
	      <div align="center">
	      <table width="80%" border="0" cellpadding="4" cellspacing="0" class="bord-r-t">
	        <tr>
	          <th height="32" class="thfont">
				      Payment Detail ID
				    </th>
	          <th height="32" class="thfont">
				      Source Payment Document Number
				    </th>
	          <th height="32"  class="thfont">
	            PO Number
	          </th>
	          <th height="32" class="thfont">
	      			Invoice Number
	      		</th>
	          <th height="32" class="thfont">
	      			Requisition Number
	      		</th>
		        <th height="32" class="thfont">
		        	Customer Number for IU:
		        </th>
	          <th height="32" class="thfont">
	      			Pay Date
	      		</th>
	          <th height="32" class="thfont">
	      			Net Payment Amount
	      		</th>
	        </tr>
	        <tbody>
						<c:forEach items="${disbursementDetailsList}" var="item">
						  <c:if test="${item.id != PaymentDetail.id}">
							<tr>
								<td nowrap=nowrap class="datacell">
									<a href="<%= request.getContextPath().toString() %>/epicpaymentdetail.do?sourceDocNbr=<c:out value="${item.custPaymentDocNbr}"/>&docTypeCode=<c:out value="${item.financialDocumentTypeCode}"/>"><c:out value="${item.id}"/></a>&nbsp;
								</td>
								<td nowrap=nowrap class="datacell">
									<c:out value="${item.custPaymentDocNbr}" />&nbsp;
								</td>
								<td nowrap=nowrap class="datacell">	
									<c:out value="${item.purchaseOrderNbr}" />&nbsp;
								</td>
								<td nowrap=nowrap class="datacell">
									<c:out value="${item.invoiceNbr}" />&nbsp;
								</td>
								<td nowrap=nowrap class="datacell">
									<c:out value="${item.requisitionNbr}" />&nbsp;
								</td>
				        <td nowrap=nowrap class="datacell">
				        	<c:out value="${PaymentDetail.paymentGroup.customerIuNbr}"/>&nbsp;
								</td>
								<td nowrap=nowrap class="datacell">
									<fmt:formatDate value="${item.paymentGroup.paymentDate}" pattern="MM/dd/yyyy" />&nbsp;
								</td>
								<td nowrap=nowrap class="datacell" align="right" >
									<fmt:formatNumber value="${item.netPaymentAmount}" type="currency" />&nbsp;
								</td>
							</tr>
							</c:if>
						</c:forEach>
			  	</tbody>
		  	</table>
		  	</div>
	  	</td>
	  </tr>
	</c:if>
  <tr>
	  <td width="10">&nbsp;</td>
	  <td>&nbsp;</td>
	  <td width="10">&nbsp;</td>
  </tr>
</table>    	
<c:import url="/backdoor.jsp"/>     
  </body>
</html:html>
