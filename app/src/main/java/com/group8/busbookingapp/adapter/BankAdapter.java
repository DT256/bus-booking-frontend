package com.group8.busbookingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.dto.Bank;

import java.util.ArrayList;
import java.util.List;
import android.widget.Filter;
import android.widget.Filterable;

public class BankAdapter extends ArrayAdapter<Bank> {
    private Context context;
    private List<Bank> originalList;
    private List<Bank> filteredList;

    public BankAdapter(Context context, List<Bank> bankList) {
        super(context, 0, bankList);
        this.context = context;
        this.originalList = new ArrayList<>(bankList);
        this.filteredList = bankList;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Bank getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bank bank = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bank, parent, false);
        }

        TextView textShortName = convertView.findViewById(R.id.textShortName);
        ImageView imageLogo = convertView.findViewById(R.id.imageLogo);

        textShortName.setText(bank.getShortName());

        Glide.with(context) // dùng Glide để load ảnh logo
                .load(bank.getLogo())
                .placeholder(R.drawable.ic_money) // ảnh chờ
                .into(imageLogo);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Bank> suggestions = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    suggestions.addAll(originalList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Bank bank : originalList) {
                        if (bank.getShortName().toLowerCase().contains(filterPattern)) {
                            suggestions.add(bank);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = suggestions;
                results.count = suggestions.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Bank>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

