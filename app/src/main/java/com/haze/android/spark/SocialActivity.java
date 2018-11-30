package com.haze.android.spark;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SocialActivity extends AppCompatActivity {

    RelativeLayout socialClose;
    Button blueBtn,greenBtn;
    TextView socialDetail,socialUsername,socialPlatform;
    String name,platform,detail,blueStr,greenStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            name = extras.getString("socialName");
            platform = extras.getString("socialPlatform");
            detail = extras.getString("socialDetail");
            blueStr = extras.getString("socialBlue");
            greenStr = extras.getString("socialGreen");
        }

        socialUsername = (TextView)findViewById(R.id.social_user_name);
        socialPlatform = (TextView)findViewById(R.id.social_account);
        socialDetail = (TextView)findViewById(R.id.social_detail);
        blueBtn = (Button)findViewById(R.id.social_blueBtnId);
        greenBtn = (Button)findViewById(R.id.social_greenBtnId);
        socialClose = (RelativeLayout)findViewById(R.id.social_close);

        socialUsername.setText(name);
        socialPlatform.setText(platform);
        socialDetail.setText(detail);
        blueBtn.setText(blueStr);
        greenBtn.setText(greenStr);

        blueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (platform.equals("WhatsApp")){
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    if (detail.trim().isEmpty()){
                        Toast.makeText(SocialActivity.this, "No Data Available!!!", Toast.LENGTH_SHORT).show();
                    }else {
                        i.setData(Uri.parse("tel:"+detail));
                    }
                    startActivity(i);
                }else if (platform.equals("Snapchat")){
                    Intent nativeAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://snapchat.com/add/" + detail));
                    startActivity(nativeAppIntent);
                }else if (platform.equals("Facebook")){
                    Intent nativeAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(detail+""));
                    startActivity(nativeAppIntent);
                }else if (platform.equals("Instagram")){
                    Intent nativeAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/" + detail));
                    startActivity(nativeAppIntent);
                }

            }
        });

        socialClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        greenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(SocialActivity.this,socialDetail.getText().toString());
            }
        });

    }

    private void setClipboard(Context context, String text) {

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show();

    }

}
