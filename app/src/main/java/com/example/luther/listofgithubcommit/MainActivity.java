package com.example.luther.listofgithubcommit;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.luther.listofgithubcommit.adapters.CustomAdapterGithubCommitsList;
import com.example.luther.listofgithubcommit.data.DataContract;
import com.example.luther.listofgithubcommit.data.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener{

    View loadMoreProgressView;
    private RelativeLayout bottomLoadMoreLayout;
    private ListView gitDataListView;
    private int currentFirstVisibleItem = 0;
    private int currentVisibleItemCount = 0;
    private int totalItemCount = 0;
    private int currentScrollState = 0;
    private boolean loadingMore = false;
    private int offset = 1;
    private DatabaseHelper mDataHelper;
    private boolean finallyCompleted = false;
    private CustomAdapterGithubCommitsList customCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDataHelper = DatabaseHelper.getInstance(this);
        gitDataListView = (ListView)findViewById(R.id.listview_githubdata);
        bottomLoadMoreLayout = (RelativeLayout) findViewById(R.id.loadItemsLayout_listView);
        bottomLoadMoreLayout.setVisibility(View.INVISIBLE);
        Cursor cursor;
        //Displaying data from local database.
        cursor = mDataHelper.getReadableDatabase().query(
                DataContract.GitDetails.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );
        customCursorAdapter = new CustomAdapterGithubCommitsList(this, cursor);
        gitDataListView.setAdapter(customCursorAdapter);
        if(cursor.getCount() == 0) {
            LoadGitData loadGitData= new LoadGitData(offset);
            loadGitData.execute();
        }
        gitDataListView.setOnScrollListener(this);


    }

    //Scrolling activity call
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        this.currentScrollState = scrollState;
        this.isScrollCompleted();
    }
    private void isScrollCompleted() {
        if(this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE && ((currentFirstVisibleItem + currentVisibleItemCount)>=totalItemCount))
        {
            if(!loadingMore)
            {
                loadingMore = true;
                offset +=1;
                if(finallyCompleted){
                }else {
                    LoadGitData loadGitData = new LoadGitData(offset);
                    loadGitData.execute();
                    showLoadMoreProgress(true);
                }
            }
        }
    }
    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.currentFirstVisibleItem = firstVisibleItem;
        this.currentVisibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    public void showLoadMoreProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            bottomLoadMoreLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


// For Loading up the data
    public class LoadGitData extends AsyncTask<String, Void, String> {

        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;
        URL url = null;
        Boolean postexec = false;
        String searchable;
        int offset = 1;

    //Storing up the data in local database.
        public void addToJSON(String response){
            try {
                    JSONArray data = new JSONArray(response);
                    if(data.length() < 15){
                        finallyCompleted = true;
                    }
                    if(data.length() > 0){
                        JSONObject obj;
                        for (int i = 0; i < data.length(); i++){
                            String uuid="",description="",repositoryName="",watchers="0",bugs="0",language="null";
                            obj = data.getJSONObject(i);
                            if(obj.has("id")){
                                uuid = obj.getString("id");
                            }
                            if(obj.has("description")){
                                description = obj.getString("description");
                            }
                            if(obj.has("name")){
                                repositoryName = obj.getString("name");
                            }
                            if(obj.has("watchers_count")){
                                watchers = obj.getString("watchers_count");;
                            }
                            if(obj.has("open_issues_count")){
                                bugs = obj.getString("open_issues_count");
                            }
                            if(obj.has("language")){
                                language = obj.getString("language");
                            }
                            SQLiteDatabase db = mDataHelper.getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put(DataContract.GitDetails.USER_UUID, uuid);
                            cv.put(DataContract.GitDetails.BUGS, bugs);
                            cv.put(DataContract.GitDetails.DESCRIPTION, description);
                            cv.put(DataContract.GitDetails.WATCHERS, watchers);
                            cv.put(DataContract.GitDetails.LANGUAGE_USED, language);
                            cv.put(DataContract.GitDetails.REPOSITORY_NAME, repositoryName);
                            int value = (int)db.insertWithOnConflict(DataContract.GitDetails.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                        }
                    }else{

                    }
            }catch(Exception e){
                System.out.println(e);
            }

        }

        public LoadGitData( int offset) {
            this.offset = offset ;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                url = new URL("https://api.github.com/users/JakeWharton/repos?page=" + offset + "&per_page=15");
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                System.out.println(urlConnection.getResponseCode());
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb2 = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb2.append(line);
                }
                reader.close();
                addToJSON(sb2.toString());
                postexec = true;
            }
            catch (Exception e) {
                System.out.println(e);
            }
            return null;}

        protected void onPostExecute(String result) {
            if(bottomLoadMoreLayout.isShown())
                bottomLoadMoreLayout.setVisibility(View.INVISIBLE);
            if(postexec){
                if(bottomLoadMoreLayout.isShown())
                    bottomLoadMoreLayout.setVisibility(View.INVISIBLE);
                if (loadingMore) {
                    loadingMore = false;
                }
                Cursor cursor;
                cursor = mDataHelper.getReadableDatabase().query(
                        DataContract.GitDetails.TABLE_NAME,  // Table to Query
                        null, // all columns
                        null, // Columns for the "where" clause
                        null, // Values for the "where" clause
                        null, // columns to group by
                        null, // columns to filter by row groups
                        null // sort order
                );
                System.out.println(cursor.getCount());
                customCursorAdapter.swapCursor(cursor);
                customCursorAdapter.notifyDataSetChanged();
            }else{
                finallyCompleted = false;
                Toast.makeText(getBaseContext(),"No internet connection",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
