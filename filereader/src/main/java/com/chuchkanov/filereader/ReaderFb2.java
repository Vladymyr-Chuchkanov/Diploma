package com.chuchkanov.filereader;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


public class ReaderFb2 extends ReaderFormat {
    private List<String> images = new ArrayList<>();
    private List<Section> sections = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private String authorLine = "";
    private String language = "eng";
    private int img_id = 0;
    private Section s= new Section(0,"","");
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
    public void parseFile( File file){

        try {


            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();
            NodeList langs = doc.getElementsByTagName("lang");
            if(langs.getLength()>0) {
                language = langs.item(0).getTextContent();
            }
            NodeList imageNodes = doc.getElementsByTagName("binary");
            for (int i = 0; i < imageNodes.getLength(); i++) {
                Node imageNode = imageNodes.item(i);
                if (imageNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element imageElement = (Element) imageNode;
                    String imageContent = imageElement.getTextContent();
                    images.add(imageContent);
                }
            }
            sections.add(new Section( s.SECTION_TYPE_TEXT, "",""));
            NodeList title = doc.getElementsByTagName("description");
            getChildElements(title,0);
            NodeList body = doc.getElementsByTagName("body");
            getChildElements(body,0);

            NodeList authorInfo = doc.getElementsByTagName("author");
            if(authorInfo.getLength()>0) {
                Node temp = authorInfo.item(0);
                if (temp.getNodeType() == Node.ELEMENT_NODE){
                    Element el = (Element) temp;
                    authorLine+=el.getTextContent().replace("\n"," ");
                }
            }
            NodeList bookName = doc.getElementsByTagName("book-title");
            if(bookName.getLength()>0) {
                Node temp = bookName.item(0);
                if (temp.getNodeType() == Node.ELEMENT_NODE){
                    Element el = (Element) temp;
                    authorLine+=" - "+el.getTextContent().replace("\n"," ");
                }
            }





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

                if(temp1.equals("title")){
                    sections.add(new Section( s.SECTION_TYPE_TEXT, "", temp2));

                    tit2=1;
                    //System.out.println("Title next");
                }

                if(childNodes.getLength()<=1){
                    if(temp1.equals("image")){
                        sections.add(new Section( s.SECTION_TYPE_IMAGE, images.get(img_id), "image"));
                        img_id+=1;
                        sections.add(new Section( s.SECTION_TYPE_TEXT, "", temp2));

                    }
                    if(tit!=1) {
                        sections.get(sections.size() - 1).appendSection(temp2 + "\n");
                    }
                }

                getChildElements(childNodes, tit2);
                tit2 = 0;
            }


        }

    }
}
