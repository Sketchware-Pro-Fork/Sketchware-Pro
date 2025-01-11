package pro.sketchware.managers.exclude.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import pro.sketchware.R;
import pro.sketchware.databinding.PropertyCheckboxItemSinglelineBinding;
import pro.sketchware.managers.exclude.BuiltInClassesExcludeManager;

public class BuiltInClassesExcludeManagerDialogAdapter extends RecyclerView.Adapter<BuiltInClassesExcludeManagerDialogAdapter.ViewHolder> {

    private final List<String> items;
    private BuiltInClassesExcludeManager builtInClassExcludeManager;
    private DualListener<Boolean, String> itemCheckedChange; 

    public BuiltInClassesExcludeManagerDialogAdapter(final List<String> items) {
        this.items = items;
    }

    public void setBuiltInClassesExcludeManager(final BuiltInClassesExcludeManager builtInClassExcludeManager) {
        this.builtInClassExcludeManager = builtInClassExcludeManager;
    }

    public void setItemCheckedChange(final DualListener<Boolean, String> itemCheckedChange) {
        this.itemCheckedChange = itemCheckedChange;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        var binding = PropertyCheckboxItemSinglelineBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, itemCheckedChange, builtInClassExcludeManager);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final PropertyCheckboxItemSinglelineBinding binding;
        private final DualListener<Boolean, String> itemCheckedChange;
        private final BuiltInClassesExcludeManager builtInClassExcludeManager;
        private String currentClassName;

        public ViewHolder(@NonNull final PropertyCheckboxItemSinglelineBinding binding, final DualListener<Boolean, String> itemCheckedChange, final BuiltInClassesExcludeManager builtInClassExcludeManager) {
            super(binding.getRoot());
            this.binding = binding;
            this.itemCheckedChange = itemCheckedChange;
            this.builtInClassExcludeManager = builtInClassExcludeManager;
            binding.getRoot().setOnClickListener(v -> {
                binding.checkboxValue.setChecked(!binding.checkboxValue.isChecked());
                itemCheckedChange.call(binding.checkboxValue.isChecked(), currentClassName);
            });
        }

        public void bind(final String className) {
            currentClassName = className;
            binding.tvName.setText(className);
            binding.imgLeftIcon.setImageResource(R.drawable.ic_mtrl_java);
            binding.checkboxValue.setChecked(builtInClassExcludeManager.isPresent(className));
        }
    }

    @FunctionalInterface
    public interface DualListener<A, B> {
        public void call(final A value1, final B value2);
    }
}