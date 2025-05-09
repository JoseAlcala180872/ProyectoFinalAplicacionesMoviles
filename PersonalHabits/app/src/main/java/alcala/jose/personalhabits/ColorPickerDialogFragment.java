package alcala.jose.personalhabits;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import alcala.jose.personalhabits.repositories.ColorRepository;
import kotlin.Pair;

public class ColorPickerDialogFragment extends DialogFragment {

    private RecyclerView colorRecyclerView;
    private ColorAdapter colorAdapter;
    private String selectedColor = "#FFFFFF";  // Default white
    private View dialogView;  // Store inflated view
    private View colorPreviewDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_picker, null);
        colorPreviewDialog = dialogView.findViewById(R.id.colorPreviewDialog);
        colorRecyclerView = dialogView.findViewById(R.id.colorRecyclerView);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        // Set up RecyclerView
        colorRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        colorAdapter = new ColorAdapter(new ColorRepository().getColors(), color -> {
            selectedColor = color;
            updateColorPreview();
        });
        colorRecyclerView.setAdapter(colorAdapter);

        // Confirm color selection
        confirmButton.setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString("color", selectedColor);
            getParentFragmentManager().setFragmentResult("color_picked", result);
            dismiss();
        });

        updateColorPreview();

        return new android.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Escoger Color")
                .create();
    }

    private void updateColorPreview() {
        if (colorPreviewDialog != null) {
            colorPreviewDialog.setBackgroundColor(Color.parseColor(selectedColor));
        }
    }

    // Adapter for RecyclerView
    private static class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

        private final List<kotlin.Pair<String, String>> colors;
        private final OnColorSelectedListener listener;

        ColorAdapter(@NotNull List<kotlin.Pair<@NotNull String, @NotNull String>> colors, OnColorSelectedListener listener) {
            this.colors = colors;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_item, parent, false);
            return new ColorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
            kotlin.Pair<String, String> color = colors.get(position);
            holder.colorPreview.setBackgroundColor(Color.parseColor(color.getSecond()));
            holder.itemView.setOnClickListener(v -> listener.onColorSelected(color.getSecond()));
        }

        @Override
        public int getItemCount() {
            return colors.size();
        }

        static class ColorViewHolder extends RecyclerView.ViewHolder {
            View colorPreview;

            ColorViewHolder(View itemView) {
                super(itemView);
                colorPreview = itemView.findViewById(R.id.colorPreview);
            }
        }

        interface OnColorSelectedListener {
            void onColorSelected(String color);
        }
    }
}
