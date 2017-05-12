package crickit.debut.com.library;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by android on 25/4/17.
 */

public class ShowDialog {
    private Context mContext;

    public ShowDialog(Context mContext)
    {
        this.mContext=mContext;
    }
      public void showForceUpdateDialog() {
      android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(mContext);

      alertDialogBuilder.setTitle(mContext.getResources().getString(R.string.updateavailableTitle));
      alertDialogBuilder.setMessage(mContext.getResources().getString(R.string.updateMessage));
      alertDialogBuilder.setCancelable(false);
      alertDialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
              dialog.cancel();
              mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.aps.imenufr")));

          }
      });
      alertDialogBuilder.show();

  }

}
