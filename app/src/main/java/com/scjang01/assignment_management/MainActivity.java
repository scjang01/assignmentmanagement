package com.scjang01.assignment_management;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scjang01.assignment_management.model.AssignmentItem;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. JSON 데이터 로드
        List<AssignmentItem> assignmentList = loadAssignmentsFromJson();

        // 2. RecyclerView 설정
        RecyclerView recyclerView = findViewById(R.id.rv_assignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // 3. Adapter 연결
        AssignmentAdapter adapter = new AssignmentAdapter(assignmentList);
        recyclerView.setAdapter(adapter);
    }

    private List<AssignmentItem> loadAssignmentsFromJson() {
        String json = null;
        try {
            InputStream is = getAssets().open("assignments.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<List<AssignmentItem>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}