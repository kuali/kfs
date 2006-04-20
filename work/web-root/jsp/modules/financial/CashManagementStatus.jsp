<%@ include file="/jsp/core/tldHeader.jsp" %>

<kul:page docTitle="Cash Management status" showDocumentInfo="false" headerTitle="Cash Management status" transactionalDocument="false" htmlFormAction="cashManagementStatus" >

<br>
<table width="100%" border=0 cellspacing=0 cellpadding=0>
    <html:messages id="msg" message="true">
        <tr>
            <td>
                <bean:write filter="false" name="msg" />
            </td>
        </tr>
    </html:messages>
</table>
</kul:page>
