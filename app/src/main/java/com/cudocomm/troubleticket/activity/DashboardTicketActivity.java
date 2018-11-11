package com.cudocomm.troubleticket.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.adapter.TicketAdapter;
import com.cudocomm.troubleticket.adapter.ViewPagerAdapter;
import com.cudocomm.troubleticket.fragment.TicketListKadipFragment;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.util.ApiClient;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;

public class DashboardTicketActivity extends AppCompatActivity {

    private RecyclerView ticketListRV;
    List<Ticket> tickets;
    private SwipeRefreshLayout ticketSwiper;
    private TicketAdapter ticketAdapter;

    public View emptyListLayout;

    private EditText searchET;

    String searchString = "";

    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();

    String param,title;
    Toolbar toolbar;
    TabLayout ticketTabLayout;
    ViewPager ticketViewer;

    ViewPagerAdapter viewPagerAdapter;
    String urlAC, urlNonAC, urlOchn, urlNex, totalURL = "";
    String totalAC, totalNonAC, totalOchn, totalNex;
    List<String>totalArray = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_ticket);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        setTitle(bundle.getString("title"));

        title = bundle.getString("title");
        param = bundle.getString("param");

        setURL();
        initComponent();
        updateComponent();
        initComponent2();
        loadAllTickets(urlAC,"ac");

//        totalNonAC = totalURL;
//        totalURL = "";

//        totalOchn = totalURL;
//        totalURL = "";

