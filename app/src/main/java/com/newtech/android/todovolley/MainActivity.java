package com.newtech.android.todovolley;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String URL_ROOT = "https://5fd06c4e1f237400166318f5.mockapi.io/";
    RecyclerView mRecyclerTodo;
    Button btnSave, btnCancel;
    EditText edtTodo;
    TodoAdapter mTodoAdapter;
    private List<Todo> mTodos;
    private Todo todoSelected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mappingView();
        getData();
        setEventForButton();
    }

    private void setEventForButton() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edtTodo.getText())) {
                    String todoStr = edtTodo.getText().toString();
                    if (todoSelected != null) {
                        todoSelected.setName(todoStr);
                    } else {
                        todoSelected = new Todo(todoStr);
                    }
                    saveTodo(todoSelected);
                }
            }
        });
    }

    private void showTodo(Todo todo) {
        edtTodo.setText(todo.getName());
        todoSelected = todo;
    }

    private void saveTodo(Todo todo) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        StringBuffer url = new StringBuffer(URL_ROOT + "/todos");
        int method = Request.Method.POST;
        if (todo.getId() != null) {
            url.append("/" + todo.getId());
            method = Request.Method.PUT;
        }
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(method, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getData();
                todoSelected = null;
                edtTodo.setText("");
                Toast.makeText(MainActivity.this, "Thêm " + todo.getName() + " thành công", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("name", todo.getName());
                param.put("isComplete", todo.getIsComplete() + "");
                return param;
            }
        };
        queue.add(stringRequest);
    }

    private void initAdapter(List<Todo> todos) {
        if (mTodoAdapter == null) {
            mTodoAdapter = new TodoAdapter(this, todos);
            mRecyclerTodo.setAdapter(mTodoAdapter);
        } else {
            mTodoAdapter.setTodos(todos);
        }
        setEventForAdapter();
    }

    private void setEventForAdapter() {
        mTodoAdapter.setItemClickListener(new TodoAdapter.ItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                switch (view.getId()) {
                    case R.id.button_edit:
                        showTodo(mTodos.get(pos));
                        break;
                    case R.id.button_delete:
                        confirmDeleteTodo(mTodos.get(pos));
                        break;
                    default:
                        return;
                }
            }
        });
    }

    private void confirmDeleteTodo(Todo todo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Thông báo xác nhận")
                .setMessage("Xác nhận xoá '"+todo.getName()+"' ?")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTodo(todo);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Huỷ bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void deleteTodo(Todo todo) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL_ROOT + "/todos/"+todo.getId();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getData();
                Toast.makeText(MainActivity.this, "Xoá thành công", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Xoá thất bại: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private void mappingView() {
        mRecyclerTodo = findViewById(R.id.recycler_todo);
        mRecyclerTodo.setLayoutManager(new LinearLayoutManager(this));
        btnCancel = findViewById(R.id.button_save);
        btnSave = findViewById(R.id.button_save);

        edtTodo = findViewById(R.id.input_todo);
    }

    private void getData() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL_ROOT + "/todos";
        Gson gson = new Gson();
        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Type type = new TypeToken<List<Todo>>() {
                }.getType();
                mTodos = gson.fromJson(String.valueOf(response), type);
                initAdapter(mTodos);
                Log.d("TAG", mTodos.get(0).getName());
                Toast.makeText(MainActivity.this, "Results: " + mTodos.size(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", error.getMessage());
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonArrayRequest);
    }
}