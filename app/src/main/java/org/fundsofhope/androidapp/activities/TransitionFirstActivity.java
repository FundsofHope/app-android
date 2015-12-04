package org.fundsofhope.androidapp.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import org.fundsofhope.androidapp.R;
import org.fundsofhope.androidapp.Utils;

public class TransitionFirstActivity extends Activity {

    private View fabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_first);
        int loader = R.drawable.logo;

        ImageView image=(ImageView)findViewById(R.id.image);
        String image_url = "http://api.androidhive.info/images/sample.jpg";
        ImageLoader imgLoader = new ImageLoader(getApplicationContext());
        imgLoader.DisplayImage(image_url, loader, image);


        // Set explode animation when enter and exit the activity
        Utils.configureWindowEnterExitTransition(getWindow());

        // Fab Button
        fabButton = findViewById(R.id.fab_button);
        fabButton.setOnClickListener(fabClickListener);
        Utils.configureFab(fabButton);
    }


    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        Intent i  = new Intent (TransitionFirstActivity.this, TransitionSecondActivity.class);

        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(TransitionFirstActivity.this,
                Pair.create(fabButton, "fab"));

        startActivity(i, transitionActivityOptions.toBundle());
        }
    };
}
