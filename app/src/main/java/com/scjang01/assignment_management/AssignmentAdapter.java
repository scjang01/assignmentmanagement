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
import com.scjang01.assignment_management.model.DateHeader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 과제 리스트와 날짜 헤더를 처리하는 어댑터
 */
public class AssignmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Object> items;

    public AssignmentAdapter(List<Object> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        // 객체 타입에 따라 헤더/아이템 뷰 타입 결정
        if (items.get(position) instanceof DateHeader) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_date_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_assignment, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            DateHeader header = (DateHeader) items.get(position);
            ((HeaderViewHolder) holder).tvDateHeader.setText(header.getDate());
        } else if (holder instanceof ItemViewHolder) {
            AssignmentItem item = (AssignmentItem) items.get(position);
            bindItemViewHolder((ItemViewHolder) holder, item);
        }
    }

    private void bindItemViewHolder(ItemViewHolder holder, AssignmentItem item) {
        holder.tvTitle.setText(item.getTitle());
        holder.tvCategory.setText(item.getCategory() != null ? item.getCategory() : "기타");
        holder.tvDueDate.setText(item.getDeadline());

        // 카테고리별 태그 배경 및 텍스트 색상 설정
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

        // 기한 임박 및 마감 여부 계산
        boolean isMissed = false;
        boolean isUrgent = false;
        long hoursLeft = 0;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime deadline = LocalDateTime.parse(item.getDeadline(), formatter);
                hoursLeft = ChronoUnit.HOURS.between(LocalDateTime.now(), deadline);
                
                if (LocalDateTime.now().isAfter(deadline)) {
                    isMissed = true;
                }
                if (hoursLeft <= 24 && hoursLeft > 0) {
                    isUrgent = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 제출 여부 및 기한 상태에 따른 카드 스타일 적용
        if (item.isSubmitted()) {
            // 제출 완료 상태
            holder.itemView.setBackgroundResource(R.drawable.bg_card_completed);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getColor(R.color.card_title_normal));
            holder.tvDueDate.setTextColor(holder.itemView.getContext().getColor(R.color.card_subtitle_normal));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            holder.tvStatus.setText(R.string.tag_completed);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_completed);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.tag_completed_text));
        } else if (isMissed) {
            // 마감 지남 상태
            holder.itemView.setBackgroundResource(R.drawable.bg_card_missed);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getColor(R.color.card_title_missed));
            holder.tvDueDate.setTextColor(holder.itemView.getContext().getColor(R.color.card_subtitle_missed));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            holder.tvStatus.setText(R.string.tag_missed);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_missed);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.tag_missed_text));
        } else if (isUrgent) {
            // 마감 임박 상태 (24시간 이내)
            holder.itemView.setBackgroundResource(R.drawable.bg_card_urgent);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getColor(R.color.card_title_urgent));
            holder.tvDueDate.setTextColor(holder.itemView.getContext().getColor(R.color.card_subtitle_urgent));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            holder.tvStatus.setText(R.string.tag_urgent);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_urgent);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.tag_urgent_text));
        } else {
            // 진행 중 상태
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
     * 날짜 구분선 뷰홀더
     */
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateHeader = itemView.findViewById(R.id.tv_date_header);
        }
    }

    /**
     * 과제 아이템 뷰홀더
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTitle, tvDueDate, tvStatus;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category_tag);
            tvTitle = itemView.findViewById(R.id.tv_assignment_title);
            tvDueDate = itemView.findViewById(R.id.tv_due_date);
            tvStatus = itemView.findViewById(R.id.tv_status_tag);
        }
    }
}