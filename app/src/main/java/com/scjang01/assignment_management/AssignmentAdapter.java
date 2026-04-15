package com.scjang01.assignment_management;

import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scjang01.assignment_management.model.AssignmentItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 과제 리스트 보여주는 리사이클러뷰 어댑터
 */
public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    private List<AssignmentItem> items;

    public AssignmentAdapter(List<AssignmentItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssignmentItem item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvCategory.setText(item.getCategory() != null ? item.getCategory() : "기타");
        holder.tvDueDate.setText(item.getDeadline());

        // 카테고리별로 태그 디자인(색상) 다르게 적용
        String category = item.getCategory() != null ? item.getCategory() : "";
        switch (category) {
            case "퀴즈":
                holder.tvCategory.setBackgroundResource(R.drawable.bg_tag_quiz);
                holder.tvCategory.setTextColor(holder.itemView.getContext().getColor(R.color.tag_quiz_text));
                break;
            case "녹화강의":
                holder.tvCategory.setBackgroundResource(R.drawable.bg_tag_recorded);
                holder.tvCategory.setTextColor(holder.itemView.getContext().getColor(R.color.tag_recorded_text));
                break;
            case "실시간강의":
                holder.tvCategory.setBackgroundResource(R.drawable.bg_tag_live);
                holder.tvCategory.setTextColor(holder.itemView.getContext().getColor(R.color.tag_live_text));
                break;
            case "과제":
            default:
                holder.tvCategory.setBackgroundResource(R.drawable.bg_tag_assignment);
                holder.tvCategory.setTextColor(holder.itemView.getContext().getColor(R.color.tag_assignment_text));
                break;
        }

        // 현재 시간 기준으로 마감 지났는지 체크
        boolean isMissed = false;
        try {
            DateTimeFormatter formatter = null;
            LocalDateTime deadline = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                deadline = LocalDateTime.parse(item.getDeadline(), formatter);
                if (LocalDateTime.now().isAfter(deadline)) {
                    isMissed = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 제출했거나 마감 지났을 때 카드 스타일 업데이트
        if (item.isSubmitted()) {
            // 완료: 배경 바꾸고 타이틀에 취소선 긋기
            holder.itemView.setBackgroundResource(R.drawable.bg_card_completed);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getColor(R.color.card_title_normal));
            holder.tvDueDate.setTextColor(holder.itemView.getContext().getColor(R.color.card_subtitle_normal));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            holder.tvStatus.setText(R.string.tag_completed);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_completed);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.tag_completed_text));
        } else if (isMissed) {
            // 마감 지남: 붉은 계열 배경으로 변경
            holder.itemView.setBackgroundResource(R.drawable.bg_card_missed);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getColor(R.color.card_title_missed));
            holder.tvDueDate.setTextColor(holder.itemView.getContext().getColor(R.color.card_subtitle_missed));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            holder.tvStatus.setText(R.string.tag_missed);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_missed);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.tag_missed_text));
        } else {
            // 진행 중: 기본 스타일
            holder.itemView.setBackgroundResource(R.drawable.bg_card_in_progress);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getColor(R.color.card_title_normal));
            holder.tvDueDate.setTextColor(holder.itemView.getContext().getColor(R.color.card_subtitle_normal));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            holder.tvStatus.setText(R.string.tag_in_progress);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_in_progress);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.tag_in_progress_text));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * 리스트 아이템 뷰홀더
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTitle, tvDueDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category_tag);
            tvTitle = itemView.findViewById(R.id.tv_assignment_title);
            tvDueDate = itemView.findViewById(R.id.tv_due_date);
            tvStatus = itemView.findViewById(R.id.tv_status_tag);
        }
    }
}