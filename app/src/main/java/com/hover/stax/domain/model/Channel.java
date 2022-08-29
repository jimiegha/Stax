package com.hover.stax.domain.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

@Entity(tableName = "channels")
public class Channel implements Comparable<Channel> {

    @PrimaryKey
    @NonNull
    public int id;
    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @NonNull
    @ColumnInfo(name = "country_alpha2")
    public String countryAlpha2;

    @ColumnInfo(name = "root_code")
    public String rootCode;

    @NonNull
    @ColumnInfo(name = "currency")
    public String currency;

    @NonNull
    @ColumnInfo(name = "hni_list")
    public String hniList;

    @NonNull
    @ColumnInfo(name = "logo_url")
    public String logoUrl;

    @NonNull
    @ColumnInfo(name = "institution_id")
    public int institutionId;

    @NonNull
    @ColumnInfo(name = "primary_color_hex")
    public String primaryColorHex;

    @NonNull
    @ColumnInfo(name = "published", defaultValue = "0")
    public Boolean published;

    @NonNull
    @ColumnInfo(name = "secondary_color_hex")
    public String secondaryColorHex;

    // Dont use the below, it needs to be removed
    @NonNull
    @ColumnInfo(name = "selected", defaultValue = "0")
    public boolean selected;
    @NonNull
    @ColumnInfo(name = "defaultAccount", defaultValue = "0")
    public boolean defaultAccount;

    @NonNull
    @ColumnInfo(name = "isFavorite", defaultValue = "0")
    public boolean isFavorite;

    @ColumnInfo(name = "pin")
    public String pin;
    @ColumnInfo(name = "latestBalance")
    public String latestBalance;
    @ColumnInfo(name = "latestBalanceTimestamp", defaultValue = "CURRENT_TIMESTAMP")
    public Long latestBalanceTimestamp;
    @ColumnInfo(name = "account_no")
    public String accountNo;
//    @Ignore
//    public String spentThisMonth, spentDifferenceToLastMonth;

    public Channel() {
    }

    public Channel(int _id, String addChannel) {
        this.id = _id;
        this.name = addChannel;
    }

    public Channel(JSONObject jsonObject, String rootUrl) {
        update(jsonObject, rootUrl);
    }

    public Channel update(JSONObject jsonObject, String rootUrl) {
        try {
            id = jsonObject.getInt("id");
            name = jsonObject.getString("name");
            rootCode = jsonObject.getString("root_code");
            countryAlpha2 = jsonObject.getString("country_alpha2").toUpperCase();
            currency = jsonObject.getString("currency");
            hniList = jsonObject.getString("hni_list");
            published = jsonObject.getBoolean("published");
            logoUrl = rootUrl + jsonObject.getString("logo_url");
            institutionId = jsonObject.getInt("institution_id");
            primaryColorHex = jsonObject.getString("primary_color_hex");
            secondaryColorHex = jsonObject.getString("secondary_color_hex");
        } catch (JSONException e) {
            Timber.d(e.getLocalizedMessage());
        }
        return this;
    }

    public String getUssdName() {
        return name + " - " + rootCode + " - " + countryAlpha2;
    }

    @Override
    public String toString() {
        return name + " " + countryAlpha2;
    }

    @Override
    public int compareTo(Channel cOther) {
        return this.toString().compareTo(cOther.toString());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Channel)) return false;
        Channel c = (Channel) other;
        return id == c.id;
    }
}