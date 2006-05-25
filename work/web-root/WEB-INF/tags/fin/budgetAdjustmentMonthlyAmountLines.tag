<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="fn" uri="/tlds/fn.tld" %>
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>

<%@ attribute name="isSource" required="true"
              description="Boolean whether this group is of source or target lines." %>
<%@ attribute name="dataCellCssClass" required="true"
              description="The name of the CSS class for this data cell." %>
<%@ attribute name="accountingLine" required="true"
              description="The name in the form of the accounting line
              being edited or displayed by this row." %>

<c:set var="capitalSourceOrTarget" value="${isSource ? 'Source' : 'Target'}"/>
<c:set var="baAttributes" value="${DataDictionary.BudgetAdjustmentSourceAccountingLine.attributes}" />
<tr>
    <th>&nbsp;</th>
    <td class="total-line" colspan="10">
        <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" class="datatable">
            <tr>
                <kul:htmlAttributeHeaderCell attributeEntry="${baAttributes.financialDocumentMonth1LineAmount}"/>
                <kul:htmlAttributeHeaderCell attributeEntry="${baAttributes.financialDocumentMonth2LineAmount}"/>
                <kul:htmlAttributeHeaderCell attributeEntry="${baAttributes.financialDocumentMonth3LineAmount}"/>
                <kul:htmlAttributeHeaderCell attributeEntry="${baAttributes.financialDocumentMonth4LineAmount}"/>
                <kul:htmlAttributeHeaderCell attributeEntry="${baAttributes.financialDocumentMonth5LineAmount}"/>
                <kul:htmlAttributeHeaderCell attributeEntry="${baAttributes.financialDocumentMonth6LineAmount}"/>
                <kul:htmlAttributeHeaderCell attributeEntry="${baAttributes.financialDocumentMonth7LineAmount}"/>
                <kul:htmlAttributeHeaderCell attributeEntry="${baAttributes.financialDocumentMonth8LineAmount}"/>
                <kul:htmlAttributeHeaderCell attributeEntry="${baAttributes.financialDocumentMonth9LineAmount}"/>
                <kul:htmlAttributeHeaderCell attributeEntry="${baAttributes.financialDocumentMonth10LineAmount}"/>
                <kul:htmlAttributeHeaderCell attributeEntry="${baAttributes.financialDocumentMonth11LineAmount}"/>
                <kul:htmlAttributeHeaderCell attributeEntry="${baAttributes.financialDocumentMonth12LineAmount}"/>
            </tr>
            <tr>
    	        <td class="${dataCellCssClass}"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth1LineAmount}" property="${accountingLine}.financialDocumentMonth1LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}"/></td>
    	        <td class="${dataCellCssClass}"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth2LineAmount}" property="${accountingLine}.financialDocumentMonth2LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}"/></td>
    	        <td class="${dataCellCssClass}"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth3LineAmount}" property="${accountingLine}.financialDocumentMonth3LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}"/></td>
    	        <td class="${dataCellCssClass}"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth4LineAmount}" property="${accountingLine}.financialDocumentMonth4LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}"/></td>
    	        <td class="${dataCellCssClass}"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth5LineAmount}" property="${accountingLine}.financialDocumentMonth5LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}"/></td>
    	        <td class="${dataCellCssClass}"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth6LineAmount}" property="${accountingLine}.financialDocumentMonth6LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}"/></td>
    	        <td class="${dataCellCssClass}"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth7LineAmount}" property="${accountingLine}.financialDocumentMonth7LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}"/></td>
    	        <td class="${dataCellCssClass}"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth8LineAmount}" property="${accountingLine}.financialDocumentMonth8LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}"/></td>
    	        <td class="${dataCellCssClass}"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth9LineAmount}" property="${accountingLine}.financialDocumentMonth9LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}"/></td>
    	        <td class="${dataCellCssClass}"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth10LineAmount}" property="${accountingLine}.financialDocumentMonth10LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}"/></td>
    	        <td class="${dataCellCssClass}"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth11LineAmount}" property="${accountingLine}.financialDocumentMonth11LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}"/></td>
    	        <td class="${dataCellCssClass}"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth12LineAmount}" property="${accountingLine}.financialDocumentMonth12LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}"/></td>
            </tr>
        </table>
    </td>
    <th>&nbsp;</th>
</tr>
