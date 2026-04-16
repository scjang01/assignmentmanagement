package com.scjang01.assignment_management;

import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scjang01.assignment_management.databinding.ItemAssignmentBinding;
import com.scjang01.assignment_management.databinding.ItemDateHeaderBinding;
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
            ItemDateHeaderBinding binding = ItemDateHeaderBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new HeaderViewHolder(binding);
        } else {
            ItemAssignmentBinding binding = ItemAssignmentBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ItemViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            DateHeader header = (DateHeader) items.get(position);
            ((HeaderViewHolder) holder).binding.tvDateHeader.setText(header.getDate());
        } else if (holder instanceof ItemViewHolder) {
            AssignmentItem item = (AssignmentItem) items.get(position);
            bindItemViewHolder((ItemViewHolder) holder, item);
        }
    }

    private void bindItemViewHolder(ItemViewHolder holder, AssignmentItem item) {
        ItemAssignmentBinding binding = holder.binding;
        binding.tvAssignmentTitle.setText(item.getTitle());
        binding.tvCategoryTag.setText(item.getCategory() != null ? item.getCategory() : "기타");
        binding.tvDueDate.setText(item.getDeadline());

        // 카테고리별 태그 배경 및 텍스트 색상 설정
        String category = item.getCategory() != null ? item.getCategory() : "";
        switch (category) {
            case "퀴즈":
                binding.tvCategoryTag.setBackgroundResource(R.drawable.bg_tag_quiz);
                binding.tvCategoryTag.setTextColor(holder.itemView.getContext().getColor(R.color.tag_quiz_text));
                break;
            case "녹화강의":
                binding.tvCategoryTag.setBackgroundResource(R.drawable.bg_tag_recorded);
                binding.tvCategoryTag.setTextColor(holder.itemView.getContext().getColor(R.color.tag_recorded_text));
                break;
            case "실시간강의":
                binding.tvCategoryTag.setBackgroundResource(R.drawable.bg_tag_live);
                binding.tvCategoryTag.setTextColor(holder.itemView.getContext().getColor(R.color.tag_live_text));
                break;
            case "과제":
            default:
                binding.tvCategoryTag.setBackgroundResource(R.drawable.bg_tag_assignment);
                binding.tvCategoryTag.setTextColor(holder.itemView.getContext().getColor(R.color.tag_assignment_text));
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
            binding.getRoot().setBackgroundResource(R.drawable.bg_card_completed);
            binding.tvAssignmentTitle.setTextColor(holder.itemView.getContext().getColor(R.color.card_title_normal));
            binding.tvDueDate.setTextColor(holder.itemView.getContext().getColor(R.color.card_subtitle_normal));
            binding.tvAssignmentTitle.setPaintFlags(binding.tvAssignmentTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            binding.tvStatusTag.setText(R.string.tag_completed);
            binding.tvStatusTag.setBackgroundResource(R.drawable.bg_tag_completed);
            binding.tvStatusTag.setTextColor(holder.itemView.getContext().getColor(R.color.tag_completed_text));
        } else if (isMissed) {
            // 마감 지남 상태
            binding.getRoot().setBackgroundResource(R.drawable.bg_card_missed);
            binding.tvAssignmentTitle.setTextColor(holder.itemView.getContext().getColor(R.color.card_title_missed));
            binding.tvDueDate.setTextColor(holder.itemView.getContext().getColor(R.color.card_subtitle_missed));
            binding.tvAssignmentTitle.setPaintFlags(binding.tvAssignmentTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            binding.tvStatusTag.setText(R.string.tag_missed);
            binding.tvStatusTag.setBackgroundResource(R.drawable.bg_tag_missed);
            binding.tvStatusTag.setTextColor(holder.itemView.getContext().getColor(R.color.tag_missed_text));
        } else if (isUrgent) {
            // 마감 임박 상태 (24시간 이내)
            binding.getRoot().setBackgroundResource(R.drawable.bg_card_urgent);
            binding.tvAssignmentTitle.setTextColor(holder.itemView.getContext().getColor(R.color.card_title_urgent));
            binding.tvDueDate.setTextColor(holder.itemView.getContext().getColor(R.color.card_subtitle_urgent));
            binding.tvAssignmentTitle.setPaintFlags(binding.tvAssignmentTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            binding.tvStatusTag.setText(R.string.tag_urgent);
            binding.tvStatusTag.setBackgroundResource(R.drawable.bg_tag_urgent);
            binding.tvStatusTag.setTextColor(holder.itemView.getContext().getColor(R.color.tag_urgent_text));
        } else {
            // 진행 중 상태
            binding.getRoot().setBackgroundResource(R.drawable.bg_card_in_progress);
            binding.tvAssignmentTitle.setTextColor(holder.itemView.getContext().getColor(R.color.card_title_normal));
            binding.tvDueDate.setTextColor(holder.itemView.getContext().getColor(R.color.card_subtitle_normal));
            binding.tvAssignmentTitle.setPaintFlags(binding.tvAssignmentTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            binding.tvStatusTag.setText(R.string.tag_in_progress);
            binding.tvStatusTag.setBackgroundResource(R.drawable.bg_tag_in_progress);
            binding.tvStatusTag.setTextColor(holder.itemView.getContext().getColor(R.color.tag_in_progress_text));
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
        final ItemDateHeaderBinding binding;

        public HeaderViewHolder(@NonNull ItemDateHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    /**
     * 과제 아이템 뷰홀더
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        final ItemAssignmentBinding binding;

        public ItemViewHolder(@NonNull ItemAssignmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}