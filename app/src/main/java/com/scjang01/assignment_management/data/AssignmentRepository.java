package com.scjang01.assignment_management.data;

import android.content.Context;
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
 * 과제 데이터 관리하는 곳
 * 지금은 로컬 JSON에서 가져오지만, 나중에 서버 생기면 여기만 고치면 됨
 */
public class AssignmentRepository {
    private final Context context;
    private final Gson gson;

    public AssignmentRepository(Context context) {
        this.context = context.getApplicationContext();
        this.gson = new Gson(); // 나중에 의존성 주입 쓰면 좋겠지만 일단은 여기서 생성
    }

    // 과제 리스트 긁어오기
    public List<AssignmentItem> getAssignments() {
        String json = loadJsonFromAssets("assignments.json");
        if (json == null) return new ArrayList<>();

        Type listType = new TypeToken<List<AssignmentItem>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    // assets 폴더에 있는 JSON 파일 읽는 유틸
    private String loadJsonFromAssets(String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
