package com.example.catty.comp448_assignmenttwo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    private List<City> cityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CityListRecyclerViewAdapter adapter;
    private DatabaseHelper db;
    private EditText filterText;
    private View rootView;

    public static FavouritesFragment newInstance() {
        FavouritesFragment fragment = new FavouritesFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
         recyclerView = rootView.findViewById(R.id.recyclerView);

         filterText = (EditText) rootView.findViewById(R.id.filterList);

         //Filter for favourites
        filterText.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                 //nothing
             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
             }

             @Override
             public void afterTextChanged(Editable s) {
                //nothing
             }
         });

         db = new DatabaseHelper(getContext());

         adapter = new CityListRecyclerViewAdapter(getContext(), cityList);

         RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
         recyclerView.setLayoutManager(layoutManager);
         recyclerView.setItemAnimator(new DefaultItemAnimator());
         recyclerView.setAdapter(adapter);

         recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(),
                 recyclerView, new RecyclerTouchListener.ClickListener() {
             @Override
             public void onClick(View view, int position) {
                 //On selected switch to search fragment and display relevant information about city
                 Intent intent = new Intent("SEARCH");
                 intent.putExtra("CITY", cityList.get(position).getCity());
                 getActivity().sendBroadcast(intent);
             }

             @Override
             public void onLongClick(View view, int position) {
                showActionsDialog(position);
             }
         }));
         updateList();
         return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(updateList);
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(updateList, new IntentFilter("UPDATE_LIST"));

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    //Delete city from favourites list
    private void deleteCity(int i){
        db.deleteCity(cityList.get(i));
        cityList.remove(i);
        adapter.notifyItemRemoved(i);
        adapter.getFilter().filter(filterText.getText().toString());

        Snackbar.make(recyclerView, R.string.city_deleted_from_favourites, Snackbar.LENGTH_LONG).show();
    }

    //Update favourites list
    private void updateList(){
        cityList.clear();

        cityList.addAll(db.getAllCities());
        adapter.notifyDataSetChanged();
        int size = db.getCityCount();
        adapter.getFilter().filter(filterText.getText().toString());

    }

    private void showActionsDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Do you want to delete this city?");
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCity(position);
            }
        });
        builder.show();
    }

    private BroadcastReceiver updateList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateList();
        }
    };

}
