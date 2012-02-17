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

<kul:tab tabTitle="Nonresident Alien Tax" defaultOpen="false" tabErrorKey="${KFSConstants.DV_NRATAX_TAB_ERRORS}">
<c:set var="nraTaxAttributes" value="${DataDictionary.DisbursementVoucherNonResidentAlienTax.attributes}" />

<div class="tab-container" align=center > 
<h3>NRA Tax</h3>

<table cellpadding=0 class="datatable" summary="NRA Tax Section">
  <tr>
    <th width="25%">
      <div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.incomeClassCode}"/></div>
    </th>
    <td width="25%">
      <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.incomeClassCode}" property="document.dvNonResidentAlienTax.incomeClassCode" extraReadOnlyProperty="document.dvNonResidentAlienTax.incomeClassName"readOnly="${!taxEntryMode or not empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}"/>
        <c:if test="${taxEntryMode and empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}">
          <kul:lookup boClassName="org.kuali.kfs.fp.businessobject.TaxIncomeClassCode" fieldConversions="code:document.dvNonResidentAlienTax.incomeClassCode"/>
        </c:if>
    </td>
    <th width="25%" scope="row">
      <div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.foreignSourceIncomeCode}"/></div>
    </th>
    <td width="25%">
      <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.foreignSourceIncomeCode}" property="document.dvNonResidentAlienTax.foreignSourceIncomeCode" readOnly="${!taxEntryMode or not empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}"/>
    </td>
  </tr>
          
  <tr>
    <th scope="row">
      <div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.federalIncomeTaxPercent}"/></div>
    </th>
    <td>
      <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.federalIncomeTaxPercent}" property="document.dvNonResidentAlienTax.federalIncomeTaxPercent" readOnly="${!taxEntryMode or not empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}"/>
        <c:if test="${taxEntryMode and empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}">
          <kul:lookup boClassName="org.kuali.kfs.fp.businessobject.NonResidentAlienTaxPercent" fieldConversions="incomeTaxPercent:document.dvNonResidentAlienTax.federalIncomeTaxPercent" lookupParameters="document.dvNonResidentAlienTax.incomeClassCode:incomeClassCode,'F':incomeTaxTypeCode" />
        </c:if>
    </td>
    <th scope="row">
      <div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.incomeTaxTreatyExemptCode}"/></div>
    </th>
    <td>
      <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.incomeTaxTreatyExemptCode}" property="document.dvNonResidentAlienTax.incomeTaxTreatyExemptCode" readOnly="${!taxEntryMode or not empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}"/>
    </td>
  </tr>
          
  <tr>
    <th scope="row">
      <div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.stateIncomeTaxPercent}"/></div>
    </th>
    <td>
      <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.stateIncomeTaxPercent}" property="document.dvNonResidentAlienTax.stateIncomeTaxPercent" readOnly="${!taxEntryMode or not empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}"/>
       <c:if test="${taxEntryMode and empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}">
          <kul:lookup boClassName="org.kuali.kfs.fp.businessobject.NonResidentAlienTaxPercent" fieldConversions="incomeTaxPercent:document.dvNonResidentAlienTax.stateIncomeTaxPercent" lookupParameters="document.dvNonResidentAlienTax.incomeClassCode:incomeClassCode,'S':incomeTaxTypeCode"/>
       </c:if>
    </td>
    <th scope="row">
      <div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.taxOtherExemptIndicator}"/></div>
    </th>
    <td>
      <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.taxOtherExemptIndicator}" property="document.dvNonResidentAlienTax.taxOtherExemptIndicator" readOnly="${!taxEntryMode or not empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}"/>
    </td>
  </tr>
          
  <tr>
    <th scope="row">
    	<div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.postalCountryCode}"/>
    		<c:if test="${taxEntryMode}"><br> *required unless Income Class Code is Non Reportable</c:if>
    	</div>
    </th>
    <td>
      <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.postalCountryCode}" property="document.dvNonResidentAlienTax.postalCountryCode" readOnly="${!taxEntryMode or not empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}"/>
    </td>
    <th scope="row">
      <div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.incomeTaxGrossUpCode}"/></div>
    </th>
    <td>
      <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.incomeTaxGrossUpCode}" property="document.dvNonResidentAlienTax.incomeTaxGrossUpCode" readOnly="${!taxEntryMode or not empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}"/>
    </td>
  </tr>
  
  <tr>
    <th scope="row">
      <div align="right">
        <kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.taxNQIId}" /></div>
    </th>
    <td>
      <kul:htmlControlAttribute 
        attributeEntry="${nraTaxAttributes.taxNQIId}" property="document.dvNonResidentAlienTax.taxNQIId" 
        readOnly="${!taxEntryMode or not empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}"/>
    </td>
    <th scope="row">
      <div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.taxUSAIDPerDiemIndicator}"/></div>
    </th>
    <td>
      <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.taxUSAIDPerDiemIndicator}" property="document.dvNonResidentAlienTax.taxUSAIDPerDiemIndicator" readOnly="${!taxEntryMode or not empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}"/>
    </td>
  </tr>
  
  <tr>
    <th scope="row">
      <div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.referenceFinancialDocumentNumber}"/></div>
    </th>
    <td>
      <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.referenceFinancialDocumentNumber}" property="document.dvNonResidentAlienTax.referenceFinancialDocumentNumber" readOnly="${!taxEntryMode or not empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}"/>
    </td>
    <th scope="row">
      <div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.taxSpecialW4Amount}"/></div>
    </th>
    <td>
      <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.taxSpecialW4Amount}" property="document.dvNonResidentAlienTax.taxSpecialW4Amount" readOnly="${!taxEntryMode or not empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}"/>
    </td>
  </tr>
          
	<c:if test="${taxEntryMode}">
	<tr>
	  <td class="infoline" colspan="4">
	    <center>
	    <c:if test="${empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}">
        <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-genlines.gif" styleClass="tinybutton" property="methodToCall.generateNonResidentAlienTaxLines" title="Generate Non-resident Alien Tax Lines" alt="Generate Non-resident Alien Tax Lines"/>
        <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-clearall.gif" styleClass="tinybutton" property="methodToCall.clearNonResidentAlienTaxInfo" title="Clear All Info From NRA Tax Entries" alt="Clear All Info From NRA Tax Entries"/>
	    </c:if>
      <c:if test="${!empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}">
        <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-clearlines.gif" styleClass="tinybutton" property="methodToCall.clearNonResidentAlienTaxLines"  title="Clear Non-resident Alien Tax Lines" alt="Clear Non-resident Alien Tax Lines" />
        <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-clearall.gif" styleClass="tinybutton" property="methodToCall.clearNonResidentAlienTaxInfo" title="Clear All Info From NRA Tax Entries" alt="Clear All Info From NRA Tax Entries"/>
      </c:if>
	    </center>
	  </td>
	</tr> 
	</c:if> 
</table>

</div>
</kul:tab>
