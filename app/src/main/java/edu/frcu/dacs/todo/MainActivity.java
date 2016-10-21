package edu.frcu.dacs.todo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import edu.frcu.dacs.todo.models.ToDoEntry;
import edu.frcu.dacs.todo.util.RequestSingleton;

public class MainActivity extends AppCompatActivity {
    public final static int NUEVATODO = 0;
    public final static int VERTODO = 1;
    ArrayAdapter<ToDoEntry> adapter;
    ListView listview;

    public static int STORAGEMODE = R.id.rbLocalBD;
    public static String FILENAME = "todo_file";

    private static String host = "http://192.168.8.212/TODO/api/";
    private static String obtener_todas = "tareas/todas";
    private static String nueva_tarea = "tareas/nueva";

    ArrayList<ToDoEntry> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Cargar settings
        SharedPreferences settings = getApplicationContext().getSharedPreferences("preferencesfile", Context.MODE_PRIVATE);

        if (settings.getString("TOKEN", null) == null){
            Intent myIntent = new Intent(getBaseContext(), LoginActivity.class);
            startActivityForResult(myIntent, 0);
            finish();
        }else {
            SharedPreferences setting = getPreferences(MODE_PRIVATE);
            MainActivity.STORAGEMODE = setting.getInt("storageMode", R.id.rbLocalBD);

            RadioButton rbSelected = (RadioButton) findViewById(MainActivity.STORAGEMODE);
            rbSelected.setChecked(true);

            listview = (ListView) findViewById(R.id.listview);
            final Button bAgregar = (Button) findViewById(R.id.bAdd);
            final Button bRefresh = (Button) findViewById(R.id.bRefresh);
            final Button bLogout = (Button) findViewById(R.id.bLogout);

            adapter = new ArrayAdapter(getApplicationContext(), R.layout.todo_layout, R.id.lTodoTitulo, list);
            listview.setAdapter(adapter);

            cargarListadoDeTareas();

            RequestSingleton.getInstance(getApplicationContext()).getRequestQueue().start();
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    final ToDoEntry item = (ToDoEntry) parent.getItemAtPosition(position);
                    verToDo(view, item);
                }
            });

            bAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nuevaToDo(v);
                }
            });

            bRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.clear();
                    adapter.notifyDataSetChanged();

                    cargarListadoDeTareas();
                }
            });

            final RadioGroup rgStorageMode = (RadioGroup) findViewById(R.id.rgStorageMode);
            rgStorageMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    MainActivity.STORAGEMODE = checkedId;

                    list.clear();
                    adapter.notifyDataSetChanged();

                    cargarListadoDeTareas();
                }
            });

            final RadioButton rbLocalBD = (RadioButton) findViewById(R.id.rbLocalBD);
            rbLocalBD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RadioButton) v).setChecked(true);
                }
            });
            final RadioButton rbLocalArchivo = (RadioButton) findViewById(R.id.rbLocalArchivo);
            rbLocalArchivo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RadioButton) v).setChecked(true);
                }
            });
            final RadioButton rbRemoto = (RadioButton) findViewById(R.id.rbRemoto);
            rbRemoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RadioButton) v).setChecked(true);
                }
            });
            bLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences settings = getApplicationContext().getSharedPreferences("preferencesfile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("TOKEN", null);
                    editor.commit();

                    Intent myIntent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();

                }
            });
        }
    }

    private void cargarListadoDeTareas(){
        switch (MainActivity.STORAGEMODE){
            case R.id.rbRemoto:
                cargarListado();
                break;
            case R.id.rbLocalArchivo:
                cargarListadoLocalArchivo();
                break;
            case R.id.rbLocalBD:
                cargarListadoLocalBD();
                break;
        }
    }

    private void cargarListado() {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, String.format("%s%s", host, obtener_todas), null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        JsonParser parser = new JsonParser();
                        JsonElement mJson =  parser.parse(response.toString());
                        Gson gson = new Gson();

                        Type collectionType = new TypeToken<List<ToDoEntry>>() {}.getType();

                        List<ToDoEntry> entries = gson.fromJson(mJson, collectionType);

                        for (ToDoEntry todo : entries) {
                            list.add(todo);
                        }
                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getApplicationContext(), "Error Sending the request", Toast.LENGTH_LONG).show();
                    }
                });

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        RequestSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest, settings.getString("TOKEN", null));
    }

    private void cargarListadoLocalBD() {
        List<ToDoEntry> entries = ToDoEntry.listAll(ToDoEntry.class);

        for (ToDoEntry todo : entries) {
            list.add(todo);
        }
        adapter.notifyDataSetChanged();
    }

    private void cargarListadoLocalArchivo() {
        try {
            FileInputStream fileIn = openFileInput(MainActivity.FILENAME);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            ArrayList<ToDoEntry> entries = (ArrayList<ToDoEntry>) in.readObject();

            in.close();
            fileIn.close();

            for (ToDoEntry todo : entries) {
                list.add(todo);
            }

            adapter.notifyDataSetChanged();
        }catch (EOFException e){
            Toast.makeText(getApplicationContext(), "Fin de Archivo", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Exception cargando Tareas desde Archivo", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void nuevaToDo(View v){
        Intent i = new Intent(this, NewTodoActivity.class);
        i.putExtra("VERTODO", NUEVATODO);
        startActivityForResult(i, NUEVATODO);
    }

    public void verToDo(View v, ToDoEntry entry){
        Intent i = new Intent(this, NewTodoActivity.class);
        i.putExtra("TITULO", entry.getTitulo());
        i.putExtra("DESCRIPCION", entry.getDescripcion());
        i.putExtra("VERTODO", VERTODO);

        startActivity(i);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Creacion cancelada", Toast.LENGTH_SHORT).show();
        } else {
            String titulo = data.getExtras().getString("TITULO");
            String descripcion = data.getExtras().getString("DESCRIPCION");

            ToDoEntry todo = new ToDoEntry(titulo, descripcion);
            list.add(todo);
            adapter.notifyDataSetChanged();

            switch (MainActivity.STORAGEMODE){
                case R.id.rbRemoto:
                    enviarNuevoToDo(todo);
                    break;
                case R.id.rbLocalArchivo:
                    guardarNuevoToDoLocalArchivo(todo);
                    break;
                case R.id.rbLocalBD:
                    guardarNuevoToDoLocalBD(todo);
                    break;
            }
        }
    }

    private void guardarNuevoToDoLocalArchivo(ToDoEntry todo) {

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(MainActivity.FILENAME, MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(list);
            out.close();
            fos.close();

            Toast.makeText(getApplicationContext(), "Tarea guardada en Archivo", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception Guardando Tarea en Archivo", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void guardarNuevoToDoLocalBD(ToDoEntry todo) {
        todo.save();
        Toast.makeText(getApplicationContext(), "Tarea guardada en Base de Datos", Toast.LENGTH_LONG).show();
    }

    private void enviarNuevoToDo(ToDoEntry todo) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(todo);
        try {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, String.format("%s%s", host, nueva_tarea), new JSONObject(jsonString), new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Tarea guardada en Servidor", Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Toast.makeText(getApplicationContext(), "Error de comunicacion", Toast.LENGTH_LONG).show();
                        }
                    });

            SharedPreferences settings = getPreferences(MODE_PRIVATE);
            RequestSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest, settings.getString("TOKEN", null));
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Ocurrion un error al preparar el request", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("storageMode", MainActivity.STORAGEMODE);

        // Commit the edits!
        editor.commit();
    }
}
