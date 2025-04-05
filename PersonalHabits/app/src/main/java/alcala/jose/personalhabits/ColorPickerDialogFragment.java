package alcala.jose.personalhabits;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

public class ColorPickerDialogFragment extends DialogFragment {

    private EditText colorNameInput;
    private View colorPreview;
    private SeekBar redSeekBar, greenSeekBar, blueSeekBar;
    private int red = 128, green = 128, blue = 128;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_picker, null);

        colorNameInput = view.findViewById(R.id.ColorNameInput);
        colorPreview = view.findViewById(R.id.colorPreview);
        redSeekBar = view.findViewById(R.id.redSeekBar);
        greenSeekBar = view.findViewById(R.id.greenSeekBar);
        blueSeekBar = view.findViewById(R.id.blueSeekBar);
        Button confirmButton = view.findViewById(R.id.confirmButton);

        // Set initial color preview
        updateColorPreview();

        // SeekBar listeners
        redSeekBar.setOnSeekBarChangeListener(colorChangeListener);
        greenSeekBar.setOnSeekBarChangeListener(colorChangeListener);
        blueSeekBar.setOnSeekBarChangeListener(colorChangeListener);

        confirmButton.setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putInt("color", Color.rgb(red, green, blue));
            getParentFragmentManager().setFragmentResult("color_picked", result);
            dismiss();
        });

        return new android.app.AlertDialog.Builder(requireContext())
                .setView(view)
                .setTitle("Escoger Color")
                .create();
    }

    private final SeekBar.OnSeekBarChangeListener colorChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == redSeekBar) red = progress;
            else if (seekBar == greenSeekBar) green = progress;
            else if (seekBar == blueSeekBar) blue = progress;
            updateColorPreview();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private void updateColorPreview() {
        int color = Color.rgb(red, green, blue);
        colorPreview.setBackgroundColor(color);
    }
}
