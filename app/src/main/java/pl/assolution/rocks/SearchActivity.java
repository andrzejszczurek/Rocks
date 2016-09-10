package pl.assolution.rocks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import static pl.assolution.rocks.InternetAccessChecker.checkInternetConnection;

public class SearchActivity extends AppCompatActivity implements InternetAccessChecker.InternetAccessListener {

    private CoordinatorLayout coordinatorLayoutItemSearch;
    private RocksApplication.LoginManager loginManager;

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

        Spinner searchList = (Spinner) findViewById(R.id.search_spinner);
        ImageButton searchImgBtn = (ImageButton) findViewById(R.id.search_ibtn);
        EditText searchQuertEt = (EditText) findViewById(R.id.search_et);

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

}
