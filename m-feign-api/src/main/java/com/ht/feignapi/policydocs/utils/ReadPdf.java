package com.ht.feignapi.policydocs.utils;

import org.apache.axis.encoding.Base64;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * <p>
 * </p>
 *
 * @author hy.wang
 * @since 20/12/29
 */
public class ReadPdf {


    private static final Logger log = LoggerFactory.getLogger(ReadPdf.class);


    public static void main(String[] args) {


        String path = "/Users/zhouqy/documents/hlta_reimbursement/demo.pdf";
        File pdfFile = new File(path);
        readPdfContent(pdfFile);
//        getPdfContent(pdfFile);

    }


    public static String getPdfContent(File pdfFile){
        String content = null;
        try{
            PDDocument document = PDDocument.load(pdfFile);
            document.getClass();
            if(!document.isEncrypted()) {
                // 方式二：
                document = PDDocument.load(pdfFile);
                // 获取页码
                int pages = document.getNumberOfPages();
                // 读文本内容
                PDFTextStripper stripper = new PDFTextStripper();
                // 设置按顺序输出
                stripper.setSortByPosition(true);
                stripper.setStartPage(1);
                stripper.setEndPage(pages);
                content = stripper.getText(document);
                System.out.println(content);
                document.close();
            }

        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;

    }


    public static String readPdfContent(File file) {

        String fileName = file.getName();
        InputStream is = null;
        PDDocument document = null;
        StringBuffer pdfContentSB = new StringBuffer();
        try {
            if (fileName.endsWith(".pdf")) {
                document = PDDocument.load(file);
                int pageSize = document.getNumberOfPages();
                // 一页一页读取
                int count=0;
                for (int i = 0; i < pageSize; i++) {
                    // 文本内容
                    PDFTextStripper stripper = new PDFTextStripper();
                    // 设置按顺序输出
                    stripper.setSortByPosition(true);
                    stripper.setStartPage(i + 1);
                    stripper.setEndPage(i + 1);
                    String text = stripper.getText(document);
                    log.info("readPdfContent text={}",text);
                    pdfContentSB.append("\n");
                    pdfContentSB.append(text);
                    // 图片内容
                    PDPage page = document.getPage(i);
                    PDResources resources = page.getResources();
                    Iterable<COSName> cosNames = resources.getXObjectNames();
                    if (cosNames != null) {
                        Iterator<COSName> cosNamesIter = cosNames.iterator();
                        while (cosNamesIter.hasNext()) {
                            COSName cosName = cosNamesIter.next();
                            if (resources.isImageXObject(cosName)) {
                                PDImageXObject Ipdmage = (PDImageXObject) resources.getXObject(cosName);
                                BufferedImage image = Ipdmage.getImage();

                                BufferedImage bufferedImage=new BufferedImage(217,190,BufferedImage.TYPE_INT_RGB);
                                Graphics graphics=bufferedImage.getGraphics();
                                //将原始位图缩小后绘制到bufferedImage对象中
                                graphics.drawImage(image,0,0,217,190,null);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                                try {
                                    ImageIO.write(image, "jpeg", stream);
                                    String base64 = Base64.encode(stream.toByteArray());
                                    String imagesDocsContent = OcrUtils.getImagesDocs(base64);
                                    pdfContentSB.append(imagesDocsContent);
                                    log.info("readPdfContent OcrUtils.getImagesDocs count={}",count++);

                                } catch (IOException e) {
                                    log.error("readPdfContent error={}",e);

                                } finally {
                                    try {
                                        stream.close();
                                    } catch (IOException e) {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (InvalidPasswordException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (document != null) {
                    document.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }

        return pdfContentSB.toString();
    }


}
