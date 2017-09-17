package com.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ecommerce.data.Campaign;
import com.ecommerce.data.Order;
import com.ecommerce.data.User;
import com.ecommerce.utils.StringConstants;

import java.io.File;
import java.util.ArrayList;

/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */
public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    ProgressDialog progDialog;
    private SharedPreferences prefs;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private StorageReference mStorageRef;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    final ArrayList<Order> orders = new ArrayList<Order>();
    final ArrayList<Campaign> campaigns = new ArrayList<Campaign>();

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_authentication);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        progDialog = new ProgressDialog(SignInActivity.this);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(StringConstants.PREF_USERNAME, user.getDisplayName());
                            editor.putString(StringConstants.PREF_USERUSERID, user.getUid());
                            editor.apply();

//                            if (!getIntent().getExtras().getString("role").equals("seller"))
//                                insertUser(user, "buyer");
//                            else {
//                                insertUser(user, "seller");
//                            }
                            //createCampaign(new Campaign());
                            fetchCampaign("-KtVPRiDc-uF_oHYfVQO");
                            //updateUserRole(user);

                            createOrder(new Order());


                            //fetchOrder("order1");
                            //fetchOrders();


                            updateUI(user);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    /****************** DB FUNCTIONS*********************************/
    /**
     * Create User
     *
     * @param fbUser
     */
    private void insertUser(FirebaseUser fbUser, String role) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        User currentUser = new User();
        currentUser.setEmail(fbUser.getEmail());
        currentUser.setName(fbUser.getDisplayName());
        //currentUser.setRole("buyer");
        currentUser.setRole(role);
        mDatabase.child(fbUser.getUid()).setValue(currentUser);


