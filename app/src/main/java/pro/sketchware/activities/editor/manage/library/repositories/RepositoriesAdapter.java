package pro.sketchware.activities.editor.manage.library.repositories;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import pro.sketchware.databinding.LayoutRepositoryItemBinding;
import pro.sketchware.utility.UI;

public final class RepositoriesAdapter
        extends ListAdapter<Repository, RepositoriesAdapter.RepositoriesAdapterViewHolder> {

    public final OnClickListener onClick;

    public interface OnClickListener {
        void onClick(int position);
    }

    public RepositoriesAdapter(final OnClickListener onClick) {
        super(new RepositoriesAdapterDiffCallback());
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public RepositoriesAdapterViewHolder onCreateViewHolder(
            @NonNull final ViewGroup parent, final int viewType) {
        final LayoutRepositoryItemBinding binding =
                LayoutRepositoryItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        return new RepositoriesAdapterViewHolder(binding, onClick);
    }

    @Override
    public void onBindViewHolder(
            @NonNull final RepositoriesAdapterViewHolder holder, final int position) {
        holder.bind(getItem(position), position);
        holder.itemView.setBackgroundResource(
                UI.getShapedBackgroundForList(getCurrentList(), position));
    }

    public static final class RepositoriesAdapterViewHolder extends RecyclerView.ViewHolder {
        private final LayoutRepositoryItemBinding binding;
        private final OnClickListener onClick;
        private int currentIndex;

        public RepositoriesAdapterViewHolder(
                final LayoutRepositoryItemBinding binding, OnClickListener onClick) {
            super(binding.getRoot());
            this.binding = binding;
            this.onClick = onClick;
            itemView.setOnClickListener(v -> onClick.onClick(currentIndex));
        }

        public void bind(final Repository repo, final int index) {
            currentIndex = index;
            binding.name.setText(repo.getName());
            binding.url.setText(repo.getUrl());
        }
    }

    public static final class RepositoriesAdapterDiffCallback
            extends DiffUtil.ItemCallback<Repository> {
        @Override
        public boolean areItemsTheSame(
                @NonNull final Repository oldItem, @NonNull final Repository newItem) {
            return oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull final Repository oldItem, @NonNull final Repository newItem) {
            return oldItem.equals(newItem);
        }
    }
}
