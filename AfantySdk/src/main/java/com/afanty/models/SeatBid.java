package com.afanty.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SeatBid {
    public List<Bid> bid;
    public String seat;
    public int group = 0;
    public Object ext;

    public SeatBid(String jsonStr, String rId, JSONObject extJson) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            this.seat = json.optString("seat");
            this.bid = createBid(json.optJSONArray("bid"), rId, extJson);
            this.group = json.optInt("group");
            this.ext = json.opt("ext");
        } catch (Exception e) {

        }
    }

    private List<Bid> createBid(JSONArray jsonArray, String rId,JSONObject extJson) {
        List<Bid> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Bid bid = new Bid(jsonArray.getJSONObject(i).toString());
                bid.id = rId;
                int refreshInterval = 0;
                if (extJson != null && extJson.has(bid.getPid())) {
                    JSONObject jsonTagId = extJson.getJSONObject(bid.getPid());
                    if (jsonTagId.has("r_s_t")) {
                        refreshInterval = jsonTagId.optInt("r_s_t", 0);
                    }
                }
                bid.setRefreshInterval(refreshInterval);
                list.add(bid);
            }
        } catch (Exception e) {
        }
        return list;
    }
}
