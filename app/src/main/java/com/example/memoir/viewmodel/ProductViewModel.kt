package com.example.memoir.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memoir.R
import com.example.memoir.model.ProductModel
import com.example.memoir.repository.ProductRepository

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {
    private val _products = MutableLiveData<List<ProductModel>>()
    val products: LiveData<List<ProductModel>> get() = _products

    fun loadProducts() {
        // Load products from repository
        val products = listOf(
            ProductModel("1", "Rose Bouquet",  29.99, R.drawable.bangles),
            ProductModel("2", "Tulip Collection",24.99, R.drawable.bangles),
            ProductModel("3", "Orchid Arrangement", 39.99, R.drawable.bangles),
//            ProductModel(
//                "4",
//                "Sunflower Bunch",
//                "Adoration, loyalty, longevity",
//                19.99,
//                R.drawable.sunflowers
//            ),
//            ProductModel(
//                "5",
//                "Lily Bouquet",
//                "Purity, innocence, virtue",
//                34.99,
//                R.drawable.lily
//            ),
//            ProductModel(
//                "6",
//                "Forget-Me-Not Set",
//                "Remembrance, true love, loyalty",
//                27.99,
//                R.drawable.forget_me_not
//            ),
//            ProductModel(
//                "7",
//                "Chrysanthemum Elegance",
//                "Loyalty, friendship, abundance",
//                32.99,
//                R.drawable.chrysanthemum
//            ),
//            ProductModel(
//                "8",
//                "Lavender Bliss",
//                "Serenity, calmness, devotion",
//                22.99,
//                R.drawable.lavenders
//            ),
//            ProductModel(
//                "9",
//                "Peony Romance",
//                "Prosperity, good fortune, romance",
//                42.99,
//                R.drawable.peony
//            ),
//            ProductModel(
//                "10",
//                "Daisy Freshness",
//                "Happiness, innocence, simplicity",
//                18.99,
//                R.drawable.daisy
//            )
        )
        _products.value = products
    }
}