package com.catl.bom.load;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import wt.util.WTException;

public class BOMLoadHelper
{
	public static void doLoadBOM(String oid, File file) throws InvalidFormatException, FileNotFoundException, IOException, WTException
	{
		BOMLoad load = new BOMLoad(file);
		ArrayList<String> checkResult = load.precheckLoadFile(oid);
		if (checkResult.isEmpty())
		{
			load.loadBOM(oid);
		} else
		{
			String errors = "";
			for (String error : checkResult)
			{
				errors = errors + error + "\n";
			}
			throw new WTException(errors);
		}
	}

	public static void main(String[] args)
	{
		ArrayList<String> checkResult = new ArrayList<String>();
		checkResult.add("a");
		checkResult.add("v");
		checkResult.add("e");
		String errors = "";
		for (String error : checkResult)
		{
			errors = errors + error + "\n";
		}
		try
		{
			throw new WTException(errors);
		} catch (WTException e)
		{
			// e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

}
