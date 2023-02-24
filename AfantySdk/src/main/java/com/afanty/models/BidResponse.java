package com.afanty.models;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BidResponse {
    public String id;
    public List<SeatBid> seatbid;
    public String bidid;
    public String cur = "USD";
    public String customdata;
    public int nbr;

    public BidResponse(String jsonStr, String tagId) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            this.id = json.optString("id");
            this.bidid = json.optString("bidid");
            this.cur = json.optString("cur");
            this.customdata = json.optString("customdata");
            this.nbr = json.optInt("nbr");
            this.seatbid = createSeatBids(json.optJSONArray("seatbid"), id, json.optJSONObject("ext"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<SeatBid> createSeatBids(JSONArray array, String _id, JSONObject json) {
        List<SeatBid> list = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                SeatBid seatBid = new SeatBid(array.optJSONObject(i).toString(), _id, json);
                list.add(seatBid);
            }
        } catch (Exception e) {
        }
        return list;
    }

    public Bid getFirstBid() {
        if (seatbid == null || seatbid.size() <= 0) {
            return null;
        }
        List<Bid> bids = seatbid.get(0).bid;
        if (bids == null || bids.size() <= 0) {
            return null;
        }
        return bids.get(0);
    }
}
