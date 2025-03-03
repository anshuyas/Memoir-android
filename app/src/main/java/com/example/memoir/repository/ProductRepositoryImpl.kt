package com.example.memoir.repository

import com.example.memoir.R
import com.example.memoir.model.ProductModel

class ProductRepositoryImpl: ProductRepository {
    // Sample product data
    private val sampleProducts = mapOf(
        "1" to ProductModel("1", "Aqua Ring",  7.99, R.drawable.bluering),
        "2" to ProductModel("2", "Couple Ring",  8.50, R.drawable.couplering),
        "3" to ProductModel("3", "Leaf Ring", 6.33, R.drawable.leafring),
        "4" to ProductModel("4", "Moon Ring", 5.99, R.drawable.moonring),
        "5" to ProductModel("5", "Bow Necklace", 14.99, R.drawable.bownecklace),
        "6" to ProductModel("6", "Pearl Necklace",  20.99, R.drawable.pearlnecklace),
        "7" to ProductModel("7", "Silver Heart Necklace",  12.99, R.drawable.silverheart),
        "8" to ProductModel("8", "Swarovski ",  45.99, R.drawable.swarovski),
        "9" to ProductModel("9", "Gold Bracelet",  36.99, R.drawable.goldbangle),
        "10" to ProductModel("10", "Diamond Bracelet",  48.99, R.drawable.diamongbangle),
        "11" to ProductModel("11", "Pink Bracelet",  26.99, R.drawable.pinkbangle),
        "12" to ProductModel("12", "Star Bracelet",  15.99, R.drawable.startbangle),
        "13" to ProductModel("13", "Bow Earring",  5.50, R.drawable.bowtop),
        "14" to ProductModel("14", "Butterfly Earring",  6.70, R.drawable.butterflytop),
        "15" to ProductModel("15", "Heart Earring",  8.50, R.drawable.hearttop),
        "16" to ProductModel("16", "Flower Earring",  3.50, R.drawable.flowertop),
    )

    override fun getProductById(productId: String, callback: (ProductModel?, Boolean) -> Unit) {
        val product = sampleProducts[productId]
        callback(product, product != null)
    }

}