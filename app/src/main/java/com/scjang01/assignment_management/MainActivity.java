package com.scjang01.assignment_management;

import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.scjang01.assignment_management.data.AssignmentRepository;
import com.scjang01.assignment_management.databinding.ActivityMainBinding;
import com.scjang01.assignment_management.model.AssignmentItem;
import com.scjang01.assignment_management.model.DateHeader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 메인 화면 로직
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AssignmentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 화면 상단바/하단바 영역까지 꽉 채우기
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 데이터 가져올 준비
        repository = new AssignmentRepository(this);
        
        // 상단 툴바 설정 (타이틀 안 보이게 처리)
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 진행/임박/완료/마감 숫자 텍스트뷰들
        int countInProgress = 0;
        int countUrgent = 0;
        int countCompleted = 0;
        int countMissed = 0;

        List<AssignmentItem> assignmentList = repository.getAssignments();

        // 마감 기한 기준 오름차순 정렬
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            Collections.sort(assignmentList, (item1, item2) -> {
                LocalDateTime date1 = LocalDateTime.parse(item1.getDeadline(), formatter);
                LocalDateTime date2 = LocalDateTime.parse(item2.getDeadline(), formatter);
                return date1.compareTo(date2);
            });
        }

        // 날짜별 헤더를 포함한 새 리스트 생성
        List<Object> displayItems = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String lastDate = "";
            LocalDateTime today = LocalDateTime.now();
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            DateTimeFormatter headerFormatter = DateTimeFormatter.ofPattern("M. d(E)", Locale.KOREAN);

            for (AssignmentItem item : assignmentList) {
                LocalDateTime dateTime = LocalDateTime.parse(item.getDeadline(), inputFormatter);
                String currentDate = dateTime.toLocalDate().toString();
                String stringToday = today.toLocalDate().toString();


                // 날짜가 변경될 때마다 헤더 객체 삽입
                if (!currentDate.equals(lastDate)) {
                    if (stringToday.equals(currentDate)) {
                        displayItems.add(new DateHeader(dateTime.format(headerFormatter)+" 오늘"));
                    }
                    else {
                        displayItems.add(new DateHeader(dateTime.format(headerFormatter)));
                    }
                    lastDate = currentDate;
                }
                displayItems.add(item);
            }
        } else {
            // API 26 미만 기기는 헤더 없이 리스트 구성
            displayItems.addAll(assignmentList);
        }

        // 통계 바 데이터 계산
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
        binding.tvCountInProgress.setText(String.format("%02d", countInProgress));
        binding.tvCountUrgent.setText(String.format("%02d", countUrgent));
        binding.tvCountCompleted.setText(String.format("%02d", countCompleted));
        binding.tvCountMissed.setText(String.format("%02d", countMissed));

        // 상태바/네비바 때문에 레이아웃 가려지지 않게 여백 조정
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 리스트(RecyclerView) 설정
        binding.rvAssignments.setLayoutManager(new LinearLayoutManager(this));
        
        // 어댑터 붙여서 화면에 리스트 뿌리기
        AssignmentAdapter adapter = new AssignmentAdapter(displayItems);
        binding.rvAssignments.setAdapter(adapter);
    }
}