package com.external.verification.models

import kotlinx.serialization.Serializable

@Serializable
data class Division(
    val id: String,
    val name: String,
    val bn_name: String,
    val url: String
)

@Serializable
data class District(
    val id: String,
    val division_id: String,
    val name: String,
    val bn_name: String,
    val lat: String,
    val lon: String,
    val url: String
)

@Serializable
data class Upazila(
    val id: String,
    val district_id: String,
    val name: String,
    val bn_name: String,
    val url: String
)

@Serializable
data class Union(
    val id: String,
    val upazila_id: String,
    val name: String,
    val bn_name: String,
    val url: String
)

@Serializable
data class GeoResponse(
    val success: Boolean,
    val data: List<GeoItem>? = null,
    val error: String? = null
)

@Serializable
data class GeoItem(
    val id: String,
    val name: String,
    val bn_name: String
)
