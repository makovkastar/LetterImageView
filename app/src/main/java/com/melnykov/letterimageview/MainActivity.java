package com.melnykov.letterimageview;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.melnykov.initialsimageview.R;


public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LetterImageView.init(this, R.array.colors, LetterImageView.SHAPE_OVAL);
        Adapter listAdapter = new Adapter(this);
        String[] names = getResources().getStringArray(R.array.names);
        for (String name : names)
            listAdapter.add(new Data(name));
        setListAdapter(listAdapter);
    }

    private static class Adapter extends ArrayAdapter<Data> {

        public Adapter(Context context) {
            super(context, R.layout.list_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Data data = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            LetterImageView letterImageView = (LetterImageView) convertView.findViewById(R.id.iv_avatar);
            TextView textView = (TextView) convertView.findViewById(R.id.tv_name);
            if (position % 2 == 0)
                letterImageView.setShape(LetterImageView.SHAPE_OVAL);
            else
                letterImageView.setShape(LetterImageView.SHAPE_RECTANGLE);
            // store current color
            if (data.color == 0)
                data.color = letterImageView.getShapeColor();
            letterImageView.setShapeColor(data.color);
            letterImageView.setLetter(data.text.charAt(0));
            textView.setText(data.text);

            return convertView;
        }
    }

    private static class Data {
        private int color;
        private String text;

        private Data(String text) {
            this.text = text;
        }
    }
}
