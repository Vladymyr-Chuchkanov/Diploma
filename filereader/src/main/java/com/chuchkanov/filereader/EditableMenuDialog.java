package com.chuchkanov.filereader;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditableMenuDialog extends DialogFragment {
    private Context appContext;
    private FileViewer fileViewer;
    private AlertDialog.Builder builderAdd;

    public String dialogResultString ="";
    public int dialogPurpose = 1;
    public String additionalInfo;
    public final int DIALOG_PURPOSE_GO_TO_PAGE=1;
    public final int DIALOG_PURPOSE_CHANGE_FONT_SIZE = 2;
    public final int DIALOG_PURPOSE_SEARCH = 3;
    public EditableMenuDialog(Context context, FileViewer fv, int purpose, String addInfo){
        appContext = context;
        fileViewer = fv;
        dialogPurpose = purpose;
        additionalInfo = addInfo;
    }
    public EditableMenuDialog(Context context, FileViewer fv, int purpose, String addInfo, AlertDialog.Builder build){
        appContext = context;
        fileViewer = fv;
        dialogPurpose = purpose;
        additionalInfo = addInfo;
        builderAdd = build;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if(builderAdd!=null){
            return builderAdd.create();
        }



        if(dialogPurpose== DIALOG_PURPOSE_GO_TO_PAGE){
            createGoToPageDialog(builder);
        }
        else if(dialogPurpose==DIALOG_PURPOSE_CHANGE_FONT_SIZE){
            createFontSizeDialog(builder);
        }
        else if(dialogPurpose==DIALOG_PURPOSE_SEARCH){
            createSearchDialog(builder);
        }

        return builder.create();
    }
    private void createGoToPageDialog(AlertDialog.Builder builder){
        EditText et = new EditText(appContext);
        builder.setPositiveButton(fileViewer.styleSet.currentLanguage.get("yes"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialogResultString = String.valueOf(et.getText());
                if(isNumeric(dialogResultString)){
                    fileViewer.displayText(fileViewer.goToPage(Integer.parseInt(dialogResultString)));
                }
                fileViewer.mainTextView.setClickable(true);
                dialog.cancel();
            }
        });
        builder.setMessage(fileViewer.styleSet.currentLanguage.get("goToPage")+additionalInfo);
        builder.setNegativeButton(fileViewer.styleSet.currentLanguage.get("no"), null);
        builder.setView(et);
    }
    private void createFontSizeDialog(AlertDialog.Builder builder){
        TextView textView = new TextView(fileViewer.appContext);
        textView.setGravity(Gravity.CENTER);
        textView.setText(Integer.toString((int) fileViewer.styleSet.currentFontSize));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        );
        textView.setLayoutParams(layoutParams);
        Button minusButton = new Button(fileViewer.appContext);
        minusButton.setText("-");
        minusButton.setLayoutParams(layoutParams);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileViewer.styleSet.currentFontSize--;
                textView.setText(Integer.toString((int) fileViewer.styleSet.currentFontSize));
            }
        });
        Button plusButton = new Button(fileViewer.appContext);
        plusButton.setText("+");
        plusButton.setLayoutParams(layoutParams);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileViewer.styleSet.currentFontSize++;
                textView.setText(Integer.toString((int) fileViewer.styleSet.currentFontSize));
            }
        });
        LinearLayout layout = new LinearLayout(fileViewer.appContext);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(minusButton);
        layout.addView(textView);
        layout.addView(plusButton);

        builder.setMessage(fileViewer.styleSet.currentLanguage.get("fontsize")+additionalInfo);
        builder.setView(layout);
        builder.setPositiveButton(fileViewer.styleSet.currentLanguage.get("yes"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialogResultString = String.valueOf(textView.getText());
                if(isNumeric(dialogResultString)){
                    fileViewer.updateFontSize(Integer.parseInt(dialogResultString));

                }
                dialog.cancel();
            }
        });
        builder.setNegativeButton(fileViewer.styleSet.currentLanguage.get("no"), null);


    }
    private void createSearchDialog(AlertDialog.Builder builder){






        EditText et = new EditText(fileViewer.appContext);
        et.setGravity(Gravity.LEFT);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        );
        et.setLayoutParams(layoutParams);
        Button searchButton = new Button(fileViewer.appContext);
        searchButton.setText(fileViewer.styleSet.currentLanguage.get("search"));
        searchButton.setLayoutParams(layoutParams);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(builder.getContext());
                ScrollView scrollView = new ScrollView(fileViewer.appContext);
                LinearLayout linearLayout = new LinearLayout(fileViewer.appContext);
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT/2,1));
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                String text = String.valueOf(et.getText()).toLowerCase(Locale.ROOT).trim();
                String[] splitedText = text.split(" ");
                String res = fileViewer.invertedIndex.getIndByName(splitedText[0], (text.length()==1));
                String[] indexes = res.split(";");
                for(int i = 1; i < indexes.length; i++){
                    int section = Integer.parseInt(indexes[i].split(",")[0]);
                    int textInd = Integer.parseInt(indexes[i].split(",")[1]);

                    if(section>=0 && section<fileViewer.book.sections.size()){
                        String tempText = fileViewer.book.sections.get(section).getText();//.replaceAll("\n\n","\n");
                        if(textInd+text.length()>tempText.length()){
                            continue;
                        }
                        String text2 = tempText.substring(textInd,textInd+text.length()).toLowerCase(Locale.ROOT);

                        if(text2.equals(text)){
                            TextView tempTv = new TextView(appContext);
                            int indend = textInd+30;
                            if(indend>tempText.length()){
                                indend = tempText.length();
                            }
                            tempTv.setText(tempText.substring(textInd,indend));
                            int finalTextInd = textInd;
                            ShapeDrawable shapeDrawable = new ShapeDrawable();
                            shapeDrawable.getPaint().setColor(Color.WHITE);
                            shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
                            shapeDrawable.getPaint().setStrokeWidth(3);
                            tempTv.setBackground(shapeDrawable);

                            tempTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    fileViewer.displayText(fileViewer.goToIndex(section, finalTextInd, text));

                                }
                            });

                            linearLayout.addView(tempTv);
                        }
                    }
                }
                scrollView.addView(linearLayout);
                builder1.setView(scrollView);
                builder1.setNegativeButton(fileViewer.styleSet.currentLanguage.get("back"), null);
                EditableMenuDialog editableMenuDialog = new EditableMenuDialog(appContext, fileViewer, -1, "", builder1);
                FragmentManager manager = ((AppCompatActivity) appContext).getSupportFragmentManager();

                FragmentTransaction transaction = manager.beginTransaction();
                editableMenuDialog.show(transaction, "dialog");

            }
        });


        LinearLayout layout2 = new LinearLayout(fileViewer.appContext);
        layout2.setOrientation(LinearLayout.HORIZONTAL);
        layout2.addView(et);
        layout2.addView(searchButton);
        builder.setView(layout2);
        builder.setNegativeButton(fileViewer.styleSet.currentLanguage.get("back"), null);

    }
    public boolean isNumeric(String str){
        str = str.trim();

        if (str == null || str.isEmpty()) {
            return false;
        }
        if(str.charAt(0)=='-'){
            str = str.substring(1);
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

}