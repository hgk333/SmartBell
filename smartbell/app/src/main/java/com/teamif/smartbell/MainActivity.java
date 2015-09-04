package com.teamif.smartbell;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//Import statements for communicate with server


public class MainActivity extends Activity implements OnClickListener, RadioGroup.OnCheckedChangeListener {
    /**
     * Called when the activity is first created.
     */

    static final String TAG = "MainActivity";
    static final String TEMP_DEVICE_NO = "1000000";
    static final String CHANGE_STATUS_URL = "http://210.121.154.236/node/smartbelltest/changedevicestatus";
    static final String VIEW_LOG_URL = "http://210.121.154.236/node/smartbelltest/viewlog";
    static final String SERVER_PARAM_DEVICE_NO = "deviceNo";
    static final String SERVER_PARAM_NEW_STATUS = "newStatus";

    static final String DEVICE_STATUS_IN_ROOM = "10";
    static final String DEVICE_STATUS_ON_CLASS = "20";
    static final String DEVICE_STATUS_IN_CAMPUS = "30";
    static final String DEVICE_STATUS_GO_OUTSIDE = "40";
    static final String DEVICE_STATUS_OFF_OFFICE = "00";

    private ProgressDialog pDialog;
    private Button updateButton;
    private VisitListAdpater adapter;
    JsonArrayRequest logReq;
    List<VisitorData> mVisitListData;
    ListView mVisitListView;
    VisitListAdpater mVisitorAdapter;

    private String name;
    private String purpose;
    private String time;
    private String thumbnail;
    private String phone;

    private ImageView btHome;
    private ImageView btSearch;
    private ImageView btInfo;
    private ImageView btAt;
    private ImageView btMenu;

    private RelativeLayout officeInfo;

    private RadioGroup radioGroup;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mVisitListData = new ArrayList<VisitorData>();

        //
        AddTestData();

        mVisitorAdapter = new VisitListAdpater(this,mVisitListData );

        mVisitListView = (ListView) findViewById( R.id.visitor_list );
        mVisitListView.setAdapter(mVisitorAdapter);
        // made tony

        btHome = (ImageView)findViewById(R.id.button_home);
        btSearch = (ImageView)findViewById(R.id.button_search);
        btInfo = (ImageView)findViewById(R.id.button_info);
        btAt = (ImageView)findViewById(R.id.button_at);
        btMenu = (ImageView)findViewById(R.id.ivMenu);
        officeInfo = (RelativeLayout)findViewById(R.id.office_info);
        radioGroup = (RadioGroup)findViewById(R.id.rgOfficeState);
        radioGroup.setOnCheckedChangeListener(this);

        btHome.setOnClickListener(this);
        btSearch.setOnClickListener(this);
        btInfo.setOnClickListener(this);
        btAt.setOnClickListener(this);
        btMenu.setOnClickListener(this);



        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();




        logReq = new JsonArrayRequest("http://210.121.154.236/node/smartbelltest/viewlog",new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                hidePDialog();

                //Toast.makeText(getApplicationContext(), "파싱 포문 전", Toast.LENGTH_SHORT).show();

                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject obj = response.getJSONObject(i);
                        VisitorData visitorData = new VisitorData();
                        visitorData.setName(obj.getString("visiter_name"));
                        name = visitorData.getName();
                        visitorData.setThumbnailURL(obj
                                .getString("picture_path"));
                        thumbnail = visitorData.getThumbnailURL();
                        visitorData.setPurpose((obj.getString("purpose")));
                        purpose = visitorData.getPurpose();

                        visitorData.setTime(obj
                                .getString("visit_time"));
                        time = visitorData.getTime();

                        // Genre is json array
                        // JSONArray genreArry =
                        // obj.getJSONArray("genre");
                        ArrayList<String> genre = new ArrayList<String>();

                        genre.add(obj.getString("visiter_number"));
                        phone = obj.getString("visiter_phone");

                        visitorData.setVisitorID(phone);

                        // adding movie to movies array
                        mVisitListData.add(visitorData);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                // notifying list adapter about data changes
                // so that it renders the list view with updated data
                mVisitorAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        AppController.getInstance().addToRequestQueue(logReq);


    }

    public void AddTestData()
    {
        VisitorData visitor;
        visitor = new VisitorData();
        visitor.setVisitorID("0123456");
        visitor.setPurpose("'성적'문의");
        mVisitListData.add( visitor );


        visitor = new VisitorData();
        visitor.setVisitorID("0123456");
        visitor.setPurpose("'제안'문의");
        mVisitListData.add( visitor );
    }

