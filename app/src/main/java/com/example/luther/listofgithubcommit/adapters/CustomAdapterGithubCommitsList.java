package com.example.luther.listofgithubcommit.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.luther.listofgithubcommit.R;
import com.example.luther.listofgithubcommit.data.DataContract;


public class CustomAdapterGithubCommitsList extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;



    public CustomAdapterGithubCommitsList(Context context, Cursor c) {
        super(context, c, false);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return  inflater.inflate(R.layout.list_item_github_elements, viewGroup, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor c) {
        final String description = c.getString(c.getColumnIndexOrThrow(DataContract.GitDetails.DESCRIPTION));
        final String repository = c.getString(c.getColumnIndexOrThrow(DataContract.GitDetails.REPOSITORY_NAME));
        final String watchers = c.getString(c.getColumnIndexOrThrow(DataContract.GitDetails.WATCHERS));
        final String bugs = c.getString(c.getColumnIndexOrThrow(DataContract.GitDetails.BUGS));
        final String language = c.getString(c.getColumnIndexOrThrow(DataContract.GitDetails.LANGUAGE_USED));
        TextView repositoryToDisplay = (TextView)view.findViewById(R.id.repository);
        TextView watchersToDisplay = (TextView)view.findViewById(R.id.watcher_count);
        TextView bugsToDisplay = (TextView)view.findViewById(R.id.bugtext);
        TextView languageToDisplay = (TextView)view.findViewById(R.id.codeText);
        TextView descriptionToDisplay = (TextView)view.findViewById(R.id.description);
        ImageView languageImage = (ImageView)view.findViewById(R.id.code);
        if(language == null || language.equals("null")){
            languageImage.setVisibility(View.GONE);
            languageToDisplay.setVisibility(View.GONE);
        }else{
            languageImage.setVisibility(View.VISIBLE);
            languageToDisplay.setVisibility(View.VISIBLE);
            languageToDisplay.setText(language);
        }
        if(description.equals("null") || description == null){
            descriptionToDisplay.setText("No description found");
        }else {
            descriptionToDisplay.setText(description);
        }
        bugsToDisplay.setText(bugs);
        watchersToDisplay.setText(watchers);
        repositoryToDisplay.setText(repository);

    }
}