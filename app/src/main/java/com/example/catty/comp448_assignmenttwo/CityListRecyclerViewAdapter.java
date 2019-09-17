package com.example.catty.comp448_assignmenttwo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class CityListRecyclerViewAdapter extends RecyclerView.Adapter<CityListRecyclerViewAdapter.MyViewHolder>{

    private Context context;
    private List<City> cityList;
    private List<City> displayedCityList;

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        public TextView city;

        public MyViewHolder(View view){
            super(view);
            city = view.findViewById(R.id.cityName);
        }
    }

    public CityListRecyclerViewAdapter(Context context, List<City> cityList){
        this.context = context;
        this.cityList = cityList;
        this.displayedCityList = cityList;
    }

    //Filter to filter the favourites to display items that match the constraints
    public Filter getFilter(){
        final Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<City> filterList = new ArrayList<>();

                if(cityList == null){
                    cityList = new ArrayList<>(displayedCityList);
                }

                //If constraint not specified return the original list
                if(constraint == null|| constraint.length() == 0){
                    results.count = cityList.size();
                    results.values = cityList;
                }else{
                    //Otherwise get constraint and return list of items that match a part of the constraint
                    constraint = constraint.toString().toLowerCase();
                    for(int i = 0; i < cityList.size(); i++){
                        String city = cityList.get(i).getCity();
                        if(city.toLowerCase().contains(constraint.toString())){
                            filterList.add(new City(cityList.get(i).getId(), cityList.get(i).getCity()));
                        }
                        results.count = filterList.size();
                        results.values = filterList;
                    }
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                displayedCityList = (ArrayList<City>) results.values;
                notifyDataSetChanged();
            }
        };

        return filter;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.city_list_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        City city = displayedCityList.get(i);
        myViewHolder.city.setText(city.getCity());
    }

    @Override
    public int getItemCount() {
        return displayedCityList.size();
    }


}
