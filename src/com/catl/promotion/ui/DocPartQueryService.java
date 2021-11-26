package com.catl.promotion.ui;

import wt.fc.QueryResult;
import wt.util.WTException;

public interface DocPartQueryService {
	public QueryResult getDocPartByNumberName(String docpart, String number, String name, String context) throws WTException;
}
