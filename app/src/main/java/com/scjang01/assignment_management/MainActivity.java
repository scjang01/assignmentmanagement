package com.scjang01.assignment_management;

import android.os.Bundle;

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
import java.util.ArrayList;
import java.util.List;

/**
 * 앱의 메인 화면을 담당하는 액티비티 클래스입니다.
 * AppCompatActivity를 상속받아 안드로이드의 기본 기능을 사용합니다.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 액티비티가 생성될 때 호출되는 생명주기 메서드입니다.
        super.onCreate(savedInstanceState);
        
        // 화면의 상단바와 하단바 영역까지 콘텐츠를 확장하는 설정입니다.
        EdgeToEdge.enable(this);
        
        // 이 액티비티가 사용할 레이아웃 파일(activity_main.xml)을 설정합니다.
        setContentView(R.layout.activity_main);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        
        // 시스템 바(상태바, 네비게이션바)의 크기만큼 패딩을 주어 콘텐츠가 가려지지 않게 합니다.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. JSON 데이터 로드: assets 폴더에 있는 assignments.json 파일을 읽어와 리스트로 변환합니다.
        List<AssignmentItem> assignmentList = loadAssignmentsFromJson();

        // 2. RecyclerView 설정: 레이아웃 파일에서 정의한 리사이클러뷰를 찾아옵니다.
        RecyclerView recyclerView = findViewById(R.id.rv_assignments);
        
        // 레이아웃 매니저는 아이템들이 화면에 어떻게 배치될지 결정합니다. (여기서는 세로 일렬 배치)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // 3. Adapter 연결: 로드한 데이터와 레이아웃을 연결해주는 어댑터를 생성하고 설정합니다.
        AssignmentAdapter adapter = new AssignmentAdapter(assignmentList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * assets 폴더의 JSON 파일을 읽어서 객체 리스트로 변환하는 유틸리티 메서드입니다.
     */
    private List<AssignmentItem> loadAssignmentsFromJson() {
        String json = null;
        try {
            // assets 폴더에 있는 'assignments.json' 파일을 엽니다.
            InputStream is = getAssets().open("assignments.json");
            int size = is.available(); // 파일의 크기를 확인합니다.
            byte[] buffer = new byte[size]; // 데이터를 담을 바구니(버퍼)를 만듭니다.
            is.read(buffer); // 파일 내용을 읽어 버퍼에 채웁니다.
            is.close(); // 파일을 다 읽었으니 닫아줍니다.
            
            // 바이트 배열을 UTF-8 형식의 문자열(JSON 문자열)로 변환합니다.
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            // 파일을 읽는 도중 에러가 발생하면 로그를 출력하고 빈 리스트를 반환합니다.
            ex.printStackTrace();
            return new ArrayList<>();
        }

        // Gson 라이브러리를 사용하여 JSON 문자열을 Java 객체(List<AssignmentItem>)로 변환합니다.
        Gson gson = new Gson();
        Type listType = new TypeToken<List<AssignmentItem>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}