package com.catl.doc;

import org.apache.log4j.Logger;

import com.catl.common.constant.DocState;
import com.catl.common.util.CatlConstant;
import com.catl.common.util.DocUtil;
import com.catl.common.util.TypeUtil;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

public class EDatasheetDocUtil {
	
	private static Logger log=Logger.getLogger(EDatasheetDocUtil.class.getName());
	
	public static boolean isEDatasheetDoc(WTDocument doc) throws WTException{
		return TypeUtil.isSpecifiedType(doc, CatlConstant.DATASHEET_DOC_TYPE);
	}
	
	public static WTDocument getEDatasheetDocByName(String name, String exceptiveNumber) throws WTException{
		if(name == null)
			throw new WTException("Datasheet 名称不能唯恐");
		
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int index0 = qs.appendClassList(WTDocument.class, true);
		
		if(exceptiveNumber != null){
			qs.appendWhere(new SearchCondition(WTDocument.class,WTDocument.NUMBER,SearchCondition.NOT_EQUAL,exceptiveNumber), new int[]{index0});
			qs.appendAnd();
		}
		
		ClassAttribute nameAttr = new ClassAttribute(WTDocument.class, WTDocument.NAME);
        SearchCondition scStringDefinitionName = new SearchCondition(SQLFunction.newSQLFunction(SQLFunction.UPPER,
                nameAttr), SearchCondition.EQUAL, new ConstantExpression((Object) name.toUpperCase()));
        qs.appendWhere(scStringDefinitionName, new int[]{ index0 });

		log.debug("==QuerySQL:"+qs.toString());
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
		Persistable[] p = null;
		while (qr.hasMoreElements()) {
			p = (Persistable[]) qr.nextElement();
			WTDocument doc = (WTDocument)p[0];
			if(EDatasheetDocUtil.isEDatasheetDoc(doc))
				return DocUtil.getLastestWTDocument(doc);
		}
		return null;
	}
}
