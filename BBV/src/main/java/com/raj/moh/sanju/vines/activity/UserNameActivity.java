package com.raj.moh.sanju.vines.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.raj.moh.sanju.vines.MainActivity;
import com.rajmoh.allvines.R;
import com.rajmoh.allvines.databinding.ActivityUserNameBinding;


public class UserNameActivity extends AppCompatActivity {

    ActivityUserNameBinding activityUserNameBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUserNameBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_name);
        activityUserNameBinding.setUsernNameActivity(this);
        activityUserNameBinding.setClickEvent(new ClickEvent());

    }

    public class ClickEvent {
        /**
         * ********************** BACK BUTTON ***************************
         */
        public void dashboard()
        {
            if(TextUtils.isEmpty(activityUserNameBinding.edittextUname.getText()))
            {   activityUserNameBinding.edittextUname.requestFocus();
                activityUserNameBinding.edittextUname.setError("empty");
                return;
            }

            Intent mainactivity=new Intent(UserNameActivity.this,MainActivity.class);
            mainactivity.putParcelableArrayListExtra("data",getIntent().getParcelableArrayListExtra("data"));
            startActivity(mainactivity);
            finish();

        }


    }
}
