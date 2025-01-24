package com.example.facturaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.facturaapp.data.DatabaseProvider
import com.example.facturaapp.data.FacturaRepository
import com.example.facturaapp.ui.FacturaScreen
import com.example.facturaapp.ui.FacturaViewModel
import com.example.facturaapp.ui.FacturaViewModelFactory

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        val database = DatabaseProvider.getDatabase(this)
        val repository = FacturaRepository(database.facturaDao())
        val viewModel = ViewModelProvider(this, FacturaViewModelFactory(repository))[FacturaViewModel::class.java]

        setContent{
            FacturaScreen(viewModel = viewModel)
        }
    }
}