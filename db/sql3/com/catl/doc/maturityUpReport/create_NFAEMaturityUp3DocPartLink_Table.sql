set echo on
REM Creating table NFAEMaturityUp3DocPartLink for com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink
set echo off
CREATE TABLE NFAEMaturityUp3DocPartLink (
   classnamekeyA6   VARCHAR2(600),
   idA3A6   NUMBER,
   classnamekeyroleAObjectRef   VARCHAR2(600),
   idA3A5   NUMBER,
   classnamekeyroleBObjectRef   VARCHAR2(600),
   idA3B5   NUMBER,
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
 CONSTRAINT PK_NFAEMaturityUp3DocPartLink PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE NFAEMaturityUp3DocPartLink IS 'Table NFAEMaturityUp3DocPartLink created for com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink'
/
REM @//com/catl/doc/maturityUpReport/NFAEMaturityUp3DocPartLink_UserAdditions
