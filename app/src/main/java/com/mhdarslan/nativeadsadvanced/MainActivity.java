package com.mhdarslan.nativeadsadvanced;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {

    private Button refreshButton;
    private UnifiedNativeAd nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        refreshButton = findViewById(R.id.btn_refreshAd);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAd();
            }
        });
        refreshAd();


    }
    private void refreshAd(){
        refreshButton.setEnabled(false);

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.native_advanced_ad_unit_id));
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                if(nativeAd != null)
                    nativeAd = unifiedNativeAd;
                CardView cardView = findViewById(R.id.ad_container);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.native_ad_layout,null);
                populateNativeAd(unifiedNativeAd,adView);
                cardView.removeAllViews();
                cardView.addView(adView);

                refreshButton.setEnabled(true);
            }
        });

        AdLoader adLoader = builder.withAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int i) {
                refreshButton.setEnabled(true);
                Toast.makeText(MainActivity.this, "Failed to load ad.", Toast.LENGTH_SHORT).show();
                super.onAdFailedToLoad(i);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void populateNativeAd(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView){
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        adView.setBodyView(adView.findViewById(R.id.ad_body_text));
        adView.setStarRatingView(adView.findViewById(R.id.star_rating));
        adView.setMediaView((MediaView) adView.findViewById(R.id.media_view));
        adView.setCallToActionView(adView.findViewById(R.id.add_call_to_action));
        adView.setIconView(adView.findViewById(R.id.adv_icon));

        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        if(nativeAd.getBody() == null){
            adView.getBodyView().setVisibility(View.INVISIBLE);
        }else {
            ((TextView)adView.getBodyView()).setText(nativeAd.getBody());
            adView.getBodyView().setVisibility(View.VISIBLE);
        }

        if(nativeAd.getAdvertiser() == null){
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        }else {
            ((TextView)adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        if(nativeAd.getStarRating() == null){
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        }else {
            ((RatingBar)adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if(nativeAd.getIcon() == null){
            adView.getIconView().setVisibility(View.GONE);
        }else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if(nativeAd.getCallToAction() == null){
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        }else {
            ((Button)adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        adView.setNativeAd(nativeAd);
    }

    @Override
    protected void onDestroy() {
        if(nativeAd != null)
            nativeAd.destroy();
        super.onDestroy();
    }
}