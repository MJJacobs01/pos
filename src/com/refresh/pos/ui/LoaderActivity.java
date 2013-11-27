package com.refresh.pos.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.refresh.pos.R;
import com.refresh.pos.domain.DateTimeStrategy;
import com.refresh.pos.domain.inventory.Inventory;
import com.refresh.pos.domain.sale.Register;
import com.refresh.pos.domain.sale.SaleLedger;
import com.refresh.pos.techicalservices.AndroidDatabase;
import com.refresh.pos.techicalservices.Database;
import com.refresh.pos.techicalservices.inventory.InventoryDao;
import com.refresh.pos.techicalservices.inventory.InventoryDaoAndroid;
import com.refresh.pos.techicalservices.sale.SaleDao;
import com.refresh.pos.techicalservices.sale.SaleDaoAndroid;

/**
 * This is the first activity page, core-app and database created here.
 * Dependency injection happen here.
 *  
 * @author Refresh Team
 *
 */
public class LoaderActivity extends Activity {

        private LinearLayout layout;
        private Button goButton;
        private boolean gone;
        

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
//                initiateCoreApp();
                initiateUI(savedInstanceState);
        }
        
        /**
         * Loads database and DAO.
         */
        private void initiateCoreApp() {
                Database database = new AndroidDatabase(this);
                InventoryDao inventoryDao = new InventoryDaoAndroid(database);
                SaleDao saleDao = new SaleDaoAndroid(database);
                
                Inventory.setInventoryDao(inventoryDao);
                Register.setSaleDao(saleDao);
                SaleLedger.setSaleDao(saleDao);
                
                DateTimeStrategy.setLocale("th", "TH");
                
                Log.d("Core App", "INITIATE");
        }

        private void initiateUI(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_loader);
                goButton = (Button) findViewById(R.id.goButton);
                goButton.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                                Intent newActivity = new Intent(LoaderActivity.this, MainActivity.class);
                                startActivity(newActivity);
                                gone = true;
                        }
                });
                 new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                    if (!gone) {
                            Intent newActivity = new Intent(LoaderActivity.this, MainActivity.class);
                            startActivity(newActivity);
                     }
             }
         }, 3000);
        }


}