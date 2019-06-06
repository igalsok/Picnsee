package com.igaltech.Picnsee;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.squareup.picasso.Picasso;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteHolder> {


private MainActivity activity;
private PhotoViewAttacher attacher;
    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, MainActivity activity) {
        super(options);
        this.activity = activity;


    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull Note model) {
        final Note note = model;
       Picasso.get().load(model.getUrl()).into(holder.img);
       holder.cardView.setOnClickListener(new View.OnClickListener(){

           @Override
           public void onClick(View view) {
               AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
               LayoutInflater inflater = LayoutInflater.from(activity);
               View newView = inflater.inflate(R.layout.full_photo, null);
               Picasso.get().load(note.getPhotoUrl()).into((PhotoView)newView.findViewById(R.id.fullPhoto));
               attacher = new PhotoViewAttacher((ImageView)newView.findViewById(R.id.fullPhoto));
               alertDialog.setView(newView);
               alertDialog.create();
               alertDialog.show();
           }
       });

    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row,viewGroup,false);
        return new NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder{
        ImageView img;
        CardView cardView;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
           img =  itemView.findViewById(R.id.feedImage);
           cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}
