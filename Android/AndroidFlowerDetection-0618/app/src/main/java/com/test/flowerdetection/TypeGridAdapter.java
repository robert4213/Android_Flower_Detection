package com.test.flowerdetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TypeGridAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    ArrayList<byte[]> SubImagesList;
    ArrayList<String> Categories;

    public TypeGridAdapter(Context context, int layout, ArrayList<byte[]> ImagesList, ArrayList<String> Categories) {
        this.context = context;
        this.layout = layout;
        this.SubImagesList = ImagesList;
        this.Categories = Categories;
    }

    @Override
    public int getCount() {
        return Categories.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtName = (TextView) row.findViewById(R.id.sub_type);
            holder.imageView = (ImageView) row.findViewById(R.id.sub_icon);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.txtName.setText(Categories.get(position));

        byte[] b = SubImagesList.get(position);

        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);

        Bitmap image = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
        holder.imageView.setImageBitmap(image);

        return row;
    }

}
