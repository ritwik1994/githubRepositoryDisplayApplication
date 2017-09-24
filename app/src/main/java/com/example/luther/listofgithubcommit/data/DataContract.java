package com.example.luther.listofgithubcommit.data;

import android.provider.BaseColumns;


public class DataContract {
    public static final class GitDetails implements BaseColumns{
        public static final String TABLE_NAME = "GitDetails";
        public static final String USER_UUID = "UserUUID";
        public static final String REPOSITORY_NAME = "RepositoryName";
        public static final String DESCRIPTION = "Description";
        public static final String LANGUAGE_USED = "LanguageUsed";
        public static final String WATCHERS = "Watchers";
        public static final String BUGS = "Bugs";
    }
}
