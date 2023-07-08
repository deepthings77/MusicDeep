package com.example.musicdeep;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> readStoragePermissionLauncher;
    ListView listView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        readStoragePermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                        new ActivityResultCallback<Boolean>() {
                            @Override
                            public void onActivityResult(Boolean granted) {

                                if (granted) {
                                        Toast.makeText(MainActivity.this, "WelCome to MusicDeep\n Go Deep into Music", Toast.LENGTH_SHORT).show();
                                    // Permission granted, proceed with your code
                                    ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());

                                    String [] items = new String[mySongs.size()];
                                    for(int i=0;i<mySongs.size();i++){
                                        items[i] = mySongs.get(i).getName().replace(".mp3", "");
                                    }


                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                                    listView.setAdapter(adapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(MainActivity.this, PlaySong.class);
                                            String currentSong = listView.getItemAtPosition(position).toString();
                                            intent.putExtra("songList", mySongs);
                                            intent.putExtra("currentSong", currentSong);
                                            intent.putExtra("position", position);
                                            startActivity(intent);

                                        }
                                    });
                                }
                                else {
                                    // Permission denied, show a message to the user
//                                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                                    readStoragePermissionLauncher.launch(READ_EXTERNAL_STORAGE);

                                }
                            }
                        });

        readStoragePermissionLauncher.launch(READ_EXTERNAL_STORAGE);

    }
   public ArrayList<File> fetchSongs(File file)
    {
        ArrayList arrayList = new ArrayList();
        File [] songs = file.listFiles();
        if(songs !=null){
            for(File myFile: songs){
                if(!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll(fetchSongs(myFile));
                }
                else{
                    if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
                        arrayList.add(myFile);
                    }
                }
            }

        }
        return arrayList;
}}
