package pl.assolution.rocks;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import static pl.assolution.rocks.InternetAccessChecker.checkInternetConnection;

public class SearchActivity extends AppCompatActivity implements InternetAccessChecker.InternetAccessListener {

    private static final String TAG_DESIGNATION= "designation";
    private static final String TAG_TYPE= "type";
    private static final String TAG_AUTHOR= "author";

    private CoordinatorLayout coordinatorLayoutItemSearch;
    private RocksApplication.LoginManager loginManager;
    private EditText searchQuertEt;
    private RadioGroup radioGroup;
    protected RadioButton defaultName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        coordinatorLayoutItemSearch = (CoordinatorLayout) findViewById(R.id.coordinator_layout_search_item);
        RocksApplication rocksApplication  = (RocksApplication) getApplication();
        rocksApplication.getLoginManager();
        loginManager = rocksApplication.getLoginManager();
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        defaultName = (RadioButton) findViewById(R.id.checkbox_name);
        defaultName.setChecked(true);

        ImageButton searchImgBtn = (ImageButton) findViewById(R.id.search_ibtn);
        searchQuertEt = (EditText) findViewById(R.id.search_et);

        searchImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isError = false;
                String query = searchQuertEt.getText().toString();

                if(TextUtils.isEmpty(query)) {
                    searchQuertEt.setError("To pole jest wymagane");
                    isError = true;
                }
                if(!isError) {
                    if(checkInternetConnection(coordinatorLayoutItemSearch, InternetAccessChecker.isConnected())) {
                        searchAndDisplay(query);
                    }
                }
            }
        });

        checkInternetConnection(coordinatorLayoutItemSearch, InternetAccessChecker.isConnected());
    }

    @Override
    protected void onResume() {
        super.onResume();
        RocksApplication.getInstance().setAccessListener(this);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkInternetConnection(coordinatorLayoutItemSearch, isConnected);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_search).setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
            case R.id.action_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                finish();
                loginManager.logout();
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchAndDisplay(String query) {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        Intent intent = new Intent(getApplicationContext(), SearchItemsActivity.class);
        intent.putExtra("query", query);

        switch(checkedId) {
            case R.id.checkbox_name:
                intent.putExtra("tag",TAG_DESIGNATION);
                break;
            case R.id.checkbox_author:
                intent.putExtra("tag", TAG_AUTHOR);
                break;
            case R.id.checkbox_type:
                intent.putExtra("tag", TAG_TYPE);
                break;
        }
        startActivity(intent);
    }
}
