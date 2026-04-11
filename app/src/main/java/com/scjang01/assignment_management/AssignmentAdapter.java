package com.scjang01.assignment_management;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scjang01.assignment_management.model.AssignmentItem;

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
        holder.tvSubject.setText(item.getSubject());
        holder.tvDueDate.setText(item.getDeadline());

        // 상태에 따른 태그 처리 (간단한 예시)
        if (item.isSubmitted()) {
            holder.tvStatus.setText(R.string.tag_completed);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_completed);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.tag_completed_text));
        } else {
            // 마감시간 체크 로직은 나중에 추가 가능
            holder.tvStatus.setText(R.string.tag_missed);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_missed);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.tag_missed_text));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject, tvTitle, tvDueDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tv_category_tag);
            tvTitle = itemView.findViewById(R.id.tv_assignment_title);
            tvDueDate = itemView.findViewById(R.id.tv_due_date);
            tvStatus = itemView.findViewById(R.id.tv_status_tag);
        }
    }
}