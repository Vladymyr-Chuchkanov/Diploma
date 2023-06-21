package com.chuchkanov.filereader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Book {


    private List<String> title = new ArrayList<>();
    private String bookAndAuthorLine = "";
    private String pathToFile = "";
    private int currentPage = 0;
    private int currentSection = 0;
    private ReaderFormat readerFormat;
    private int absolutePageNumber = 0;
    private int absoluteCurrentPage = 0;
    private String language = "";
    public String searchText = "";
    public int searchIndex = -1;
    public List<Section> sections = new ArrayList<>();

    public Book(String path, ReaderFormat readerFormat1){
        try {
            Section s= new Section(0,"","");
            File file = new File(path);
            if(!file.isFile()){
                sections.add(new Section(s.SECTION_TYPE_TEXT, "no such file!", "Error"));

                return;
            }
            pathToFile = path;
            String extension = getFileExtension(path);
            readerFormat = readerFormat1;

            readerFormat.parseFile(file);
            language = readerFormat.getLanguage();
            sections = readerFormat.getSections();
            title = readerFormat.getTitles();
            bookAndAuthorLine = readerFormat.getAuthorLine();



        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public Book(String path, File filesDir){
        try {
            Section s= new Section(0,"","");
            File file = new File(path);
            if(!file.isFile()){
                sections.add(new Section(s.SECTION_TYPE_TEXT, "no such file!", "Error"));

                return;
            }
            pathToFile = path;
            String extension = getFileExtension(path);
            if(extension.equals("fb2")){
                readerFormat = new ReaderFb2();
            }
            else if(extension.equals("epub")){
                readerFormat = new ReaderEpub(filesDir);
            }
            else{
                return;
            }

            readerFormat.parseFile(file);
            language = readerFormat.getLanguage();
            sections = readerFormat.getSections();
            title = readerFormat.getTitles();
            bookAndAuthorLine = readerFormat.getAuthorLine();



        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public InvertedIndex createSearchIndex(){
        InvertedIndex ii = new InvertedIndex();
        for(int i = 0; i < sections.size(); i++){
            if(sections.get(i).getType()==1){
                ii.addText(sections.get(i).getText(), i);
            }
        }
        return ii;
    }
    private static String getFileExtension(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            int dotIndex = filePath.lastIndexOf(".");
            if (dotIndex >= 0 && dotIndex < filePath.length() - 1) {
                return filePath.substring(dotIndex + 1);
            }
        }
        return "";
    }
    public List<String> getTitles(){
        if(title.size()>0){
            return title;
        }
        if(readerFormat!=null){
            return readerFormat.getTitles();
        }
        List<String> titles = new ArrayList<>();
        for(Section s: sections){
            titles.add(s.getTitle());
        }
        return titles;
    }
    public int getSectionType(int page){
        if(page<0||page>sections.size()-1){
            return -1;
        }
        return sections.get(page).getType();
    }
    public int getCurrentPage(){
        return currentPage;
    }
    public String getLanguage(){
        return language;
    }
    public int getCurrentSection(){
        return currentSection;
    }
    public String getTitle(int section){
        if(section<0||section>title.size()-1){
            return "";
        }
        return title.get(section);
    }
    public List<String> getTableOfContents(){
        List<String> ls = new ArrayList<>();
        for(int i = 0; i < sections.size(); i++){
            ls.add(sections.get(i).getTitle());
        }
        return ls;
    }
    public String moveToSection(int section){
        if(section<0||section>sections.size()-1){
            return "";
        }
        currentSection = section;
        absoluteCurrentPage = 1;
        for(int i =0; i < section;i++){
            absoluteCurrentPage+=sections.get(i).getPagesNumber();
        }
        currentPage = 0;
        return sections.get(section).getPage(0);
    }
    public String movePage(int direction){

        if(sections.size()==0){
            return "";
        }
        if(currentSection<0){
            return sections.get(0).getPage(0);
        }
        if(currentSection>sections.size()-1){
            return sections.get(sections.size()-1).getPage(sections.get(sections.size()-1).getPagesNumber()-1);
        }
        if(0<=currentPage&&currentPage<=sections.get(currentSection).getPagesNumber()-1&&direction==0){
            return sections.get(currentSection).getPage(currentPage);
        }
        if(0<=currentPage&&currentPage<=sections.get(currentSection).getPagesNumber()-1&&direction>0){
            currentPage++;
            absoluteCurrentPage++;
            return movePage(direction-1);
        }
        if(0<=currentPage&&currentPage<=sections.get(currentSection).getPagesNumber()-1&&direction<0){
            currentPage--;
            absoluteCurrentPage--;
            return movePage(direction+1);
        }
        if(currentPage<0){
            if(currentSection==0){
                currentPage=0;
                direction=0;
                return movePage(direction);
            }else{
                currentSection--;
                currentPage=sections.get(currentSection).getPagesNumber()-1;
                return movePage(direction);
            }
        }
        if(currentPage>sections.get(currentSection).getPagesNumber()-1){
            if(currentSection==sections.size()-1){
                currentPage=sections.get(currentSection).getPagesNumber()-1;
                currentSection=sections.size()-1;
                direction=0;
                return movePage(direction);
            }else{
                currentSection++;
                currentPage=0;
                return movePage(direction);
            }
        }
        return "";
    }
    public String goToIndex(int section, int index, String searchText1){

        currentSection = section;
        searchText = searchText1;
        currentPage = 0;
        absoluteCurrentPage = 1;
        for(int i =0; i< section;i++){
            absoluteCurrentPage+=sections.get(i).getPagesNumber();
        }
        String text = sections.get(section).getText();
        List<String> pages = sections.get(section).pages;
        for(int i = 0; i < pages.size(); i++){
            index-=pages.get(i).length();
            if(index<0){
                index+=pages.get(i).length();
                currentPage=i;
                searchIndex=index;
                absoluteCurrentPage+=i+1;
                return pages.get(i);
            }
        }

        currentPage = pages.size()-1;
        absoluteCurrentPage += pages.size();
        return pages.get(pages.size()-1);
    }
    public String goToPage(int moveTo){

        if(moveTo<0){
            moveTo=0;
        }
        if(moveTo>absolutePageNumber){
            moveTo=absolutePageNumber;
        }
        absoluteCurrentPage = moveTo;
        for(int i =0;i<sections.size();i++){
            int temp =sections.get(i).getPagesNumber();
            if(temp<moveTo){
                moveTo-=temp;
                continue;
            }
            currentSection = i;
            currentPage = moveTo-1;
            if(currentPage<0){
                currentPage=0;
            }
            return sections.get(i).getPage(currentPage);
        }
        return "";


    }
    public void updatePaging(int charsPerLine, int maxLines, int page, int section){
        absolutePageNumber=0;
        for(int i = 0; i < sections.size();i++){
            int temp = sections.get(i).getPagesNumber();
            sections.get(i).updatePaging(charsPerLine, maxLines);
            if(section==i&&sections.get(i).getPagesNumber()!=temp){
                page = (int) (page/temp*sections.get(i).getPagesNumber());
            }
            absolutePageNumber+=sections.get(i).getPagesNumber();
            if(i<section){
                absoluteCurrentPage =absolutePageNumber;
            }
            if(i==section){
                absoluteCurrentPage+=page+1;
            }
        }
        currentPage = page;
        currentSection = section;
    }
    public int getAbsoluteCurrentPage(){
        return absoluteCurrentPage;
    }
    public int getAbsolutePageNumber(){
        return absolutePageNumber;
    }
}
