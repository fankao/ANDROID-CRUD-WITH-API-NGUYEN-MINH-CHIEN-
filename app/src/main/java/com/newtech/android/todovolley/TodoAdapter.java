package com.newtech.android.todovolley;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.LocationViewHolder> {
    private Context context;
    private List<Todo> todos;
    private ItemClickListener itemClickListener;

    public  interface  ItemClickListener{
        void onClick(View view, int pos);
    }

    public TodoAdapter(Context context, List<Todo> todos) {
        this.context = context;
        this.todos = todos;
    }

    @NonNull
    @Override
    public TodoAdapter.LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_todo,parent,false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoAdapter.LocationViewHolder holder, int position) {
       holder.txtTodo.setText(todos.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
        this.notifyDataSetChanged();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTodo;
        ImageView btnEdit, btnDelete;
        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTodo = itemView.findViewById(R.id.text_todo);
            btnDelete = itemView.findViewById(R.id.button_delete);
            btnEdit = itemView.findViewById(R.id.button_edit);

            btnEdit.setOnClickListener(this);
            btnDelete.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition());
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
