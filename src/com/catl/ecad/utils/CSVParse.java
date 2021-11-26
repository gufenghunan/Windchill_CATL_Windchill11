/*
 * Copyright (c) 2013-2015 SoftEasy. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of SoftEasy and is
 * subject to the terms of a software license agreement. You shall not disclose
 * such confidential information and shall use it only in accordance with the
 * terms of the license agreement. 
 */
package com.catl.ecad.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * @version 1.0 2008-4-28
 * @author Frank Chen
 */
public class CSVParse {

    /**
     * 
     * Split String to ArrayList
     * 
     * @param lineStr
     *            String
     * @return ArrayList
     */
    public static ArrayList parseCSV(String lineStr) {
        ArrayList<String> colList = new ArrayList<String>();
        StringBuffer sb = new StringBuffer(lineStr);
        int colFlg = 0;
        boolean comaFlg = false;
        StringBuffer colBuf = new StringBuffer();
        while (sb.length() > 0) {
            char c = sb.charAt(0);
            if (comaFlg == false) {
                if (c == ',') {
                    colList.add(colBuf.toString());
                    colBuf = new StringBuffer();
                } else if (c == '\"') {
                    colFlg = 1;
                    char tmpc = '\0';
                    if (sb.length() > 1) {
                        tmpc = sb.charAt(1);
                    }
                    if (tmpc == '\"') {
                        colFlg = 2;
                        sb.deleteCharAt(1);
                    }
                    comaFlg = true;
                } else {
                    colFlg = 2;
                    colBuf.append(c);
                    comaFlg = true;
                }
            } else {
                if (colFlg == 2 && c == ',') {
                    colList.add(colBuf.toString());
                    colBuf = new StringBuffer();
                    comaFlg = false;
                } else if (colFlg == 1 && c == '\"') {
                    char tmpc = '\0';
                    if (sb.length() > 1) {
                        tmpc = sb.charAt(1);
                    }
                    if (tmpc == '\"') {
                        colBuf.append(tmpc);
                        sb.deleteCharAt(1);
                    } else {
                        colFlg = 2;
                    }
                } else {
                    colBuf.append(c);
                }
            }
            sb.deleteCharAt(0);
        }
        colList.add(colBuf.toString());
        return colList;
    }

    /**
     * Read from CSV file,and split String to ArrayList
     * 
     * @param filePath
     *            String
     * @return ArrayList
     */
    public static ArrayList parseCSVFile(String filePath) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filePath));
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return null;
        }
        ArrayList<String> lineList = new ArrayList<String>();
        try {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine.startsWith("#")) {
                    continue;
                }
                lineList.add(strLine);
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        ArrayList<ArrayList> lineColList = new ArrayList<ArrayList>();
        Iterator ite = lineList.iterator();
        while (ite.hasNext()) {
            String lineStr = (String) ite.next();
            lineColList.add(parseCSV(lineStr));
        }
        return lineColList;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        String s1 = "aaa,\"bb,xxx\",cccc";
        String path = "E:\\Design.csv";
        ArrayList arraylist = parseCSVFile(path);

        for (int i = 0; i < arraylist.size(); i++) {
            System.out.println("================================\n"+arraylist.get(i)+"\n================================");
            ArrayList arraylistline = (ArrayList) arraylist.get(i);
            for (int j = 0; j < arraylistline.size(); j++) {
                System.out.println(arraylistline.get(j));
            }
        }
        System.out.println("");
        s1 = "aaa,\"bb,bc\",ccc";
        arraylist = parseCSV(s1);
        for (int i = 0; i < arraylist.size(); i++) {
            System.out.println(arraylist.get(i));
        }
        
        
    }

}
