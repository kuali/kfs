<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="editingMode" required="false" description="used to decide if items may be edited" type="java.util.Map"%>
<%@ attribute name="isSource" required="true" %>
<%@ attribute name="hasUnits" required="true" %>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<c:if test="${isSource}" >
  <c:set var="lineAttributes" value="${DataDictionary.EndowmentSourceTransactionLine.attributes}" />
  <c:set var="newTransactionLine" value="newSourceTransactionLine" />
  <c:set var="methodToCallAdd" value="methodToCall.insertSourceTransactionLine" />
  <c:set var="methodToCallDelete" value="methodToCall.deleteSourceTransactionLine" />
  <c:set var="methodToCallRefreshTaxLotLines" value="methodToCall.refreshSourceTaxLots" />
  <c:set var="methodToCallBalanceInquiry" value="methodToCall.performBalanceInquiryForSourceTransactionLine" />
  <c:set var="transLines" value="document.sourceTransactionLines"/> 
  <c:set var="totalIncomeAmount" value="${KualiForm.document.sourceIncomeTotal}"/> 
  <c:set var="totalPrincipalAmount" value="${KualiForm.document.sourcePrincipalTotal}"/>
  <c:set var="totalIncomeUnits" value="${KualiForm.document.sourceIncomeTotalUnits}"/>
  <c:set var="totalPrincipalUnits" value="${KualiForm.document.sourcePrincipalTotalUnits}"/>
</c:if>
<c:if test="${not isSource}">
  <c:set var="lineAttributes" value="${DataDictionary.EndowmentTargetTransactionLine.attributes}" />
  <c:set var="newTransactionLine" value="newTargetTransactionLine" />
  <c:set var="methodToCallAdd" value="methodToCall.insertTargetTransactionLine" />
  <c:set var="methodToCallDelete" value="methodToCall.deleteTargetTransactionLine" />
  <c:set var="methodToCallRefreshTaxLotLines" value="methodToCall.refreshTargetTaxLots" />
  <c:set var="methodToCallBalanceInquiry" value="methodToCall.performBalanceInquiryForTargetTransactionLine" />
  <c:set var="transLines" value="document.targetTransactionLines"/>
  <c:set var="totalIncomeAmount" value="${KualiForm.document.targetIncomeTotal}"/> 
  <c:set var="totalPrincipalAmount" value="${KualiForm.document.targetPrincipalTotal}"/>
  <c:set var="totalIncomeUnits" value="${KualiForm.document.targetIncomeTotalUnits}"/>
  <c:set var="totalPrincipalUnits" value="${KualiForm.document.targetPrincipalTotalUnits}"/>
</c:if>

