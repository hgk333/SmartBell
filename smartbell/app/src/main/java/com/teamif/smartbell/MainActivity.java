package com.teamif.smartbell;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Import statements for communicate with server


public class MainActivity extends Activity implements OnClickListener, RadioGroup.OnCheckedChangeListener {
    /**
     * Called when the activity is first created.
     */

    static final String TAG = "MainActivity";
    static final String TEMP_DEVICE_NO = "1000000";

    static final String SERVER_URL = "http://alpha.jiams.kr:3301";
    //static final String SERVER_URL = "http://210.121.154.235:3301";
    static final String API_JSON_TEST_URL = "/sb/jsontest";
    static final String API_VIEW_VISITOR_URL = "/sb/viewlog";
    static final String API_VIEW_FEEDBACK_URL = "/sb/fblist";
    static final String API_CHANGE_STATUS_URL = "/sb/changedevicestatus";
    static final String API_LEAVE_FEEDBACK_URL = "/sb/feedback";

    //static final String CHANGE_STATUS_URL = "http://210.121.154.236/node/smartbelltest/changedevicestatus";
    static final String VIEW_LOG_URL = "http://210.121.154.236/node/smartbelltest/viewlog";
    static final String SERVER_PARAM_DEVICE_NO = "device_id";
    static final String SERVER_PARAM_NEW_STATUS = "status";

    static final String SERVER_PARAM_VISITOR_SEQ_NO = "seq_no";
    static final String SERVER_PARAM_FEEDBACK = "feedback";



    static final String DEVICE_STATUS_IN_ROOM = "1";
    static final String DEVICE_STATUS_ON_CLASS = "2";
    static final String DEVICE_STATUS_IN_CAMPUS = "3";
    static final String DEVICE_STATUS_GO_OUTSIDE = "4";
    static final String DEVICE_STATUS_OFF_OFFICE = "5 ";

    private static ProgressDialog pDialog;
    private Button updateButton;
    private VisitListAdpater adapter;
    JsonArrayRequest logReq;
    List<VisitorData> mVisitListData;
    ListView mVisitListView;
    VisitListAdpater mVisitorAdapter;

//    private String name;
//    private String purpose;
//    private String time;
//    private String thumbnail;
//    private String phone;

    private ImageView btHome;
    private ImageView btSearch;
    private ImageView btInfo;
    private ImageView btAt;
    private ImageView btMenu;

    private RelativeLayout officeInfo;

    private LinearLayout videoScreen;

    private RadioGroup radioGroup;

    private String pfName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mVisitListData = new ArrayList<VisitorData>();

        //
        //AddTestData();

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
        videoScreen = (LinearLayout)findViewById(R.id.videoScreen);

        btHome.setOnClickListener(this);
        btSearch.setOnClickListener(this);
        btInfo.setOnClickListener(this);
        btAt.setOnClickListener(this);
        btMenu.setOnClickListener(this);



        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        UpdateVisitorList();

