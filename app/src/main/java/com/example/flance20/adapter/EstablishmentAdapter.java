package com.example.flance20.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flance20.BookingPage;
import com.example.flance20.R;
import com.example.flance20.model.EstablishmentsMain;

import java.io.InputStream;
import java.util.List;

public class EstablishmentAdapter extends RecyclerView.Adapter<EstablishmentAdapter.EstablishmentViewHolder> {
// Адаптер для прогрузки кафешек на главной
    Context context;
    List<EstablishmentsMain> establishments;
    // конструктор
    public EstablishmentAdapter(Context context, List<EstablishmentsMain> establishments) {
        this.context = context;
        this.establishments = establishments;
    }

    @NonNull
    @Override
    public EstablishmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View establishmentItem = LayoutInflater.from(context).inflate(R.layout.establishment_item, parent,false);
        return new EstablishmentAdapter.EstablishmentViewHolder(establishmentItem);
    }

    // Загрузка картинок по url
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    // Установка View'сов для карточки + onClickListener
    @Override
    public void onBindViewHolder(@NonNull EstablishmentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        System.out.println(establishments.get(position).getUrl_preview_img());
        new DownloadImageTask(holder.establishment_img).execute(establishments.get(position).getUrl_preview_img());
        holder.establishment_name.setText(establishments.get(position).getName());
        holder.establishment_address.setText(establishments.get(position).getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BookingPage.class);
                intent.putExtra("id", establishments.get(position).getId());
                intent.putExtra("name", establishments.get(position).getName());
                intent.putExtra("context", "Main");
                context.startActivity(intent);
            }
        });
    }
    // кол-во карточек
    @Override
    public int getItemCount() {
        return establishments.size();
    }
    // Инициализация View'ов
    public static final class EstablishmentViewHolder extends RecyclerView.ViewHolder{
        ImageView establishment_img;
        TextView establishment_name, establishment_address;

        public EstablishmentViewHolder(@NonNull View itemView) {
            super(itemView);
            establishment_img = itemView.findViewById(R.id.preview_img);
            establishment_name = itemView.findViewById(R.id.name);
            establishment_address = itemView.findViewById(R.id.addressText);
        }
    }
}