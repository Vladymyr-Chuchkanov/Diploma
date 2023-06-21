package com.chuchkanov.filereader;

import android.os.Build;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ReaderEpub extends ReaderFormat {
    private List<String> images = new ArrayList<>();
    private List<Section> sections = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private String authorLine = "";
    private String language = "eng";
    private int img_id = 0;
    private Section s= new Section(0,"","");
    public File dir;

    public ReaderEpub(File dir0){
        dir = dir0;
    }
    @Override
    public List<String> getTitles(){
        for(int i =0;i<sections.size();i++){
            titles.add(sections.get(i).getTitle());
        }
        return titles;
    }
    @Override
    public  List<Section> getSections(){
        return sections;
    }
    @Override
    public String getAuthorLine(){
        return authorLine;
    }
    @Override
    public String getLanguage(){
        return language;
    }
    @Override
    public void parseFile(File file) {
        String epubFilePath = file.getPath();
        String outputDirectory = file.getParentFile().getPath()+"/"+file.getName().replaceAll("[.]","")+"_unziped";
        openEpubFile(epubFilePath, outputDirectory);
        File tempDir = new File(outputDirectory);
        try {
            if (tempDir.exists()) {
                File[] files = tempDir.listFiles();
                String path2 = "";
                Map<String, String> idsHrefs = new HashMap<>();
                List<String> spine = new ArrayList<>();
                if (files != null) {
                    for (File file0 : files) {
                        String str0 = file0.getName();
                        if (str0.equals("OPS")) {
                            for(File l: file0.listFiles()) {
                                if(l.getName().equals("content.opf")) {
                                    produceSpine(l, idsHrefs, spine);
                                }
                                else if(l.getName().equals("images")){
                                    for(File img: l.listFiles()){
                                        byte[] imageBytes = readImageBytes(img.getPath());
                                        String base64String = encodeToBase64(imageBytes);
                                        images.add(base64String);

                                    }
                                }
                            }
                        }

                    }
                }
                sections.add(new Section( s.SECTION_TYPE_TEXT, "",""));
                for(String keyName : spine){
                    String name = idsHrefs.get(keyName);
                    File tempFile = new File(outputDirectory+"/OPS/"+name);
                    parseEpub(tempFile);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int h = 0;

    }
    private void produceSpine(File l, Map<String,String> idsHrefs, List<String> spine){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(l);
            NodeList nl = doc.getElementsByTagName("manifest");
            NodeList temp1 = nl.item(0).getChildNodes();
            for (int i = 0; i < temp1.getLength(); i++) {
                Node temp = temp1.item(i);
                if (temp.getNodeType() == Node.ELEMENT_NODE) {
                    String val = "";
                    String key = "";
                    Element el = (Element) temp;
                    for(int j = 0; j< el.getAttributes().getLength();j++){
                        String tmp0 = el.getAttributes().item(j).getNodeName();
                        if(tmp0.equals("href")){
                            val = el.getAttributes().item(j).getNodeValue();
                        }
                        else if(el.getAttributes().item(j).getNodeName().equals("id")){
                            key = el.getAttributes().item(j).getNodeValue();
                        }
                    }
                    idsHrefs.put(key,val);
                }
            }
            NodeList nl1 = doc.getElementsByTagName("spine");
            NodeList temp2 = nl1.item(0).getChildNodes();
            for (int i = 0; i < temp2.getLength(); i++) {
                Node temp = temp2.item(i);
                if (temp.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) temp;
                    for(int j = 0; j< el.getAttributes().getLength();j++){
                        if(el.getAttributes().item(j).getNodeName().equals("idref")){
                            spine.add(el.getAttributes().item(j).getNodeValue());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void parseEpub(File file){
        try {


            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            NodeList body = doc.getElementsByTagName("body");
            getChildElements(body,0);






        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getChildElements(NodeList list,int tit){
        int tit2 = 0;
        for(int i = 0; i< list.getLength();i++){
            Node sectionNode = list.item(i);
            if (sectionNode.getNodeType() == Node.ELEMENT_NODE) {
                Element sectionElement = (Element) sectionNode;
                NodeList childNodes = sectionElement.getChildNodes();
                String temp1 = sectionElement.getTagName();
                String temp2 = sectionElement.getTextContent();


                if(temp1.equals("div")&&sectionElement.getAttributes().item(0).getNodeValue().contains("title")){
                    sections.add(new Section( s.SECTION_TYPE_TEXT, "", temp2));
                    continue;
                }
                if(temp1.equals("image")){
                    sections.add(new Section( s.SECTION_TYPE_IMAGE, images.get(img_id), "image"));
                    img_id+=1;
                    sections.add(new Section( s.SECTION_TYPE_TEXT, "", temp2));
                }

                if(temp1.equals("p")){
                    sections.get(sections.size()-1).appendSection("\n"+temp2);
                }

                getChildElements(childNodes, tit2);
                tit2 = 0;
            }


        }

    }
    private void openEpubFile(String epubFilePath, String outputDirectory){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.createDirectories(Paths.get(outputDirectory));
            }

            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(epubFilePath))) {
                ZipEntry entry;
                byte[] buffer = new byte[1024];


                while ((entry = zipInputStream.getNextEntry()) != null) {

                    String outputPath = outputDirectory + File.separator + entry.getName();
                    new File(outputPath).getParentFile().mkdirs();

                    try (OutputStream outputStream = new FileOutputStream(outputPath)) {
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static byte[] readImageBytes(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] imageBytes = new byte[(int) file.length()];
            fileInputStream.read(imageBytes);
            fileInputStream.close();
            return imageBytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static String encodeToBase64(byte[] imageBytes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(imageBytes);
        }
        return "";
    }
}
