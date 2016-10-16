package com.example.hk4774.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {

    ListView videolist;
    int count;
    String thumbPath;
    String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID};
    private Cursor videoCursor;
    private int videoColumnIndex;
    private TextView emptyText;
    private AdapterView.OnItemClickListener videogridlistener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            videoCursor.moveToPosition(position);
            String filename = videoCursor.getString(videoColumnIndex);
            Log.i("FileName: ", filename);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(filename));
            intent.setDataAndType(Uri.parse(filename), "video/mp4");
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialization();
    }

    private void initialization() {
        String[] videoProjection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE};
        videoCursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection, null, null, null);
        count = videoCursor.getCount();
        videolist = (ListView) findViewById(R.id.PhoneVideoList);
        emptyText = (TextView) findViewById(android.R.id.empty);
        videolist.setEmptyView(emptyText);
        videolist.setAdapter(new VideoListAdapter(this.getApplicationContext()));
        videolist.setOnItemClickListener(videogridlistener);
    }

    public class VideoListAdapter extends BaseAdapter {
        int layoutResourceId;
        private Context vContext;

        public VideoListAdapter(Context c) {
            vContext = c;
        }

        public int getCount() {
            return videoCursor.getCount();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemRow = null;
            listItemRow = LayoutInflater.from(vContext).inflate(R.layout.listitem, parent, false);

            TextView txtTitle = (TextView) listItemRow.findViewById(R.id.txtTitle);
            TextView txtSize = (TextView) listItemRow.findViewById(R.id.txtSize);
            ImageView thumbImage = (ImageView) listItemRow.findViewById(R.id.imgIcon);

            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            videoCursor.moveToPosition(position);
            txtTitle.setText(videoCursor.getString(videoColumnIndex));

            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            videoCursor.moveToPosition(position);
            txtSize.setText(" Size:" + getStringSizeLengthFile(Long.parseLong(videoCursor.getString(videoColumnIndex))));

            int videoId = videoCursor.getInt(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            Cursor videoThumbnailCursor = managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + videoId, null, null);

            if (videoThumbnailCursor.moveToFirst()) {
                thumbPath = videoThumbnailCursor.getString(videoThumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                Log.i("ThumbPath: ", thumbPath);
            }
            thumbImage.setImageURI(Uri.parse(thumbPath));

            return listItemRow;

        }

        public String getStringSizeLengthFile(long size) {

            DecimalFormat df = new DecimalFormat("0.00");


            float sizeKb = 1024.0f;
            float sizeMo = sizeKb * sizeKb;
            float sizeGo = sizeMo * sizeKb;
            float sizeTerra = sizeGo * sizeKb;


            if (size < sizeMo)
                return df.format(size / sizeKb) + " Kb";
            else if (size < sizeGo)
                return df.format(size / sizeMo) + " Mb";
            else if (size < sizeTerra)
                return df.format(size / sizeGo) + " Gb";

            return "";
        }
    }
}
