package com.teamif.smartbell;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class FeedbackListAdpater extends BaseAdapter  {
    private Activity activity;
    private LayoutInflater inflater;
    private List<VisitorData> visitorDatas;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public FeedbackListAdpater(Activity activity, List<VisitorData> visitorDatas) {
        this.activity = activity;
        this.visitorDatas = visitorDatas;
    }

    @Override
    public int getCount() {
        return visitorDatas.size();
    }

    @Override
    public Object getItem(int location) {
        return visitorDatas.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feedback_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView tv_name = (TextView) convertView.findViewById(R.id.fb_name);
        TextView tv_purpose = (TextView) convertView.findViewById(R.id.fb_purpose);
        TextView tv_visit_time = (TextView) convertView.findViewById(R.id.fb_visit_time);
        TextView tv_feedback = (TextView) convertView.findViewById(R.id.fb_feedback);

        // getting movie data for the row
        VisitorData m = visitorDatas.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailURL(), imageLoader);

        tv_name.setText(m.getName());

        tv_purpose.setText(/*"Rating: " +*/ String.valueOf(m.getPurpose()));

        tv_feedback.setText(m.getFeedback());

        tv_visit_time.setText(String.valueOf(m.getTime()));

        return convertView;
    }

//    public OnClickListener getListener ( VisitorData m){
//
//        OnClickListener listener = new Listener(this.activity, m);
//
//        return listener;
//
//
//    }//end of OnClickListnener

//    class Listener implements OnClickListener {
//
//
//        VisitorData m;
//        Activity a;
//
//
//        public Listener (Activity a ,VisitorData m){
//
//            this.m = m;
//            this.a =a;
//
//        }
//
//        public void onClick(View v) {
//
//            // TODO Auto-generated method stub
//            //Toast.makeText(getApplicationContext(), "good!", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent( this.a , PopUpInfo.class);
//
//            intent.putExtra("seq_no", m.getVisitorSeqNo() ); //seq_no
//            intent.putExtra("name", m.getName() ); //title
//            intent.putExtra("purpose", m.getPurpose()); //rating
//            intent.putExtra("visit_time", m.getTime()); //year
//            intent.putExtra("thumbnail", m.getThumbnailURL()); //thumbnail
//            intent.putExtra("phone", m.getVisitorID()); //genre
//            intent.putExtra("feedback", m.getFeedback()); //genre
//
//            a.startActivity(intent);
//
//
//        }
//
//    }//end of OnClickListener

}