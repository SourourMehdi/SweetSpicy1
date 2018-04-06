
package com.example.sourour.sweetspicy;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sourour.sweetspicy.Common.Common;
import com.example.sourour.sweetspicy.Database.Database;
import com.example.sourour.sweetspicy.Model.Order;
import com.example.sourour.sweetspicy.Model.Request;
import com.example.sourour.sweetspicy.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView textTotalPrice;
    FButton btnPlace;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //firebase
        database= FirebaseDatabase.getInstance();
        requests=database.getReference("requests");

        //init

        recyclerView=(RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        textTotalPrice=(TextView)findViewById(R.id.total);
        btnPlace=(FButton)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAlertDialog();


            }
        });

        loadListFood();


    }

    private void showAlertDialog() {

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(Cart.this);
        alertdialog.setTitle("One More Step !");
        alertdialog.setMessage("Enter your address : ");

        final EditText edtAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        edtAddress.setLayoutParams(lp);
        alertdialog.setView(edtAddress); // add edit text to alert dialog

        alertdialog.setIcon(R.drawable.shop);
        alertdialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // create new request

                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        textTotalPrice.getText().toString(),cart
                );

                //submit to firebase
                //System.CurentMilli to key

                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);

                //deleye cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "Thank you ! Order Placed ", Toast.LENGTH_SHORT).show();
                finish();

            }
        });

        alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });

        alertdialog.show();

    }

    private void loadListFood() {

        cart= new Database(this).getCarts();
        adapter= new CartAdapter(cart,this);
        recyclerView.setAdapter(adapter);


        //calculate total price

        int total = 0;
        for (Order order:cart)
            total+=Integer.parseInt(order.getPrice())*(Integer.parseInt(order.getQuantity()));

            Locale locale = new Locale("en"," US");
            NumberFormat fmt= NumberFormat.getCurrencyInstance(locale);

            textTotalPrice.setText(fmt.format(total));


    }
}
