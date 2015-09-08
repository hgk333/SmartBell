package com.teamif.smartbell;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
    TextView tvSettingName;
    TextView tvSettingAddress;
    TextView tvSettingEmail;
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
    Button btEditInfoOfPro;

    String pfName;
    String pfAddress;
    String pfEmail;
    AudioManager checkAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_slide_menu);

        stateOfAram = false;

        tvName = (TextView)findViewById(R.id.tvName); //교수이름 입력받아 바꾸기위한 변수
        tvProfessor = (TextView)findViewById(R.id.tvProfessor); // 페이지이동
        tvFeedback = (TextView)findViewById(R.id.tvFeedback); //페이지이동
        tvSetting = (TextView)findViewById(R.id.tvSetting); //페이지이동
        tvSettingName = (TextView)findViewById(R.id.tvSettingName);
        tvSettingAddress = (TextView)findViewById(R.id.tvSettingAddress);
        tvSettingEmail = (TextView)findViewById(R.id.tvSettingEmail);
        LLPro = (LinearLayout)findViewById(R.id.LLPro);
        LLBase = (LinearLayout)findViewById(R.id.LLbase);
        ivBackPro = (ImageView)findViewById(R.id.ivBackPro);
        ivBackPro2 = (ImageView)findViewById(R.id.ivBackPro2);
        ivBackPro3 = (ImageView)findViewById(R.id.ivBackPro3);
        LLFeed = (LinearLayout)findViewById(R.id.LLFeedback);
        LLSetting = (LinearLayout)findViewById(R.id.LLSetting);
        swAram = (Switch)findViewById(R.id.swAram);
        swEti = (Switch)findViewById(R.id.swEti);
        btEditInfoOfPro = (Button)findViewById(R.id.btEditInfoOfPro);

        tvProfessor.setOnClickListener(this);
        tvFeedback.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        ivBackPro.setOnClickListener(this);
        ivBackPro2.setOnClickListener(this);
        ivBackPro3.setOnClickListener(this);
        swAram.setOnCheckedChangeListener(this);
        swEti.setOnCheckedChangeListener(this);
        btEditInfoOfPro.setOnClickListener(this);

        checkAudio = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
        switch (checkAudio.getRingerMode()) {
            case AudioManager.RINGER_MODE_VIBRATE:
            case AudioManager.RINGER_MODE_SILENT:
                //진동 혹은 무음
                swEti.setChecked(true);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                //소리
                swEti.setChecked(false);
                break;
        }
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
            case R.id.btEditInfoOfPro :
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
                dialog1.setTitle("교수님 성함");
                dialog1.setMessage("이름을 입력하세요");
                // Set an EditText view to get user input
                final EditText etInput1 = new EditText(this);
                dialog1.setView(etInput1);
                AlertDialog.Builder builder = dialog1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        pfName = etInput1.getText().toString();
                        // Do something with value!
                        tvName.setText(pfName);
                        tvSettingName.setText(pfName);
                        //add to code for send the information to server
                        AlertDialog.Builder dialog2 = new AlertDialog.Builder(LeftSlideMenu.this);
                        dialog2.setTitle("교수님 메일");
                        dialog2.setMessage("메일주소를 입력하세요");
                        // Set an EditText view to get user input
                        final EditText etInput2 = new EditText(LeftSlideMenu.this);
                        dialog2.setView(etInput2);
                        dialog2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                pfEmail = etInput2.getText().toString();
                                tvSettingEmail.setText(pfEmail);
                                //add to code for sending the information to server
                                AlertDialog.Builder alert = new AlertDialog.Builder(LeftSlideMenu.this);
                                alert.setTitle("교수님 연구실");
                                alert.setMessage("연구실 주소를 입력하세요");
                                // Set an EditText view to get user input
                                final EditText etInput3 = new EditText(LeftSlideMenu.this);
                                alert.setView(etInput3);
                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        pfAddress = etInput3.getText().toString();
                                        tvSettingAddress.setText(pfAddress);
                                        //add to code for sending the information to server
                                    }
                                });
                                alert.setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                // Canceled.
                                            }
                                        });
                                alert.show();
                            }
                        });
                        dialog2.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Canceled.
                                    }
                                });
                        dialog2.show();
                    }
                });
                dialog1.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });
                dialog1.show();
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
                if(b == false) { // setting when eti switch is on
                    Log.d("switch ", "eti on");
                    Toast.makeText(this,"에티켓 On",Toast.LENGTH_SHORT).show();
                    checkAudio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                }
                else{ // setting when eti switch is off
                    Log.d("switch " ,"eti of");
                    Toast.makeText(this,"에티켓 off", Toast.LENGTH_SHORT).show();
                    checkAudio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
                break;
        }
    }
}