        // JSONObjectRequest test code
//        String a = SERVER_URL + JSON_TEST_URL;
//        JsonObjectRequest testReq;
//        testReq = new JsonObjectRequest(Request.Method.GET, a, null,
//            new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject jsonObject) {
//                        VolleyLog.d(TAG, "Error: " + jsonObject.toString());
//                        hidePDialog();
//                    }
//                },
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError volleyError) {
//                    VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
//                    hidePDialog();
//                }
//        });
//        AppController.getInstance().addToRequestQueue(testReq);



//        String viewVisitorURL = GetServerAPIURL(API_VIEW_VISITOR_URL);
//        //String a_URL = "http://alpha.jiams.kr:3301/sb/api/viewlog";
//
//        //logReq = new JsonArrayRequest("http://210.121.154.236/node/smartbelltest/viewlog",new Response.Listener<JSONArray>() {
//        logReq = new JsonArrayRequest(viewVisitorURL ,new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                Log.d(TAG, response.toString());
//
//                hidePDialog();
//
//                //Toast.makeText(getApplicationContext(), "파싱 포문 전", Toast.LENGTH_SHORT).show();
//
//                // Parsing json
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//
//                        JSONObject obj = response.getJSONObject(i);
//                        VisitorData visitorData = new VisitorData();
//                        SetJSONObjectAsVistorData(visitorData, obj);
////                        visitorData.setName(obj.getString("visitor_name"));
////                        name = visitorData.getName();
////                        visitorData.setThumbnailURL(obj
////                                .getString("picture_path"));
////                        thumbnail = visitorData.getThumbnailURL();
////                        visitorData.setPurpose((obj.getString("purpose")));
////                        purpose = visitorData.getPurpose();
////
////                        visitorData.setTime(obj
////                                .getString("visit_time"));
////                        time = visitorData.getTime();
////
////                        // Genre is json array
////                        // JSONArray genreArry =
////                        // obj.getJSONArray("genre");
////                        ArrayList<String> genre = new ArrayList<String>();
////
////                        genre.add(obj.getString("visitor_number"));
////                        phone = obj.getString("visitor_phone");
////
////                        visitorData.setVisitorID(phone);
//
//                        // adding movie to movies array
//                        mVisitListData.add(visitorData);
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                // notifying list adapter about data changes
//                // so that it renders the list view with updated data
//                mVisitorAdapter.notifyDataSetChanged();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                hidePDialog();
//
//            }
//        });
//
//        AppController.getInstance().addToRequestQueue(logReq);
        SharedPreferences preferences=getSharedPreferences("activity_left_slide_menu", 0);
        pfName=preferences.getString("pfName"," ");
        Log.d("preference","PfName"+pfName);
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

    private static void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    private void UpdateVisitorList() {

        UpdateVisitorListEx(API_VIEW_VISITOR_URL, mVisitListData, mVisitorAdapter);
    }

    public static void UpdateVisitorListEx(String API, final List<VisitorData> visitorListDatas, final BaseAdapter adapter)
    {
        //String viewVisitorURL = GetServerAPIURL(API_VIEW_VISITOR_URL);
        String viewVisitorURL = GetServerAPIURL(API);
        //String a_URL = "http://alpha.jiams.kr:3301/sb/api/viewlog";

        //logReq = new JsonArrayRequest("http://210.121.154.236/node/smartbelltest/viewlog",new Response.Listener<JSONArray>() {
        JsonArrayRequest req = new JsonArrayRequest(viewVisitorURL ,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                hidePDialog();

                visitorListDatas.clear();

                //Toast.makeText(getApplicationContext(), "파싱 포문 전", Toast.LENGTH_SHORT).show();

                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject obj = response.getJSONObject(i);
                        VisitorData visitorData = new VisitorData();
                        AppController.SetJSONObjectAsVistorData(visitorData, obj);

                        // adding movie to movies array
                        visitorListDatas.add(visitorData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        AppController.getInstance().addToRequestQueue(req);
    }

    // 서버로 부터 방문자 리스트를 가져와서
    // 리스트 뷰의 기존 데이터를 지우고 새 데이터로 업데이트 한다.
//    private void updateList(JsonArrayRequest visitReq,
//                            final List<VisitorData> visitorDataList) {
//
//        // 로딩 다이얼로그 출력
//        pDialog = new ProgressDialog(this);
//        // Showing progress dialog before making http request
//        pDialog.setMessage("Loading...");
//        pDialog.show();
//
//
//        // 기존 데이터 삭제
//        visitorDataList.clear();
//        // Creating volley request obj
//
//
//        visitReq = new JsonArrayRequest(vistorRequstURL,new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                Log.d(TAG, response.toString());
//                hidePDialog();
//
//                // Parsing json
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//
//                        JSONObject obj = response.getJSONObject(i);
//                        VisitorData visitorData = new VisitorData();
//                        SetJSONObjectAsVistorData(visitorData, obj);
////                        visitorData.setName(obj.getString("visitor_name"));
////                        name = visitorData.getName();
////                        visitorData.setThumbnailURL(obj
////                                .getString("picture_path"));
////                        thumbnail = visitorData.getThumbnailURL();
////                        visitorData.setPurpose((obj.getString("purpose")));
////                        purpose = visitorData.getPurpose();
////
////                        visitorData.setTime(obj
////                                .getString("visit_time"));
////                        time = visitorData.getTime();
////
////                        // Genre is json array
////                        // JSONArray genreArry =
////                        // obj.getJSONArray("genre");
////                        ArrayList<String> genre = new ArrayList<String>();
////
////                        genre.add(obj.getString("visitor_number"));
////                        phone = obj.getString("visitor_phone");
////
////                        visitorData.setVisitorID(phone);
//
//                        // adding movie to movies array
//                        mVisitListData.add(visitorData);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                // notifying list adapter about data changes
//                // so that it renders the list view with updated data
//                mVisitorAdapter.notifyDataSetChanged();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                hidePDialog();
//
//            }
//        });
//
//        AppController.getInstance().addToRequestQueue(visitReq);
//
//    }// end of updateList


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
                Log.d("test", "at click");
                videoScreen.setVisibility(View.VISIBLE);
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
        videoScreen.setVisibility(View.INVISIBLE);
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

    public static String GetServerAPIURL(String apiURL)
    {
        return SERVER_URL + apiURL;
    }

//    //서버로 상태 변경 요청을 전송하는 메소드.
//    private void changeDeviceStatus(String newStatus) {
//
//        String URL = GetServerAPIURL(API_CHANGE_STATUS_URL);
//
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put(SERVER_PARAM_DEVICE_NO,TEMP_DEVICE_NO);
//        params.put(SERVER_PARAM_NEW_STATUS,newStatus);
//
//        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public  void onResponse(JSONObject response) {
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                }
//        });
//    }

    public void changeDeviceStatus(String newStatus) {

        String URL = String.format("%s", MainActivity.GetServerAPIURL(MainActivity.API_CHANGE_STATUS_URL));
        URL = URL + String.format("?%s=%s", MainActivity.SERVER_PARAM_DEVICE_NO, TEMP_DEVICE_NO);
        URL = URL + String.format("&%s=%s", MainActivity.SERVER_PARAM_NEW_STATUS, newStatus);
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
        };
        AppController.getInstance().addToRequestQueue(req);
    }

//    private void changeDeviceStatus(String newStatus){
//
//        HttpClient http = new DefaultHttpClient();
//        try{
//
//            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//
//            nameValuePairs.add(new BasicNameValuePair( SERVER_PARAM_DEVICE_NO , TEMP_DEVICE_NO ));
//            nameValuePairs.add(new BasicNameValuePair( SERVER_PARAM_NEW_STATUS , newStatus ));
//
//            HttpParams params = http.getParams();
//            HttpConnectionParams.setConnectionTimeout(params, 10000);
//            HttpConnectionParams.setSoTimeout(params, 10000);
//
//            HttpPost httpPost = new HttpPost(CHANGE_STATUS_URL);
//            UrlEncodedFormEntity entityRequest =
//                    new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
//
//            httpPost.setEntity(entityRequest);
//
//            HttpResponse responsePost = http.execute(httpPost);
//            HttpEntity resEntity = responsePost.getEntity();
//
//
//        }catch(Exception e){e.printStackTrace();}
//
//
//
//    }//end of changeDeviceStatus

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
