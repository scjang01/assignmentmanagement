package com.scjang01.assignment_management;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 메인 액티비티 클래스
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Edge-to-Edge 설정
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        //Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        findViewById(R.id.tv_count_in_progress);
        findViewById(R.id.tv_count_urgent);
        findViewById(R.id.tv_count_completed);
        findViewById(R.id.tv_count_missed);

        int countInprogress = 0;
        int countUrgent = 0;
        int countCompleted = 0;
        int countMissed = 0;

        List<AssignmentItem> items = loadAssignmentsFromJson();

        for (AssignmentItem item : items) {
            LocalDateTime now = null;
            DateTimeFormatter formatter = null;
            long hoursLeft = 0;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                now = LocalDateTime.now();
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime deadline = LocalDateTime.parse(item.getDeadline(), formatter);
                hoursLeft = ChronoUnit.HOURS.between(now, deadline);
            }

            if (item.isSubmitted()) {
                countCompleted++;
            } else if (hoursLeft < 0) {
                countMissed++;
            } else {
                countInprogress++;
                if (hoursLeft <= 24) {
                    countUrgent++;
                }
            }
        }

        TextView tvInProgress = findViewById(R.id.tv_count_in_progress);
        TextView tvUrgent = findViewById(R.id.tv_count_urgent);
        TextView tvCompleted = findViewById(R.id.tv_count_completed);
        TextView tvMissed = findViewById(R.id.tv_count_missed);

        tvInProgress.setText(String.format("%02d", countInprogress));
        tvUrgent.setText(String.format("%02d", countUrgent));
        tvCompleted.setText(String.format("%02d", countCompleted));
        tvMissed.setText(String.format("%02d", countMissed));

        // 작업 중(끝지점)

        // 시스템 바 인셋 적용 (Padding 설정)
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
        
        // 3. 어댑터 연결
        AssignmentAdapter adapter = new AssignmentAdapter(assignmentList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * assets/assignments.json 파일에서 데이터를 읽어 리스트로 변환
     */
    private List<AssignmentItem> loadAssignmentsFromJson() {
        String json = null;
        try {
            InputStream is = getAssets().open("assignments.json");
            int size = is.available();
            byte[] buffer = new byte[size]; // 데이터 읽기용 버퍼
            is.read(buffer);
            is.close();
            
            // UTF-8 인코딩으로 문자열 변환
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }

        // Gson을 이용한 객체 역직렬화
        Gson gson = new Gson();
        Type listType = new TypeToken<List<AssignmentItem>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}