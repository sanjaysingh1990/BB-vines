package crickit.debut.com.library;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by android on 25/4/17.
 */

public class CheckLatestVersion {
    private String PLAY_STORE_LINK;
    private UpdateApplication updateApplication;
    private String currentVersion;
    private String latestVersion;
    private Context mContext;

    public CheckLatestVersion(Context mContext)
    {
        this.mContext=mContext;
    }

    /**
     * *************** get application current version ****************
     */
    public void getCurrentVersion(String mPlayStoreLink, UpdateApplication mUpdateApplication)  {
        this.updateApplication = mUpdateApplication;
        this.PLAY_STORE_LINK = mPlayStoreLink;
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pInfo = null;
            pInfo = pm.getPackageInfo(mContext.getPackageName(), 0);
            currentVersion = pInfo.versionName;
            Log.e("cv", currentVersion + "");
            new GetLatestVersion().execute();
        }
        catch (Exception ex)
        {
            if(this.updateApplication!=null)
            {
                updateApplication.noUpdate(mContext.getResources().getString(R.string.no_package_found));
            }
        }
    }

    private class GetLatestVersion extends AsyncTask<String, String, JSONObject> {

        //private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
//It retrieves the latest version by scraping the content of current version from play store at runtime
                Document doc = Jsoup.connect(PLAY_STORE_LINK).get();
                latestVersion = doc.getElementsByAttributeValue("itemprop", "softwareVersion").first().text();
                Log.e("lv", latestVersion + "");
            } catch (Exception e) {
                e.printStackTrace();

            }

            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (latestVersion != null) {
                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    if (updateApplication != null)
                        updateApplication.newVersionFound(latestVersion);

                } else {
                    if (updateApplication != null)
                        updateApplication.noUpdate(mContext.getResources().getString(R.string.no_update_found));
                }
            } else {
                if (updateApplication != null)
                    updateApplication.noUpdate(mContext.getResources().getString(R.string.no_update_found));

            }

            super.onPostExecute(jsonObject);
        }
    }
}
