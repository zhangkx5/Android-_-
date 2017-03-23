package com.example.kaixin.final_experiment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private ArrayList<NoteItem> note_list;
    private LayoutInflater mInflater;

    public interface OnItemClickListener {
        void onItemClick(View view, int position, NoteItem item);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickLitener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view, int position, NoteItem item);
    }

    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public NoteAdapter(Context context, ArrayList<NoteItem> items) {
        super();
        note_list = items;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.note_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        holder.Content = (TextView) view.findViewById(R.id.note_content);
        holder.Time =(TextView) view.findViewById(R.id.note_time);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.Content.setText(note_list.get(i).getContent());
        viewHolder.Time.setText(note_list.get(i).getTime());

        if (mOnItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(viewHolder.itemView, i, note_list.get(i));
                }
            });
        }

        if(mOnItemLongClickListener != null){
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemLongClickListener.onItemLongClick(viewHolder.itemView, i, note_list.get(i));
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return note_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
        TextView Content;
        TextView Time;
    }

}

