package gq.rbeat.cashy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class BillsAdapter extends ArrayAdapter<String> {
    private ToPay toPay;

    public BillsAdapter(Context context, ToPay toPay) {
        super(context, 0, toPay.getName());
        this.toPay = toPay;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String name = toPay.getName().get(position);
        Double sum = toPay.getSum().get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, parent, false);
        }
        // Lookup view for data population
        TextView left = convertView.findViewById(R.id.left);
        TextView right = convertView.findViewById(R.id.right);
        // Populate the data into the template view using the data object
        left.setText(name);
        right.setText("Amount: " + new DecimalFormat("##.##").format(sum));
        // Return the completed view to render on screen
        return convertView;
    }
}
