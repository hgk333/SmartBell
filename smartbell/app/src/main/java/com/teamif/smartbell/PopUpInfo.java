package com.teamif.smartbell;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class PopUpInfo extends Activity {

	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup);

		imageLoader = AppController.getInstance().getImageLoader();

		Intent callerIntent = getIntent();
		String name = (String) callerIntent.getSerializableExtra("name");
		String purpose = (String) callerIntent.getSerializableExtra("purpose");
		String time = (String) callerIntent.getSerializableExtra("visit_time");
		String thumbnail = (String) callerIntent
				.getSerializableExtra("thumbnail");
		String phone = (String) callerIntent.getSerializableExtra("phone");
		
		purpose = "방문목적\n" + purpose;

		NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.popupthumbnail);

		TextView tv1 = (TextView) findViewById(R.id.popupname);
		TextView tv2 = (TextView) findViewById(R.id.popuppurpose);
		TextView tv3 = (TextView) findViewById(R.id.popupvisittime);

		Button backBtn = (Button) findViewById(R.id.popupbtnback);
        Button feedbackBtn = (Button) findViewById(R.id.popupbtnfeedback);
        Button callBtn = (Button) findViewById(R.id.popupbtncall);

        //전화번호냐, 학번이냐를 판단하여 피드백 단추나 전화걸기 단추가 보이도록 함.
        if (true){

            feedbackBtn.setVisibility(View.VISIBLE);

        }else {

            callBtn.setVisibility(View.VISIBLE);

        }

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		thumbNail.setImageUrl(thumbnail, imageLoader);

		tv1.setText(name);
		tv2.setText(purpose);
		tv3.setText(time);

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}

		});

        callBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        } );

        feedbackBtn.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

	}// end of onCreate

	@Override
	protected void onApplyThemeResource(Resources.Theme theme, int resid,
			boolean first) {
		super.onApplyThemeResource(theme, resid, first);

		// no background panel is shown
		theme.applyStyle(android.R.style.Theme_Panel, true);
	}

}// end of activity