package com.catl.ri.riA.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import wt.query.QueryException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import net.sf.json.JSONArray;

public interface MechanicalAsmService {

 /**
  * 获取机械件组合下面子件的属性信息	
  * @param name
  * @return
  * @throws WTException
  * @throws WTPropertyVetoException
  * @throws FileNotFoundException
  * @throws IOException
  */
 JSONArray getAsmInfo(String name) throws WTException, WTPropertyVetoException, FileNotFoundException, IOException;

  /**
   * 获取机械件组合的模糊搜索信息
   * @return
   * @throws QueryException
   * @throws WTPropertyVetoException
   * @throws WTException
   */
 JSONArray getAsmPNJson() throws QueryException, WTPropertyVetoException, WTException;

}
