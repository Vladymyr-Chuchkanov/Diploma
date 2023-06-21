package com.chuchkanov.filereader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InvertedIndex {
    public Map<String, String> invIndex = new HashMap<>();

    public void addText(String Text, int section){
        // = Text.replaceAll("\n\n","\n");
        String[] newWords = Text.split(" ");

        int ind = 0;
        for (String word : newWords) {
            String newVal = "";
            word = word.toLowerCase(Locale.ROOT);

            if (word.equals("")) {
                ind++;
                continue;
            }
            if (invIndex.containsKey(word)) {
                newVal = invIndex.get(word);
            }
            newVal += ";" + Integer.toString(section) + "," + Integer.toString(ind);
            invIndex.put(word, newVal);
            ind += 1 + word.length();
        }


    }
    public String getIndByName(String name, boolean isSingleWord){
        if(name.equals("")){
            return "";
        }
        if(invIndex.containsKey(name)&&!isSingleWord){
            return invIndex.get(name);
        }
        String res = "";
        List<String> keys = new ArrayList<>(invIndex.keySet());
        for(String k : keys){
            if(k.contains(name)){
                res+=invIndex.get(k);
            }
        }
        return res;

    }

}
