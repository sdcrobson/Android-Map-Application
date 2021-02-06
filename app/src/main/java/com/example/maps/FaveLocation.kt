package com.example.maps

import com.mapbox.geojson.Feature
import com.mapbox.mapboxsdk.geometry.LatLng


class FaveLocation (latLng: LatLng, title: String) {

    var getLatLng: LatLng = latLng

    get(){
        return field
    }

    var getTitle: String = title

    get(){
        return field
    }
}