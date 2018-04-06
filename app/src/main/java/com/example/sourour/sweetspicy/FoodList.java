package com.example.sourour.sweetspicy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.sourour.sweetspicy.Interface.ItemClickListener;
import com.example.sourour.sweetspicy.Model.Food;
import com.example.sourour.sweetspicy.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;
    String categoryId = "";
    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        //Firebase
        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("food");


        recyclerView=(RecyclerView)findViewById(R.id.recycler_food);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //get intent here

        if (getIntent() != null)
        {
           categoryId=getIntent().getStringExtra("categoryId");
        }

        if (!categoryId.isEmpty() && categoryId!=null)
        {
            loadListFood(categoryId);
        }




    }

    private void loadListFood(String categoryId) {

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,R.layout.food_item,FoodViewHolder.class,foodList.orderByChild("menuId").equalTo(categoryId)) {
            //like select * from food where menuId=
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                final Food local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                    //start new activity

                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("foodID",adapter.getRef(position).getKey()); //send foodId to new activity

                        startActivity(foodDetail);


                    }
                });

            }
        };
            //set adapter

        recyclerView.setAdapter(adapter);

    }
}
