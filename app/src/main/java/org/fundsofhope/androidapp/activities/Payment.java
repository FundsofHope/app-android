package org.fundsofhope.androidapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.stripe.android.*;

import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

/**
 * Created by Anip on 3/18/2016.
 */
public class Payment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Card card = new Card("4242-4242-4242-4242", 12, 2017, "123");

        if ( !card.validateCard() ) {
            // Show errors
        }
        else {
            //Stripe stripe = new Stripe("pk_test_6pRNASCoBOKtIshFeQd4XMUh");
           /* stripe.createToken(
                    card,
                    new TokenCallback() {
                        public  void onSuccess(Token token) {
                            // Send token to your server
                        }
                        public void onError(Exception error) {
                            // Show localized error message
                            //Toast.makeText(this,error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
            );*/
        }
    }
}