    private static final String vistorRequstURL = "http://210.121.154.236/node/smartbelltest/viewlog";

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    // 서버로 부터 방문자 리스트를 가져와서
    // 리스트 뷰의 기존 데이터를 지우고 새 데이터로 업데이트 한다.
    private void updateList(JsonArrayRequest visitReq,
                            final List<VisitorData> visitorDataList) {

        // 로딩 다이얼로그 출력
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();


        // 기존 데이터 삭제
        visitorDataList.clear();
        // Creating volley request obj


        visitReq = new JsonArrayRequest(vistorRequstURL,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                hidePDialog();

                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject obj = response.getJSONObject(i);
                        VisitorData visitorData = new VisitorData();
                        visitorData.setName(obj.getString("visiter_name"));
                        name = visitorData.getName();
                        visitorData.setThumbnailURL(obj
                                .getString("picture_path"));
                        thumbnail = visitorData.getThumbnailURL();
                        visitorData.setPurpose((obj.getString("purpose")));
                        purpose = visitorData.getPurpose();

                        visitorData.setTime(obj
                                .getString("visit_time"));
                        time = visitorData.getTime();

                        // Genre is json array
                        // JSONArray genreArry =
                        // obj.getJSONArray("genre");
                        ArrayList<String> genre = new ArrayList<String>();

                        genre.add(obj.getString("visiter_number"));
                        phone = obj.getString("visiter_number");

                        visitorData.setVisitorID(phone);

                        // adding movie to movies array
                        mVisitListData.add(visitorData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                // notifying list adapter about data changes
                // so that it renders the list view with updated data
                mVisitorAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        AppController.getInstance().addToRequestQueue(visitReq);

    }// end of updateList


    //made by tony



    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_home: // 방문자 리스트<처음화면>
                setVisible();
                Log.d("test","homeclick");
                mVisitListView.setVisibility(View.VISIBLE);

                break;
            case R.id.button_search: // 방문 통계
                Log.d("test","search click");
                setVisible();

                break;
            case R.id.button_info: // 연구실 상대 변경
                Log.d("test","info click");
                setVisible();
                officeInfo.setVisibility(View.VISIBLE);

                break;
            case R.id.button_at: // 현재 사무실 앞 상황 카메라로 찍은 스샷띄우기
                setVisible();
                Log.d("test","at click");

                break;
            case R.id.ivMenu :
                Log.d("test","menu click");
                Intent intent = new Intent(MainActivity.this,LeftSlideMenu.class);
                intent.putExtra("text","left");
                startActivity(intent);

                break;
        }
    }
    private void setVisible(){
        officeInfo.setVisibility(View.INVISIBLE);
        mVisitListView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        switch (id){ // 서버로 전송
            case R.id.rbstay : // at office
                Log.d("radiobutton", " at office");
                changeDeviceStatus(DEVICE_STATUS_IN_ROOM);

                break;
            case R.id.rbClass : // taking a class
                Log.d("radiobutton","taking a class");
                changeDeviceStatus(DEVICE_STATUS_ON_CLASS);

                break;
            case R.id.rbOut : // outside
                Log.d("radiobutton","at outside");
                changeDeviceStatus(DEVICE_STATUS_GO_OUTSIDE);

                break;
            case R.id.rbSchool :// at school
                Log.d("radiobutton","at school");
                changeDeviceStatus(DEVICE_STATUS_IN_CAMPUS);

                break;
            case R.id.rbLeaveOffice : // go to home
                Log.d("radiobutton","go to home");
                changeDeviceStatus(DEVICE_STATUS_OFF_OFFICE);

                break;
        }
    }

    //서버로 상태 변경 요청을 전송하는 메소드.
    private void changeDeviceStatus(String newStatus){

        HttpClient http = new DefaultHttpClient();
        try{

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair( SERVER_PARAM_DEVICE_NO , TEMP_DEVICE_NO ));
            nameValuePairs.add(new BasicNameValuePair( SERVER_PARAM_NEW_STATUS , newStatus ));

            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpConnectionParams.setSoTimeout(params, 10000);

            HttpPost httpPost = new HttpPost(CHANGE_STATUS_URL);
            UrlEncodedFormEntity entityRequest =
                    new UrlEncodedFormEntity(nameValuePairs, "UTF-8");

            httpPost.setEntity(entityRequest);

            HttpResponse responsePost = http.execute(httpPost);
            HttpEntity resEntity = responsePost.getEntity();


        }catch(Exception e){e.printStackTrace();}



    }//end of changeDeviceStatus


    @Override
    public void onDestroy() {
        super.onDestroy();
        //방문기록 로딩시 앱이 종료될 경우, 프로그래스 대화상자를 없애기 위함.
        hidePDialog();
    }//end of onDestroy
/*
    @Override
    public void onResume(){

        //액티비티 전환 시 방문기록을 새로 고침 하기 위함.
        updateList(logReq, mVisitListData);

    }//end of onResume
*/
}
