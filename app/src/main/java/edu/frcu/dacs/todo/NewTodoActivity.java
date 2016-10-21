package edu.frcu.dacs.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.frcu.dacs.todo.models.ToDoEntry;

public class NewTodoActivity extends AppCompatActivity {

    Button bAceptar, bCancelar;
    EditText tTitulo, tDescripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_todo);

        bAceptar = (Button)findViewById(R.id.btAceptar);
        bCancelar = (Button)findViewById(R.id.btCancelar);
        tTitulo = (EditText)findViewById(R.id.tTitulo);
        tDescripcion = (EditText)findViewById(R.id.tDescripcion);
        Intent i = getIntent();
        int verTodo = i.getExtras().getInt("VERTODO");
        if(verTodo == MainActivity.VERTODO){
            bAceptar.setVisibility(View.INVISIBLE);
            bCancelar.setText(R.string.b_cerrar);
            tTitulo.setEnabled(false);
            tDescripcion.setEnabled(false);
            tTitulo.setText(i.getExtras().getString("TITULO"));
            tDescripcion.setText(i.getExtras().getString("DESCRIPCION"));
        }else{
            bAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tTitulo.getText().length() > 0){
                        ToDoEntry todo = new ToDoEntry(tTitulo.getText().toString(), tDescripcion.getText().toString());
                        Intent i = getIntent();
                        i.putExtra("TITULO", tTitulo.getText().toString());
                        i.putExtra("DESCRIPCION", tDescripcion.getText().toString());
                        setResult(RESULT_OK, i);

                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), R.string.titulo_vacio, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        bCancelar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
