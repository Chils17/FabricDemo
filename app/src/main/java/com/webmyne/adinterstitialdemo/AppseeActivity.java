package com.webmyne.adinterstitialdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.appsee.Appsee;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.PurchaseEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.crashlytics.android.answers.SignUpEvent;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import io.fabric.sdk.android.Fabric;

public class AppseeActivity extends AppCompatActivity {

    private Button btnOk;
    private BranchUniversalObject branchUniversalObject;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this);
        Appsee.start(getString(R.string.com_appsee_apikey));

        Fabric.with(this, new Answers());

        Fabric.with(this, new Crashlytics());


        setContentView(R.layout.activity_appsee);

        // TODO: Move this to where you establish a user session
        //logUser();

        //contentViewEvent();

        //logPurchase();

        //searchItem();

        //signUpEvent();

        btnOk = (Button) findViewById(R.id.btnOk);
        imageView = (ImageView) findViewById(R.id.imageView);

        onUserSessionStarted();

        onUserDeposit();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appsee.stop();
            }
        });

        // Hook up your share button to initiate sharing
        findViewById(R.id.buttonShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                initiateSharing();
            }
        });
        contentLoaded();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PubNubActivity.class);
                startActivity(intent);
            }
        });

    }

    private void signUpEvent() {
        Answers.getInstance().logSignUp(new SignUpEvent()
                .putMethod("Digits")
                .putSuccess(true)
                .putCustomAttribute("Custom String", "My String")
                .putCustomAttribute("Custom Number", 25));
    }

    private void searchItem() {
        Answers.getInstance().logSearch(new SearchEvent()
                .putQuery("mobile analytics")
                .putCustomAttribute("Custom String", "My String")
                .putCustomAttribute("Custom Number", 25));
    }

    private void logPurchase() {
        Answers.getInstance().logPurchase(new PurchaseEvent()
                .putItemPrice(BigDecimal.valueOf(13.50))
                .putCurrency(Currency.getInstance("USD"))
                .putItemName("Answers Shirt")
                .putItemType("Apparel")
                .putItemId("sku-350")
                .putSuccess(true));
    }


    private void contentViewEvent() {
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Answers setup process super easy!")
                .putContentType("Technical documentation")
                .putContentId("article-350")
                .putCustomAttribute("Custom String", "foo")
                .putCustomAttribute("Custom Number", 35));
    }


    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

    // TODO: Move this method and use your own event name to track your key metrics
    public void onKeyMetric() {
        // TODO: Use your own string attributes to track common values over time
        // TODO: Use your own number attributes to track median value over time
        Answers.getInstance().logCustom(new CustomEvent("Video Played")
                .putCustomAttribute("Category", "Comedy")
                .putCustomAttribute("Length", 350));
    }


    private void onUserSessionStarted() {
        Appsee.setUserId("User1234");
    }

    private void onUserDeposit() {
        Appsee.addEvent("UserDepositFinished");
        Appsee.addEvent("ItemPurchased", new HashMap<String, Object>() {{
            put("Price", 100);
            put("Country", "USA");
        }});
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName("Test User");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                if (error == null && branchUniversalObject != null) {
                    // This code will execute when your app is opened from a Branch deep link, which
                    // means that you can route to a custom activity depending on what they clicked.
                    // In this example, we'll just print out the data from the link that was clicked.

                    Log.i("BranchTestBed", "referring Branch Universal Object: " + branchUniversalObject.toString());

                    // check if the item is contained in the metadata
                    if (branchUniversalObject.getMetadata().containsKey("item_id")) {
                        Intent i = new Intent(getApplicationContext(), ItemViewActivity.class);
                        i.putExtra("picture_id", branchUniversalObject.getMetadata().get("item_id"));
                        startActivity(i);
                    }
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    // This would be your own method where you've loaded the content for this page
    void contentLoaded() {
        // Initialize a Branch Universal Object for the page the user is viewing
        branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item_id_12345")
                .setTitle("My Content Title")
                .setContentDescription("Check out this awesome piece of content")
                .setContentImageUrl("https://example.com/mycontent-12345.png")
                .addContentMetadata("item_id", "12345")
                .addContentMetadata("user_id", "678910");

        // Trigger a view on the content for analytics tracking
        branchUniversalObject.registerView();

        // List on Google App Indexing
        branchUniversalObject.listOnGoogleSearch(this);
    }


    // This is the function to handle sharing when a user clicks the share button
    void initiateSharing() {
        // Create your link properties
        // More link properties available at https://dev.branch.io/getting-started/configuring-links/guide/#link-control-parameters
        LinkProperties linkProperties = new LinkProperties()
                .setFeature("sharing");

        // Customize the appearance of your share sheet
        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(this, "Check this out!", "Hey friend - I know you'll love this: ")
                .setCopyUrlStyle(getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy link", "Link added to clipboard!")
                .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER);

        // Show the share sheet for the content you want the user to share. A link will be automatically created and put in the message.
        branchUniversalObject.showShareSheet(this, linkProperties, shareSheetStyle, new Branch.BranchLinkShareListener() {
            @Override
            public void onShareLinkDialogLaunched() {
            }

            @Override
            public void onShareLinkDialogDismissed() {
            }

            @Override
            public void onChannelSelected(String channelName) {
            }

            @Override
            public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                // The link will be available in sharedLink
            }
        });
    }


}
