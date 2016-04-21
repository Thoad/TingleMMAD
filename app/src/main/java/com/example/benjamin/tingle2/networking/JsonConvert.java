package com.example.benjamin.tingle2.networking;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Extract Outpan information from an Outpan JSON string
 */
public class JsonConvert {

    /**
     * Given a jsonstring from Outpan, extracts and return the name of the item
     * @param jsonstring
     * @return The name of the item extracted from jsonstring
     * @throws JSONException
     */
    public String parseJsonString(String jsonstring) throws JSONException{
        JSONObject jsonObject = new JSONObject(jsonstring);
        String name = jsonObject.getString("name");

        return name;
    }
}

/* EXAMPLE JSON OBJECT
{
    "gtin": "0036000291452",
    "outpan_url": "https:\/\/www.outpan.com\/view_product.php?barcode=0036000291452",
    "name": "Grand Marnier Raspberry Peach Signature Collection Liqueur - 750 mL",
    "attributes": {
        "Brand": "Kimberly-Clark brands",
        "Manufacturer": "Marnier Lapostolle",
        "Volume": "750 mL"
    },
    "images": [],
    "videos": [],
    "categories": []
}

 */
