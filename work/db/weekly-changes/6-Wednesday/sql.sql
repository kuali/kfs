truncate table krns_sesn_doc_t;

drop table krns_sesn_doc_t;

CREATE TABLE krns_sesn_doc_t
    (sesn_doc_id                   VARCHAR2(40) NOT NULL,
    doc_hdr_id                     VARCHAR2(14) NOT NULL,
    PRNCPL_ID 			   VARCHAR2(40) NOT NULL,
    IP_ADDR 			   VARCHAR2(60) NOT NULL,
    serialzd_doc_frm               BLOB,
    last_updt_dt                   DATE,
    content_encrypted_ind          CHAR(1) DEFAULT 'N');
    
CREATE INDEX krns_sesn_doc_ti1 ON krns_sesn_doc_t
  (
    last_updt_dt                    ASC
  );

ALTER TABLE krns_sesn_doc_t
ADD CONSTRAINT krns_sesn_doc_tp1 
PRIMARY KEY (sesn_doc_id, doc_hdr_id, prncpl_id, ip_addr);