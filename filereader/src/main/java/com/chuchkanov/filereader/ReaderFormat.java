package com.chuchkanov.filereader;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public abstract class ReaderFormat {
    private List<String> images = new ArrayList<>();
    private List<Section> sections = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private String language = "eng";
    private String authorLine = "";
    private int img_id = 0;

    public abstract List<String> getTitles();
    public  abstract List<Section> getSections();
    public abstract  String getLanguage();
    public abstract String getAuthorLine();

    public abstract void parseFile(File file);



}
