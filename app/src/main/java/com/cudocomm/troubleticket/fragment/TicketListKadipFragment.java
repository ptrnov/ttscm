package com.cudocomm.troubleticket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cudocomm.troubleticket.R;
import com.cudocomm.troubleticket.TTSApplication;
import com.cudocomm.troubleticket.activity.TicketActivity;
import com.cudocomm.troubleticket.adapter.TicketAdapter;
import com.cudocomm.troubleticket.model.Ticket;
import com.cudocomm.troubleticket.util.CommonsUtil;
import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.cudocomm.troubleticket.util.Preferences;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketListKadipFragment extends BaseFragment {

    private View rootView;

    private RecyclerView ticketListRV;
    List<Ticket> tickets;

    private SwipeRefreshLayout ticketSwiper;
    private TicketAdapter ticketAdapter;

    public View emptyListLayout;

    String val,URL;

    public TicketListKadipFragment() {
        // Required empty public constructor
    }

    public static TicketListKadipFragment newInstance(String val,String url) {
        TicketListKadipFragment fragment = new TicketListKadipFragment();
        Bundle args = new Bundle();
        args.putString("param", val);
        args.putString("url",url);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(getContext());
        if (getArguments() != null) {
            val = getArguments().getString("param");
            URL = getArguments().getString("url");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ticket_list, container, false);

        initComponent();

        return rootView;
    }

    private void initComponent() {
        tickets = new ArrayList<>();
        emptyListLayout = rootView.findViewById(R.id.emptyListLayout);
        ticketSwiper = (SwipeRefreshLayout) rootView.findViewById(R.id.ticketSwiper);

        ticketListRV = (RecyclerView) rootView.findViewById(R.id.ticketListRV);
        ticketSwiper.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if(val.equals(Constants.AC_NIELSEN)) {
            loadTickets();
        } else if(val.equals(Constants.NON_AC_NIELSEN)) {
            loadAllTickets();
        }else if(val.equals(Constants.O_CHANNEL)) {
            loadAllTickets2();
        }else if(val.equals(Constants.NEXMEDIA)) {
            loadAllTickets3();
        }
        ticketSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(val.equals(Constants.AC_NIELSEN)) {
                    loadTickets();
                } else if(val.equals(Constants.NON_AC_NIELSEN)) {
                    loadAllTickets();
                }else if(val.equals(Constants.O_CHANNEL)) {
                    loadAllTickets2();
                }else if(val.equals(Constants.NEXMEDIA)) {
                    loadAllTickets3();
                }
            }
        });
    }

    public void refresh() {
        if(val.equals(Constants.AC_NIELSEN)) {
            loadTickets();
        } else if(val.equals(Constants.NON_AC_NIELSEN)) {
            loadAllTickets();
        }else if(val.equals(Constants.O_CHANNEL)) {
            loadAllTickets2();
        }else if(val.equals(Constants.NEXMEDIA)) {
            loadAllTickets3();
        }
    }

    public void loadTickets() {
        ticketSwiper.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl(URL),
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Logcat.e("response: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            if (jObj.getString("status").equalsIgnoreCase("success")) {
                                Type type = new TypeToken<List<Ticket>>(){}.getType();
                                tickets = gson.fromJson(jObj.getString("tickets"), type);
                                if (String.valueOf(tickets).equals("null")){
                                    emptyListLayout.setVisibility(View.VISIBLE);
                                    ticketListRV.setVisibility(View.GONE);
                                }else {
                                    if(tickets.size() > 0) {
                                        if(ticketAdapter == null) {
                                            ticketAdapter = new TicketAdapter(tickets, new TicketAdapter.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(Ticket ticket) {
                                                    Intent intent = new Intent(context, TicketActivity.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable(Constants.SELECTED_TICKET, ticket);
                                                    intent.putExtras(bundle);
                                                    getActivity().startActivityForResult(intent, Constants.REQUEST_CODE);

                                                }
                                            });
                                            ticketAdapter.setHasStableIds(true);
                                            ticketListRV.setHasFixedSize(true);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
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

                            }
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

    public void loadAllTickets() {
        ticketSwiper.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl(URL),
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Logcat.e("response: " + response);
                        emptyListLayout.setVisibility(View.GONE);
                        ticketListRV.setVisibility(View.VISIBLE);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            if (jObj.getString("status").equalsIgnoreCase("success")) {
                                Type type = new TypeToken<List<Ticket>>(){}.getType();
                                tickets = gson.fromJson(jObj.getString("tickets"), type);

                                Log.d("ticket1111",String.valueOf(tickets));
                                if (String.valueOf(tickets).equals("null")){
                                    emptyListLayout.setVisibility(View.VISIBLE);
                                    ticketListRV.setVisibility(View.GONE);
                                }else {
                                    if(tickets.size() > 0) {
                                        if(ticketAdapter == null) {
                                            ticketAdapter = new TicketAdapter(tickets, new TicketAdapter.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(Ticket ticket) {
                                                    Intent intent = new Intent(context, TicketActivity.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable(Constants.SELECTED_TICKET, ticket);
                                                    intent.putExtras(bundle);
                                                    getActivity().startActivityForResult(intent, Constants.REQUEST_CODE);

                                                }
                                            });
                                            ticketAdapter.setHasStableIds(true);
                                            ticketListRV.setHasFixedSize(true);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
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

                            }
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

    public void loadAllTickets2() {
        ticketSwiper.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl(URL),
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Logcat.e("response: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            if (jObj.getString("status").equalsIgnoreCase("success")) {
                                Type type = new TypeToken<List<Ticket>>(){}.getType();
                                tickets = gson.fromJson(jObj.getString("tickets"), type);

                                Log.d("ticket1111",String.valueOf(tickets));
                                if (String.valueOf(tickets).equals("null")){
                                    emptyListLayout.setVisibility(View.VISIBLE);
                                    ticketListRV.setVisibility(View.GONE);
                                }else {
                                    if(tickets.size() > 0) {
                                        if(ticketAdapter == null) {
                                            ticketAdapter = new TicketAdapter(tickets, new TicketAdapter.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(Ticket ticket) {
                                                    Intent intent = new Intent(context, TicketActivity.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable(Constants.SELECTED_TICKET, ticket);
                                                    intent.putExtras(bundle);
                                                    getActivity().startActivityForResult(intent, Constants.REQUEST_CODE);

                                                }
                                            });
                                            ticketAdapter.setHasStableIds(true);
                                            ticketListRV.setHasFixedSize(true);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
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

                            }
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

    public void loadAllTickets3() {
        ticketSwiper.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.POST, CommonsUtil.getAbsoluteUrl(URL),
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Logcat.e("response: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            if (jObj.getString("status").equalsIgnoreCase("success")) {
                                Type type = new TypeToken<List<Ticket>>(){}.getType();
                                tickets = gson.fromJson(jObj.getString("tickets"), type);

                                Log.d("ticket1111",String.valueOf(tickets));
                                if (String.valueOf(tickets).equals("null")){
                                    emptyListLayout.setVisibility(View.VISIBLE);
                                    ticketListRV.setVisibility(View.GONE);
                                }else {
                                    if(tickets.size() > 0) {
                                        if(ticketAdapter == null) {
                                            ticketAdapter = new TicketAdapter(tickets, new TicketAdapter.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(Ticket ticket) {
                                                    Intent intent = new Intent(context, TicketActivity.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable(Constants.SELECTED_TICKET, ticket);
                                                    intent.putExtras(bundle);
                                                    getActivity().startActivityForResult(intent, Constants.REQUEST_CODE);

                                                }
                                            });
                                            ticketAdapter.setHasStableIds(true);
                                            ticketListRV.setHasFixedSize(true);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
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

                            }
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

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Logcat.i("MyTicketNew.java for korwil onActivityResult()");
        refresh();
    }

}
