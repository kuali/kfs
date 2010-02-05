update fp_dv_pmt_reas_t set dv_pmt_reas_desc = 'Used to make death benefit payments.' where dv_pmt_reas_cd = 'D'
;

update fp_dv_pmt_reas_t set dv_pmt_reas_desc = 'Revolving funds are a kind of cash account from which funds can be borrowed when cash on hand is needed for disbursements. Revolving funds are established as special types of payees that can be repaid using a DV document.' where dv_pmt_reas_cd = 'K'
;

update fp_dv_pmt_reas_t set dv_pmt_reas_desc = 'Used to make payments required under a contractual agreement.' where dv_pmt_reas_cd = 'L'
;