package com.catl.cadence;

import com.catl.cadence.service.CadenceService;
import com.catl.cadence.service.CadenceServiceFwd;

public class CadenceHelper {
	public static final CadenceService service = new CadenceServiceFwd();
}
