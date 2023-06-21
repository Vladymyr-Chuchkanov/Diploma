package com.chuchkanov.filereader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileViewer{
    private String currentText;
    private int currentPage = 0;
    private boolean isClickAllowed = true;
    private int charsPerLine = 0;
    private int maxLines = 0;
    private int currentSection = 0;

    public int absolutePageNumber = 0;
    public int absoluteCurrentPage = 1;
    public InvertedIndex invertedIndex = new InvertedIndex();
    public TextView mainTextView;
    public LinearLayout mainLinearLayout;
    public Context appContext;
    public StyleSet styleSet;
    public String defaultLanguage = "eng";
    public Book book;
    public ContextMenu mainMenu;

    public int getAbsoluteCurrentPage() {
        return absoluteCurrentPage;
    }
    public int getAbsolutePageNumber() {
        return absolutePageNumber;
    }
    public FileViewer(LinearLayout lr){
        appContext = lr.getContext();
        mainLinearLayout = lr;
        styleSet = new StyleSet();
        setMainView();
    }
    public void setBook(String path, ReaderFormat rf){
        book = new Book(path, rf);
        currentText = book.goToPage(currentPage);
        styleSet.setLanguage(defaultLanguage);
        invertedIndex = book.createSearchIndex();
        if(mainTextView!=null){
            mainTextView.setText(currentText);
        }
    }
    public void setBook(String path, File filesDir){
        book = new Book(path, filesDir);
        currentText = book.goToPage(currentPage);
        styleSet.setLanguage(defaultLanguage);
        invertedIndex = book.createSearchIndex();
        if(mainTextView!=null){
            mainTextView.setText(currentText);
        }
    }
    public void updateFont(Context context, String name){
        styleSet.updateFont(context, name, mainTextView);
    }
    @SuppressLint({"ClickableViewAccessibility","RestrictedApi"})
    private void setMainView() {
        FileViewer tempFV = this;


        mainTextView = new TextView(appContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                10
        );
        mainTextView.setLayoutParams(layoutParams);
        styleSet.doAfterTextViewCreation(mainTextView);

        mainTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float textViewWidth = v.getWidth();
                float touchX = event.getX();
                long lastClickTime = 0;
                long CLICK_TIME_THRESHOLD = 1000;
                float leftSideClick = 0.3f * textViewWidth;
                float rightSideClick = 0.7f * textViewWidth;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        long currentTime = System.currentTimeMillis();
                        long elapsedTime = currentTime - lastClickTime;

                        if (elapsedTime < CLICK_TIME_THRESHOLD) {
                            isClickAllowed = false;
                            return true;
                        }
                        lastClickTime = currentTime;
                        System.out.println(1);
                        if (touchX <= leftSideClick) {
                            displayText(movePage(-1));
                        }
                        else if(touchX >= rightSideClick){
                            displayText(movePage(1));
                        }else{
                            v.showContextMenu();
                        }

                        Handler handler = new Handler();
                        handler.postDelayed(() -> isClickAllowed = true, CLICK_TIME_THRESHOLD);

                        break;
                }


                return false;
            }
        });

        mainTextView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                mainMenu = menu;
                SubMenu sbTitle = menu.addSubMenu(0, 1231223445, 1, styleSet.currentLanguage.get("title"));
                sbTitle.add(1, 12312333, 1, styleSet.currentLanguage.get("back"))
                        .setIcon(android.R.drawable.ic_menu_revert)
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                sbTitle.close();
                                v.showContextMenu();
                                return true;
                            }
                        });
                if(book!=null){
                    addTitleToSubMenu(sbTitle);
                }
                menu.add(0,777777,1,styleSet.currentLanguage.get("navigation")).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        checkPaging();
                        EditableMenuDialog editableMenuDialog = new EditableMenuDialog(appContext, tempFV, styleSet.DIALOG_PURPOSE_GO_TO_PAGE, "  /"+ absolutePageNumber);

                        FragmentManager manager = ((AppCompatActivity) appContext).getSupportFragmentManager();

                        FragmentTransaction transaction = manager.beginTransaction();
                        editableMenuDialog.show(transaction, "dialog");
                        mainTextView.setClickable(false);


                        return false;
                    }
                });

                SubMenu sbFontsMenu = menu.addSubMenu(0,1109,1,styleSet.currentLanguage.get("fonts"));
                sbFontsMenu.add(1, 123933, 1, styleSet.currentLanguage.get("back"))
                        .setIcon(android.R.drawable.ic_menu_revert)
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                sbFontsMenu.close();
                                v.showContextMenu();
                                return true;
                            }
                        });
                sbFontsMenu.add(2,456,2,"        "+styleSet.currentLanguage.get("fontsize")).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        EditableMenuDialog editableMenuDialog = new EditableMenuDialog(appContext, tempFV, styleSet.DIALOG_PURPOSE_CHANGE_FONT_SIZE, "");
                        FragmentManager manager = ((AppCompatActivity) appContext).getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        editableMenuDialog.show(transaction, "dialog");
                        return false;
                    }
                });
                SubMenu sbFontTypes = sbFontsMenu.addSubMenu(2,457,2,"        "+styleSet.currentLanguage.get("fonttypes"));
                sbFontTypes.add(14, 123553, 1, styleSet.currentLanguage.get("back"))
                        .setIcon(android.R.drawable.ic_menu_revert)
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                sbFontTypes.close();
                                v.showContextMenu();
                                return true;
                            }
                        });
                addFontToFontTypes(sbFontTypes);

                menu.add(0,7789,1, styleSet.currentLanguage.get("search")).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        checkPaging();
                        EditableMenuDialog editableMenuDialog = new EditableMenuDialog(appContext, tempFV, styleSet.DIALOG_PURPOSE_SEARCH, "");
                        FragmentManager manager = ((AppCompatActivity) appContext).getSupportFragmentManager();

                        FragmentTransaction transaction = manager.beginTransaction();
                        editableMenuDialog.show(transaction, "dialog");

                        return false;
                    }
                });






            }
        });
        mainLinearLayout.addView(mainTextView);

    }
    public void addFontToFontTypes(SubMenu sb){
        List<String> names = new ArrayList<>(styleSet.typefaceMap.keySet());
        for(int i =0;i < names.size(); i++){
            String name = names.get(i);
            sb.add(1, i, 2,"        "+ name).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    styleSet.updateFontByName(name, mainTextView);
                    return false;
                }
            });
        }
    }
    public void addTitleToSubMenu(SubMenu sb){
        List<String> tableOfContents =  book.getTableOfContents();
        for(int i =0; i < tableOfContents.size();i++){
            if(!tableOfContents.get(i).equals("")&&!tableOfContents.get(i).equals("image")){
                MenuItem temp =  sb.add(1,i,2,tableOfContents.get(i));
                if(i==currentSection){
                    SpannableString spannable = new SpannableString(temp.getTitle());
                    spannable.setSpan(new ForegroundColorSpan(styleSet.TITLE_SELECTED_COLOR), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    temp.setTitle(spannable);
                }
                else{
                    SpannableString spannable = new SpannableString(temp.getTitle());
                    spannable.setSpan(new ForegroundColorSpan(styleSet.TITLE_NOT_SELECTED_COLOR), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    temp.setTitle(spannable);
                }
                temp.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        displayText(goToSection(item.getItemId()));
                        return false;
                    }
                });
            }
        }
    }
    public void updateFontSize(int size){
        styleSet.updateFontSize(mainTextView, size);
        //checkPaging();
    }
    public void checkPaging(){

        ((TextView)mainTextView).setText(currentText);
        int cml = ((TextView) mainTextView).getLayout().getLineCount();
        int elipsized = ((TextView) mainTextView).getLayout().getEllipsisCount(cml-1);
        if(elipsized>0){
            styleSet.updateEllipsize(-0.01);
        }
        int currentCharsPerLine = styleSet.getMaxCharactersPerLine((TextView) mainTextView);
        int currentmaxLines = styleSet.getMaxLines((TextView) mainTextView);
        if(charsPerLine!=currentCharsPerLine||currentmaxLines!=maxLines){
            charsPerLine=currentCharsPerLine;
            maxLines = currentmaxLines;
            book.updatePaging(charsPerLine, maxLines, currentPage, currentSection);
            currentPage = book.getCurrentPage();
            currentSection = book.getCurrentSection();
            absoluteCurrentPage = book.getAbsoluteCurrentPage();
            absolutePageNumber = book.getAbsolutePageNumber();
            currentText = book.goToPage(absoluteCurrentPage);
        }
        ((TextView)mainTextView).setText(currentText);
        cml = ((TextView) mainTextView).getLayout().getLineCount();
        elipsized = ((TextView) mainTextView).getLayout().getEllipsisCount(cml-1);
        if(elipsized>0){

            checkPaging();
        }



    }
    public void displayText(String text){




        if(mainTextView==null||book==null){
            return;
        }
        currentPage = book.getCurrentPage();
        currentSection = book.getCurrentSection();
        absoluteCurrentPage = book.getAbsoluteCurrentPage();
        if(book.getSectionType(currentSection)!=2)
        {
            currentText = text;
            checkPaging();
        }
        if(book.getSectionType(currentSection)==1) {
            String title = "";
            if(currentPage==0){
                title = book.getTitle(currentSection);
            }

            if(!title.equals("")){
                String fullText = title+"\n"+text;
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(fullText);
                StyleSpan boldStyleSpan = new StyleSpan(Typeface.BOLD);
                int startIndex = fullText.indexOf(title);
                int endIndex = startIndex + title.length();
                spannableStringBuilder.setSpan(boldStyleSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                if(book.searchIndex>=0){
                    int startIndex2 = book.searchIndex+endIndex+1;
                    int endIndex2 = startIndex2 + book.searchText.length();
                    book.searchText="";
                    book.searchIndex = -1;
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.DKGRAY);
                    BackgroundColorSpan backgroundSpan = new BackgroundColorSpan(Color.YELLOW);
                    spannableStringBuilder.setSpan(colorSpan, startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.setSpan(backgroundSpan, startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                mainTextView.setText(spannableStringBuilder);
            }
            else if(!book.searchText.equals("")){
                String fullText = text;
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(fullText);
                int startIndex = book.searchIndex;
                int endIndex = startIndex + book.searchText.length();
                if(startIndex<0){
                    mainTextView.setText(text);
                    return;
                }
                book.searchText="";
                book.searchIndex = -1;
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.DKGRAY);
                BackgroundColorSpan backgroundSpan = new BackgroundColorSpan(Color.YELLOW);
                spannableStringBuilder.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(backgroundSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                mainTextView.setText(spannableStringBuilder);
            }
            else{
                mainTextView.setText(text);
            }


        }
        else if(book.getSectionType(currentSection)==2)
        {
            text = text.replace("\n","");
            byte[] decodedBytes = Base64.decode(text, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, ((int)(mainTextView.getWidth()*0.9)), ((int)(mainTextView.getHeight()*0.9)), false);
            ImageSpan imageSpan = new ImageSpan(appContext, resizedBitmap);
            SpannableString spannableString = new SpannableString(" ");
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mainTextView.setText(spannableString);
        }


    }
    public String goToPage(int page){
        if(maxLines==0||charsPerLine==0){
            checkPaging();
        }
        return book.goToPage(page);
    }
    public String goToIndex(int section, int index, String text){
        if(maxLines==0||charsPerLine==0){
            checkPaging();
        }
        return book.goToIndex(section, index, text);
    }
    public String movePage(int direction){
        if(maxLines==0||charsPerLine==0){
            checkPaging();
        }
        return book.movePage(direction);
    }
    public String goToSection(int section){
        if(maxLines==0||charsPerLine==0){
            checkPaging();
        }
        return book.moveToSection(section);
    }



}