//        Example on how to push Object with nested Children to DB
//        DatabaseReference mDatabaseOrder = FirebaseDatabase.getInstance().getReference("orders");
//        Order currentOrder = new Order();
//        currentOrder.setOrderQuantity(2);
//        currentOrder.setUser(currentUser);
//        mDatabaseOrder.child("order1").setValue(currentOrder);
    }


    //ToDo

    /**
     * Update User role to Seller
     *
     * @param fbUser
     */
    private void updateUserRole(FirebaseUser fbUser) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(fbUser.getUid()).child("role").setValue("seller");
    }


    /**
     * Fetch all Campaigns - Buyer
     */
    private void fetchCampaigns() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("campaigns");
        //todo sort by created date
        Query campaignsQuery = ref.orderByKey();
        campaignsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot campaignSnapshot : dataSnapshot.getChildren()) {
                    Campaign tempCampaign = campaignSnapshot.getValue(Campaign.class);
                    campaigns.add(tempCampaign);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }


    /**
     * Fetch all Campaigns for a seller
     */
    private void fetchCampaignsBySeller(String sellerId) {

        // fetch all campaign ids from seller
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        Query ordersQuery = ref.orderByChild(sellerId);
        Query sellersQuery = ref.orderByKey();
        sellersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot sellerSnapshot : dataSnapshot.getChildren()) {
                    User tempUser = sellerSnapshot.getValue(User.class);
                    String[] campaigns = tempUser.getCampaignsAsSeller();
                    //todo write Firebase cloud function to fetch all Campaigns that matches the Campaign ids of the array
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


    }


    /**
     * Create Campaign: SELLER
     */

    private void createCampaign(Campaign mCampaign) {

        //This should be input to this method

        ArrayList<String> ordersBuBuyers = new ArrayList<>();
        ordersBuBuyers.add("KtUExfNsyyT6v44YMX4");
        //*******START******
        mCampaign = new Campaign();
        mCampaign.setActive(true);
        //while creating order, both total & available quantity will be the same
        mCampaign.setTotalQuantity(100);
        mCampaign.setAvailableQuantity(100);
        mCampaign.setBaseDiscount(8.5);
        //todo: valid Date in string format
        mCampaign.setMaxDiscount(20);
        //fetch this image URL from uploadFile function
        mCampaign.setImageURL("");
        mCampaign.setName("Product Name");
        mCampaign.setPrice(20.5);
        mCampaign.setOrdersByBuyers(ordersBuBuyers);
        //*******END******

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("campaigns");
        //generate key and place it as unique id
        String mKey = mDatabase.push().getKey();
        mCampaign.setId(mKey);
        mDatabase.child(mKey).setValue(mCampaign);
    }


    /**
     * Fetch Campaign details based on key: BUYER
     */
    private void fetchCampaign(String campaignId) {
        final ArrayList<Campaign> campaigns = new ArrayList<Campaign>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("campaigns").child(campaignId);
        Query campaignsQuery = ref.orderByKey();
        campaignsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot campaignSnapshot : dataSnapshot.getChildren()) {
                    Campaign tempCampaign = campaignSnapshot.getValue(Campaign.class);
                    campaigns.add(tempCampaign);
                }
                Log.i("tst","tst");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }


    /**
     * Create Order: BUYER
     */

    private void createOrder(Order mOrder) {

        //This should be input to this method
        //*******START******
        mOrder = new Order();
        //fetch User key & Campaign key and persist below for future quick access
        FirebaseUser user = mAuth.getCurrentUser();

        mOrder.setUserId(user.getUid());
        mOrder.setCampaignId("KtVPRiDc-uF_oHYfVQO");
        //while creating order, both total & available quantity will be the same
        mOrder.setOrderQuantity(123);
        mOrder.setPrice(200);
        //*******END******

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("orders");
        //generate key and place it as unique id
        String mKey = mDatabase.push().getKey();
        mOrder.setId(mKey);
        mDatabase.child(mKey).setValue(mOrder, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                if (databaseError != null) {
                    Log.e(TAG, "Failed to write message", databaseError.toException());
                } else {
                    // ToDo: cloud code - do the following
                    reference.getKey();

                    // put Orderid on Campaign: ordersByBuyers (fetch the row from Campaign with specific key & update new orderId on that
                    // put Orderid on User: ordersByBuyers
                    // update CAMPAIGN row: available stock (fetch current available stock & reduce the orderedQuantity from it
                }
            }

        });


    }


    /**
     * Fetch wallet balance & history
     */

    private void fetchWalletInformation(User user) {
        // fetch wallet balance
        // fetch list of recent wallet transactions (campaign name, campaign pic, order % completion, cashback amount)
    }


    /**
     * Sale closed: by SELLER
     */
    private void closeCampaign(Campaign campaign) {
        // todo: cloud function
        // update campaign to inactive
        // fetch all orders associated with the campaign & update cashback to each order
    }


    //Method to Upload Image to Storage
    private void uploadFile() {
        //todo: Image picker
        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        StorageReference riversRef = mStorageRef.child("images/" + file.getLastPathSegment());

// Register observers to listen for when the download is done or if it fails
        riversRef.putFile(file).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests")
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //todo: store this link on Campaign
            }
        });
    }


    private void fetchOrder(String orderId) {
        final ArrayList<Order> orders = new ArrayList<Order>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders");
        Query ordersQuery = ref.orderByChild(orderId);
        ordersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Order tempOrder = orderSnapshot.getValue(Order.class);
                    orders.add(tempOrder);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }


    private void fetchOrders() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders");
        //todo sort by created date
        Query ordersQuery = ref.orderByKey();
        ordersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Order tempOrder = orderSnapshot.getValue(Order.class);
                    orders.add(orderSnapshot.getValue(Order.class));
                    //todo: do whatever you want with this data once callback occurs. ex: set adapter of recycle view
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }


    /**
     * Sample code for Delete record
     *
     * @param orderId
     */
    private void deleteOrder(String orderId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders").child(orderId);
        ref.removeValue();
    }


    /**
     * User Sign Out
     * //todo: permit user to perform operation only when session is active
     */

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    /**
     * ONLY FOR TESTING PURPOSE
     *
     * @param user
     */
    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        } else if (i == R.id.sign_out_button) {
            signOut();
        }
    }


    public void showProgressDialog() {
        progDialog.setMessage("Loading");
        //show dialog
        progDialog.show();
    }

    public void hideProgressDialog() {
        progDialog.dismiss();
    }

}