<table cellpadding="0" cellspacing="0" class="datatable" summary="Transaction Lines section">
	    <tr>
	            <td colspan="1" class="tab-subhead" style="border-right: none;" align="left">
	            <c:if test="${isSource}">FROM</c:if>
	            <c:if test="${not isSource}">TO</c:if>
	            </td>    
	            <td colspan="5" class="tab-subhead" style="border-right: none;border-left: none;" >
	            &nbsp;
	            </td>
                <td colspan="2" class="tab-subhead" align="right" nowrap="nowrap" style="border-left: none;">
					<SCRIPT type="text/javascript">
                		<!--
                  		function hideImport() {
                      		document.getElementById("showLink").style.display="inline";
                      		document.getElementById("uploadDiv").style.display="none";
                  		}
                  		function showImport() {
                      		document.getElementById("showLink").style.display="none";
                      		document.getElementById("uploadDiv").style.display="inline";
                  		}
                  		document.write(
                    		'<a id="showLink" href="#" onclick="showImport();return false;">' +
                      		'<img src="${ConfigProperties.externalizable.images.url}tinybutton-importlines.gif" title="import transaction lines from file" alt="import transaction lines from file"' +
                      		'     width=72 height=15 border=0 align="right" class="det-button">' +
                    		'<\/a>' +
                    		'<div id="uploadDiv" style="display:none;" >' +
                      		'<html:file size="30" property="transactionLineImportFile" />' +
                      		'<html:image property="methodToCall.importTransactionLines" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
                                    styleClass="tinybutton" alt="add imported transaction lines" title="add imported transaction lines" />' +
                      		'<html:image property="methodToCall.cancel" src="${ConfigProperties.externalizable.images.url}tinybutton-cancelimport.gif"
                                    styleClass="tinybutton" alt="cancel import" title="cancel import" onclick="hideImport();return false;" />' +
                    		'<\/div>');
                		//-->
            		</SCRIPT>
					<NOSCRIPT>
						Import lines
						<html:file size="30" property="transactionLineImportFile" style="font:10px;height:16px;" />
						<html:image property="methodToCall.importTransactionLines" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add imported items" title="add imported items" />
					</NOSCRIPT>
				</td>
	    </tr>
		<tr>
            <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.kemid}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.etranCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.transactionLineDescription}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.transactionIPIndicatorCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.transactionAmount}"/>
            <c:if test="${hasUnits}">
           		<kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.transactionUnits}"/>
            </c:if>	
            <c:if test="${not readOnly}">
                <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
            </c:if>
		</tr>
      
        <c:if test="${not readOnly}">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="add:" scope="row"/>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.kemid}" property="${newTransactionLine}.kemid" />
                    <kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.KEMID"
				                fieldConversions="kemid:${newTransactionLine}.kemid" />
				    <br/>
					<div id="${newTransactionLine}.div" class="fineprint"">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.kemid}" property="${newTransactionLine}.kemidObj.shortTitle" readOnly="true" />
            		</div>					                
				</td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.etranCode}" property="${newTransactionLine}.etranCode" />
                    <kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode"
				                fieldConversions="code:${newTransactionLine}.etranCode" />
				    <br/>
					<div id="${newTransactionLine}.div" class="fineprint">
            			<kul:htmlControlAttribute attributeEntry="${lineAttributes.etranCode}" property="${newTransactionLine}.etranCodeObj.name" readOnly="true" />
            		</div>						                
				                
                </td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionLineDescription}" property="${newTransactionLine}.transactionLineDescription" /></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionIPIndicatorCode}" property="${newTransactionLine}.transactionIPIndicatorCode" /></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionAmount}" property="${newTransactionLine}.transactionAmount" styleClass="right"/></td>
                <c:if test="${hasUnits}">
                	<td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionUnits}" property="${newTransactionLine}.transactionUnits" styleClass="right"/></td>
                </c:if>
                <td class="infoline">
                  	<c:if test="${isSource && KualiForm.showFromTransactionLine}">
	                	<div align="center">
	                		<html:image property="${methodToCallAdd}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add Transaction Line" title="add" styleClass="tinybutton"/>
	                	</div>
                	</c:if>
                	<c:if test="${not isSource}">
	                	<div align="center">
	                		<html:image property="${methodToCallAdd}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add Transaction Line" title="add" styleClass="tinybutton"/>
	                	</div>
                	</c:if>
                </td>
            </tr>
        </c:if>
      
        <logic:iterate id="item" name="KualiForm" property="${transLines}" indexId="ctr">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="${ctr+1}:" scope="row" ></kul:htmlAttributeHeaderCell>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.kemid}" property="${transLines}[${ctr}].kemid" readOnly="${readOnly}"/>
                	<c:if test="${not readOnly}">
                		<kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.KEMID"
				                    fieldConversions="kemid:${transLines}[${ctr}].kemid" />
                    </c:if>				                
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${lineAttributes.etranCode}" property="${transLines}[${ctr}].etranCode" readOnly="${readOnly}"/>
                	<c:if test="${not readOnly}">
                		<kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode"
				                    fieldConversions="code:${transLines}[${ctr}].etranCode" />
				    </c:if>
                </td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionLineDescription}" property="${transLines}[${ctr}].transactionLineDescription" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionIPIndicatorCode}" property="${transLines}[${ctr}].transactionIPIndicatorCode" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionAmount}" property="${transLines}[${ctr}].transactionAmount" readOnly="${readOnly}" styleClass="right"/></td>
                
                <c:if test="${hasUnits}">
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.transactionUnits}" property="${transLines}[${ctr}].transactionUnits" readOnly="${readOnly}" styleClass="right"/></td>              
                </c:if>
                <td class="datacell">
                <div align="center">
                <c:if test="${not readOnly}" >
                <c:if test="${hasUnits}">
               		<html:image property="${methodToCallRefreshTaxLotLines}.line${ctr}" src="${ConfigProperties.externalizable.images.url}tinybutton-refresh.gif" title="Refresh" alt="Refresh" styleClass="tinybutton" />
                </c:if>
                	<html:image property="${methodToCallBalanceInquiry}.line${ctr}" src="${ConfigProperties.externalizable.images.url}tinybutton-balinquiry.gif" title="Balance Inquiry for Line ${ctr+1}" alt="Balance Inquiry for Line ${ctr+1}" styleClass="tinybutton"/>
                	<html:image property="${methodToCallDelete}.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="Delete Transaction Line ${ctr+1}" alt="Delete Transaction Line  ${ctr+1}" styleClass="tinybutton"/>
                </c:if>
                </div>
                </td>
            </tr>
        </logic:iterate>
        
        <tr>
			<td class="total-line" colspan="5">
				&nbsp;
			</td>
			<td class="total-line">
				<strong>Total Income Amount:
					${totalIncomeAmount}</strong>
			</td>
			<td class="total-line">
			    <strong>Total Principal Amount:
					${totalPrincipalAmount}</strong>
			</td>
			<c:if test="${!readOnly}">
				<td class="total-line">
					&nbsp;
				</td>
			</c:if>
		</tr>
        <tr>
			<td class="total-line" colspan="5">
				&nbsp;
			</td>
			<td class="total-line">
				<strong>Total Income Units:
					${totalIncomeUnits}</strong>
			</td>
			<td class="total-line">
			    <strong>Total Principal Units:
					${totalPrincipalUnits}</strong>
			</td>
			<c:if test="${!readOnly}">
				<td class="total-line">
					&nbsp;
				</td>
			</c:if>
		</tr>