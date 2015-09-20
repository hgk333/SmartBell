package com.teamif.smartbell;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class PopUpInfo extends Activity {

	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	private String visitor_seq_no;

	String phoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup);

		imageLoader = AppController.getInstance().getImageLoader();

		Intent callerIntent = getIntent();

		visitor_seq_no = (String) callerIntent.getSerializableExtra("seq_no");

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
			phoneNumber="01012345678";
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
				startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNumber)));
				// uri parse만 바꾸면 됨.
            }
        } );

        feedbackBtn.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View view) {
				FeedbackDialog();
            }
        });

	}// end of onCreate

	private void FeedbackDialog()
	{
		AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
		dialog1.setTitle("피드백");
		dialog1.setMessage("요청에 대한 의견을 입력하세요");
		// Set an EditText view to get user input
		final EditText etInput1 = new EditText(this);
		dialog1.setView(etInput1);
		AlertDialog.Builder builder = dialog1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String strFeedback = etInput1.getText().toString();
				SendFeedbackToServer(strFeedback);
			}
		});
		dialog1.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.

					}
				});
		dialog1.show();
	}

	private void SendFeedbackToServer(String feedBack) {

		String URL = String.format("%s", MainActivity.GetServerAPIURL(MainActivity.API_LEAVE_FEEDBACK_URL));
		URL = URL + String.format("?%s=%s", MainActivity.SERVER_PARAM_VISITOR_SEQ_NO, visitor_seq_no);
		try {
			URL = URL + String.format("&%s=%s", MainActivity.SERVER_PARAM_FEEDBACK, URLEncoder.encode(feedBack, "UTF-8"));
		}
		catch (Exception ignore)
		{

		}
		StringRequest req = new StringRequest(Request.Method.GET, URL,
				new Response.Listener<String>() {
					@Override
					public  void onResponse(String response) {
						int x;
						x = 0;
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						int x;
						x = 0;
					}
				}) {
//			@Override
//			public String getBodyContentType() {
//				return "application/json; charset=utf-8";
//			}

		};

		AppController.getInstance().addToRequestQueue(req);
	}


	@Override
	protected void onApplyThemeResource(Resources.Theme theme, int resid,
			boolean first) {
		super.onApplyThemeResource(theme, resid, first);

		// no background panel is shown
		theme.applyStyle(android.R.style.Theme_Panel, true);
	}

}// end of activity