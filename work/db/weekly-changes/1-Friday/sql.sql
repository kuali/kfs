-- the float doc header extensions table really needs to be of type NUMBER which has a precision of 38
-- this results in proper conversion to mysql as part of the impex process
ALTER TABLE KREW_DOC_HDR_EXT_FLT_T MODIFY VAL NUMBER
/
