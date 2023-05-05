package com.example.eventfinder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Venue#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Venue extends Fragment {

    private JSONObject venueJSON;

    public Fragment_Venue() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                this.venueJSON = new JSONObject(getArguments().getString("venueResponse"));
                Log.e("Fragment_Venue", this.venueJSON.toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__venue, container, false);
        TextView name = view.findViewById(R.id.venue_1);
        String venueName = this.venueJSON.optString("name");
        name.setText(venueName);
        name.setSelected(true);

        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                FetchUtil.getCoordinatesOfLocation(requireContext(), venueName, new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) {
                        try {
                            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(jsonObject.getString("lat")),
                                            Double.parseDouble(jsonObject.getString("lng")))));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(jsonObject.getString("lat")),
                                    Double.parseDouble(jsonObject.getString("lng"))), 15f));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });

        TextView address = view.findViewById(R.id.address_1);
        address.setText(Objects.requireNonNull(this.venueJSON.optJSONObject("address")).optString("line1"));
        address.setSelected(true);


        String city_value = Objects.requireNonNull(this.venueJSON.optJSONObject("city")).optString("name", "");
        String stateName = Objects.requireNonNull(this.venueJSON.optJSONObject("state")).optString("name", "");
        city_value += ", " + stateName;

        TextView city = view.findViewById(R.id.city_state);
        city.setText(city_value);
        city.setSelected(true);

        String phone = Objects.requireNonNull(this.venueJSON.optJSONObject("boxOfficeInfo")).optString("phoneNumberDetail", "");
        TextView phoneView = view.findViewById(R.id.contact);
        phoneView.setText(phone);
        phoneView.setSelected(true);

        String hours = Objects.requireNonNull(this.venueJSON.optJSONObject("boxOfficeInfo")).optString("openHoursDetail", "");
        TextView hoursView = view.findViewById(R.id.open_hours_value);
        hoursView.setText(hours);
        hoursView.setSelected(true);

        String general = Objects.requireNonNull(this.venueJSON.optJSONObject("generalInfo")).optString("generalRule", "");
        TextView generalView = view.findViewById(R.id.general_rule_value);
        generalView.setText(general);
        generalView.setSelected(true);

        String child = Objects.requireNonNull(this.venueJSON.optJSONObject("generalInfo")).optString("childRule", "");
        TextView childView = view.findViewById(R.id.child_rule_value);
        childView.setText(child);
        childView.setSelected(true);

        hoursView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hoursView.getMaxLines() == 3) {
                    hoursView.setMaxLines(Integer.MAX_VALUE);
                    hoursView.setEllipsize(null);
                } else {
                    hoursView.setMaxLines(3);
                    hoursView.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });

        generalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (generalView.getMaxLines() == 3) {
                    generalView.setMaxLines(Integer.MAX_VALUE);
                    generalView.setEllipsize(null);
                } else {
                    generalView.setMaxLines(3);
                    generalView.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });


        childView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (childView.getMaxLines() == 3) {
                    childView.setMaxLines(Integer.MAX_VALUE);
                    childView.setEllipsize(null);
                } else {
                    childView.setMaxLines(3);
                    childView.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });


        return view;
    }
}