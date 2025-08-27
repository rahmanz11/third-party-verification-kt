package com.external.verification.services

import com.external.verification.models.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.io.InputStream

class GeoDataService {
    
    private val mutex = Mutex()
    private var isInitialized = false
    
    // Data storage
    private val divisions = mutableListOf<Division>()
    private val districts = mutableListOf<District>()
    private val upazilas = mutableListOf<Upazila>()
    private val unions = mutableListOf<Union>()
    
    // Hierarchical mappings
    private val divisionToDistricts = mutableMapOf<String, List<District>>()
    private val districtToUpazilas = mutableMapOf<String, List<Upazila>>()
    private val upazilaToUnions = mutableMapOf<String, List<Union>>()
    
    suspend fun initialize() {
        mutex.withLock {
            if (isInitialized) return
            
            try {
                loadDivisions()
                loadDistricts()
                loadUpazilas()
                loadUnions()
                buildHierarchicalMappings()
                isInitialized = true
            } catch (e: Exception) {
                throw RuntimeException("Failed to initialize geographic data: ${e.message}", e)
            }
        }
    }
    
    private fun loadDivisions() {
        val inputStream = javaClass.classLoader.getResourceAsStream("geo/divisions.json")
        inputStream?.use { stream ->
            val jsonString = stream.bufferedReader().use { it.readText() }
            val divisionsList = Json.decodeFromString<List<Division>>(jsonString)
            divisions.addAll(divisionsList)
        } ?: throw RuntimeException("Could not load divisions.json")
    }
    
    private fun loadDistricts() {
        val inputStream = javaClass.classLoader.getResourceAsStream("geo/districts.json")
        inputStream?.use { stream ->
            val jsonString = stream.bufferedReader().use { it.readText() }
            val districtsList = Json.decodeFromString<List<District>>(jsonString)
            districts.addAll(districtsList)
        } ?: throw RuntimeException("Could not load districts.json")
    }
    
    private fun loadUpazilas() {
        val inputStream = javaClass.classLoader.getResourceAsStream("geo/upazilas.json")
        inputStream?.use { stream ->
            val jsonString = stream.bufferedReader().use { it.readText() }
            val upazilasList = Json.decodeFromString<List<Upazila>>(jsonString)
            upazilas.addAll(upazilasList)
        } ?: throw RuntimeException("Could not load upazilas.json")
    }
    
    private fun loadUnions() {
        val inputStream = javaClass.classLoader.getResourceAsStream("geo/unions.json")
        inputStream?.use { stream ->
            val jsonString = stream.bufferedReader().use { it.readText() }
            val unionsList = Json.decodeFromString<List<Union>>(jsonString)
            unions.addAll(unionsList)
        } ?: throw RuntimeException("Could not load unions.json")
    }
    
    private fun buildHierarchicalMappings() {
        // Build division to districts mapping
        divisions.forEach { division ->
            val divisionDistricts = districts.filter { it.division_id == division.id }
            divisionToDistricts[division.id] = divisionDistricts
        }
        
        // Build district to upazilas mapping
        districts.forEach { district ->
            val districtUpazilas = upazilas.filter { it.district_id == district.id }
            districtToUpazilas[district.id] = districtUpazilas
        }
        
        // Build upazila to unions mapping
        upazilas.forEach { upazila ->
            val upazilaUnions = unions.filter { it.upazila_id == upazila.id }
            upazilaToUnions[upazila.id] = upazilaUnions
        }
    }
    
    fun getAllDivisions(): List<GeoItem> {
        if (!isInitialized) throw RuntimeException("GeoDataService not initialized")
        return divisions.map { GeoItem(it.id, it.name, it.bn_name) }
    }
    
    fun getDistrictsByDivision(divisionId: String): List<GeoItem> {
        if (!isInitialized) throw RuntimeException("GeoDataService not initialized")
        val divisionDistricts = divisionToDistricts[divisionId] ?: emptyList()
        return divisionDistricts.map { GeoItem(it.id, it.name, it.bn_name) }
    }
    
    fun getUpazilasByDistrict(districtId: String): List<GeoItem> {
        if (!isInitialized) throw RuntimeException("GeoDataService not initialized")
        val districtUpazilas = districtToUpazilas[districtId] ?: emptyList()
        return districtUpazilas.map { GeoItem(it.id, it.name, it.bn_name) }
    }
    
    fun getUnionsByUpazila(upazilaId: String): List<GeoItem> {
        if (!isInitialized) throw RuntimeException("GeoDataService not initialized")
        val upazilaUnions = upazilaToUnions[upazilaId] ?: emptyList()
        return upazilaUnions.map { GeoItem(it.id, it.name, it.bn_name) }
    }
    
    fun getDivisionById(divisionId: String): Division? {
        if (!isInitialized) throw RuntimeException("GeoDataService not initialized")
        return divisions.find { it.id == divisionId }
    }
    
    fun getDistrictById(districtId: String): District? {
        if (!isInitialized) throw RuntimeException("GeoDataService not initialized")
        return districts.find { it.id == districtId }
    }
    
    fun getUpazilaById(upazilaId: String): Upazila? {
        if (!isInitialized) throw RuntimeException("GeoDataService not initialized")
        return upazilas.find { it.id == upazilaId }
    }
    
    fun getUnionById(unionId: String): Union? {
        if (!isInitialized) throw RuntimeException("GeoDataService not initialized")
        return unions.find { it.id == unionId }
    }
    
    fun isInitialized(): Boolean = isInitialized
}
