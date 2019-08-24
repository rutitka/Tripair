package com.example.tripair;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.DefaultItemAnimator;
        import android.support.v7.widget.DividerItemDecoration;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.widget.ImageButton;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.util.*;

        import com.example.dataUser.Trip;
        import com.example.dataUser.User;
        import com.example.recycleViewPack.ContactPOJO;
        import com.example.recycleViewPack.CustomContactAdapter;
        import com.example.recycleViewPack.OnRecyclerClickListener;
        import com.example.recycleViewPack.TripPOJO;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

public class OptionalPartnerPerTripActivity extends AppCompatActivity {
    private ArrayList<ContactPOJO> mArrayList = new ArrayList<>();
    Trip m_trip;
    String m_partnerID;
    String m_image;
    private RecyclerView mRecyclerView1;
    private CustomContactAdapter mAdapter;
    private String m_uid;
    private User m_user;
    private String m_tripCountry;
    private String m_tripCity;
    private ArrayList<ContactPOJO> mArrayDemoFav = new ArrayList<>();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRef = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optional_partner_per_trip);
        Intent intent = getIntent();
        m_uid = intent.getStringExtra("userUid");
        m_user = (User)intent.getSerializableExtra("user");
        m_tripCountry = intent.getStringExtra("tripCountry");
        m_tripCity = intent.getStringExtra("tripCity");

        TextView lineText = findViewById(R.id.txt_line);
        lineText.setText("Your optional partners to - "+m_tripCountry+","+m_tripCity);

      //  mArrayList = (ArrayList<ContactPOJO>) intent.getSerializableExtra("arrayPartner");
        mRecyclerView1 = findViewById(R.id.recycleView);
        mAdapter = new CustomContactAdapter(mArrayList, new OnRecyclerClickListener() {
            @Override
            public void onRecyclerViewItemClicked(int position, int id) {
                Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();
            }
            public void onRecyclerViewItemFavClicked(int position, int id) {
                Toast.makeText(getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
                addPartnerToFavorites(position);

            }
    });


        mRecyclerView1.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView1.setItemAnimator( new DefaultItemAnimator());
        mRecyclerView1.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView1.setAdapter(mAdapter);

        ValueEventListener UserListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for (DataSnapshot ds : children) {
                        m_trip = ds.getValue(Trip.class);
                        m_partnerID = m_trip.getM_ownerID();
                        if (! (m_partnerID.equals(m_uid))) {
                            getUserPartnerAccordingID(m_partnerID);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRef.child("Countries").child(m_tripCountry).child(m_tripCity).addValueEventListener(UserListener2);
    }


    private void getUserPartnerAccordingID(String partnerID) {
        ValueEventListener UserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User userPartner;
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    userPartner = dataSnapshot.getValue(User.class);
                    Trip tripOfPartner = userPartner.getAllTrips().findInTripList(m_tripCountry, m_tripCity);
                    if (tripOfPartner != null) {
                        addUserPartnerToList(userPartner.getFirstName(), userPartner.getLastName(), tripOfPartner.getArriveDay(),
                                tripOfPartner.getArriveMonth(), tripOfPartner.getArriveYear(), tripOfPartner.getLeftDay(), tripOfPartner.getLeftMonth(), tripOfPartner.getLeftYear(), userPartner.isSmoking(), userPartner.getAge()," ");
                    }
                    String partnerID = m_partnerID;
                    getURLPartnerAccordingID(partnerID);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRef.child("usersProfile").child(m_partnerID).addValueEventListener(UserListener);
    }

    private void getURLPartnerAccordingID(String partnerID) {
        ValueEventListener UserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String url;
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    url = (String)dataSnapshot.getValue();
                    String partnerID = m_partnerID;
                     updateUrlForContanct(url, partnerID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRef.child("Image").child(m_partnerID).addValueEventListener(UserListener);
    }
    private void updateUrlForContanct(String url, String partnerID) {
        for (int i = 0; i < mArrayList.size(); i++) {
            if (partnerID.equals(mArrayList.get(i))) {
                mArrayList.get(i).setmImage(url);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
    private void addUserPartnerToList(String firstName, String lastName, int arriveDay, int arriveMonth, int arriveYear, int leftDay, int leftMonth, int leftYear, boolean smoking, int age, String image) {

        // get the array of all trips and make array of tripPOJO
        ContactPOJO UserPartner = null;
        UserPartner = new ContactPOJO(firstName,lastName, arriveDay, arriveMonth, arriveYear, leftDay, leftMonth, leftYear, smoking, age, image);
        mArrayList.add(UserPartner);
        mAdapter.notifyDataSetChanged();
    }

    private void addPartnerToFavorites(int position) {
        TextView name, date, smoke,age,leftDate;
        ImageButton btnFav,btnDelete;

            Log.v("ViewHolder","in View Holder");
            name = findViewById(R.id.txt_line);
            date = findViewById(R.id.txt_dest_insert);
            leftDate = findViewById(R.id.txt_dateL);
            smoke = findViewById(R.id.txt_smoke_insert);
            age=findViewById(R.id.txt_age_insert);

        ContactPOJO contact = new ContactPOJO();
        contact.setmName(name.getText().toString());
        contact.setmAge(Integer.parseInt(age.getText().toString()));
        contact.setmDateDest(date.getText().toString());
        contact.setmDateLeft(leftDate.getText().toString());
        contact.setmSmoking(smoke.getText().toString());
        Integer id  = position;
        mArrayDemoFav.add(contact);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_partner_page,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.editTrip:
            {
                Intent intent = new Intent(this, TripSettingsActivity.class);
                intent.putExtra("userUid", m_uid);
                intent.putExtra("user", m_user);
                startActivity(intent);
            }

            case R.id.editPartner:
            {

            }

            case R.id.favPartners:
            {
                Intent intent = new Intent(this, FavPartnersActivity.class);
                intent.putExtra("favArr", mArrayDemoFav);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
