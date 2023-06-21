package com.chuchkanov.filereader;

import java.util.ArrayList;
import java.util.List;

public class Section {
    private final int type;
    private String text;
    private String title;
    private int charNumber;
    private int charsPerLine = 0;
    private int maxLines = 0;

    public int SECTION_TYPE_TEXT = 1;
    public int SECTION_TYPE_IMAGE = 2;
    public List<String> pages = new ArrayList<>();

    public Section( int type0, String text0, String title0){
        charNumber = text0.length();
        text=text0;
        type=type0;
        title = title0;
        updatePaging(charsPerLine, maxLines);
    }
    public String getTitle(){
        return title;
    }
    public void appendSection(String text0){
        text+=text0;
        charNumber = text.length();
        updatePaging(charsPerLine, maxLines);
    }
    public String getText(){
        return text;
    }
    public int getType(){
        return type;
    }
    public int getCharsNumber(){
        return charNumber;
    }
    public int getPagesNumber(){
        return pages.size();
    }
    public String getImageString(){
        return text;
    }
    public void updatePaging(int currentCharsPerRow, int currentMaxLines){

        pages.clear();
        if(type==SECTION_TYPE_IMAGE){
            pages.add(text);
            return;
        }
        if(currentCharsPerRow==0 || currentMaxLines==0){
            pages.add(text);
            return;
        }
        String text2 = text;//.replaceAll("\n\n","\n");
        String[] paragraphs = text2.split("\n");
        String page = "";
        double lines = 0;
        for(int i =0; i<paragraphs.length;i++){
            if(pages.size()==0&&lines==0){
                lines+=3;
            }
            if(paragraphs[i].equals("")){
                lines++;
                page+="\n";
                continue;
            }
            int parLength = paragraphs[i].length();
            double newLines = Math.ceil((double)parLength/currentCharsPerRow);

            if(lines+newLines<=currentMaxLines){
                page+=paragraphs[i]+"\n";
                lines+=newLines;
                continue;
            }
            double freeLines = currentMaxLines-lines;
            if(freeLines<0){
                freeLines=0;
                pages.add(page);
                page="";
                lines=0;
                continue;
            }
            String line = paragraphs[i].substring(0, (int) (freeLines*currentCharsPerRow));
            while(line.length()>1){
                char c = line.charAt(line.length()-1);
                if(c=='.'||c=='!'||c=='?'||(c==','&&freeLines<2)){
                    break;
                }
                line = line.substring(0,line.length()-1);
            }
            if(line.length()>1){
                page+=line;
                paragraphs[i]=paragraphs[i].replace(line, "");
            }else if(page.equals("")){
                line =  paragraphs[i].substring(0, (int) (freeLines*currentCharsPerRow));
                while(line.length()<parLength-1){
                    char c = line.charAt(line.length()-1);
                    if(c=='.'||c=='!'||c=='?'||(c==','&&freeLines<2)){
                        break;
                    }
                    line =paragraphs[i].substring(0,line.length()+1);
                }
                page+=line;
                paragraphs[i]=paragraphs[i].replace(line, "");
            }
            pages.add(page);
            page="";
            lines=0;

            i--;
        }
        if(!page.equals("")){
            pages.add(page);
        }
    }
    public String getPage(int i){
        if(i<0||i>pages.size()-1){
            return "";
        }
        return pages.get(i);
    }


}
