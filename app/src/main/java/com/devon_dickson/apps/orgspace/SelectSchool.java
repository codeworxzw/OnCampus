package com.devon_dickson.apps.orgspace;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;


public class SelectSchool extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_school);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ListView schoolList;
        schoolList = (ListView) findViewById(R.id.schoolList);

        String[] schools = getResources().getStringArray(R.array.schools);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,schools);
        schoolList.setAdapter(adapter);
    }
}