//        totalNex = totalURL;
//        totalURL = "";



    }

    public void setURL(){
        if (title.equals("Open Tickets")){
            urlAC = "openticket_AC_kadivdashboard";
            urlNonAC = "openticket_NON_AC_kadivdashboard";
            urlOchn = "openticket_OCH_kadivdashboard";
            urlNex = "openticket_NEX_kadivdashboard";
        }else if (title.equals("Close Tickets")){
            urlAC = "closeticket_AC_kadivdashboard";
            urlNonAC = "closeticket_NON_AC_kadivdashboard";
            urlOchn = "openticket_OCH_kadivdashboard";
            urlNex = "openticket_NEX_kadivdashboard";
        }else if (title.equals("Critical Tickets")){
            urlAC = "critical_ticket_AC_kadivdashboard";
            urlNonAC = "critical_ticket_NON_AC_kadivdashboard";
            urlOchn = "openticket_OCH_kadivdashboard";
            urlNex = "openticket_NEX_kadivdashboard";
        }else if (title.equals("Major Tickets")){
            urlAC = "major_ticket_AC_kadivdashboard";
            urlNonAC = "major_ticket_NON_AC_kadivdashboard";
            urlOchn = "openticket_OCH_kadivdashboard";
            urlNex = "openticket_NEX_kadivdashboard";
        }else if (title.equals("Minor Tickets")){
            urlAC = "minor_ticket_AC_kadivdashboard";
            urlNonAC = "minor_ticket_NON_AC_kadivdashboard";
            urlOchn = "openticket_OCH_kadivdashboard";
            urlNex = "openticket_NEX_kadivdashboard";
        }
    }

    private void initComponent2() {
//        titleTV = (TextView) findViewById(R.id.positionTV);
//        statusTV = (TextView) findViewById(R.id.dateTV);
        ticketTabLayout = (TabLayout) findViewById(R.id.ticket_tab_layout);
        ticketViewer = (ViewPager) findViewById(R.id.ticket_viewer);
    }

    private void updateComponent2(List<String> totalAC2) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new TicketListKadipFragment().newInstance(Constants.AC_NIELSEN,urlAC), Constants.AC_NIELSEN+ " ("+ totalAC2.get(0) +")");
        viewPagerAdapter.addFragment(new TicketListKadipFragment().newInstance(Constants.NON_AC_NIELSEN,urlNonAC), Constants.NON_AC_NIELSEN+ " ("+ totalAC2.get(1) +")");
        viewPagerAdapter.addFragment(new TicketListKadipFragment().newInstance(Constants.O_CHANNEL,urlOchn), Constants.O_CHANNEL+ " ("+ totalAC2.get(2) +")");
        viewPagerAdapter.addFragment(new TicketListKadipFragment().newInstance(Constants.NEXMEDIA,urlNex), Constants.NEXMEDIA+ " ("+ totalAC2.get(3) +")");

        ticketViewer.setAdapter(viewPagerAdapter);
        ticketViewer.setOffscreenPageLimit(4);
        ticketTabLayout.setupWithViewPager(ticketViewer);
        ticketTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        ticketTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        ticketTabLayout.setTabTextColors(getResources().getColor(R.color.md_grey_300), getResources().getColor(R.color.md_white_1000));

    }

    public void loadAllTickets(String url, final String ket) {
        ticketSwiper.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl(url),
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Logcat.e("response: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int total = jObj.getInt("total");
                            totalURL = String.valueOf(total);
                            Log.d("total2222",String.valueOf(total));
                            setTotal(String.valueOf(total),ket);
                            loadAllTickets2(urlNonAC,"non");
                            Log.d("TOTAL11111111",String.valueOf(total));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ticketSwiper.setRefreshing(false);

                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Logcat.e("Volley error: " + error.getMessage() + ", code: " + error.networkResponse);
                        ticketSwiper.setRefreshing(false);
                        Toast.makeText(TTSApplication.getContext(), "Terjadi Kesalahan. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> items = new HashMap<>();
                Logcat.e("params: " + items.toString());
                return items;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        TTSApplication.getInstance().addToRequestQueue(request);
    }

    public void loadAllTickets2(String url, final String ket) {
        ticketSwiper.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl(url),
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Logcat.e("response: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int total = jObj.getInt("total");
                            totalURL = String.valueOf(total);
                            Log.d("total2222",String.valueOf(total));
                            setTotal(String.valueOf(total),ket);
                            loadAllTickets3(urlOchn,"ochn");
                            Log.d("TOTAL11111111",String.valueOf(total));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ticketSwiper.setRefreshing(false);

                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Logcat.e("Volley error: " + error.getMessage() + ", code: " + error.networkResponse);
                        ticketSwiper.setRefreshing(false);
                        Toast.makeText(TTSApplication.getContext(), "Terjadi Kesalahan. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> items = new HashMap<>();
                Logcat.e("params: " + items.toString());
                return items;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        TTSApplication.getInstance().addToRequestQueue(request);
    }

    public void loadAllTickets3(String url, final String ket) {
        ticketSwiper.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl(url),
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Logcat.e("response: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int total = jObj.getInt("total");
                            totalURL = String.valueOf(total);
                            Log.d("total2222",String.valueOf(total));
                            setTotal(String.valueOf(total),ket);
                            loadAllTickets4(urlNex,"nex");
                            Log.d("TOTAL11111111",String.valueOf(total));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ticketSwiper.setRefreshing(false);

                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Logcat.e("Volley error: " + error.getMessage() + ", code: " + error.networkResponse);
                        ticketSwiper.setRefreshing(false);
                        Toast.makeText(TTSApplication.getContext(), "Terjadi Kesalahan. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> items = new HashMap<>();
                Logcat.e("params: " + items.toString());
                return items;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        TTSApplication.getInstance().addToRequestQueue(request);
    }

    public void loadAllTickets4(String url, final String ket) {
        ticketSwiper.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl(url),
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Logcat.e("response: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int total = jObj.getInt("total");
                            totalURL = String.valueOf(total);
                            Log.d("total2222",String.valueOf(total));
                            setTotal(String.valueOf(total),ket);
                            Log.d("TOTAL11111111",String.valueOf(total));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ticketSwiper.setRefreshing(false);

                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Logcat.e("Volley error: " + error.getMessage() + ", code: " + error.networkResponse);
                        ticketSwiper.setRefreshing(false);
                        Toast.makeText(TTSApplication.getContext(), "Terjadi Kesalahan. Umumnya karena masalah jaringan.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> items = new HashMap<>();
                Logcat.e("params: " + items.toString());
                return items;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        TTSApplication.getInstance().addToRequestQueue(request);
    }


    public void setTotal(String value, String from){
        if (from.equals("ac")) {
            this.totalAC = value;
            totalArray.add(totalAC);
        }else if (from.equals("non")){
            totalNonAC = value;
            totalArray.add(totalNonAC);
        }else if (from.equals("ochn")){
            totalOchn = value;
            totalArray.add(totalOchn);
        }else if (from.equals("nex")){
            totalNex = value;
            totalArray.add(totalNex);
        }
        if (totalArray.size() == 4){
            updateComponent2(totalArray);
            totalArray.clear();
        }
    }

    private void initComponent() {
        tickets = new ArrayList<>();
        searchET = (EditText) findViewById(R.id.searchET);
        emptyListLayout = findViewById(R.id.emptyListLayout);
        ticketSwiper = (SwipeRefreshLayout) findViewById(R.id.ticketSwiper);
        ticketListRV = (RecyclerView) findViewById(R.id.ticketListRV);
        ticketSwiper.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void updateComponent() {

        ticketSwiper.post(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });

        ticketSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        searchET.setText("");
        searchET.addTextChangedListener(searchListener());

    }

    public TextWatcher searchListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                searchString = searchET.getText().toString();
                new ToDoListTask().execute();
            }
        };
    }

    public void refresh() {
        new ToDoListTask().execute();

    }

    private void processList() {
        if(tickets.size() > 0) {
            if(ticketAdapter == null) {
                ticketAdapter = new TicketAdapter(tickets, new TicketAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Ticket ticket) {
                        Intent intent = new Intent(getApplicationContext(), TicketActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.SELECTED_TICKET, ticket);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, Constants.REQUEST_CODE);

                    }
                });
                ticketAdapter.setHasStableIds(true);
                ticketListRV.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                ticketListRV.setLayoutManager(linearLayoutManager);


                ticketListRV.setAdapter(ticketAdapter);
            } else {
                ticketAdapter.swap(tickets);
            }
            emptyListLayout.setVisibility(View.GONE);
            ticketListRV.setVisibility(View.VISIBLE);
        } else {
            emptyListLayout.setVisibility(View.VISIBLE);
            ticketListRV.setVisibility(View.GONE);
        }


    }

    class ToDoListTask extends AsyncTask<Void, Void, Void> {

        String result;
        JSONObject object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ticketSwiper.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                result = ApiClient.post(
                        CommonsUtil.getAbsoluteUrl(param),
                        new FormBody.Builder().add("search", searchString).build());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                object = new JSONObject(result);
                if(object.get(Constants.RESPONSE_STATUS).equals(Constants.RESPONSE_SUCCESS)) {
                    Type type = new TypeToken<List<Ticket>>(){}.getType();
                    tickets = gson.fromJson(object.getString("data"), type);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            processList();
            ticketSwiper.setRefreshing(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new ToDoListTask().execute();
    }
}
