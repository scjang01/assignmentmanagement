package com.scjang01.assignment_management;

import android.graphics.Paint;
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

        // 카테고리별 태그 색상 지정
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

        boolean isMissed = false;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime deadline = LocalDateTime.parse(item.getDeadline(), formatter);
            if (LocalDateTime.now().isAfter(deadline)) {
                isMissed = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 제출 여부 및 마감 시간에 따른 카드 상태 변경
        if (item.isSubmitted()) {
            holder.itemView.setBackgroundResource(R.drawable.bg_card_completed);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getColor(R.color.card_title_normal));
            holder.tvDueDate.setTextColor(holder.itemView.getContext().getColor(R.color.card_subtitle_normal));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            holder.tvStatus.setText(R.string.tag_completed);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_completed);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.tag_completed_text));
        } else if (isMissed) {
            holder.itemView.setBackgroundResource(R.drawable.bg_card_missed);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getColor(R.color.card_title_missed));
            holder.tvDueDate.setTextColor(holder.itemView.getContext().getColor(R.color.card_subtitle_missed));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            holder.tvStatus.setText(R.string.tag_missed);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_missed);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.tag_missed_text));
        } else {
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