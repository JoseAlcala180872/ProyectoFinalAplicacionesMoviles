package alcala.jose.personalhabits.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import alcala.jose.personalhabits.Charts.ChartDTO;
import alcala.jose.personalhabits.Charts.CustomBarDrawable;
import alcala.jose.personalhabits.DataClasses.Habitos;
import alcala.jose.personalhabits.R;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.MyViewHolder> {

    Context context;
    ArrayList<ChartDTO> fullChartList;
    ArrayList<ChartDTO> visibleChartList;
    String filter;


    public ChartAdapter(Context context, ArrayList<ChartDTO> chartList, String filter) {
        this.context = context;
        this.fullChartList = chartList;
        this.filter=filter;
        applyFilter(filter);

    }

    public void applyFilter(String filter) {
        this.filter = filter;
        if ("Todas".equalsIgnoreCase(filter.trim()) || filter.trim().isEmpty()) {
            visibleChartList = new ArrayList<>(fullChartList);
        } else {
            visibleChartList = new ArrayList<>();
            for (ChartDTO chart : fullChartList) {
                if (chart.getLabel().equalsIgnoreCase(filter.trim())) {
                    visibleChartList.add(chart);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateData(ArrayList<ChartDTO> newData) {
        this.fullChartList = new ArrayList<>(newData);
        applyFilter(filter);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chart_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChartDTO chart = visibleChartList.get(position);
        holder.label.setText(chart.getLabel());
        holder.number.setText(chart.getNumber());

        Habitos habito = new Habitos(chart.getLabel(), chart.getPorcentaje(), chart.getColor(), 100f);
        holder.chart.setBackground(new CustomBarDrawable(context, habito));
    }

    @Override
    public int getItemCount() {
        return visibleChartList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View chart;
        TextView label;
        TextView number;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            chart = itemView.findViewById(R.id.chartView);
            label = itemView.findViewById(R.id.chartTitle);
            number = itemView.findViewById(R.id.chartNumber);
        }
    }
}
