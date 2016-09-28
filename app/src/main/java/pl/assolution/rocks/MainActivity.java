package pl.assolution.rocks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import static pl.assolution.rocks.InternetAccessChecker.checkInternetConnection;

public class MainActivity extends AppCompatActivity implements InternetAccessChecker.InternetAccessListener {

    private static final String TAG_USER = "user";
    protected Button viewAllBtn;
    protected Button addItemBtn;
    protected Button searchBtn;
    protected Button showMyBtn;
    private CoordinatorLayout mainCoordinatorLayout ;
    private RocksApplication.LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RocksApplication rocksApplication  = (RocksApplication) getApplication();
        rocksApplication.getLoginManager();
        loginManager = rocksApplication.getLoginManager();
        if(loginManager.isUserNotLogged()) {
            finish();
            Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            startActivity(intent);
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(TAG_USER, MODE_PRIVATE);
        String author = sharedPreferences.getString(TAG_USER, null);
        assert author != null;
        String toobar_label = "Zalogowany jako ".concat(author);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(toobar_label);


        mainCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_layout);

        viewAllBtn = (Button) findViewById(R.id.view_all_btn);
        addItemBtn = (Button) findViewById(R.id.add_item_btn);
        showMyBtn = (Button) findViewById(R.id.view_my_btn);
        searchBtn = (Button) findViewById(R.id.search_btn);

        showMyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternetConnection(mainCoordinatorLayout, InternetAccessChecker.isConnected())){
                    Intent intent = new Intent(getApplicationContext(), AllItemsActivity.class);
                    intent.putExtra("query", "my");
                    startActivity(intent);
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternetConnection(mainCoordinatorLayout, InternetAccessChecker.isConnected())) {
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    intent.putExtra("searching", "search");
                    startActivity(intent);
                }
            }
        });

        viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternetConnection(mainCoordinatorLayout, InternetAccessChecker.isConnected())) {
                    Intent intent = new Intent(getApplicationContext(), AllItemsActivity.class);
                    intent.putExtra("query", "all");
                    startActivity(intent);
                }
            }
        });

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternetConnection(mainCoordinatorLayout, InternetAccessChecker.isConnected())) {
                    Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
                    intent.putExtra("editor", "add");
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        RocksApplication.getInstance().setAccessListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkInternetConnection(mainCoordinatorLayout , isConnected);
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
            case R.id.action_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                loginManager.logout();
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
