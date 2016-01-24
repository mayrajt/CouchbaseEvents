package com.jaramillomayra.couchbaseevents;

        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.util.*;
        import android.view.View;
        import android.widget.Button;

        import com.couchbase.lite.*;
        import com.couchbase.lite.android.AndroidContext;

        import java.io.ByteArrayInputStream;
        import java.io.IOException;
        import java.util.HashMap;
        import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final String TAG = "CouchbaseEvents";
    public static final String DB_NAME = "couchbaseevents";
    Manager manager = null;
    Database database = null;
    Button boton1, boton2, boton3, boton4;
    String docId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        boton1 = (Button) findViewById(R.id.crearDoc);
        boton2 = (Button) findViewById(R.id.actualizaDoc);
        boton3 = (Button) findViewById(R.id.consultaDoc);
        boton4 = (Button) findViewById(R.id.eliminaDoc);

        HelloCBL();

        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docId = createDocument(database);
            }
        });

        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDoc(database, docId);
            }
        });
        boton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveDocument(docId);
            }
        });

        boton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delteDocument(docId);
            }
        });

    }

    public void HelloCBL() {
        try {
            manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            database = manager.getDatabase(DB_NAME);
            Log.d(TAG, "Base de datos creada correctamente " + database.getName());
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener la base de datos", e);
            return;
        }

        // String documentId = createDocument(database);
        // updateDoc(database, documentId);
        // retrieveDocument(documentId);

    }

    public String createDocument(Database database) {
        Document document = database.createDocument();
        String documentId = document.getId();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("nombre", "Gran Fiesta");
        map.put("locacion", "Mi casa");
        try {
            document.putProperties(map);
            Log.d(TAG, "Documento escrito en la base de datos con el ID =" + document.getId());
        } catch (CouchbaseLiteException ex) {
            Log.e(TAG, "Error escribiendo en la base de datos", ex);
        }
        return documentId;
    }

    public Database getDatabaseInstance() throws CouchbaseLiteException {
        if ((this.database == null) & (this.manager != null)) {
            this.database = manager.getDatabase(DB_NAME);
        }
        return database;
    }

    public Manager getManagerInstance() throws IOException {
        if (manager == null) {
            manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
        }
        return manager;
    }

    Document retrieveDocument(String DocID) {
        Document retrieveDoc = database.getDocument(DocID);
        Log.d(TAG, "Cargando documento " + String.valueOf(retrieveDoc.getProperties()));
        return retrieveDoc;
    }

    public void updateDoc(Database database, String documentId) {

        Document document = database.getDocument(documentId);
        try {
            Map<String, Object> updateProperties = new HashMap<String, Object>();
            updateProperties.putAll(document.getProperties());
            updateProperties.put("descripcionEvento", "Todos estan invitados!");
            updateProperties.put("Direccion", "123 Elm St.");

            document.putProperties(updateProperties);
            Log.d(TAG, "Documento con el id " + documentId + " actalizado");
        } catch (Exception e) {

            Log.e(TAG, "Error escribiendo", e);
        }
    }

    public void delteDocument(String docId) {
        try {
            retrieveDocument(docId).delete();
            Log.d(TAG, "Documento eliminado, estatus = " + retrieveDocument(docId).isDeleted());
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "No se puede eliminar este documento", e);
        }

    }

    public void addAttachment(Database database, String docId) {
        Document document = database.getDocument(docId);
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{0, 0, 0, 0});
            UnsavedRevision revision = document.getCurrentRevision().createRevision();
            revision.setAttachment("binaryData", "application/octet-stream", inputStream);
            revision.save();
        } catch (Exception e) {
            Log.e(TAG, "Error escribiendo", e);
        }

    }
}
