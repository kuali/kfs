SELECT  doc_typ_nm, lbl, doc_hdlr_url, post_prcsr, appl_id, doc_search_help_url, DOC_TYP_DESC
    FROM KREW_DOC_TYP_T
    WHERE ACTV_IND = 1 AND CUR_IND = 1
    ORDER BY doc_typ_nm
/
SELECT t.doc_typ_nm, n.nm, 
    SUBSTR( n.typ, -(INSTR( REVERSE( n.typ ), '.' ) - 1) ) AS node_type,
    n.actvn_typ, n.next_doc_stat, nn.nm AS NEXT_NODE_NM
    FROM  KREW_DOC_TYP_T t
        INNER JOIN KREW_RTE_NODE_T n ON n.doc_typ_id = t.doc_typ_id
        LEFT OUTER JOIN KREW_RTE_NODE_LNK_T nl ON nl.FROM_RTE_NODE_ID = n.RTE_NODE_ID
        LEFT OUTER JOIN KREW_RTE_NODE_T nn ON nn.RTE_NODE_ID = nl.TO_RTE_NODE_ID
    WHERE t.ACTV_IND = 1 AND t.CUR_IND = 1
    ORDER BY n.rte_node_id, nn.NM
/

SELECT t.doc_typ_nm, DOC_STAT_NM
	FROM KREW_DOC_TYP_T t
        INNER JOIN KREW_DOC_TYP_APP_DOC_STAT_T ads ON ads.doc_typ_id = t.doc_typ_id
    WHERE t.ACTV_IND = 1 AND t.CUR_IND = 1
    ORDER BY doc_typ_nm, doc_stat_nm
/

SELECT t.doc_typ_nm, ad.nm, ad.lbl, ad.RULE_ATTR_TYP_CD, ad.CLS_NM, ad.APPL_ID
	FROM KREW_DOC_TYP_T t
        INNER JOIN KREW_DOC_TYP_ATTR_T a ON a.doc_typ_id = t.doc_typ_id
        INNER JOIN KREW_RULE_ATTR_T ad ON ad.RULE_ATTR_ID = a.RULE_ATTR_ID
    WHERE t.ACTV_IND = 1 AND t.CUR_IND = 1
    ORDER BY doc_typ_nm, DOC_TYP_ATTRIB_ID
/
