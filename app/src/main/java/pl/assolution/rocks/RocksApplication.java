package pl.assolution.rocks;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * Created by Andrzej on 2016-09-08. (ready)
 */
public class RocksApplication extends Application {

    private static RocksApplication myApplicationInstance;
    private LoginManager loginManager;

    public LoginManager getLoginManager() {
        return loginManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplicationInstance = this;
        loginManager = new LoginManager();
    }

    public static synchronized RocksApplication getInstance() {
        return myApplicationInstance;
    }

    public void setAccessListener(InternetAccessChecker.InternetAccessListener listener) {
        InternetAccessChecker.internetAccesslistener = listener;
    }

    class LoginManager {
        private static final String USER = "user";
        private String userLogin;
        private SharedPreferences sharedPreferences;

        public LoginManager() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            this.userLogin = sharedPreferences.getString(USER, null);
        }

        public boolean isUserNotLogged() {
            return TextUtils.isEmpty(userLogin);
        }

        public void saveLogin(String userLogin) {
            this.userLogin = userLogin;

            SharedPreferences.Editor editor = getSharedPreferences(USER, MODE_PRIVATE).edit();
            editor.putString(USER, userLogin);
            editor.apply();
        }

        public void logout() {
            userLogin = null;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(USER);
            editor.apply();
        }
    }
}
