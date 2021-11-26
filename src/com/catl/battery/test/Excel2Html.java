package com.catl.battery.test;
import java.io.BufferedOutputStream;  
import java.io.BufferedWriter;  
import java.io.File;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.OutputStreamWriter;  
import java.net.URISyntaxException;  
import java.util.List;  
  
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.transform.OutputKeys;  
import javax.xml.transform.Transformer;  
import javax.xml.transform.TransformerException;  
import javax.xml.transform.TransformerFactory;  
import javax.xml.transform.dom.DOMSource;  
import javax.xml.transform.stream.StreamResult;  
  
import org.apache.commons.io.output.ByteArrayOutputStream;  
import org.apache.log4j.Logger;  
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;  
import org.apache.poi.hssf.usermodel.HSSFPictureData;  
import org.apache.poi.hssf.usermodel.HSSFWorkbook;  
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;  
import org.apache.poi.ss.usermodel.Workbook;  
import org.apache.poi.ss.usermodel.WorkbookFactory;  
import org.w3c.dom.Document;  
  
/** 
 * java将excel转换为HTML 
 * @author wu.85@163.com 
 */  
public class Excel2Html {  
    private static final Logger logger = Logger.getLogger(Excel2Html.class.getName());  
    private static final String DEFAULT_PICTURE_FOLDER = "pictures";  
    private static final String DEFAULT_HTML_TYPE = ".html";// 默认转换的HTML文件后缀  
  
    public static void main(String[] args) {  
        File outputFolder = null;  
        File outputPictureFolder = null;  
            // 转换后HTML文件存放位置  
            outputFolder = new File("E:\\");  
            System.out.println(outputFolder);
            if (null != outputFolder) {  
                // 转换后原excel中图片存放位置  
                String outputPictureFolderPath = outputFolder.getAbsolutePath()  
                        + File.separator + DEFAULT_PICTURE_FOLDER;  
                outputPictureFolder = new File(outputPictureFolderPath);  
                outputPictureFolder.mkdir();  
            }  
         
        try {  
            // 被转换的excel文件  
            File convertedWordFile = new File(  
                    "E:\\机械件库转html模版.xls");  
            convert2Html(convertedWordFile, outputFolder, outputPictureFolder);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public static void writeFile(String content, String path) {  
        FileOutputStream fos = null;  
        BufferedWriter bw = null;  
        try {  
            File file = new File(path);  
            fos = new FileOutputStream(file);  
            bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));  
            bw.write(content);  
        } catch (FileNotFoundException fnfe) {  
            fnfe.printStackTrace();  
        } catch (IOException ioe) {  
            ioe.printStackTrace();  
        } finally {  
            try {  
                if (bw != null)  
                    bw.close();  
                if (fos != null)  
                    fos.close();  
            } catch (IOException ie) {  
            }  
        }  
    }  
  
    public static Workbook getWorkbook(File file) {  
        Workbook workbook = null;  
        try {  
            if (null != file && file.exists()) {  
                workbook = WorkbookFactory.create(file);  
            }  
        } catch (IOException e) {  
            logger.error("IOException in getWorkbook:", e);  
        } catch (InvalidFormatException e) {  
            logger.error("InvalidFormatException in getWorkbook:", e);  
        }  
        return workbook;  
    }  
  
    /** 
     * @param excelFile 被转换的word文件 
     * @param outputFolder 转换后HTML文件存放位置 
     * @param outputPictureFolder 转换后原word中图片存放位置 
     * @throws TransformerException 
     * @throws IOException 
     * @throws ParserConfigurationException 
     */  
    public static void convert2Html(File excelFile, File outputFolder,  
            final File outputPictureFolder) throws TransformerException,  
            IOException, ParserConfigurationException {  
        // 创建excel ExcelToHtmlConverter对象  
        ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter(  
                DocumentBuilderFactory.newInstance().newDocumentBuilder()  
                        .newDocument());  
        excelToHtmlConverter.setOutputColumnHeaders(false);  
        excelToHtmlConverter.setOutputRowNumbers(false);  
          
        // 创建POI工作薄对象  
        HSSFWorkbook workbook = (HSSFWorkbook) getWorkbook(excelFile);  
        excelToHtmlConverter.processWorkbook(workbook);  
  
        Document htmlDocument = excelToHtmlConverter.getDocument();  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        DOMSource domSource = new DOMSource(htmlDocument);  
        StreamResult streamResult = new StreamResult(out);  
  
        TransformerFactory tf = TransformerFactory.newInstance();  
        Transformer serializer = tf.newTransformer();  
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");  
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");  
        serializer.setOutputProperty(OutputKeys.METHOD, "html");  
        serializer.transform(domSource, streamResult);  
  
        writePicures(workbook.getAllPictures(), outputPictureFolder.getAbsolutePath()+ File.separator );  
        writeFile(new String(out.toByteArray()), outputFolder.getAbsolutePath()  
                + File.separator + excelFile.getName() + DEFAULT_HTML_TYPE);  
        out.close();  
    }  
  
    public static void writePicures(List<HSSFPictureData> pics,String picturesFolder)  
            throws IOException {  
        if (pics != null) {  
            int count = 0;  
            for (int i = 0; i < pics.size(); i++) {  
                HSSFPictureData picData = pics.get(i);  
                if (null == picData) {  
                    continue;  
                }  
                byte[] bytes = picData.getData();  
                FileOutputStream output = new FileOutputStream(picturesFolder + count  
                        + "." + picData.suggestFileExtension());  
                BufferedOutputStream writer = new BufferedOutputStream(output);  
                writer.write(bytes);  
                writer.flush();  
                writer.close();  
                output.close();  
                count++;  
            }  
        }  
    }  
}  