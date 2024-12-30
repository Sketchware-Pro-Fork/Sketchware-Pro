package pro.sketchware.activities.editor.theme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import pro.sketchware.R;
import pro.sketchware.databinding.LayoutMaterialThemeEditorItemBinding;
import pro.sketchware.utility.UI;

public class MaterialThemeEditorAdapter extends ListAdapter<ThemeItem, MaterialThemeEditorAdapter.MaterialThemeEditorAdapterViewHolder> {

    public final OnClickListener onClick;
    public final OnLongClickListener onLongClick;

    public interface OnClickListener {
        void onClick(ThemeItem themeItem, int position);
    }

    public interface OnLongClickListener {
        void onLongClick(ThemeItem themeItem, int position);
    }

    public MaterialThemeEditorAdapter(OnClickListener onClick, OnLongClickListener onLongClick) {
        super(new MaterialThemeEditorAdapterDiffCallback());
        this.onClick = onClick;
        this.onLongClick = onLongClick;
    }

    @NonNull
    @Override
    public MaterialThemeEditorAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutMaterialThemeEditorItemBinding binding = LayoutMaterialThemeEditorItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new MaterialThemeEditorAdapterViewHolder(binding, onClick, onLongClick);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialThemeEditorAdapterViewHolder holder, int position) {
        holder.bind(getItem(position), position);
        holder.itemView.setBackgroundResource(UI.getShapedBackgroundForList(getCurrentList(), position));
    }

    public static class MaterialThemeEditorAdapterViewHolder extends RecyclerView.ViewHolder {
        public final LayoutMaterialThemeEditorItemBinding binding;
        public final OnClickListener onClick;
        public final OnLongClickListener onLongClick;
        public ThemeItem currentThemeItem;
        public Integer currentIndex;

        public MaterialThemeEditorAdapterViewHolder(LayoutMaterialThemeEditorItemBinding binding, OnClickListener onClick, OnLongClickListener onLongClick) {
            super(binding.getRoot());
            this.binding = binding;
            this.onClick = onClick;
            this.onLongClick = onLongClick;

            itemView.setOnClickListener(v -> {
                if (currentThemeItem != null && currentIndex != null) {
                    onClick.onClick(currentThemeItem, currentIndex);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (currentThemeItem != null && currentIndex != null) {
                    onLongClick.onLongClick(currentThemeItem, currentIndex);
                }
                return true;
            });
        }

        public void bind(ThemeItem themeItem, int index) {
            currentThemeItem = themeItem;
            currentIndex = index;
            binding.name.setText(themeItem.getColorName());
        }
    }

   public static class MaterialThemeEditorAdapterDiffCallback extends DiffUtil.ItemCallback<ThemeItem> {
        @Override
        public boolean areItemsTheSame(@NonNull ThemeItem oldItem, @NonNull ThemeItem newItem) {
            return oldItem.getColorName().equals(newItem.getColorName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ThemeItem oldItem, @NonNull ThemeItem newItem) {
            return oldItem.equals(newItem);
        }
    }
}