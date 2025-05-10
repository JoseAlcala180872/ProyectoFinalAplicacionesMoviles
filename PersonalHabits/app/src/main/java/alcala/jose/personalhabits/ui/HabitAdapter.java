package alcala.jose.personalhabits.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import alcala.jose.personalhabits.Dominio.Habito;
import alcala.jose.personalhabits.EditHabito;
import alcala.jose.personalhabits.R;
import alcala.jose.personalhabits.Repositories.HabitRepository;
import alcala.jose.personalhabits.Repositories.UserRepository;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.MyViewHolder> {

    Context context;
    ArrayList<Habito> habitList;
    boolean showCompleteButton;  // New flag

    UserRepository userRepository = new UserRepository();
    HabitRepository habitRepository = new HabitRepository();

    public HabitAdapter(Context context, ArrayList<Habito> habitList, boolean showCompleteButton) {
        this.context = context;
        this.habitList = habitList;
        this.showCompleteButton = showCompleteButton;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.habit_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Habito habit = habitList.get(position);

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.habit_icon_color);
        if (drawable != null) {
            drawable.mutate().setTint(habit.getColor());
            holder.iconColor.setImageDrawable(drawable);
        }

        holder.habitTitle.setText(habit.getNombre());
        holder.habitDescription.setText(habit.getDescripcion());
        holder.habitCategory.setText(habit.getCategoria());

        // Hide the complete button if the flag is false
        if (!showCompleteButton) {
            holder.completeButton.setVisibility(View.GONE);  // Hide the button
        } else {
            holder.completeButton.setVisibility(View.VISIBLE);  // Show the button

            holder.completeButton.setOnClickListener(v -> {
                habitRepository.updateCompletionStatus(habit.getId(), true, success -> {
                    if (success) {
                        Toast.makeText(context, "Hábito completado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al completar el hábito", Toast.LENGTH_SHORT).show();
                    }
                    return null;
                });
            });

        }

        holder.optionsButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.habit_options_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_edit) {
                    Log.d("HabitAdapter", "Edit selected for habit: " + habit.getId());
                    Intent intent = new Intent(context, EditHabito.class);
                    intent.putExtra("habitId", habit.getId());
                    intent.putExtra("userId", habit.getUserId());
                    intent.putExtra("habitName", habit.getNombre());
                    intent.putExtra("habitDescription", habit.getDescripcion());
                    intent.putExtra("habitColor", habit.getColor());
                    intent.putExtra("habitCategory", habit.getCategoria());
                    intent.putStringArrayListExtra("habitFrequency", (ArrayList<String>) habit.getFrecuencia());
                    intent.putExtra("habitTime", habit.getHora());

                    context.startActivity(intent);
                } else if (itemId == R.id.menu_delete) {
                    Log.d("HabitAdapter", "Delete selected for habit: " + (ArrayList<String>) habit.getFrecuencia());
                    habitRepository.deleteHabit(habit.getId(), success -> {
                        if (success) {
                            Toast.makeText(context, "Hábito eliminado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al eliminar el hábito", Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    });
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iconColor;
        TextView habitTitle;
        TextView habitDescription;
        TextView habitCategory;
        Button completeButton;
        ImageButton optionsButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iconColor = itemView.findViewById(R.id.icon_color);
            habitTitle = itemView.findViewById(R.id.habitTitle);
            habitDescription = itemView.findViewById(R.id.habitDescription);
            habitCategory = itemView.findViewById(R.id.habitCategory);
            completeButton = itemView.findViewById(R.id.completeButton);
            optionsButton = itemView.findViewById(R.id.optionsButton);
        }
    }
}


