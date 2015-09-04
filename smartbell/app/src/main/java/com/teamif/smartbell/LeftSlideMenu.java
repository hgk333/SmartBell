package com.teamif.smartbell;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class LeftSlideMenu extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    TextView tvName;
    TextView tvProfessor;
    TextView tvFeedback;
    TextView tvSetting;
    LinearLayout LLPro;
    LinearLayout LLBase;
    LinearLayout LLFeed;
    LinearLayout LLSetting;
    ImageView ivBackPro;
    ImageView ivBackPro2;
    ImageView ivBackPro3;
    boolean stateOfAram;
    Switch swAram;
    Switch swEti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_slide_menu);

        stateOfAram = false;

        tvName = (TextView)findViewById(R.id.tvName); //교수이름 입력받아 바꾸기위한 변수
        tvProfessor = (TextView)findViewById(R.id.tvProfessor); // 페이지이동
        tvFeedback = (TextView)findViewById(R.id.tvFeedback); //페이지이동
        tvSetting = (TextView)findViewById(R.id.tvSetting); //페이지이동
        LLPro = (LinearLayout)findViewById(R.id.LLPro);
        LLBase = (LinearLayout)findViewById(R.id.LLbase);
        ivBackPro = (ImageView)findViewById(R.id.ivBackPro);
        ivBackPro2 = (ImageView)findViewById(R.id.ivBackPro2);
        ivBackPro3 = (ImageView)findViewById(R.id.ivBackPro3);
        LLFeed = (LinearLayout)findViewById(R.id.LLFeedback);
        LLSetting = (LinearLayout)findViewById(R.id.LLSetting);
        swAram = (Switch)findViewById(R.id.swAram);
        swEti = (Switch)findViewById(R.id.swEti);

        tvProfessor.setOnClickListener(this);
        tvFeedback.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        ivBackPro.setOnClickListener(this);
        ivBackPro2.setOnClickListener(this);
        ivBackPro3.setOnClickListener(this);
        swAram.setOnCheckedChangeListener(this);
        swEti.setOnCheckedChangeListener(this);

        viewPage();
        LLBase.setVisibility(View.VISIBLE);
        //이까지함 연결중....!

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_left_slide_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.tvProfessor: // 교수님 페이지
                Log.d("touch check","Professor Page");
                viewPage();
                LLPro.setVisibility(View.VISIBLE);

                break;
            case R.id.tvFeedback : // feedbac page
                Log.d("touch check","feedback page");
                viewPage();
                LLFeed.setVisibility(View.VISIBLE);

                break;
            case R.id.tvSetting : // setting page
                Log.d("touch check","setting page");
                viewPage();
                LLSetting.setVisibility(View.VISIBLE);

                break;
            case R.id.ivBackPro :
            case R.id.ivBackPro2 :
            case R.id.ivBackPro3 :
                Log.d("touch check","backbutton"+view.getId());
                viewPage();
                LLBase.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void viewPage(){
        LLBase.setVisibility(View.INVISIBLE);
        LLPro.setVisibility(View.INVISIBLE);
        LLFeed.setVisibility(View.INVISIBLE);
        LLSetting.setVisibility(View.INVISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch(compoundButton.getId())
        {
            case R.id.swAram :
                if(b == true) { // setting when aram switch is on
                    Log.d("switch " ,"aram on");
                    Toast.makeText(this,"알람 On",Toast.LENGTH_SHORT).show();
                    stateOfAram = true;

                }
                else{ // setting when aramswitch is off
                    Log.d("switch " ,"aram off");
                    Toast.makeText(this,"알람 off", Toast.LENGTH_SHORT).show();
                    stateOfAram = false;
                }
                break;
            case R.id.swEti :
                if(stateOfAram == false){
                    swEti.setChecked(true);
                    Toast.makeText(this,"알람이 off인 상태에선 바꿀수 없습니다",Toast.LENGTH_SHORT).show();
                    break;
                }
                if(b == true) { // setting when eti switch is on
                    Log.d("switch " ,"eti on");
                    Toast.makeText(this,"에티켓 On",Toast.LENGTH_SHORT).show();

                }
                else{ // setting when eti switch is off
                    Log.d("switch " ,"eti of");
                    Toast.makeText(this,"에티켓 off", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

