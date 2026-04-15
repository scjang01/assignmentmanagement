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

import com.scjang01.assignment_management.data.AssignmentRepository;
import com.scjang01.assignment_management.model.AssignmentItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 메인 화면 로직
 */
public class MainActivity extends AppCompatActivity {

    private AssignmentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 화면 상단바/하단바 영역까지 꽉 채우기
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // 데이터 가져올 준비
        repository = new AssignmentRepository(this);
        
        // 상단 툴바 설정 (타이틀 안 보이게 처리)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 진행/임박/완료/마감 숫자 텍스트뷰들
        int countInProgress = 0;
        int countUrgent = 0;
        int countCompleted = 0;
        int countMissed = 0;

        List<AssignmentItem> assignmentList = repository.getAssignments();

        // 전체 과제 돌면서 상태값(뱃지 숫자) 계산
        for (AssignmentItem item : assignmentList) {
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
                countInProgress++;
                if (hoursLeft <= 24) { // 마감 24시간 안 남았으면 임박
                    countUrgent++;
                }
            }
        }

        // 상단 통계 숫자 업데이트 (02, 05 처럼 두자리 포맷 유지)
        TextView tvInProgress = findViewById(R.id.tv_count_in_progress);
        TextView tvUrgent = findViewById(R.id.tv_count_urgent);
        TextView tvCompleted = findViewById(R.id.tv_count_completed);
        TextView tvMissed = findViewById(R.id.tv_count_missed);

        tvInProgress.setText(String.format("%02d", countInProgress));
        tvUrgent.setText(String.format("%02d", countUrgent));
        tvCompleted.setText(String.format("%02d", countCompleted));
        tvMissed.setText(String.format("%02d", countMissed));

        // 상태바/네비바 때문에 레이아웃 가려지지 않게 여백 조정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 리스트(RecyclerView) 설정
        RecyclerView recyclerView = findViewById(R.id.rv_assignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // 어댑터 붙여서 화면에 리스트 뿌리기
        AssignmentAdapter adapter = new AssignmentAdapter(assignmentList);
        recyclerView.setAdapter(adapter);
    }
}