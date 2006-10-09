<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" 
              description="The DataDictionary entry containing attributes for this row's fields."%>


<div class="h2-container">
    <h2>Requisition Detail</h2>
</div>

<table cellpadding="0" cellspacing="0" class="datatable" summary="Requisition Detail Section">
    <tr>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.contractManagerCode}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <kul:htmlControlAttribute 
                property="document.contractManagerCode" 
                attributeEntry="${documentAttributes.contractManagerCode}" 
                readOnly="true" />
        </td>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.fundingSourceCode}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <kul:htmlControlAttribute
                property="document.fundingSourceCode"
                attributeEntry="${documentAttributes.fundingSourceCode}"
                readOnly="true"/>
        </td>
    </tr>
</table> 

