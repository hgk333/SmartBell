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

public class VisitListAdpater extends BaseAdapter  {
    private Activity activity;
    private LayoutInflater inflater;
    private List<VisitorData> visitorDatas;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public VisitListAdpater(Activity activity, List<VisitorData> visitorDatas) {
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
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);

        // getting movie data for the row
        VisitorData m = visitorDatas.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailURL(), imageLoader);

        // title
        title.setText(m.getName());

        // rating
        rating.setText(/*"Rating: " +*/ String.valueOf(m.getPurpose()));

        // genre
        String genreStr = "";
//        for (String str : m.getVisitorID()) {
//            genreStr += str + ", ";
//        }
//        genreStr = genreStr.length() > 0 ? genreStr.substring(0,
//                genreStr.length() - 2) : genreStr;
        genre.setText(m.getVisitorID());

        // release year
        year.setText(String.valueOf(m.getTime()));

        //이벵트 등록크큭
        convertView.setOnClickListener(getListener(m));


        return convertView;
    }

    public OnClickListener getListener ( VisitorData m){

        OnClickListener listener = new Listener(this.activity, m);

        return listener;


    }//end of OnClickListnener

    class Listener implements OnClickListener {


        VisitorData m;
        Activity a;


        public Listener (Activity a ,VisitorData m){

            this.m = m;
            this.a =a;

        }

        public void onClick(View v) {

            // TODO Auto-generated method stub
            //Toast.makeText(getApplicationContext(), "good!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent( this.a , PopUpInfo.class);

            intent.putExtra("name", m.getName() ); //title
            intent.putExtra("purpose", m.getPurpose()); //rating
            intent.putExtra("visit_time", m.getTime()); //year
            intent.putExtra("thumbnail", m.getThumbnailURL()); //thumbnail
            intent.putExtra("phone", m.getVisitorID()); //genre

            a.startActivity(intent);


        }

    }//end of OnClickListener

}