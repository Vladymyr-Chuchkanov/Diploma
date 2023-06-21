package com.chuchkanov.filereader;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.text.Layout;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class StyleSet {
    private Typeface tf;
    private double elipsCoef = 1.2;

    public int TITLE_SELECTED_COLOR = Color.GREEN;
    public int TITLE_NOT_SELECTED_COLOR = Color.WHITE;
    public Map<String, Map<String, String>> languageSettings = new HashMap<>();
    public Map<String,String> currentLanguage = new HashMap<>();
    public String defaultLang = "eng";
    public Map<String, Typeface> typefaceMap = new HashMap<>();
    public final int DIALOG_PURPOSE_GO_TO_PAGE=1;
    public final int DIALOG_PURPOSE_CHANGE_FONT_SIZE = 2;
    public final int DIALOG_PURPOSE_SEARCH = 3;
    public double currentFontSize = 0;
    public int currentTextColor;
    public Map<String, Integer> textColorsMap = new HashMap<>();

    public StyleSet(){
        setDefaultLanguageSettings();
    }
    public void setLanguage(String language){
        if(!languageSettings.containsKey(language)){
            return;
        }
        currentLanguage = languageSettings.get(language);
    }
    public void updateEllipsize(double upd){
        elipsCoef += upd;
    }
    public int getMaxLines(TextView textView) {
        int height = textView.getHeight();
        int paddingTop = textView.getPaddingTop();
        int paddingBottom = textView.getPaddingBottom();
        int lineHeight = textView.getLineHeight();
        return (int) ((height- paddingBottom*2 - paddingTop*2) / lineHeight );
    }
    public int getMaxCharactersPerLine(TextView textView) {
        CharSequence tempText =  textView.getText();
        int tempLines = textView.getMaxLines();
        textView.setMaxLines(1);
        String check = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        textView.setText(check);
        float ttt = textView.getTextSize();
        int ellipsed = textView.getLayout().getEllipsisCount(0);
        textView.setMaxLines(tempLines);
        textView.setText(tempText);
        int tmp1 = check.length();
        return (int) ((tmp1 - ellipsed)*elipsCoef);

    }
    public void doAfterTextViewCreation(TextView textView) {
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textView.setTypeface(tf);
                textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                textView.setPadding(textView.getLineHeight(),textView.getLineHeight()/2,textView.getLineHeight(),textView.getLineHeight()/2);
                textView.setMaxLines(getMaxLines(textView));
                textView.setEllipsize(android.text.TextUtils.TruncateAt.END);
                currentFontSize = textView.getTextSize();
                typefaceMap.put("Default", textView.getTypeface());
                currentTextColor = textView.getCurrentTextColor();
                textColorsMap.put("Default",currentTextColor);
                //ShapeDrawable shapeDrawable = new ShapeDrawable();
                //shapeDrawable.getPaint().setColor(Color.RED);
                //shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
                //shapeDrawable.getPaint().setStrokeWidth(2);
                //textView.setBackground(shapeDrawable);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_FULL);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    textView.setBreakStrategy(LineBreaker.BREAK_STRATEGY_HIGH_QUALITY);
                }

            }
        });

    }
    public void addFont(Typeface tf, String publicName){

        typefaceMap.put(publicName, tf);
    }
    public void updateFontByName(String name, TextView tv){
        if(!typefaceMap.containsKey(name)){
            return;
        }
        tv.setTypeface(typefaceMap.get(name));
    }
    public void removeFontByName(String name, TextView tv){
        if(!typefaceMap.containsKey(name)){
            return;
        }
        typefaceMap.remove(name);
    }
    public void addTextColor(int val, String publicName){

        textColorsMap.put(publicName, val);
    }
    public void updateTextColorByName(String name, TextView tv){
        if(!textColorsMap.containsKey(name)){
            return;
        }
        tv.setTextColor(textColorsMap.get(name));
    }
    public void removeTextColorByName(String name, TextView tv){
        if(!textColorsMap.containsKey(name)){
            return;
        }
        textColorsMap.remove(name);
    }
    public void updateFont(Context context, String name, TextView tv){

        tf = Typeface.createFromAsset(context.getAssets(),name);
        if(tv != null){
            tv.setTypeface(tf);
        }


    }
    public void updateFontSize(TextView tv, int size){
        if(size<10){
            size = 10;
        }
        if(size>60){
            size=60;
        }
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        currentFontSize = size;

    }
    public void setDefaultLanguageSettings(){
        Map<String, String> eng = new HashMap<>();
        eng.put("title", "Title");
        eng.put("settings", "Settings");
        eng.put("back","Back");
        eng.put("navigation", "Navigation");
        eng.put("yes","yes");
        eng.put("no", "no");
        eng.put("goToPage", "Go to the page:");
        eng.put("fonts", "Fonts");
        eng.put("fontsize","Font size");
        eng.put("fonttypes","Font type");
        eng.put("search", "Search");
        languageSettings.put("eng", eng);
        Map<String, String> ru = new HashMap<>();
        ru.put("title", "Оглавление");
        ru.put("settings", "Настройки");
        ru.put("back","Назад");
        ru.put("navigation", "Навигация");
        ru.put("yes","Да");
        ru.put("no", "Нет");
        ru.put("goToPage", "Перейти на страницу:");
        ru.put("fonts","Шрифты");
        ru.put("fontsize","Размер шрифта");
        ru.put("fonttype","Тип шрифта");
        ru.put("search","Поиск");
        languageSettings.put("ru", ru);
        Map<String, String> ukr = new HashMap<>();
        ukr.put("title", "Зміст");
        ukr.put("settings", "Налаштування");
        ukr.put("back","Назад");
        ukr.put("navigation", "Навігація");
        ukr.put("yes","Так");
        ukr.put("no", "Ні");
        ukr.put("goToPage", "Перейти на страницю:");
        ukr.put("fonts","Шрифти");
        ukr.put("fontsize","Розмір шрифту");
        ukr.put("fonttypes", "Тип шрифту");
        ukr.put("search", "Пошук");
        languageSettings.put("ukr", ukr);
        currentLanguage = languageSettings.get(defaultLang);
    }
}
